package com.intuit.businessprofile.base.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.intuit.businessprofile.base.entity.ProfileEntity;

@Repository
public interface ProfileRepository extends CrudRepository<ProfileEntity, UUID> {

}
