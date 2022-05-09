package com.intuit.businessprofile.controller;

import static com.intuit.businessprofile.controller.BusinessProfileController.BUSINESS_PROFILE_BASE_URL;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.businessprofile.service.BusinessProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(BUSINESS_PROFILE_BASE_URL)
@RequiredArgsConstructor
public class BusinessProfileController {

    static final String BUSINESS_PROFILE_BASE_URL = "/bp/v1/profiles";

    private final BusinessProfileService businessProfileService;

    @GetMapping(value = "/{profileId}", produces = "application/json")
    public String getProfileInformation(@PathVariable(name = "profileId") UUID profileId) {
        return businessProfileService.getBusinessProfile(profileId);
    }
}
