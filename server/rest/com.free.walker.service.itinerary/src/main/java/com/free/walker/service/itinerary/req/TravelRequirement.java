package com.free.walker.service.itinerary.req;

import java.util.UUID;

import com.free.walker.service.itinerary.SerializableJSON;
import com.free.walker.service.itinerary.basic.TravelLocation;

public interface TravelRequirement extends SerializableJSON {
    public boolean isItinerary();

    public boolean isProposal();

    public UUID getUUID();

    public TravelLocation getDestination();
}
