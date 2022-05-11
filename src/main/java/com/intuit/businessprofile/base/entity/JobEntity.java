package com.intuit.businessprofile.base.entity;

import java.util.Calendar;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import com.intuit.businessprofile.base.constant.JobStatus;

import lombok.Data;

@Data
@Entity
@Table(name = "T_JOB")
public class JobEntity {

    @Id
    @Type(type = "uuid-char")
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private Calendar createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_AT")
    private Calendar updatedAt;

    @Column(name = "CORRELATION_ID", nullable = false, updatable = false)
    private UUID correlationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private JobStatus status;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "PAYLOAD", nullable = false, updatable = false)
    private String payload;

    @Column(name = "PROFILE_ID", nullable = false, updatable = false)
    private String profileId;

    @Column(name = "ERROR")
    private String error;

    public static JobEntity getInstanceFromProfileId(UUID profileId, UUID jobId) {
        JobEntity jobEntity = new JobEntity();

        jobEntity.setId(jobId);
        jobEntity.setCorrelationId(UUID.randomUUID());
        jobEntity.setProfileId(profileId.toString());
        jobEntity.setStatus(JobStatus.ACCEPTED);

        return jobEntity;
    }
}
