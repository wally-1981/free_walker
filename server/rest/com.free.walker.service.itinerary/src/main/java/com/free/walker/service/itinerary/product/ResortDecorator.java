package com.free.walker.service.itinerary.product;

import java.util.List;

public class ResortDecorator extends TravelProductDecorator {

    public ResortDecorator(TravelProduct travelProduct) {
        super(travelProduct);
    }

    public List<TravelProductDecorator> getTravelProductDecorators() {
        travelProduct.getTravelProductDecorators().add(this);
        return travelProduct.getTravelProductDecorators();
    }

}
