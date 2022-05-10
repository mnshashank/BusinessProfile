package com.intuit.businessprofile.base.constant;

import java.util.Map;

import com.intuit.businessprofile.util.EnvironmentUtils;

public class BusinessProfileConstants {

    private BusinessProfileConstants() {
        throw new IllegalStateException("Should not instantiate constants class.");
    }

    public static final String PRODUCT_RELATIVE_URL_ENV_KEY = "PRODUCT_RELATIVE_URL";
    public static final Map<String, String> ALL_PRODUCTS = EnvironmentUtils.getAllProducts(PRODUCT_RELATIVE_URL_ENV_KEY);
}
