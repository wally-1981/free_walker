package com.free.walker.service.itinerary.req;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.junit.Test;

import com.free.walker.service.itinerary.primitive.Introspection;

public class TrafficToolSeatRequirementTest {
    @Test
    public void testToJSON4ToolType() throws JsonException {
        TravelRequirement trafficRequirement = new TrafficToolSeatRequirement(
            Introspection.JSONValues.TRAFFIC_TOOL_SEAT_CLASS_2ND);
        JsonObject jo = trafficRequirement.toJSON();
        assertEquals(Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT, jo.getString(Introspection.JSONKeys.TYPE));
        assertEquals(Introspection.JSONValues.TRAFFIC_TOOL_SEAT_CLASS_2ND.enumValue(),
            jo.getInt(Introspection.JSONKeys.TRAFFIC_TOOL_SEAT_CLASS));

        assertEquals(false, trafficRequirement.isItinerary());
    }

    @Test
    public void testFromJSON() throws JsonException {
        JsonObjectBuilder requirement = Json.createObjectBuilder();
        UUID uuid = UUID.randomUUID();
        requirement.add(Introspection.JSONKeys.UUID, uuid.toString());
        requirement.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT);
        requirement.add(Introspection.JSONKeys.SUB_TYPE, TrafficToolSeatRequirement.SUB_TYPE);
        requirement.add(Introspection.JSONKeys.TRAFFIC_TOOL_SEAT_CLASS,
            Introspection.JSONValues.TRAFFIC_TOOL_SEAT_CLASS_2ND.enumValue());
        TravelRequirement trafficToolSeatClassRequirement = new TrafficToolSeatRequirement().fromJSON(requirement
            .build());
        assertNotNull(trafficToolSeatClassRequirement);
        assertEquals(uuid.toString(), trafficToolSeatClassRequirement.getUUID().toString());
    }
}
