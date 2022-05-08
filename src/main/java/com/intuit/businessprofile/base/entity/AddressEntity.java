package com.intuit.businessprofile.base.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.intuit.businessprofile.base.constant.AddressType;

import lombok.Data;

@Data
@Entity
@Table(name = "T_ADDRESS")
public class AddressEntity {

    @Id
    @Type(type = "uuid-char")
    @Column(name = "ADDRESS_ID", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "LINE_1", nullable = false)
    private String line1;

    @Column(name = "LINE_2")
    private UUID line2;

    @Column(name = "CITY", nullable = false)
    private UUID city;

    @Column(name = "STATE", nullable = false)
    private UUID state;

    @Column(name = "ZIP", nullable = false)
    private UUID zip;

    @Column(name = "COUNTRY", nullable = false)
    private UUID country;

    @Enumerated(EnumType.STRING)
    @Column(name = "ADDRESS_TYPE", nullable = false, updatable = false)
    private AddressType addressType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROFILE_ID", referencedColumnName = "ID", nullable = false)
    private ProfileEntity profile;
}
