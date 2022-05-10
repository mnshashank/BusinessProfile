package com.intuit.businessprofile.base.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.intuit.businessprofile.base.pojo.ProductSubscription;
import com.intuit.businessprofile.base.pojo.Profile;

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

    public static List<ProductSubscriptionEntity> fromProfileAndProfileEntity(Profile profile, ProfileEntity profileEntity) {
        List<ProductSubscriptionEntity> productSubscriptions = new ArrayList<>();
        for (ProductSubscription subscription : profile.getProductSubscriptions()) {
            ProductSubscriptionEntity productSubscriptionEntity = new ProductSubscriptionEntity();
            
            productSubscriptionEntity.setProductSubscriptionCompositeKey(ProductSubscriptionCompositeKey.fromSubscriptionAndProfileEntity(subscription, profileEntity));
            productSubscriptionEntity.setProductName(subscription.getProductName());

            productSubscriptions.add(productSubscriptionEntity);
        }

        return productSubscriptions;
    }

}
