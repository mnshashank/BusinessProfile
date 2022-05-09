package com.intuit.businessprofile.service;

import java.util.Optional;
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
import com.intuit.businessprofile.base.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessProfileService {

    private final JedisTemplate jedisTemplate;

    private final ProfileRepository profileRepo;

    private final ObjectMapper mapper;

    public String getBusinessProfile(UUID profileId) {
        // check if the business profile information is in redis cache with profileId as the key.
        Optional<String> profileOptional = jedisTemplate.getIfExists(profileId.toString());
        if (profileOptional.isPresent()) {
            return profileOptional.get();
        }

        // get the profile information from the DB
        // TODO: have app level runtime exception classes and use it here.
        ProfileEntity profile = profileRepo.findById(profileId)
                .orElseThrow(RuntimeException::new);

        // create profile json to be stored and sent using the db fetched data
        ObjectNode profileNode = mapper.createObjectNode();
        profileNode.put("profileId", profileId.toString());
        profileNode.put("companyName", profile.getCompanyName());
        profileNode.put("companyLegalName", profile.getCompanyLegalName());
        profileNode.put("email", profile.getEmail());
        profileNode.put("website", profile.getWebsite());

        ArrayNode businessAddresses = mapper.createArrayNode();
        ArrayNode legalAddresses = mapper.createArrayNode();
        for (AddressEntity address : profile.getAddresses()) {
            ObjectNode addressNode = mapper.createObjectNode();
            addressNode.put("addressId", address.getId()
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

        ArrayNode subscriptions = mapper.createArrayNode();
        for (ProductSubscriptionEntity subscription : profile.getProductSubscriptions()) {
            ObjectNode subscriptionNode = mapper.createObjectNode();

            subscriptionNode.put("productId", subscription.getProductSubscriptionCompositeKey()
                    .getProductId());
            subscriptionNode.put("productName", subscription.getProductName());

            subscriptions.add(subscriptionNode);
        }

        ObjectNode taxIdentifiersNode = mapper.createObjectNode();
        TaxIdentifierEntity taxIdentifierEntity = profile.getTaxIdentifier();
        taxIdentifiersNode.put("taxIdentifierId", taxIdentifierEntity.getId()
                .toString());
        taxIdentifiersNode.put("pan", taxIdentifierEntity.getPan());
        taxIdentifiersNode.put("ein", taxIdentifierEntity.getEin());

        profileNode.put("businessAddress", businessAddresses);
        profileNode.put("legalAddress", legalAddresses);
        profileNode.put("taxIdentifiers", taxIdentifiersNode);
        profileNode.put("productSubscriptions", subscriptions);

        String profileData = profileNode.toString();

        // populate the cache with profile data
        jedisTemplate.set(profileId.toString(), profileData, 0);

        // return the profile data
        return profileData;
    }

}
