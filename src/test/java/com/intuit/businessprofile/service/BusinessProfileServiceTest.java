package com.intuit.businessprofile.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskExecutor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.businessprofile.base.entity.JobEntity;
import com.intuit.businessprofile.base.entity.ProfileEntity;
import com.intuit.businessprofile.base.exception.BusinessProfileNotAcceptableException;
import com.intuit.businessprofile.base.exception.BusinessProfileNotFoundException;
import com.intuit.businessprofile.base.pojo.Profile;
import com.intuit.businessprofile.base.repository.JobRepository;
import com.intuit.businessprofile.base.repository.ProfileRepository;

@ExtendWith(MockitoExtension.class)
class BusinessProfileServiceTest {

    private final ProfileEntity profileEntity;
    private final String profileData;
    private final UUID profileId;
    private final Profile profilePojo;

    @Mock
    private JedisTemplate jedisTemplate;

    @Mock
    private ProfileRepository profileRepo;

    @Mock
    private JobRepository jobRepo;

    @Mock
    private ProfileResponseGenerator profileResponseGenerator;

    @Mock
    private TaskExecutor taskExecutor;

    @Mock
    private JobProcessingService jobProcessingService;

    @InjectMocks
    private BusinessProfileService businessProfileService;

    BusinessProfileServiceTest() throws JsonProcessingException {
        profileData = "{\"companyName\":\"Intuit\",\"companyLegalName\":\"IntuitLLCupdated\",\"email\":\"intuit.company@gmail.com\",\"website\":\"https://intuit.com\"," +
                "\"businessAddresses\":[{\"id\":\"0ce77c95-c48d-4567-8428-ec2e92af6709\",\"line1\":\"Addressline1\",\"line2\":\"Addressline2\",\"city\":\"Bengaluru\"," +
                "\"state\":\"Karnataka\",\"zip\":\"576606\",\"country\":\"India\"}],\"legalAddresses\":[{\"id\":\"6a771440-faa5-4bc8-857f-be0ca3900518\",\"line1\":\"Addressline11\"," +
                "\"line2\":\"Addressline22\",\"city\":\"Bengaluru\",\"state\":\"Karnataka\",\"zip\":\"576606\",\"country\":\"India\"}],\"taxIdentifiers\":{\"pan\":\"GVMPI5674L\"," +
                "\"ein\":\"23334161\"},\"productSubscriptions\":[{\"productId\":\"prod1002\",\"productName\":\"QuickBookPayroll\"},{\"productId\":\"prod1001\"," + "\"productName\":\"QuickBook\"}]}";
        ObjectMapper mapper = new ObjectMapper();
        profilePojo = mapper.readValue(profileData, Profile.class);
        profileId = UUID.fromString("deda0173-c0e9-4cf7-81be-d1417ff765a0");
        profileEntity = ProfileEntity.fromProfileAndProfileId(profilePojo, profileId);
    }

    @Test
    void GetBusinessProfileTestNotPresentInCache() {
        doNothing().when(jedisTemplate)
                .set(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyLong());
        when(jedisTemplate.getIfExists(ArgumentMatchers.anyString())).thenReturn(Optional.empty());
        when(profileRepo.findById(profileId)).thenReturn(Optional.ofNullable(profileEntity));
        when(profileResponseGenerator.getProfileResponse(profileEntity, profileId)).thenReturn(profileData);

        String profileInfo = businessProfileService.getBusinessProfile(profileId);

        assertNotNull(profileInfo);
        assertEquals(profileData, profileInfo);
    }

    @Test
    void GetBusinessProfileTestPresentInCache() {
        when(jedisTemplate.getIfExists(ArgumentMatchers.anyString())).thenReturn(Optional.of(profileData));

        String profileInfo = businessProfileService.getBusinessProfile(profileId);

        assertNotNull(profileInfo);
        assertEquals(profileData, profileInfo);
    }

    @Test
    void DeleteProfileTest() {
        doNothing().when(jedisTemplate)
                .del(ArgumentMatchers.anyString());
        doNothing().when(profileRepo)
                .deleteById(profileId);
        doNothing().when(jobRepo)
                .deleteAllJobsForProfile(profileId.toString());

        businessProfileService.deleteProfile(profileId);

        verify(jedisTemplate, times(1)).del(profileId.toString());
        verify(profileRepo, times(1)).deleteById(profileId);
        verify(jobRepo, times(1)).deleteAllJobsForProfile(profileId.toString());
    }

    @Test
    void CreateProfileTest() {
        when(profileRepo.findByCompanyLegalName(ArgumentMatchers.anyString())).thenReturn(Optional.empty());
        doNothing().when(jobProcessingService)
                .createJobInDatabase(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        doNothing().when(taskExecutor)
                .execute(ArgumentMatchers.any());

        UUID profileId = businessProfileService.createProfile(profilePojo);

        assertNotNull(profileId);

        verify(profileRepo, times(1)).findByCompanyLegalName(ArgumentMatchers.anyString());
        verify(jobProcessingService, times(1)).createJobInDatabase(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        verify(taskExecutor, times(1)).execute(ArgumentMatchers.any());
    }

    @Test
    void CreateProfileTest_ProfileExists() {
        when(profileRepo.findByCompanyLegalName(ArgumentMatchers.anyString())).thenReturn(Optional.of(profileEntity));

        assertThrows(BusinessProfileNotAcceptableException.class, () -> businessProfileService.createProfile(profilePojo));
    }

    @Test
    void UpdateProfileTest() {
        when(jobRepo.findByProfileIdAndStatus(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Optional.empty());
        when(profileRepo.findById(profileId)).thenReturn(Optional.of(profileEntity));
        doNothing().when(jobProcessingService)
                .createJobInDatabase(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        doNothing().when(taskExecutor)
                .execute(ArgumentMatchers.any());

        businessProfileService.updateProfile(profilePojo, profileId);

        verify(jobRepo, times(1)).findByProfileIdAndStatus(ArgumentMatchers.any(), ArgumentMatchers.any());
        verify(profileRepo, times(1)).findById(profileId);
        verify(jobProcessingService, times(1)).createJobInDatabase(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        verify(taskExecutor, times(1)).execute(ArgumentMatchers.any());

    }

    @Test
    void UpdateProfileTestUpdateJobAlreadyPresent() {
        when(jobRepo.findByProfileIdAndStatus(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Optional.ofNullable(new JobEntity()));

        assertThrows(BusinessProfileNotAcceptableException.class, () -> businessProfileService.updateProfile(profilePojo, profileId));
    }

    @Test
    void UpdateProfileTestProfileNotPresent() {
        when(jobRepo.findByProfileIdAndStatus(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Optional.empty());
        when(profileRepo.findById(profileId)).thenReturn(Optional.empty());

        assertThrows(BusinessProfileNotFoundException.class, () -> businessProfileService.updateProfile(profilePojo, profileId));
    }
}
