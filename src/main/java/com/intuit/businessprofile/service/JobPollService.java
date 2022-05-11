package com.intuit.businessprofile.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.intuit.businessprofile.base.entity.JobEntity;
import com.intuit.businessprofile.base.pojo.ProfileResponse;
import com.intuit.businessprofile.base.repository.JobRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobPollService {

    private final JobRepository jobRepo;

    public ProfileResponse pollProfileUpdateStatus(UUID profileId) {
        log.info("Polling profile update status for profileId: {}", profileId);

        // get the status information from the Job repo
        // TODO: have app level runtime exception classes and use it here.
        JobEntity job = jobRepo.findByProfileId(profileId.toString())
                .orElseThrow(RuntimeException::new);

        return ProfileResponse.builder()
                .status(job.getStatus())
                .profileId(profileId)
                .correlationId(job.getCorrelationId())
                .error(job.getError())
                .build();
    }
}
