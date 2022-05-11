package com.intuit.businessprofile.base.pojo;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidationResponse {

    private boolean isValid;

    private String error;

    private HttpStatus httpStatus;
}
