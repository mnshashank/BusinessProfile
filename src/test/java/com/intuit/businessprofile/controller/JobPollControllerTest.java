package com.intuit.businessprofile.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.intuit.businessprofile.base.constant.JobStatus;
import com.intuit.businessprofile.base.pojo.ProfileResponse;
import com.intuit.businessprofile.service.JobPollService;

@WebMvcTest(JobPollController.class)
class JobPollControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private JobPollService jobPollService;

    @Test
    void pollUpdateProfileTest() throws Exception {
        UUID profileId = UUID.fromString("deda0173-c0e9-4cf7-81be-d1417ff765a0");
        ProfileResponse profileResponse = ProfileResponse.builder()
                .profileId(profileId)
                .jobId(profileId)
                .status(JobStatus.ACCEPTED)
                .build();

        when(jobPollService.pollProfileUpdateStatus(profileId)).thenReturn(profileResponse);

        this.mockMvc.perform(get(JobPollController.POLL_BASE_URL + "/" + profileId + "/status"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"jobId\":\"deda0173-c0e9-4cf7-81be-d1417ff765a0\",\"profileId\":\"deda0173-c0e9-4cf7-81be-d1417ff765a0\",\"status\":\"ACCEPTED\"}"));
    }

    @Test
    void pollUpdateProfileInvalidProfileIdTest() throws Exception {
        this.mockMvc.perform(get(JobPollController.POLL_BASE_URL + "/" + "dummy" + "/status"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}
