package com.free.walker.service.itinerary.req;

import java.util.UUID;

import com.free.walker.service.itinerary.Renewable;
import com.free.walker.service.itinerary.Serializable;

public interface TravelRequirement extends Serializable, Renewable {
    public boolean isItinerary();

    public boolean isProposal();

    public UUID getUUID();
}
