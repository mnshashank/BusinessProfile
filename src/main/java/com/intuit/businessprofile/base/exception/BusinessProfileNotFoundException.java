package com.intuit.businessprofile.base.exception;

public class BusinessProfileNotFoundException extends RuntimeException {

    public BusinessProfileNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public BusinessProfileNotFoundException(String message) {
        super(message);
    }
}
