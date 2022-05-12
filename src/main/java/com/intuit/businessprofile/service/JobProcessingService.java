package com.intuit.businessprofile.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.businessprofile.base.constant.JobStatus;
import com.intuit.businessprofile.base.entity.JobEntity;
import com.intuit.businessprofile.base.entity.ProfileEntity;
import com.intuit.businessprofile.base.exception.BusinessProfileBadRequestException;
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
public class JobProcessingService {

    private final JedisTemplate jedisTemplate;
    private final ProfileRepository profileRepo;
    private final JobRepository jobRepo;
    private final WebRequestPreparationService webRequestPreparationService;
    private final ProductsValidationService productsValidationService;
    private final ObjectMapper mapper;

    public void performCreation(Profile profile, UUID profileId) {
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
    }

    private JobStatus performCreateValidation(Profile profile, UUID profileId, JobStatus status, List<WebRequest> validationRequests) throws ExecutionException, InterruptedException {
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void performUpdate(Profile profile, UUID profileId) {
        ProfileEntity profileEntity = profileRepo.findById(profileId)
                .get();
        JobStatus status = JobStatus.SUCCESS;
        try {
            // call all subscribed products for validation (pick the subscribed products from database)
            List<WebRequest> validationRequests = webRequestPreparationService.getUpdateProfileValidationWebRequests(profileEntity.getProductSubscriptions(), profile);

            status = performUpdateValidation(profileEntity, profile, profileId, status, validationRequests);
        } catch (Exception exception) {
            log.error("Exception occurred while validating the profile in a separate thread for profileId: {}", profileId, exception);
            status = JobStatus.FAILED;
        }
        //update job table with the new status
        jobRepo.updateJobStatus(status, profileId.toString());
    }

    private JobStatus performUpdateValidation(ProfileEntity profileEntity, Profile profile, UUID profileId, JobStatus status, List<WebRequest> validationRequests)
            throws ExecutionException, InterruptedException {
        ValidationResponse validationResponse = productsValidationService.validateWithProducts(validationRequests);

        if (validationResponse.isValid()) {
            // update the profile entity
            ProfileEntity.updateProfile(profileEntity, profile);

            // invalidate the redis cache
            jedisTemplate.del(profileId.toString());

            // save the updated profile entity
            profileRepo.save(profileEntity);
        } else {
            log.error("Validation failed while updating for profile with id: {} and error: {}", profileId, validationResponse.getError());
            status = JobStatus.FAILED;
        }
        return status;
    }

    public void createJobInDatabase(UUID profileId, UUID jobId, Profile profile) {
        try {
            JobEntity jobEntity = JobEntity.getInstanceFromProfileId(profileId, jobId);
            jobEntity.setPayload(mapper.writeValueAsString(profile));
            jobRepo.save(jobEntity);
        } catch (JsonProcessingException jsonProcessingException) {
            throw new BusinessProfileBadRequestException("Error while parsing the request body");
        }
    }
}
