package com.free.walker.service.itinerary.product;

import java.util.UUID;

import javax.json.JsonValue;

import com.free.walker.service.itinerary.Renewable;
import com.free.walker.service.itinerary.primitive.ProductStatus;

public abstract class TravelProductItem implements TravelProduct, Renewable {
    protected TravelProduct travelProduct;
    protected UUID uuid;

    public TravelProductItem() {
        this.uuid = UUID.randomUUID();
    }

    public TravelProductItem(TravelProduct travelProduct) {
        if (travelProduct == null) {
            throw new NullPointerException();
        }

        this.uuid = UUID.randomUUID();
        this.travelProduct = travelProduct;
        this.travelProduct.getTravelProductItems().add(this);
    }

    public double getCost() {
        return travelProduct.getCost();
    }

    public TravelProduct getCore() {
        return travelProduct.getCore();
    }

    public ProductStatus getStatus() {
        return travelProduct.getStatus();
    }

    public boolean isTriv() {
        return false;
    }

    public UUID getProductUUID() {
        return travelProduct.getProductUUID();
    }

    public UUID getProposalUUID() {
        return travelProduct.getProposalUUID();
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.NULL;
    }

    public Object adapt(String attributeName, Class<?> attributeType) {
        return travelProduct.adapt(attributeName, attributeType);
    }

    public abstract String getType();

    public abstract UUID getUUID();
}
