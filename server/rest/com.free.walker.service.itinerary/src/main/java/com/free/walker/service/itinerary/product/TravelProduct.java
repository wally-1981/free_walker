package com.free.walker.service.itinerary.product;

import java.util.List;

public interface TravelProduct {
    public double getCost();

    public List<TravelProductDecorator> getTravelProductDecorators();
}
