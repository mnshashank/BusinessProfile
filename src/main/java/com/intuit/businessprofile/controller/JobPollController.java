package com.intuit.businessprofile.controller;

import static com.intuit.businessprofile.controller.JobPollController.POLL_BASE_URL;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.businessprofile.base.pojo.ProfileResponse;
import com.intuit.businessprofile.service.JobPollService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(POLL_BASE_URL)
@RequiredArgsConstructor
public class JobPollController {

    static final String POLL_BASE_URL = "/bp/v1/profiles/";

    private final JobPollService jobPollService;

    @GetMapping(value = "/{profileId}/status", produces = "application/json")
    public ProfileResponse pollUpdateProfile(@PathVariable(name = "profileId") UUID profileId) {
        return jobPollService.pollProfileUpdateStatus(profileId);
    }
}
