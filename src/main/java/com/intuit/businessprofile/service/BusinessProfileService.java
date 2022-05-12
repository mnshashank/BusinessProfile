package com.intuit.businessprofile.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import com.intuit.businessprofile.base.constant.JobStatus;
import com.intuit.businessprofile.base.entity.ProfileEntity;
import com.intuit.businessprofile.base.exception.BusinessProfileNotAcceptableException;
import com.intuit.businessprofile.base.exception.BusinessProfileNotFoundException;
import com.intuit.businessprofile.base.pojo.Profile;
import com.intuit.businessprofile.base.repository.JobRepository;
import com.intuit.businessprofile.base.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessProfileService {

    private final JedisTemplate jedisTemplate;
    private final ProfileRepository profileRepo;
    private final JobRepository jobRepo;
    private final ProfileResponseGenerator profileResponseGenerator;
    private final TaskExecutor taskExecutor;
    private final JobProcessingService jobProcessingService;

    public String getBusinessProfile(UUID profileId) {
        log.info("Getting profile information for profileId: {}", profileId);

        // check if the business profile information is in redis cache with profileId as the key.
        Optional<String> profileOptional = jedisTemplate.getIfExists(profileId.toString());
        if (profileOptional.isPresent()) {
            return profileOptional.get();
        }

        // get the profile information from the DB
        ProfileEntity profile = profileRepo.findById(profileId)
                .orElseThrow(() -> new BusinessProfileNotFoundException(String.format("Business profile for the given id %s not present in the database.", profileId)));

        // create profile json string to be stored and sent using the db fetched data
        String profileData = profileResponseGenerator.getProfileResponse(profile, profileId);

        // populate the cache with profile data
        jedisTemplate.set(profileId.toString(), profileData, 0);

        // return the profile data
        return profileData;
    }

    public void deleteProfile(UUID profileId) {
        log.info("Deleting profile information for profileId: {}", profileId);

        // delete all profile related information cascade
        profileRepo.deleteById(profileId);

        // delete the mapping from redis cache
        jedisTemplate.del(profileId.toString());

        // delete all jobs for a profileID
        jobRepo.deleteAllJobsForProfile(profileId.toString());
    }

    public UUID createProfile(Profile profile) {
        UUID profileId = UUID.randomUUID();

        log.info("Creating new profile with profileID: {}", profileId);

        // do not allow creation of profile if there is already a same company legal name in the db
        if (profileRepo.findByCompanyLegalName(profile.getCompanyLegalName())
                .isPresent()) {
            throw new BusinessProfileNotAcceptableException(String.format("Cannot create profile with given company legal name: %s", profile.getCompanyLegalName()));
        }

        // create a job in T_JOB table (in case of create jobId == profileId)
        jobProcessingService.createJobInDatabase(profileId, profileId, profile);

        // create a new thread for processing in background
        taskExecutor.execute(() -> jobProcessingService.performCreation(profile, profileId));

        return profileId;
    }

    public UUID updateProfile(Profile profile, UUID profileId) {
        log.info("Updating profile with profileID: {}", profileId);

        if (jobRepo.findByProfileIdAndStatus(profileId.toString(), JobStatus.ACCEPTED)
                .isPresent()) {
            throw new BusinessProfileNotAcceptableException("Previous update operation is still in progress, please try in a moment");
        }

        UUID jobId = UUID.randomUUID();
        // check and fetch the profile from database
        profileRepo.findById(profileId)
                .orElseThrow(() -> new BusinessProfileNotFoundException(String.format("Could not find entry for profileId: %s", profileId)));

        // create a job for update in T_Job table
        jobProcessingService.createJobInDatabase(profileId, jobId, profile);

        // create a background thread for job processing
        taskExecutor.execute(() -> jobProcessingService.performUpdate(profile, profileId));

        return jobId;
    }

}
