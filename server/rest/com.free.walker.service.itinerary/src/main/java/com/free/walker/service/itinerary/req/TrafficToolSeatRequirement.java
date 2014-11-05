package com.free.walker.service.itinerary.req;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.traffic.TrafficToolSeatClass;

public class TrafficToolSeatRequirement extends BaseTravelRequirement implements TravelRequirement {
    private TrafficToolSeatClass trafficToolSeatClass;

    public TrafficToolSeatRequirement() {
        super();
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
        resBuilder.add(Constants.JSONKeys.UUID, requirementId.toString());
        resBuilder.add(Constants.JSONKeys.TYPE, Constants.JSONKeys.REQUIREMENT);
        resBuilder.add(Constants.JSONKeys.TRAFFIC_TOOL_SEAT_CLASS, trafficToolSeatClass.enumValue());

        return resBuilder.build();
    }
}
