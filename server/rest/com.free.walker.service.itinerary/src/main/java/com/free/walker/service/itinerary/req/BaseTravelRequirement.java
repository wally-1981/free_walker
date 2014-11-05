package com.free.walker.service.itinerary.req;

import java.util.UUID;

import javax.json.JsonValue;

import com.free.walker.service.itinerary.basic.TravelLocation;

public abstract class BaseTravelRequirement implements TravelRequirement {
    protected UUID requirementId;

    public BaseTravelRequirement() {
        this.requirementId = UUID.randomUUID();
    }

    public boolean isItinerary() {
        return false;
    }

    public boolean isProposal() {
        return false;
    }

    public UUID getUUID() {
        return requirementId;
    }

    public TravelLocation getDestination() {
        return null;
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.OBJECT;
    }
}
