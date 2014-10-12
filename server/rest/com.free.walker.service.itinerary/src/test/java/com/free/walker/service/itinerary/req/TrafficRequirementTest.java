package com.free.walker.service.itinerary.req;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.TravelTimeRange;
import com.free.walker.service.itinerary.traffic.TrafficToolType;

public class TrafficRequirementTest {
    @Test
    public void testToJSON4ToolType() throws JSONException {
        TravelRequirement trafficRequirement = new TrafficRequirement(TrafficToolType.FLIGHT);
        JSONObject jo = trafficRequirement.toJSON();
        assertEquals(TrafficToolType.FLIGHT.enumValue(), jo.get(Constants.JSONKeys.TRAFFIC_TOOL_TYPE));

        assertEquals(false, trafficRequirement.isItinerary());
    }

    @Test
    public void testToJSON4ToolTypeAndTimeRange() throws JSONException {
        TrafficRequirement trafficRequirement = new TrafficRequirement(TrafficToolType.TRAIN,
            TravelTimeRange.RANGE_06_12);
        JSONObject jo = trafficRequirement.toJSON();
        assertEquals(TrafficToolType.TRAIN.enumValue(), jo.get(Constants.JSONKeys.TRAFFIC_TOOL_TYPE));
        assertTrue(jo.get(Constants.JSONKeys.DATETIME_RANGE_SELECTIONS) instanceof JSONArray);

        JSONArray selections = ((JSONArray) jo.get(Constants.JSONKeys.DATETIME_RANGE_SELECTIONS));
        assertEquals(1, selections.length());
        assertTrue(selections.get(0) instanceof JSONObject);

        JSONObject selection = (JSONObject) selections.get(0);
        assertEquals(6, selection.getInt(Constants.JSONKeys.TIME_RANGE_START));
        assertEquals(12 - 6, selection.getInt(Constants.JSONKeys.TIME_RANGE_OFFSET));

        assertEquals(false, trafficRequirement.isItinerary());
    }

    @Test
    public void testToJSON4ToolTypeAndTimeRanges() throws JSONException {
        List<TravelTimeRange> timeRangeSelections = new ArrayList<TravelTimeRange>();
        timeRangeSelections.add(TravelTimeRange.RANGE_06_12);
        timeRangeSelections.add(TravelTimeRange.RANGE_12_18);
        TrafficRequirement trafficRequirement = new TrafficRequirement(TrafficToolType.TRAIN, timeRangeSelections);
        JSONObject jo = trafficRequirement.toJSON();
        assertEquals(TrafficToolType.TRAIN.enumValue(), jo.get(Constants.JSONKeys.TRAFFIC_TOOL_TYPE));
        assertTrue(jo.get(Constants.JSONKeys.DATETIME_RANGE_SELECTIONS) instanceof JSONArray);

        JSONArray selections = ((JSONArray) jo.get(Constants.JSONKeys.DATETIME_RANGE_SELECTIONS));
        assertEquals(2, selections.length());
        assertTrue(selections.get(0) instanceof JSONObject);

        JSONObject selection1 = (JSONObject) selections.get(0);
        assertEquals(6, selection1.getInt(Constants.JSONKeys.TIME_RANGE_START));
        assertEquals(12 - 6, selection1.getInt(Constants.JSONKeys.TIME_RANGE_OFFSET));

        JSONObject selection2 = (JSONObject) selections.get(1);
        assertEquals(12, selection2.getInt(Constants.JSONKeys.TIME_RANGE_START));
        assertEquals(18 - 12, selection2.getInt(Constants.JSONKeys.TIME_RANGE_OFFSET));

        assertEquals(false, trafficRequirement.isItinerary());
    }
}
