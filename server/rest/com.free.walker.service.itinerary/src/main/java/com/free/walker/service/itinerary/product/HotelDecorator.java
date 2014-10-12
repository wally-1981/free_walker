package com.free.walker.service.itinerary.product;

import java.util.List;

public class HotelDecorator extends TravelProductDecorator {

    public HotelDecorator(TravelProduct travelProduct) {
        super(travelProduct);
    }

    public List<TravelProductDecorator> getTravelProductDecorators() {
        travelProduct.getTravelProductDecorators().add(this);
        return travelProduct.getTravelProductDecorators();
    }

}
