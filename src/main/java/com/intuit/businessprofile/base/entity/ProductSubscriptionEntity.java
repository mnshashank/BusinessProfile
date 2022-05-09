package com.intuit.businessprofile.base.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "T_PRODUCT_SUBSCRIPTION")
public class ProductSubscriptionEntity implements Serializable {

    @EmbeddedId
    private ProductSubscriptionCompositeKey productSubscriptionCompositeKey;

    @Column(name = "PRODUCT_NAME", nullable = false, updatable = false)
    private String productName;

}
