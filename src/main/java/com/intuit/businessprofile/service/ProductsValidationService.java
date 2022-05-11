package com.intuit.businessprofile.service;

import java.util.List;

import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.intuit.businessprofile.base.pojo.WebRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductsValidationService {

    private final WebclientService webclientService;

    public void validateWithProducts(List<WebRequest> webRequests) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-CorrelationID", MDC.get("X-CorrelationID"));
        for (WebRequest eachRequest : webRequests) {
            eachRequest.setHeaders(headers);
            webclientService.executeCall(eachRequest);
        }
    }
}
