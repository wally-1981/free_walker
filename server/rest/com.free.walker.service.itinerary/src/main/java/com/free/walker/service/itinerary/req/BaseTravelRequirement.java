package com.free.walker.service.itinerary.req;

import java.util.UUID;

import javax.json.JsonValue;

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

    public ValueType getValueType() {
        return JsonValue.ValueType.NULL;
    }
}
