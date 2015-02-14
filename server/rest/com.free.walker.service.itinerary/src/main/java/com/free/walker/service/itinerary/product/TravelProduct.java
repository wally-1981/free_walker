package com.free.walker.service.itinerary.product;

import java.util.List;
import java.util.UUID;

import com.free.walker.service.itinerary.Serializable;
import com.free.walker.service.itinerary.primitive.ProductStatus;

public interface TravelProduct extends Serializable {
    public UUID getProductUUID();

    public UUID getProposalUUID();

    public TravelProduct getCore();

    public ProductStatus getStatus();

    public double getCost();

    public List<TravelProductItem> getTravelProductItems();

    public Object adapt(String attributeName, Class<?> attributeType);
}
