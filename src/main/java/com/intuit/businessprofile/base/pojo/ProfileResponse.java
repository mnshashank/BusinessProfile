package com.intuit.businessprofile.base.pojo;

import java.util.UUID;

import com.intuit.businessprofile.base.constant.JobStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {

    private UUID profileId;

    private JobStatus status;
}
