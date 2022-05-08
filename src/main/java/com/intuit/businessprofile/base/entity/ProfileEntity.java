package com.intuit.businessprofile.base.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.Data;

@Data
@Entity
@Table(name = "T_PROFILE")
public class ProfileEntity {

    @Id
    @Type(type = "uuid-char")
    @Column(name = "PROFILE_ID", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "COMPANY_NAME", nullable = false)
    private String companyName;

    @Column(name = "COMPANY_LEGAL_NAME", nullable = false)
    private String companyLegalName;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "WEBSITE", nullable = false)
    private String website;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "profile")
    private TaxIdentifierEntity taxIdentifier;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "profile")
    private List<AddressEntity> addresses = new ArrayList<>();

}
