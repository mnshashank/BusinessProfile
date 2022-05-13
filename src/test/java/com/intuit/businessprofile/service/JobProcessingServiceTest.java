package com.intuit.businessprofile.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.businessprofile.base.entity.AddressEntity;
import com.intuit.businessprofile.base.entity.JobEntity;
import com.intuit.businessprofile.base.entity.ProfileEntity;
import com.intuit.businessprofile.base.exception.BusinessProfileBadRequestException;
import com.intuit.businessprofile.base.exception.BusinessProfileRuntimeException;
import com.intuit.businessprofile.base.pojo.Address;
import com.intuit.businessprofile.base.pojo.Profile;
import com.intuit.businessprofile.base.pojo.ValidationResponse;
import com.intuit.businessprofile.base.pojo.WebRequest;
import com.intuit.businessprofile.base.repository.JobRepository;
import com.intuit.businessprofile.base.repository.ProfileRepository;

@ExtendWith(MockitoExtension.class)
class JobProcessingServiceTest {

    String profileData;
    Profile profilePojo;
    ProfileEntity profileEntity;
    UUID profileId;

    @Mock
    private JedisTemplate jedisTemplate;

    @Mock
    private ProfileRepository profileRepo;

    @Mock
    private JobRepository jobRepo;

    @Mock
    private WebRequestPreparationService webRequestPreparationService;

    @Mock
    private ProductsValidationService productsValidationService;

    @Spy
    private ObjectMapper mapper;

    @Mock
    private Address address;

    @InjectMocks
    JobProcessingService jobProcessingService;

    JobProcessingServiceTest() throws JsonProcessingException {
        profileData =
                "{\"companyName\":\"Intuit\",\"companyLegalName\":\"IntuitLLCupdated\",\"email\":\"intuit.company@gmail.com\",\"website\":\"https://intuit.com\"," +
                        "\"businessAddresses\":[{\"id\":\"0ce77c95-c48d-4567-8428-ec2e92af6709\",\"line1\":\"Addressline1\",\"line2\":\"Addressline2\",\"city\":\"Bengaluru\"," +
                        "\"state\":\"Karnataka\",\"zip\":\"576606\",\"country\":\"India\"}],\"legalAddresses\":[{\"id\":\"6a771440-faa5-4bc8-857f-be0ca3900518\",\"line1\":\"Addressline11\"," +
                        "\"line2\":\"Addressline22\",\"city\":\"Bengaluru\",\"state\":\"Karnataka\",\"zip\":\"576606\",\"country\":\"India\"}],\"taxIdentifiers\":{\"pan\":\"GVMPI5674L\"," +
                        "\"ein\":\"23334161\"},\"productSubscriptions\":[{\"productId\":\"prod1002\",\"productName\":\"QuickBookPayroll\"},{\"productId\":\"prod1001\"," +
                        "\"productName\":\"QuickBook\"}]}";
        ObjectMapper mapper = new ObjectMapper();
        profilePojo = mapper.readValue(profileData, Profile.class);
        profileId = UUID.fromString("deda0173-c0e9-4cf7-81be-d1417ff765a0");
        profileEntity = ProfileEntity.fromProfileAndProfileId(profilePojo, profileId);
        List<AddressEntity> addresses = profileEntity.getAddresses();
        //setting same id as incoming request
        addresses.get(0)
                .setId(UUID.fromString("0ce77c95-c48d-4567-8428-ec2e92af6709"));
        addresses.get(1)
                .setId(UUID.fromString("6a771440-faa5-4bc8-857f-be0ca3900518"));
    }

    @Test
    void performCreationTest() throws ExecutionException, InterruptedException {
        WebRequest req1 = WebRequest.builder()
                .httpMethod(HttpMethod.POST)
                .url("intuit.com/quickbook")
                .build();
        List<WebRequest> webRequests = new ArrayList<>();
        webRequests.add(req1);
        ValidationResponse validationResponse = ValidationResponse.builder()
                .isValid(true)
                .build();

        when(webRequestPreparationService.getCreateProfileValidationWebRequests(profilePojo.getProductSubscriptions(), profilePojo)).thenReturn(webRequests);
        when(productsValidationService.validateWithProducts(webRequests)).thenReturn(validationResponse);
        when(profileRepo.save(ArgumentMatchers.any())).thenReturn(ProfileEntity.fromProfile(profilePojo));
        doNothing().when(jobRepo)
                .updateJobStatus(ArgumentMatchers.any(), ArgumentMatchers.any());

        jobProcessingService.performCreation(profilePojo, profileId);

        verify(webRequestPreparationService, times(1)).getCreateProfileValidationWebRequests(profilePojo.getProductSubscriptions(), profilePojo);
        verify(productsValidationService, times(1)).validateWithProducts(webRequests);
        verify(profileRepo, times(1)).save(ArgumentMatchers.any());
        verify(jobRepo, times(1)).updateJobStatus(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void performCreationValidateRequestsErrorTest() throws ExecutionException, InterruptedException {
        WebRequest req1 = WebRequest.builder()
                .httpMethod(HttpMethod.POST)
                .url("intuit.com/quickbook")
                .build();
        List<WebRequest> webRequests = new ArrayList<>();
        webRequests.add(req1);
        ValidationResponse validationResponse = ValidationResponse.builder()
                .isValid(false)
                .build();

        when(webRequestPreparationService.getCreateProfileValidationWebRequests(profilePojo.getProductSubscriptions(), profilePojo)).thenReturn(webRequests);
        when(productsValidationService.validateWithProducts(webRequests)).thenReturn(validationResponse);
        doNothing().when(jobRepo)
                .updateJobStatus(ArgumentMatchers.any(), ArgumentMatchers.any());

        jobProcessingService.performCreation(profilePojo, profileId);

        verify(webRequestPreparationService, times(1)).getCreateProfileValidationWebRequests(profilePojo.getProductSubscriptions(), profilePojo);
        verify(productsValidationService, times(1)).validateWithProducts(webRequests);
        verify(profileRepo, times(0)).save(ArgumentMatchers.any());
        verify(jobRepo, times(1)).updateJobStatus(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void performCreationExceptionDuringCreateWebRequestsTest() throws ExecutionException, InterruptedException {
        WebRequest req1 = WebRequest.builder()
                .httpMethod(HttpMethod.POST)
                .url("intuit.com/quickbook")
                .build();
        List<WebRequest> webRequests = new ArrayList<>();
        webRequests.add(req1);

        when(webRequestPreparationService.getCreateProfileValidationWebRequests(profilePojo.getProductSubscriptions(), profilePojo)).thenThrow(BusinessProfileRuntimeException.class);
        doNothing().when(jobRepo)
                .updateJobStatus(ArgumentMatchers.any(), ArgumentMatchers.any());

        jobProcessingService.performCreation(profilePojo, profileId);

        verify(webRequestPreparationService, times(1)).getCreateProfileValidationWebRequests(profilePojo.getProductSubscriptions(), profilePojo);
        verify(productsValidationService, times(0)).validateWithProducts(webRequests);
        verify(profileRepo, times(0)).save(ArgumentMatchers.any());
        verify(jobRepo, times(1)).updateJobStatus(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void performUpdateTest() throws ExecutionException, InterruptedException {
        WebRequest req1 = WebRequest.builder()
                .httpMethod(HttpMethod.POST)
                .url("intuit.com/quickbook")
                .build();
        List<WebRequest> webRequests = new ArrayList<>();
        webRequests.add(req1);
        ValidationResponse validationResponse = ValidationResponse.builder()
                .isValid(true)
                .build();

        when(profileRepo.findById(profileId)).thenReturn(Optional.ofNullable(profileEntity));
        when(webRequestPreparationService.getUpdateProfileValidationWebRequests(profileEntity.getProductSubscriptions(), profilePojo)).thenReturn(webRequests);
        when(productsValidationService.validateWithProducts(webRequests)).thenReturn(validationResponse);
        when(profileRepo.save(ArgumentMatchers.any())).thenReturn(ProfileEntity.fromProfile(profilePojo));
        doNothing().when(jobRepo)
                .updateJobStatus(ArgumentMatchers.any(), ArgumentMatchers.any());

        jobProcessingService.performUpdate(profilePojo, profileId);

        verify(webRequestPreparationService, times(1)).getUpdateProfileValidationWebRequests(profileEntity.getProductSubscriptions(), profilePojo);
        verify(productsValidationService, times(1)).validateWithProducts(webRequests);
        verify(profileRepo, times(1)).findById(profileId);
        verify(profileRepo, times(1)).save(ArgumentMatchers.any());
        verify(jobRepo, times(1)).updateJobStatus(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void performUpdateValidateRequestsErrorTest() throws ExecutionException, InterruptedException {
        UUID profileId = UUID.fromString("deda0173-c0e9-4cf7-81be-d1417ff765a0");
        WebRequest req1 = WebRequest.builder()
                .httpMethod(HttpMethod.POST)
                .url("intuit.com/quickbook")
                .build();
        List<WebRequest> webRequests = new ArrayList<>();
        webRequests.add(req1);
        ValidationResponse validationResponse = ValidationResponse.builder()
                .isValid(false)
                .build();

        when(profileRepo.findById(profileId)).thenReturn(Optional.ofNullable(profileEntity));
        when(webRequestPreparationService.getUpdateProfileValidationWebRequests(profileEntity.getProductSubscriptions(), profilePojo)).thenReturn(webRequests);
        when(productsValidationService.validateWithProducts(webRequests)).thenReturn(validationResponse);
        doNothing().when(jobRepo)
                .updateJobStatus(ArgumentMatchers.any(), ArgumentMatchers.any());

        jobProcessingService.performUpdate(profilePojo, profileId);

        verify(webRequestPreparationService, times(1)).getUpdateProfileValidationWebRequests(profileEntity.getProductSubscriptions(), profilePojo);
        verify(productsValidationService, times(1)).validateWithProducts(webRequests);
        verify(profileRepo, times(1)).findById(profileId);
        verify(profileRepo, times(0)).save(ArgumentMatchers.any());
        verify(jobRepo, times(1)).updateJobStatus(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void performUpdateExceptionDuringCreateWebRequestsTest() throws ExecutionException, InterruptedException {
        UUID profileId = UUID.fromString("deda0173-c0e9-4cf7-81be-d1417ff765a0");
        WebRequest req1 = WebRequest.builder()
                .httpMethod(HttpMethod.POST)
                .url("intuit.com/quickbook")
                .build();
        List<WebRequest> webRequests = new ArrayList<>();
        webRequests.add(req1);

        when(profileRepo.findById(profileId)).thenReturn(Optional.ofNullable(profileEntity));
        when(webRequestPreparationService.getUpdateProfileValidationWebRequests(profileEntity.getProductSubscriptions(), profilePojo)).thenThrow(BusinessProfileRuntimeException.class);
        doNothing().when(jobRepo)
                .updateJobStatus(ArgumentMatchers.any(), ArgumentMatchers.any());

        jobProcessingService.performUpdate(profilePojo, profileId);

        verify(webRequestPreparationService, times(1)).getUpdateProfileValidationWebRequests(profileEntity.getProductSubscriptions(), profilePojo);
        verify(productsValidationService, times(0)).validateWithProducts(webRequests);
        verify(profileRepo, times(1)).findById(profileId);
        verify(profileRepo, times(0)).save(ArgumentMatchers.any());
        verify(jobRepo, times(1)).updateJobStatus(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void createJobInDatabaseTest() {
        when(jobRepo.save(ArgumentMatchers.any())).thenReturn(JobEntity.getInstanceFromProfileId(profileId, profileId));

        jobProcessingService.createJobInDatabase(profileId, profileId, profilePojo);

        verify(jobRepo, times(1)).save(ArgumentMatchers.any());
    }

    @Test
    void createJobInDatabaseExceptionTest() {
        when(jobRepo.save(ArgumentMatchers.any())).thenThrow(BusinessProfileBadRequestException.class);

        assertThrows(BusinessProfileBadRequestException.class, () -> jobProcessingService.createJobInDatabase(profileId, profileId, profilePojo));
    }
}
