package com.free.walker.service.itinerary.product;

import java.util.LinkedList;
import java.util.List;

import com.free.walker.service.itinerary.req.TravelProposal;

public class NoneTravelProduct implements TravelProduct {
    private TravelProposal travelProposal;

    public NoneTravelProduct(TravelProposal travelProposal) {
        this.travelProposal = travelProposal;
    }

    public double getCost() {
        return 0;
    }

    public List<TravelProductDecorator> getTravelProductDecorators() {
        return new LinkedList<TravelProductDecorator>();
    }

    public TravelProposal getTravelProposal() {
        return travelProposal;
    }
}
