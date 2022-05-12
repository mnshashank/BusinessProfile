package com.intuit.businessprofile.base.pojo;

import java.util.UUID;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.intuit.businessprofile.base.constant.AddressType;

import lombok.Data;

@Data
public class Address {

    private UUID id;
    
    @NotEmpty
    @Size(max = 255)
    private String line1;

    @Size(max = 255)
    private String line2;

    @NotEmpty
    @Size(max = 32)
    private String city;

    @NotEmpty
    @Size(max = 32)
    private String state;

    @NotEmpty
    @Size(max = 16)
    private String zip;

    @NotEmpty
    @Size(max = 32)
    private String country;

    private AddressType addressType;
}
