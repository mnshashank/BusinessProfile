package com.intuit.businessprofile.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.businessprofile.base.constant.JobStatus;
import com.intuit.businessprofile.base.entity.JobEntity;
import com.intuit.businessprofile.base.entity.ProfileEntity;
import com.intuit.businessprofile.base.exception.BusinessProfileBadRequestException;
import com.intuit.businessprofile.base.exception.BusinessProfileNotAcceptableException;
import com.intuit.businessprofile.base.exception.BusinessProfileNotFoundException;
import com.intuit.businessprofile.base.pojo.Profile;
import com.intuit.businessprofile.base.pojo.ValidationResponse;
import com.intuit.businessprofile.base.pojo.WebRequest;
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
    private final ObjectMapper mapper;
    private final TaskExecutor taskExecutor;
    private final WebRequestPreparationService webRequestPreparationService;
    private final ProductsValidationService productsValidationService;

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
        createJobInDatabase(profileId, profileId, profile);

        // create a new thread for processing in background
        taskExecutor.execute(() -> {
            JobStatus status = JobStatus.SUCCESS;
            try {
                // call all subscribed products for validation (pick the subscribed products from input payload)
                List<WebRequest> validationRequests = webRequestPreparationService.getCreateProfileValidationWebRequests(profile.getProductSubscriptions(), profile);

                status = performCreateValidation(profile, profileId, status, validationRequests);
            } catch (Exception exception) {
                log.error("Exception occurred while validating the profile in a separate thread for profileId: {}", profileId);
                status = JobStatus.FAILED;
            }
            //update job table with the new status
            jobRepo.updateJobStatus(status, profileId.toString());
        });
        return profileId;
    }

    @Transactional
    public UUID updateProfile(Profile profile, UUID profileId) {
        log.info("Updating profile with profileID: {}", profileId);

        // TODO: check if there is already an on-going job for the profile

        UUID jobId = UUID.randomUUID();
        // check and fetch the profile from database
        ProfileEntity profileEntity = profileRepo.findById(profileId)
                .orElseThrow(() -> new BusinessProfileNotFoundException(String.format("Could not find entry for profileId: %s", profileId)));

        // create a job for update in T_Job table
        createJobInDatabase(profileId, jobId, profile);

        // create a background thread for processing
        taskExecutor.execute(() -> {
            JobStatus status = JobStatus.SUCCESS;
            try {
                // call all subscribed products for validation (pick the subscribed products from database)
                List<WebRequest> validationRequests = webRequestPreparationService.getUpdateProfileValidationWebRequests(profileEntity.getProductSubscriptions(), profile);

                status = performUpdateValidation(profileEntity, profile, profileId, status, validationRequests);
            } catch (Exception exception) {
                log.error("Exception occurred while validating the profile in a separate thread for profileId: {}", profileId);
                status = JobStatus.FAILED;
            }
            //update job table with the new status
            jobRepo.updateJobStatus(status, profileId.toString());
        });
        return jobId;
    }

    private JobStatus performUpdateValidation(ProfileEntity profileEntity, Profile profile, UUID profileId, JobStatus status, List<WebRequest> validationRequests) {
        ValidationResponse validationResponse = productsValidationService.validateWithProducts(validationRequests);

        if (validationResponse.isValid()) {
            // update the profile entity
            ProfileEntity.updateProfile(profileEntity, profile);

            // invalidate the redis cache
            jedisTemplate.del(profileId.toString());

            // save the updated profile entity
            profileRepo.save(profileEntity);
        } else {
            log.error("Validation failed while updating for profile with id: {}", profileId);
            status = JobStatus.FAILED;
        }
        return status;
    }

    private JobStatus performCreateValidation(Profile profile, UUID profileId, JobStatus status, List<WebRequest> validationRequests) {
        ValidationResponse validationResponse = productsValidationService.validateWithProducts(validationRequests);

        if (validationResponse.isValid()) {
            // invalidate the redis cache
            jedisTemplate.del(profileId.toString());

            // save the profile entity
            profileRepo.save(ProfileEntity.fromProfileAndProfileId(profile, profileId));
        } else {
            log.error("Validation failed while creating for profile with id: {}", profileId);
            status = JobStatus.FAILED;
        }
        return status;
    }

    private void createJobInDatabase(UUID profileId, UUID jobId, Profile profile) {
        try {
            JobEntity jobEntity = JobEntity.getInstanceFromProfileId(profileId, profileId);
            jobEntity.setPayload(mapper.writeValueAsString(profile));
            jobRepo.save(jobEntity);
        } catch (JsonProcessingException jsonProcessingException) {
            throw new BusinessProfileBadRequestException("Error while parsing the request body");
        }
    }

}
