package com.intuit.businessprofile.base.entity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
import com.intuit.businessprofile.base.exception.BusinessProfileRuntimeException;
import com.intuit.businessprofile.base.pojo.Address;
import com.intuit.businessprofile.base.pojo.Profile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "T_ADDRESS")
public class AddressEntity {

    @Id
    @Type(type = "uuid-char")
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "LINE_1", nullable = false)
    private String line1;

    @Column(name = "LINE_2")
    private String line2;

    @Column(name = "CITY", nullable = false)
    private String city;

    @Column(name = "STATE", nullable = false)
    private String state;

    @Column(name = "ZIP", nullable = false)
    private String zip;

    @Column(name = "COUNTRY", nullable = false)
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(name = "ADDRESS_TYPE", nullable = false, updatable = false)
    private AddressType addressType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROFILE_ID", referencedColumnName = "ID", nullable = false)
    private ProfileEntity profile;

    public static List<AddressEntity> fromProfileAndProfileEntity(Profile profile, ProfileEntity profileEntity) {
        List<AddressEntity> addressEntities = new ArrayList<>();

        for (Address businessAddress : profile.getBusinessAddresses()) {
            AddressEntity addressEntity = new AddressEntity();
            addressEntity.setId(UUID.randomUUID());
            addressEntity.setLine1(businessAddress.getLine1());
            addressEntity.setLine2(businessAddress.getLine2());
            addressEntity.setCity(businessAddress.getCity());
            addressEntity.setState(businessAddress.getState());
            addressEntity.setZip(businessAddress.getZip());
            addressEntity.setCountry(businessAddress.getCountry());
            addressEntity.setAddressType(AddressType.BUSINESS);
            addressEntity.setProfile(profileEntity);
            addressEntities.add(addressEntity);
        }

        for (Address legalAddress : profile.getLegalAddresses()) {
            AddressEntity addressEntity = new AddressEntity();
            addressEntity.setId(UUID.randomUUID());
            addressEntity.setLine1(legalAddress.getLine1());
            addressEntity.setLine2(legalAddress.getLine2());
            addressEntity.setCity(legalAddress.getCity());
            addressEntity.setState(legalAddress.getState());
            addressEntity.setZip(legalAddress.getZip());
            addressEntity.setCountry(legalAddress.getCountry());
            addressEntity.setAddressType(AddressType.LEGAL);
            addressEntity.setProfile(profileEntity);
            addressEntities.add(addressEntity);
        }

        return addressEntities;
    }

    public static void updateAddress(ProfileEntity profileEntity, Profile profile) {
        List<Address> addressList = new LinkedList<>();
        addressList.addAll(profile.getBusinessAddresses());
        addressList.addAll(profile.getLegalAddresses());

        for (AddressEntity addressEntity : profileEntity.getAddresses()) {
            Address address = addressList.stream()
                    .filter(addr -> addr.getId()
                            .equals(addressEntity.getId()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessProfileRuntimeException("Address not present in the database"));

            addressEntity.setLine1(address.getLine1());
            addressEntity.setLine2(address.getLine2());
            addressEntity.setCity(address.getCity());
            addressEntity.setState(address.getState());
            addressEntity.setZip(address.getZip());
            addressEntity.setCountry(address.getCountry());
        }
    }
}
