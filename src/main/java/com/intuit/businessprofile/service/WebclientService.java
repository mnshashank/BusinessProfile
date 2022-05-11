package com.intuit.businessprofile.service;

import static com.intuit.businessprofile.base.constant.BusinessProfileConstants.PRODUCTS_BASE_URL;
import static com.intuit.businessprofile.base.constant.BusinessProfileConstants.PRODUCTS_VALIDATION_URL;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.intuit.businessprofile.base.pojo.ValidationResponse;
import com.intuit.businessprofile.base.pojo.WebRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebclientService {

    private final RestTemplate restTemplate;

    @Async
    public CompletableFuture<ValidationResponse> executeCall(WebRequest webRequest) {

        try {
            ResponseEntity<ValidationResponse> responseEntity = restTemplate.exchange(
                    RequestEntity.method(webRequest.getHttpMethod(), PRODUCTS_BASE_URL + webRequest.getUrl() + PRODUCTS_VALIDATION_URL)
                            .headers(webRequest.getHeaders())
                            .body(webRequest.getRequestBody()), ValidationResponse.class);

            ValidationResponse response = responseEntity.getBody();
            if (response != null) {
                response.setHttpStatus(responseEntity.getStatusCode());
            }

            return CompletableFuture.completedFuture(response);
        } catch (HttpStatusCodeException ex) {
            return CompletableFuture.completedFuture(getErrorResponse(ex));
        }
    }

    private static ValidationResponse getErrorResponse(HttpStatusCodeException exception) {
        return ValidationResponse.builder()
                .isValid(false)
                .error(exception.getMessage())
                .httpStatus(exception.getStatusCode())
                .build();
    }
}
