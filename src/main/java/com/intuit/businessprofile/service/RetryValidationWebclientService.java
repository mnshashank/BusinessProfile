package com.intuit.businessprofile.service;

import static com.intuit.businessprofile.base.constant.BusinessProfileConstants.PRODUCTS_BASE_URL;
import static com.intuit.businessprofile.base.constant.BusinessProfileConstants.PRODUCTS_VALIDATION_URL;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.intuit.businessprofile.base.pojo.ValidationResponse;
import com.intuit.businessprofile.base.pojo.WebRequest;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RetryValidationWebclientService {

    private final RestTemplate restTemplate;

    @Retry(name = "profileValidation")
    public CompletableFuture<ValidationResponse> executeCall(WebRequest webRequest) {
        try {
            String url = PRODUCTS_BASE_URL + webRequest.getUrl() + PRODUCTS_VALIDATION_URL;
            log.info("Performing a network call to url: {}", url);
            ResponseEntity<ValidationResponse> responseEntity = restTemplate.exchange(RequestEntity.method(webRequest.getHttpMethod(), url)
                    .headers(webRequest.getHeaders())
                    .body(webRequest.getRequestBody()), ValidationResponse.class);

            ValidationResponse response = responseEntity.getBody();
            if (response != null) {
                response.setHttpStatus(responseEntity.getStatusCode());
            }

            return CompletableFuture.completedFuture(response);
        } catch (HttpStatusCodeException ex) {
            // throw ex;
            return CompletableFuture.completedFuture(getErrorResponse(ex));
        } catch (RestClientException restClientException) {
            return CompletableFuture.completedFuture(getErrorResponse(restClientException));
        }
    }

    private static ValidationResponse getErrorResponse(RestClientException exception) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (exception instanceof HttpStatusCodeException) {
            status = ((HttpStatusCodeException) exception).getStatusCode();
        }

        return ValidationResponse.builder()
                .isValid(false)
                .error(exception.getMessage())
                .httpStatus(status)
                .build();
    }
}
