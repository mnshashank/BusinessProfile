package com.intuit.businessprofile.base.pojo;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebRequest {

    private HttpMethod httpMethod;

    private String url;

    private HttpHeaders headers;

    private String requestBody;
}
