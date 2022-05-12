package com.intuit.businessprofile.base.exception;

public class BusinessProfileNotAcceptableException extends RuntimeException {

    public BusinessProfileNotAcceptableException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public BusinessProfileNotAcceptableException(String message) {
        super(message);
    }
}
