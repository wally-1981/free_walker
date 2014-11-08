package com.free.walker.service.itinerary.req;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.free.walker.service.itinerary.basic.Introspection;
import com.free.walker.service.itinerary.basic.Resort;
import com.free.walker.service.itinerary.basic.ResortStar;
import com.free.walker.service.itinerary.basic.TravelTimeRange;

public class ResortRequirement extends BaseTravelRequirement implements TravelRequirement {
    private TravelTimeRange arrivalTimeRange;
    private Resort resort;
    private ResortStar resortStar;

    public ResortRequirement() {
        super();
    }

    public ResortRequirement(TravelTimeRange arrivalTimeRange) {
        super();

        if (arrivalTimeRange == null) {
            throw new NullPointerException();
        }

        this.arrivalTimeRange = arrivalTimeRange;
    }

    public ResortRequirement(TravelTimeRange arrivalTimeRange, ResortStar resortStar) {
        this(arrivalTimeRange);

        if (resortStar == null) {
            throw new NullPointerException();
        }

        this.resortStar = resortStar;
    }

    public ResortRequirement(TravelTimeRange arrivalTimeRange, Resort resort) {
        this(arrivalTimeRange);

        if (resort == null) {
            throw new NullPointerException();
        }

        this.resort = resort;
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Introspection.JSONKeys.UUID, getUUID().toString());
        resBuilder.add(Introspection.JSONKeys.TYPE, Introspection.JSONKeys.REQUIREMENT);
        resBuilder.add(Introspection.JSONKeys.SUB_TYPE, getSubType());
        resBuilder.add(Introspection.JSONKeys.TIME_RANGE_START, arrivalTimeRange.getStart());
        resBuilder.add(Introspection.JSONKeys.TIME_RANGE_OFFSET, arrivalTimeRange.getOffset());

        if (resortStar != null) {
            resBuilder.add(Introspection.JSONKeys.STAR, resortStar.enumValue());
        }

        if (resort != null) {
            resBuilder.add(Introspection.JSONKeys.RESORT, resort.toJSON());
        }

        return resBuilder.build();
    }
}
