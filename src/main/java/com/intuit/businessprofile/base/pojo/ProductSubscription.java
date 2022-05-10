package com.intuit.businessprofile.base.pojo;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class ProductSubscription {

    @NotEmpty
    @Size(max = 10)
    private String productId;

    @NotEmpty
    @Size(max = 255)
    private String productName;
}
