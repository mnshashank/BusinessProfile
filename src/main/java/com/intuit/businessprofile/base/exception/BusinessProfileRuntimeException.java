package com.intuit.businessprofile.base.exception;

public class BusinessProfileRuntimeException extends RuntimeException {

    public BusinessProfileRuntimeException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public BusinessProfileRuntimeException(String message) {
        super(message);
    }
}
