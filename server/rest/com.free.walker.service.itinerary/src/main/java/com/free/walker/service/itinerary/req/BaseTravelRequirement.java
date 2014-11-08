package com.free.walker.service.itinerary.req;

import java.util.UUID;

import javax.json.JsonValue;

import org.apache.commons.lang3.StringUtils;

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
        return JsonValue.ValueType.OBJECT;
    }

    protected String getSubType() {
        return StringUtils.splitByCharacterTypeCamelCase(this.getClass().getSimpleName())[0].toLowerCase();
    }
}
