package com.intuit.businessprofile.base.pojo;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.intuit.businessprofile.base.constant.JobStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(Include.NON_NULL)
public class ProfileResponse {

    private UUID profileId;

    private JobStatus status;

    private String error;

    private UUID correlationId;
}
