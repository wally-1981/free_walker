package com.free.walker.service.itinerary.req;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.TravelTimeRange;
import com.free.walker.service.itinerary.basic.Resort;
import com.free.walker.service.itinerary.resort.ResortStar;

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
        resBuilder.add(Constants.JSONKeys.UUID, requirementId.toString());
        resBuilder.add(Constants.JSONKeys.TYPE, Constants.JSONKeys.REQUIREMENT);
        resBuilder.add(Constants.JSONKeys.TIME_RANGE_START, arrivalTimeRange.getStart());
        resBuilder.add(Constants.JSONKeys.TIME_RANGE_OFFSET, arrivalTimeRange.getOffset());

        if (resortStar != null) {
            resBuilder.add(Constants.JSONKeys.STAR, resortStar.enumValue());
        }

        if (resort != null) {
            resBuilder.add(Constants.JSONKeys.RESORT, resort.toJSON());
        }

        return resBuilder.build();
    }
}
