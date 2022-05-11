package com.intuit.businessprofile.base.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessProfileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleItemNotFoundException(BusinessProfileNotFoundException businessProfileNotFoundException, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(businessProfileNotFoundException.getMessage());
    }

    @ExceptionHandler(BusinessProfileBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleBadRequestException(BusinessProfileBadRequestException businessProfileBadRequestException, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(businessProfileBadRequestException.getMessage());
    }

    @ExceptionHandler(BusinessProfileNotAcceptableException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ResponseEntity<Object> handleNotAcceptableException(BusinessProfileNotAcceptableException businessProfileNotAcceptableException, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(businessProfileNotAcceptableException.getMessage());
    }

    @ExceptionHandler(BusinessProfileRuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleRuntimeException(BusinessProfileRuntimeException businessProfileRuntimeException, WebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(businessProfileRuntimeException.getMessage());
    }

}
