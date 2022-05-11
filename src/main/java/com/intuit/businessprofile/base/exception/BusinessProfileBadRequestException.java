package com.intuit.businessprofile.base.exception;

public class BusinessProfileBadRequestException extends RuntimeException {

    public BusinessProfileBadRequestException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public BusinessProfileBadRequestException(String message) {
        super(message);
    }
}
