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

import com.intuit.businessprofile.base.pojo.Profile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "T_TAX_IDENTIFIER")
public class TaxIdentifierEntity {

    @Id
    @Type(type = "uuid-char")
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "PAN", nullable = false)
    private String pan;

    @Column(name = "EIN", nullable = false)
    private String ein;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROFILE_ID", referencedColumnName = "ID", nullable = false)
    private ProfileEntity profile;

    public static TaxIdentifierEntity fromProfileAndProfileEntity(Profile profile, ProfileEntity profileEntity) {
        TaxIdentifierEntity taxIdentifierEntity = new TaxIdentifierEntity();

        taxIdentifierEntity.setId(UUID.randomUUID());
        taxIdentifierEntity.setPan(profile.getTaxIdentifiers()
                .getPan());
        taxIdentifierEntity.setEin(profile.getTaxIdentifiers()
                .getEin());
        taxIdentifierEntity.setProfile(profileEntity);

        return taxIdentifierEntity;
    }

    public static void updateTaxIdentifier(ProfileEntity profileEntity, Profile profile) {
        TaxIdentifierEntity taxIdentifierEntity = profileEntity.getTaxIdentifier();
        
        taxIdentifierEntity.setPan(profile.getTaxIdentifiers()
                .getPan());
        taxIdentifierEntity.setEin(profile.getTaxIdentifiers()
                .getEin());
    }
}
