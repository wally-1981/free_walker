package com.free.walker.service.itinerary.req;

import static org.junit.Assert.assertEquals;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.traffic.TrafficToolSeatClass;

public class TrafficToolSeatRequirementTest {
    @Test
    public void testToJSON4ToolType() throws JSONException {
        TravelRequirement trafficRequirement = new TrafficToolSeatRequirement(TrafficToolSeatClass.CLASS_2ND);
        JSONObject jo = trafficRequirement.toJSON();
        assertEquals(TrafficToolSeatClass.CLASS_2ND.enumValue(), jo.get(Constants.JSONKeys.TRAFFIC_TOOL_SEAT_CLASS));

        assertEquals(false, trafficRequirement.isItinerary());
    }
}
