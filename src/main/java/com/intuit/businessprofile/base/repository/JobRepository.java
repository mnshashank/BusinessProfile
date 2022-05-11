package com.intuit.businessprofile.base.repository;

import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.intuit.businessprofile.base.constant.JobStatus;
import com.intuit.businessprofile.base.entity.JobEntity;

@Repository
public interface JobRepository extends CrudRepository<JobEntity, UUID> {

    @Transactional
    @Modifying
    @Query("update JobEntity j set j.status = :status where j.profileId = :profileId AND j.status = 'ACCEPTED'")
    void updateJobStatus(JobStatus status, String profileId);

    @Transactional
    @Modifying
    @Query("delete from JobEntity j where j.profileId = :profileId")
    void deleteAllJobsForProfile(String profileId);
}
