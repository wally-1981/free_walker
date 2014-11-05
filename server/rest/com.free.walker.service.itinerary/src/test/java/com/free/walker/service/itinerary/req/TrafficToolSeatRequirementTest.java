package com.free.walker.service.itinerary.req;

import static org.junit.Assert.assertEquals;

import javax.json.JsonException;
import javax.json.JsonObject;

import org.junit.Test;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.traffic.TrafficToolSeatClass;

public class TrafficToolSeatRequirementTest {
    @Test
    public void testToJSON4ToolType() throws JsonException {
        TravelRequirement trafficRequirement = new TrafficToolSeatRequirement(TrafficToolSeatClass.CLASS_2ND);
        JsonObject jo = trafficRequirement.toJSON();
        assertEquals(Constants.JSONKeys.REQUIREMENT, jo.getString(Constants.JSONKeys.TYPE));
        assertEquals(TrafficToolSeatClass.CLASS_2ND.enumValue(), jo.getInt(Constants.JSONKeys.TRAFFIC_TOOL_SEAT_CLASS));

        assertEquals(false, trafficRequirement.isItinerary());
    }
}
