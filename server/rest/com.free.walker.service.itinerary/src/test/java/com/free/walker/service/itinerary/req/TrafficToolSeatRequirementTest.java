package com.free.walker.service.itinerary.req;

import static org.junit.Assert.assertEquals;

import javax.json.JsonException;
import javax.json.JsonObject;

import org.junit.Test;

import com.free.walker.service.itinerary.basic.Introspection;

public class TrafficToolSeatRequirementTest {
    @Test
    public void testToJSON4ToolType() throws JsonException {
        TravelRequirement trafficRequirement = new TrafficToolSeatRequirement(Introspection.JSONValues.SEAT_CLASS_2ND);
        JsonObject jo = trafficRequirement.toJSON();
        assertEquals(Introspection.JSONValues.REQUIREMENT, jo.getString(Introspection.JSONKeys.TYPE));
        assertEquals(Introspection.JSONValues.SEAT_CLASS_2ND.enumValue(),
            jo.getInt(Introspection.JSONKeys.TRAFFIC_TOOL_SEAT_CLASS));

        assertEquals(false, trafficRequirement.isItinerary());
    }
}
