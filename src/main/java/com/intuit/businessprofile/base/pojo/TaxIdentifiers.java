package com.intuit.businessprofile.base.pojo;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class TaxIdentifiers {

    @NotEmpty
    @Size(max = 16)
    private String pan;

    @NotEmpty
    @Size(max = 16)
    private String ein;
}
