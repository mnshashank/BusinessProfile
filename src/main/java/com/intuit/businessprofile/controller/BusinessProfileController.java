package com.intuit.businessprofile.controller;

import static com.intuit.businessprofile.controller.BusinessProfileController.BUSINESS_PROFILE_BASE_URL;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.businessprofile.base.constant.JobStatus;
import com.intuit.businessprofile.base.pojo.Profile;
import com.intuit.businessprofile.base.pojo.ProfileResponse;
import com.intuit.businessprofile.service.BusinessProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(BUSINESS_PROFILE_BASE_URL)
@RequiredArgsConstructor
@Validated
public class BusinessProfileController {

    static final String BUSINESS_PROFILE_BASE_URL = "/bp/v1/profiles";

    private final BusinessProfileService businessProfileService;

    @GetMapping(value = "/{profileId}", produces = "application/json")
    public String getProfileInformation(@PathVariable(name = "profileId") UUID profileId) {
        return businessProfileService.getBusinessProfile(profileId);
    }

    @DeleteMapping(value = "/{profileId}", produces = "application/json")
    public void deleteProfile(@PathVariable(name = "profileId") UUID profileId) {
        businessProfileService.deleteProfile(profileId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ProfileResponse createProfile(@RequestBody @Valid Profile profile) {
        return ProfileResponse.builder()
                .profileId(businessProfileService.createProfile(profile))
                .status(JobStatus.ACCEPTED)
                .build();
    }

    @PutMapping(value = "/{profileId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ProfileResponse updateProfile(@RequestBody @Valid Profile profile, @PathVariable(name = "profileId") UUID profileId) {
        businessProfileService.updateProfile(profile, profileId);
        return ProfileResponse.builder()
                .profileId(profileId)
                .status(JobStatus.ACCEPTED)
                .build();
    }
}
