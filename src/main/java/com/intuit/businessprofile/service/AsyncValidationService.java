package com.intuit.businessprofile.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.intuit.businessprofile.base.pojo.ValidationResponse;
import com.intuit.businessprofile.base.pojo.WebRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AsyncValidationService {

    private final RetryValidationWebclientService retryValidationWebclientService;

    @Async
    public CompletableFuture<ValidationResponse> performAsyncValidation(WebRequest eachRequest) {
        return retryValidationWebclientService.executeCall(eachRequest);
    }
}
