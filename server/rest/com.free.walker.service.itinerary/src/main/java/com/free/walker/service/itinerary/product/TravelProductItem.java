package com.free.walker.service.itinerary.product;

import java.util.UUID;

import javax.json.JsonValue;

public abstract class TravelProductItem implements TravelProduct {
    protected final TravelProduct travelProduct;
    protected UUID uuid;

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

    public boolean isTriv() {
        return false;
    }

    public UUID getUUID() {
        return travelProduct.getUUID();
    }

    public String getType() {
        return null;
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.NULL;
    }
}
