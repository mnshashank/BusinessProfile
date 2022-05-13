package com.intuit.businessprofile.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.intuit.businessprofile.service.BusinessProfileService;

@WebMvcTest(BusinessProfileController.class)
class BusinessProfileControllerTest {

    private static final String GET_RESPONSE = "{\"profileId\":\"deda0173-c0e9-4cf7-81be-d1417ff765a0\",\"companyName\":\"Intuit\",\"companyLegalName\":\"Intuit LLC updated\",\"email\":\"intuit" +
            ".company@gmail.com\",\"website\":\"https://intuit.com\",\"businessAddresses\":[{\"id\":\"0ce77c95-c48d-4567-8428-ec2e92af6709\",\"line1\":\"Address line 1\",\"line2\":\"Address line " +
            "2\",\"city\":\"Bengaluru\",\"state\":\"Karnataka\",\"zip\":\"576606\",\"country\":\"India\"}],\"legalAddresses\":[{\"id\":\"6a771440-faa5-4bc8-857f-be0ca3900518\",\"line1\":\"Address " +
            "line 11\",\"line2\":\"Address line 22\",\"city\":\"Bengaluru\",\"state\":\"Karnataka\",\"zip\":\"576606\",\"country\":\"India\"}]," +
            "\"taxIdentifiers\":{\"id\":\"b80e03d7-f8b6-4d64-b452-6276f70e090a\",\"pan\":\"GVMPI5674L\",\"ein\":\"23334161\"},\"productSubscriptions\":[{\"productId\":\"prod1002\"," +
            "\"productName\":\"QuickBook Payroll\"},{\"productId\":\"prod1001\",\"productName\":\"QuickBook\"}]}";
    private static final String PROFILE_DATA = "{\"companyName\":\"Intuit\",\"companyLegalName\":\"IntuitLLCupdated\",\"email\":\"intuit.company@gmail.com\",\"website\":\"https://intuit.com\"," +
            "\"businessAddresses\":[{\"id\":\"0ce77c95-c48d-4567-8428-ec2e92af6709\",\"line1\":\"Addressline1\",\"line2\":\"Addressline2\",\"city\":\"Bengaluru\"," +
            "\"state\":\"Karnataka\",\"zip\":\"576606\",\"country\":\"India\"}],\"legalAddresses\":[{\"id\":\"6a771440-faa5-4bc8-857f-be0ca3900518\",\"line1\":\"Addressline11\"," +
            "\"line2\":\"Addressline22\",\"city\":\"Bengaluru\",\"state\":\"Karnataka\",\"zip\":\"576606\",\"country\":\"India\"}],\"taxIdentifiers\":{\"pan\":\"GVMPI5674L\"," +
            "\"ein\":\"23334161\"},\"productSubscriptions\":[{\"productId\":\"prod1002\",\"productName\":\"QuickBookPayroll\"},{\"productId\":\"prod1001\"," + "\"productName\":\"QuickBook\"}]}";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BusinessProfileService businessProfileService;

    @Test
    void getProfileInformationTest() throws Exception {
        when(businessProfileService.getBusinessProfile(any())).thenReturn(GET_RESPONSE);

        this.mockMvc.perform(get(BusinessProfileController.BUSINESS_PROFILE_BASE_URL + "/deda0173-c0e9-4cf7-81be-d1417ff765a0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(GET_RESPONSE)));
    }

    @Test
    void getProfileInformationInvalidProfileIdTest() throws Exception {
        this.mockMvc.perform(get(BusinessProfileController.BUSINESS_PROFILE_BASE_URL + "/abc"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteProfileTest() throws Exception {
        doNothing().when(businessProfileService)
                .deleteProfile(any());

        this.mockMvc.perform(delete(BusinessProfileController.BUSINESS_PROFILE_BASE_URL + "/deda0173-c0e9-4cf7-81be-d1417ff765a0"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteProfileInvalidProfileIdTest() throws Exception {
        this.mockMvc.perform(delete(BusinessProfileController.BUSINESS_PROFILE_BASE_URL + "/abc"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createProfileTest() throws Exception {
        UUID profileId = UUID.fromString("deda0173-c0e9-4cf7-81be-d1417ff765a0");

        when(businessProfileService.createProfile(any())).thenReturn(profileId);

        this.mockMvc.perform(post(BusinessProfileController.BUSINESS_PROFILE_BASE_URL).contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(PROFILE_DATA))
                .andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(content().json("{\"jobId\":\"deda0173-c0e9-4cf7-81be-d1417ff765a0\",\"profileId\":\"deda0173-c0e9-4cf7-81be-d1417ff765a0\",\"status\":\"ACCEPTED\"}"));
    }

    @Test
    void createProfileInvalidRequestBody() throws Exception {
        String profileData = "{}";

        this.mockMvc.perform(post(BusinessProfileController.BUSINESS_PROFILE_BASE_URL).contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(profileData))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void putProfileTest() throws Exception {
        UUID profileId = UUID.fromString("deda0173-c0e9-4cf7-81be-d1417ff765a0");
        String profileData = "{\"companyName\":\"Intuit\",\"companyLegalName\":\"IntuitLLCupdated\",\"email\":\"intuit.company@gmail.com\",\"website\":\"https://intuit.com\"," +
                "\"businessAddresses\":[{\"id\":\"0ce77c95-c48d-4567-8428-ec2e92af6709\",\"line1\":\"Addressline1\",\"line2\":\"Addressline2\",\"city\":\"Bengaluru\"," +
                "\"state\":\"Karnataka\",\"zip\":\"576606\",\"country\":\"India\"}],\"legalAddresses\":[{\"id\":\"6a771440-faa5-4bc8-857f-be0ca3900518\",\"line1\":\"Addressline11\"," +
                "\"line2\":\"Addressline22\",\"city\":\"Bengaluru\",\"state\":\"Karnataka\",\"zip\":\"576606\",\"country\":\"India\"}],\"taxIdentifiers\":{\"pan\":\"GVMPI5674L\"," +
                "\"ein\":\"23334161\"},\"productSubscriptions\":[{\"productId\":\"prod1002\",\"productName\":\"QuickBookPayroll\"},{\"productId\":\"prod1001\"," + "\"productName\":\"QuickBook\"}]}";

        when(businessProfileService.updateProfile(any(), any())).thenReturn(profileId);

        this.mockMvc.perform(put(BusinessProfileController.BUSINESS_PROFILE_BASE_URL + "/" + profileId).contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(profileData))
                .andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(content().json("{\"jobId\":\"deda0173-c0e9-4cf7-81be-d1417ff765a0\",\"status\":\"ACCEPTED\"}"));
    }

    @Test
    void putProfileInvalidProfileIdTest() throws Exception {
        String profileData = "{\"companyName\":\"Intuit\",\"companyLegalName\":\"IntuitLLCupdated\",\"email\":\"intuit.company@gmail.com\",\"website\":\"https://intuit.com\"," +
                "\"businessAddresses\":[{\"id\":\"0ce77c95-c48d-4567-8428-ec2e92af6709\",\"line1\":\"Addressline1\",\"line2\":\"Addressline2\",\"city\":\"Bengaluru\"," +
                "\"state\":\"Karnataka\",\"zip\":\"576606\",\"country\":\"India\"}],\"legalAddresses\":[{\"id\":\"6a771440-faa5-4bc8-857f-be0ca3900518\",\"line1\":\"Addressline11\"," +
                "\"line2\":\"Addressline22\",\"city\":\"Bengaluru\",\"state\":\"Karnataka\",\"zip\":\"576606\",\"country\":\"India\"}],\"taxIdentifiers\":{\"pan\":\"GVMPI5674L\"," +
                "\"ein\":\"23334161\"},\"productSubscriptions\":[{\"productId\":\"prod1002\",\"productName\":\"QuickBookPayroll\"},{\"productId\":\"prod1001\"," + "\"productName\":\"QuickBook\"}]}";

        this.mockMvc.perform(put(BusinessProfileController.BUSINESS_PROFILE_BASE_URL + "/" + "dummy").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(profileData))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void putProfileInvalidRequestBody() throws Exception {
        String profileData = "{}";

        this.mockMvc.perform(put(BusinessProfileController.BUSINESS_PROFILE_BASE_URL + "/" + "dummy").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(profileData))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}
