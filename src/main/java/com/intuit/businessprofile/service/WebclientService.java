package com.intuit.businessprofile.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WebclientService {

    private final RestTemplate restTemplate;

    public void executeCall() {

    }
}
