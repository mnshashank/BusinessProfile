package com.intuit.businessprofile.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.intuit.businessprofile.base.pojo.ValidationResponse;
import com.intuit.businessprofile.base.pojo.WebRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductsValidationService {

    private final AsyncValidationService asyncValidationService;

    public ValidationResponse validateWithProducts(List<WebRequest> webRequests) throws ExecutionException, InterruptedException {
        List<CompletableFuture<ValidationResponse>> futures = prepareAndMakeRemoteCalls(webRequests);

        return aggregateValidationResults(futures);
    }

    private ValidationResponse aggregateValidationResults(List<CompletableFuture<ValidationResponse>> futures) throws InterruptedException, java.util.concurrent.ExecutionException {
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .join();

        boolean allValid = true;
        String error = null;
        for (CompletableFuture<ValidationResponse> response : futures) {
            ValidationResponse validationResponse = response.get();
            allValid = allValid & validationResponse.isValid();
            if (error == null) {
                error = validationResponse.getError();
            }
        }

        return ValidationResponse.builder()
                .isValid(allValid)
                .error(error)
                .build();
    }

    private List<CompletableFuture<ValidationResponse>> prepareAndMakeRemoteCalls(List<WebRequest> webRequests) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-CorrelationID", MDC.get("X-CorrelationID"));
        List<CompletableFuture<ValidationResponse>> futures = new ArrayList<>();
        for (WebRequest eachRequest : webRequests) {
            eachRequest.setHeaders(headers);
            futures.add(asyncValidationService.performAsyncValidation(eachRequest));
        }
        return futures;
    }
}
