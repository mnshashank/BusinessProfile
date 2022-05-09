package com.intuit.businessprofile.base.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class ProductSubscriptionCompositeKey implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROFILE_ID", referencedColumnName = "ID", nullable = false)
    private ProfileEntity profile;

    @Column(name = "PRODUCT_ID", nullable = false, updatable = false)
    private String productId;

}
