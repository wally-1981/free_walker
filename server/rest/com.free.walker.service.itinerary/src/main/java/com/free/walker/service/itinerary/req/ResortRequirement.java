package com.free.walker.service.itinerary.req;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.commons.lang3.StringUtils;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.Resort;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.primitive.ResortStar;
import com.free.walker.service.itinerary.primitive.TravelTimeRange;
import com.free.walker.service.itinerary.util.UuidUtil;

public class ResortRequirement extends BaseTravelRequirement implements TravelRequirement {
    public static final String SUB_TYPE;

    static {
        String[] names = StringUtils.splitByCharacterTypeCamelCase(ResortRequirement.class.getSimpleName());
        SUB_TYPE = StringUtils.join(names, '_', 0, names.length - 1).toLowerCase();
    }

    private TravelTimeRange arrivalTimeRange;
    private Resort resort;
    private ResortStar star;

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

    public ResortRequirement(TravelTimeRange arrivalTimeRange, ResortStar star) {
        this(arrivalTimeRange);

        if (star == null) {
            throw new NullPointerException();
        }

        this.star = star;
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
        resBuilder.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT);
        resBuilder.add(Introspection.JSONKeys.SUB_TYPE, SUB_TYPE);
        resBuilder.add(Introspection.JSONKeys.TIME_RANGE_START, arrivalTimeRange.realValue());
        resBuilder.add(Introspection.JSONKeys.TIME_RANGE_OFFSET, arrivalTimeRange.imaginaryValue());

        if (star != null) {
            resBuilder.add(Introspection.JSONKeys.STAR, star.enumValue());
        }

        if (resort != null) {
            resBuilder.add(Introspection.JSONKeys.RESORT, resort.toJSON());
        }

        return resBuilder.build();
    }

    public ResortRequirement fromJSON(JsonObject jsObject) throws JsonException {
        String requirementId = jsObject.getString(Introspection.JSONKeys.UUID, null);

        if (requirementId != null) {
            this.requirementId = UuidUtil.fromUuidStr(requirementId);
        } else {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.UUID, requirementId));
        }

        return newFromJSON(jsObject);
    }

    public ResortRequirement newFromJSON(JsonObject jsObject) throws JsonException {
        String type = jsObject.getString(Introspection.JSONKeys.TYPE, null);
        if (type != null && !Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT.equals(type)) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.TYPE, type));
        }

        String subType = jsObject.getString(Introspection.JSONKeys.SUB_TYPE, null);
        if (subType != null && !SUB_TYPE.equals(subType)) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.SUB_TYPE, subType));
        }

        int star = jsObject.getInt(Introspection.JSONKeys.STAR, 0);
        if (star > 0) {
            try {
                this.star = Introspection.JsonValueHelper.getResortStar(star);
            } catch (InvalidTravelReqirementException e) {
                throw new JsonException(e.getMessage(), e);
            }
        }

        int start = jsObject.getInt(Introspection.JSONKeys.TIME_RANGE_START, -1);
        int offset = jsObject.getInt(Introspection.JSONKeys.TIME_RANGE_OFFSET, -1);
        if (start >= 0 && offset >= 0) {
            try {
                this.arrivalTimeRange = Introspection.JsonValueHelper.getTravelTimeRange(start, offset);
            } catch (InvalidTravelReqirementException e) {
                throw new JsonException(e.getMessage(), e);
            }
        }

        JsonObject resortObj = jsObject.getJsonObject(Introspection.JSONKeys.RESORT);
        if (resortObj != null) {
            this.resort = (Resort) new Resort().fromJSON(resortObj);
        }

        return this;
    }
}
