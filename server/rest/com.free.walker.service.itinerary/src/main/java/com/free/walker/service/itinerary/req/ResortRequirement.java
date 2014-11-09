package com.free.walker.service.itinerary.req;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.commons.lang3.StringUtils;

import com.free.walker.service.itinerary.basic.Resort;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.primitive.ResortStar;
import com.free.walker.service.itinerary.primitive.TravelTimeRange;

public class ResortRequirement extends BaseTravelRequirement implements TravelRequirement {
    public static final String SUB_TYPE;

    static {
        String[] names = StringUtils.splitByCharacterTypeCamelCase(ResortRequirement.class.getSimpleName());
        SUB_TYPE = StringUtils.join(names, '_', 0, names.length - 1);
    }

    private TravelTimeRange arrivalTimeRange;
    private Resort resort;
    private ResortStar resortStar;

    public ResortRequirement() {
        ;
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
        resBuilder.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.REQUIREMENT);
        resBuilder.add(Introspection.JSONKeys.SUB_TYPE, getSubType());
        resBuilder.add(Introspection.JSONKeys.TIME_RANGE_START, arrivalTimeRange.realValue());
        resBuilder.add(Introspection.JSONKeys.TIME_RANGE_OFFSET, arrivalTimeRange.imaginaryValue());

        if (resortStar != null) {
            resBuilder.add(Introspection.JSONKeys.STAR, resortStar.enumValue());
        }

        if (resort != null) {
            resBuilder.add(Introspection.JSONKeys.RESORT, resort.toJSON());
        }

        return resBuilder.build();
    }

    public Object fromJSON(JsonObject jsObject) throws JsonException {
        return null;
    }
}
