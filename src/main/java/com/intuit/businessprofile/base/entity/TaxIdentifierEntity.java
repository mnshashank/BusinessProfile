package com.intuit.businessprofile.base.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.Data;

@Data
@Entity
@Table(name = "T_TAX_IDENTIFIER")
public class TaxIdentifierEntity {

    @Id
    @Type(type = "uuid-char")
    @Column(name = "TAX_IDENTIFIER_ID", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "PAN", nullable = false)
    private String pan;

    @Column(name = "EIN", nullable = false)
    private String ein;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PROFILE_ID", referencedColumnName = "ID", nullable = false)
    private ProfileEntity profile;
}
