package com.intuit.businessprofile.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.intuit.businessprofile.base.pojo.ValidationResponse;
import com.intuit.businessprofile.base.pojo.WebRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductsValidationService {

    private final WebclientService webclientService;

    private final ThreadPoolTaskExecutor taskExecutor;

    public ValidationResponse validateWithProducts(List<WebRequest> webRequests) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-CorrelationID", MDC.get("X-CorrelationID"));
            List<CompletableFuture<ValidationResponse>> futures = new ArrayList<>();
            for (WebRequest eachRequest : webRequests) {
                eachRequest.setHeaders(headers);

                futures.add(webclientService.executeCall(eachRequest));
            }

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
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
