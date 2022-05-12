package com.intuit.businessprofile.base.pojo;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class Profile {

    @NotEmpty
    @Size(max = 255)
    private String companyName;

    @NotEmpty
    @Size(max = 255)
    private String companyLegalName;

    @NotEmpty
    @Size(max = 128)
    @Pattern(regexp = "^(.+)@(.+)\\.(.+)$")
    private String email;

    @NotEmpty
    @Size(max = 255)
    private String website;

    @NotEmpty
    private List<@Valid Address> businessAddresses;

    @NotEmpty
    private List<@Valid Address> legalAddresses;

    @NotNull
    @Valid
    private TaxIdentifiers taxIdentifiers;

    private List<@Valid ProductSubscription> productSubscriptions;
}
