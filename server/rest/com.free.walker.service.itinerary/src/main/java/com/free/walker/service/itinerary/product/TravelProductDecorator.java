package com.free.walker.service.itinerary.product;

public abstract class TravelProductDecorator implements TravelProduct {
    protected final TravelProduct travelProduct;

    public TravelProductDecorator(TravelProduct travelProduct) {
        this.travelProduct = travelProduct;
    }

    public double getCost() {
        return travelProduct.getCost();
    }
}
