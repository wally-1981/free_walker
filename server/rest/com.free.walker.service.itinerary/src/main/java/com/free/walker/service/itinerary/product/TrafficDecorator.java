package com.free.walker.service.itinerary.product;

import java.util.List;

import com.free.walker.service.itinerary.traffic.TrafficTool;

public class TrafficDecorator extends TravelProductDecorator {
    private TrafficTool trafficTool;

    public TrafficDecorator(TravelProduct travelProduct, TrafficTool trafficTool) {
        super(travelProduct);

        this.trafficTool = trafficTool;
    }

    public double getCost() {
        return super.getCost() + trafficTool.getTotalFee();
    }

    public List<TravelProductDecorator> getTravelProductDecorators() {
        travelProduct.getTravelProductDecorators().add(this);
        return travelProduct.getTravelProductDecorators();
    }
}
