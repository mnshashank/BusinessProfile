package com.intuit.businessprofile.base.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "T_PRODUCT_SUBSCRIPTION")
public class ProductSubscriptionEntity {

    @EmbeddedId
    private ProductSubscriptionCompositeKey productSubscriptionCompositeKey;

    @Column(name = "PRODUCT_NAME", nullable = false, updatable = false)
    private String productName;
}
