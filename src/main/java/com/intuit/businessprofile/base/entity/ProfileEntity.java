package com.intuit.businessprofile.base.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.intuit.businessprofile.base.pojo.Profile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "T_PROFILE")
public class ProfileEntity {

    @Id
    @Type(type = "uuid-char")
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "COMPANY_NAME", nullable = false)
    private String companyName;

    @Column(name = "COMPANY_LEGAL_NAME", nullable = false)
    private String companyLegalName;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "WEBSITE", nullable = false)
    private String website;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "profile")
    private TaxIdentifierEntity taxIdentifier;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "profile")
    private List<AddressEntity> addresses = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productSubscriptionCompositeKey.profile")
    private List<ProductSubscriptionEntity> productSubscriptions = new ArrayList<>();

    public static ProfileEntity fromProfile(Profile profile) {
        return fromProfileAndProfileId(profile, UUID.randomUUID());
    }

    public static ProfileEntity fromProfileAndProfileId(Profile profile, UUID profileId) {
        ProfileEntity profileEntity = new ProfileEntity();

        profileEntity.setId(profileId);
        profileEntity.setCompanyName(profile.getCompanyName());
        profileEntity.setCompanyLegalName(profile.getCompanyLegalName());
        profileEntity.setEmail(profile.getEmail());
        profileEntity.setWebsite(profile.getWebsite());

        profileEntity.setAddresses(AddressEntity.fromProfileAndProfileEntity(profile, profileEntity));
        profileEntity.setProductSubscriptions(ProductSubscriptionEntity.fromProfileAndProfileEntity(profile, profileEntity));
        profileEntity.setTaxIdentifier(TaxIdentifierEntity.fromProfileAndProfileEntity(profile, profileEntity));

        return profileEntity;
    }

    public static void updateProfile(ProfileEntity profileEntity, Profile profile) {
        // update base members of profile
        profileEntity.setCompanyName(profile.getCompanyName());
        profileEntity.setCompanyLegalName(profile.getCompanyLegalName());
        profileEntity.setEmail(profile.getEmail());
        profileEntity.setWebsite(profile.getWebsite());

        // update addresses
        AddressEntity.updateAddress(profileEntity, profile);

        // update tax identifiers
        TaxIdentifierEntity.updateTaxIdentifier(profileEntity, profile);
    }
}
