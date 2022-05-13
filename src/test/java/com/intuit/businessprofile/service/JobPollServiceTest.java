package com.intuit.businessprofile.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.intuit.businessprofile.base.constant.JobStatus;
import com.intuit.businessprofile.base.entity.JobEntity;
import com.intuit.businessprofile.base.exception.BusinessProfileNotFoundException;
import com.intuit.businessprofile.base.pojo.ProfileResponse;
import com.intuit.businessprofile.base.repository.JobRepository;

@ExtendWith(MockitoExtension.class)
class JobPollServiceTest {

    @Mock
    JobRepository jobRepo;

    @InjectMocks
    JobPollService jobPollService;

    @Test
    void pollProfileUpdateStatusTest() {
        UUID jobId = UUID.fromString("deda0173-c0e9-4cf7-81be-d1417ff765a0");
        JobEntity jobEntity = JobEntity.getInstanceFromProfileId(jobId, jobId);

        when(jobRepo.findById(jobId)).thenReturn(Optional.ofNullable(jobEntity));

        ProfileResponse profileResponse = jobPollService.pollProfileUpdateStatus(jobId);
        assertNotNull(profileResponse);
        assertEquals(JobStatus.ACCEPTED, profileResponse.getStatus());
        assertEquals(jobId, profileResponse.getJobId());
    }

    @Test
    void pollProfileUpdateStatusJobAlreadyExistsTest() {
        UUID jobId = UUID.fromString("deda0173-c0e9-4cf7-81be-d1417ff765a0");

        when(jobRepo.findById(jobId)).thenReturn(Optional.empty());

        assertThrows(BusinessProfileNotFoundException.class, () -> jobPollService.pollProfileUpdateStatus(jobId));
    }
}
