package com.free.walker.service.itinerary.req;

import java.util.UUID;

import com.free.walker.service.itinerary.Serializable;
import com.free.walker.service.itinerary.basic.TravelLocation;

public interface TravelRequirement extends Serializable {
    public boolean isItinerary();

    public boolean isProposal();

    public UUID getUUID();

    public TravelLocation getDestination();
}
