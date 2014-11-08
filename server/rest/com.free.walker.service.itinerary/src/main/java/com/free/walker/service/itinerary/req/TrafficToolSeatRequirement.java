package com.free.walker.service.itinerary.req;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.commons.lang3.StringUtils;

import com.free.walker.service.itinerary.basic.Introspection;
import com.free.walker.service.itinerary.basic.TrafficToolSeatClass;

public class TrafficToolSeatRequirement extends BaseTravelRequirement implements TravelRequirement {
    public static final String SUB_TYPE;

    static {
        String[] names = StringUtils.splitByCharacterTypeCamelCase(TrafficToolSeatRequirement.class.getSimpleName());
        SUB_TYPE = StringUtils.join(names, '_', 0, names.length - 1);
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
        resBuilder.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.REQUIREMENT);
        resBuilder.add(Introspection.JSONKeys.SUB_TYPE, getSubType());
        resBuilder.add(Introspection.JSONKeys.TRAFFIC_TOOL_SEAT_CLASS, trafficToolSeatClass.enumValue());

        return resBuilder.build();
    }

    public Object fromJSON(JsonObject jsObject) throws JsonException {
        return null;
    }
}
