package com.intuit.businessprofile.base.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.intuit.businessprofile.base.pojo.ProductSubscription;

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

    public static ProductSubscriptionCompositeKey fromSubscriptionAndProfileEntity(ProductSubscription subscription, ProfileEntity profileEntity) {
        ProductSubscriptionCompositeKey compositeKey = new ProductSubscriptionCompositeKey();

        compositeKey.setProductId(subscription.getProductId());
        compositeKey.setProfile(profileEntity);

        return compositeKey;
    }

}
