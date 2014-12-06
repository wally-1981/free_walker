package com.free.walker.service.itinerary.product;

import java.util.List;
import java.util.UUID;

import com.free.walker.service.itinerary.Serializable;

public interface TravelProduct extends Serializable {
    public UUID getUUID();

    public TravelProduct getCore();

    public double getCost();

    public List<TravelProductItem> getTravelProductItems();
}
