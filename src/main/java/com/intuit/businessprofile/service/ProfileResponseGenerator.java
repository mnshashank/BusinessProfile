package com.intuit.businessprofile.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intuit.businessprofile.base.constant.AddressType;
import com.intuit.businessprofile.base.entity.AddressEntity;
import com.intuit.businessprofile.base.entity.ProductSubscriptionEntity;
import com.intuit.businessprofile.base.entity.ProfileEntity;
import com.intuit.businessprofile.base.entity.TaxIdentifierEntity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProfileResponseGenerator {

    private final ObjectMapper mapper;

    public String getProfileResponse(ProfileEntity profile, UUID profileId) {
        ObjectNode profileNode = mapper.createObjectNode();
        profileNode.put("profileId", profileId.toString());
        profileNode.put("companyName", profile.getCompanyName());
        profileNode.put("companyLegalName", profile.getCompanyLegalName());
        profileNode.put("email", profile.getEmail());
        profileNode.put("website", profile.getWebsite());

        populateAddresses(profile, profileNode);

        populateTaxIdentifiers(profile, profileNode);

        populateSubscriptioins(profile, profileNode);

        return profileNode.toString();
    }

    private void populateTaxIdentifiers(ProfileEntity profile, ObjectNode profileNode) {
        ObjectNode taxIdentifiersNode = mapper.createObjectNode();
        TaxIdentifierEntity taxIdentifierEntity = profile.getTaxIdentifier();
        taxIdentifiersNode.put("id", taxIdentifierEntity.getId()
                .toString());
        taxIdentifiersNode.put("pan", taxIdentifierEntity.getPan());
        taxIdentifiersNode.put("ein", taxIdentifierEntity.getEin());

        profileNode.set("taxIdentifiers", taxIdentifiersNode);
    }

    private void populateSubscriptioins(ProfileEntity profile, ObjectNode profileNode) {
        ArrayNode subscriptions = mapper.createArrayNode();
        for (ProductSubscriptionEntity subscription : profile.getProductSubscriptions()) {
            ObjectNode subscriptionNode = mapper.createObjectNode();

            subscriptionNode.put("productId", subscription.getProductSubscriptionCompositeKey()
                    .getProductId());
            subscriptionNode.put("productName", subscription.getProductName());

            subscriptions.add(subscriptionNode);
        }

        profileNode.set("productSubscriptions", subscriptions);
    }

    private void populateAddresses(ProfileEntity profile, ObjectNode profileNode) {
        ArrayNode businessAddresses = mapper.createArrayNode();
        ArrayNode legalAddresses = mapper.createArrayNode();
        for (AddressEntity address : profile.getAddresses()) {
            ObjectNode addressNode = mapper.createObjectNode();
            addressNode.put("id", address.getId()
                    .toString());
            addressNode.put("line1", address.getLine1());
            addressNode.put("line2", address.getLine2());
            addressNode.put("city", address.getCity());
            addressNode.put("state", address.getState());
            addressNode.put("zip", address.getZip());
            addressNode.put("country", address.getCountry());

            if (address.getAddressType() == AddressType.BUSINESS) {
                businessAddresses.add(addressNode);
            } else {
                legalAddresses.add(addressNode);
            }
        }

        profileNode.set("businessAddresses", businessAddresses);
        profileNode.set("legalAddresses", legalAddresses);
    }
}
