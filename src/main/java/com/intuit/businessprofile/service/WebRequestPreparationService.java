package com.intuit.businessprofile.service;

import static com.intuit.businessprofile.base.constant.BusinessProfileConstants.ALL_PRODUCTS;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.intuit.businessprofile.base.entity.ProductSubscriptionEntity;
import com.intuit.businessprofile.base.exception.BusinessProfileRuntimeException;
import com.intuit.businessprofile.base.pojo.ProductSubscription;
import com.intuit.businessprofile.base.pojo.Profile;
import com.intuit.businessprofile.base.pojo.WebRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebRequestPreparationService {

    private static final String ERROR_STRING = "Json processing error while creating validation web requests";

    public List<WebRequest> getCreateProfileValidationWebRequests(List<ProductSubscription> subscriptions, Profile profile) {
        List<WebRequest> webRequests = new ArrayList<>();
        try {
            for (ProductSubscription eachSubscription : subscriptions) {
                webRequests.add(buildWebRequest(profile, eachSubscription.getProductId()));
            }
        } catch (JsonProcessingException jsonProcessingException) {
            log.error(ERROR_STRING, jsonProcessingException);
            throw new BusinessProfileRuntimeException(ERROR_STRING, jsonProcessingException);
        }

        return webRequests;
    }

    public List<WebRequest> getUpdateProfileValidationWebRequests(List<ProductSubscriptionEntity> subscriptions, Profile profile) {
        List<WebRequest> webRequests = new ArrayList<>();
        try {
            for (ProductSubscriptionEntity eachSubscriptionEntity : subscriptions) {
                webRequests.add(buildWebRequest(profile, eachSubscriptionEntity.getProductSubscriptionCompositeKey()
                        .getProductId()));
            }
        } catch (JsonProcessingException jsonProcessingException) {
            log.error(ERROR_STRING, jsonProcessingException);
            throw new BusinessProfileRuntimeException(ERROR_STRING, jsonProcessingException);
        }

        return webRequests;
    }

    private WebRequest buildWebRequest(Profile profile, String productId) throws JsonProcessingException {
        if (ALL_PRODUCTS.containsKey(productId)) {
            return WebRequest.builder()
                    .url(ALL_PRODUCTS.get(productId))
                    .httpMethod(HttpMethod.PUT)
                    .requestBody(profile)
                    .build();
        } else {
            throw new BusinessProfileRuntimeException(String.format("Service env not configured properly, missing product %s", productId));
        }
    }
}
