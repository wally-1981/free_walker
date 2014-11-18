package com.free.walker.service.itinerary.req;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.commons.lang3.StringUtils;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.primitive.TrafficToolSeatClass;
import com.free.walker.service.itinerary.util.UuidUtil;

public class TrafficToolSeatRequirement extends BaseTravelRequirement implements TravelRequirement {
    public static final String SUB_TYPE;

    static {
        String[] names = StringUtils.splitByCharacterTypeCamelCase(TrafficToolSeatRequirement.class.getSimpleName());
        SUB_TYPE = StringUtils.join(names, '_', 0, names.length - 1).toLowerCase();
    }

    private TrafficToolSeatClass trafficToolSeatClass;

    public TrafficToolSeatRequirement() {
        ;
    }

    public TrafficToolSeatRequirement(TrafficToolSeatClass trafficToolSeatClass) {
        super();

        if (trafficToolSeatClass == null) {
            throw new NullPointerException();
        }

        this.trafficToolSeatClass = trafficToolSeatClass;
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Introspection.JSONKeys.UUID, getUUID().toString());
        resBuilder.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT);
        resBuilder.add(Introspection.JSONKeys.SUB_TYPE, SUB_TYPE);
        resBuilder.add(Introspection.JSONKeys.TRAFFIC_TOOL_SEAT_CLASS, trafficToolSeatClass.enumValue());

        return resBuilder.build();
    }

    public TrafficToolSeatRequirement fromJSON(JsonObject jsObject) throws JsonException {
        String requirementId = jsObject.getString(Introspection.JSONKeys.UUID);

        if (requirementId != null) {
            try {
                this.requirementId = UuidUtil.fromUuidStr(requirementId);
            } catch (InvalidTravelReqirementException e) {
                throw new JsonException(e.getMessage(), e);
            }            
        }

        return newFromJSON(jsObject);
    }

    public TrafficToolSeatRequirement newFromJSON(JsonObject jsObject) throws JsonException {
        String type = jsObject.getString(Introspection.JSONKeys.TYPE);
        if (type != null && !Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT.equals(type)) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.TYPE, type));
        }

        String subType = jsObject.getString(Introspection.JSONKeys.SUB_TYPE);
        if (subType != null && !SUB_TYPE.equals(subType)) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.SUB_TYPE, subType));
        }

        int trafficToolSeatClass = jsObject.getInt(Introspection.JSONKeys.TRAFFIC_TOOL_SEAT_CLASS, 0);
        if (trafficToolSeatClass > 0) {
            try {
                this.trafficToolSeatClass = Introspection.JsonValueHelper.getTrafficSeatClass(trafficToolSeatClass);
            } catch (InvalidTravelReqirementException e) {
                throw new JsonException(e.getMessage(), e);
            }
        } else {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.TRAFFIC_TOOL_SEAT_CLASS, trafficToolSeatClass));
        }

        return this;
    }
}
