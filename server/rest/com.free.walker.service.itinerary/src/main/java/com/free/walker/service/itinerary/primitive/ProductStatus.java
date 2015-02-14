package com.free.walker.service.itinerary.primitive;

import com.free.walker.service.itinerary.Enumable;

public class ProductStatus implements Enumable {
    public static final ProductStatus DRAFT_PRODUCT = new ProductStatus(-20);
    public static final ProductStatus PRIVATE_PRODUCT = new ProductStatus(-10);
    public static final ProductStatus PUBLIC_STATUS = new ProductStatus(10);
    public static final ProductStatus ACTIVE_PRODUCT = new ProductStatus(20);
    public static final ProductStatus ING_PRODUCT = new ProductStatus(30);
    public static final ProductStatus ARCHIVED_PRODUCT = new ProductStatus(40);

    public static ProductStatus valueOf(int ordinal) {
        if (ordinal == DRAFT_PRODUCT.enumValue) {
            return DRAFT_PRODUCT;
        } else if (ordinal == PRIVATE_PRODUCT.enumValue) {
            return PRIVATE_PRODUCT;
        } else if (ordinal == PUBLIC_STATUS.enumValue) {
            return PUBLIC_STATUS;
        } else if (ordinal == ACTIVE_PRODUCT.enumValue) {
            return ACTIVE_PRODUCT;
        } else if (ordinal == ING_PRODUCT.enumValue) {
            return ING_PRODUCT;
        } else if (ordinal == ARCHIVED_PRODUCT.enumValue) {
            return ARCHIVED_PRODUCT;
        } else {
            return null;
        }
    }

    private int enumValue = 0;

    private ProductStatus(int enumValue) {
        this.enumValue = enumValue;
    }

    public int enumValue() {
        return enumValue;
    }
}
