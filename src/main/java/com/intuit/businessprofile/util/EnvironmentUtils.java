package com.intuit.businessprofile.util;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnvironmentUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private EnvironmentUtils() {
        throw new IllegalStateException("Should not instantiate utils class.");
    }

    public static Map<String, String> getAllProducts(String envKey) {
        try {
            return objectMapper.readValue(System.getenv(envKey), new TypeReference<Map<String, String>>() {});
        } catch (JsonProcessingException jsonProcessingException) {
            log.error("Json processing exception while getting all products from environment", jsonProcessingException);
            // TODO: change to custom exception
            throw new RuntimeException();
        }
    }
}
