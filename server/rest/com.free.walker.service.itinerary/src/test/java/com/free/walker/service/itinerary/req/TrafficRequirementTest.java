package com.free.walker.service.itinerary.req;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;

import org.junit.Test;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.TravelTimeRange;
import com.free.walker.service.itinerary.traffic.TrafficToolType;

public class TrafficRequirementTest {
    @Test
    public void testToJSON4ToolType() throws JsonException {
        TravelRequirement trafficRequirement = new TrafficRequirement(TrafficToolType.FLIGHT);
        JsonObject jo = trafficRequirement.toJSON();
        assertEquals(Constants.JSONKeys.REQUIREMENT, jo.getString(Constants.JSONKeys.TYPE));
        assertEquals(TrafficToolType.FLIGHT.enumValue(), jo.getInt(Constants.JSONKeys.TRAFFIC_TOOL_TYPE));

        assertEquals(false, trafficRequirement.isItinerary());
    }

    @Test
    public void testToJSON4ToolTypeAndTimeRange() throws JsonException {
        TrafficRequirement trafficRequirement = new TrafficRequirement(TrafficToolType.TRAIN,
            TravelTimeRange.RANGE_06_12);
        JsonObject jo = trafficRequirement.toJSON();
        assertEquals(Constants.JSONKeys.REQUIREMENT, jo.getString(Constants.JSONKeys.TYPE));
        assertEquals(TrafficToolType.TRAIN.enumValue(), jo.getInt(Constants.JSONKeys.TRAFFIC_TOOL_TYPE));
        assertTrue(jo.get(Constants.JSONKeys.DATETIME_RANGE_SELECTIONS) instanceof JsonArray);

        JsonArray selections = ((JsonArray) jo.get(Constants.JSONKeys.DATETIME_RANGE_SELECTIONS));
        assertEquals(1, selections.size());
        assertTrue(selections.get(0) instanceof JsonObject);

        JsonObject selection = (JsonObject) selections.get(0);
        assertEquals(6, selection.getInt(Constants.JSONKeys.TIME_RANGE_START));
        assertEquals(12 - 6, selection.getInt(Constants.JSONKeys.TIME_RANGE_OFFSET));

        assertEquals(false, trafficRequirement.isItinerary());
    }

    @Test
    public void testToJSON4ToolTypeAndTimeRanges() throws JsonException {
        List<TravelTimeRange> timeRangeSelections = new ArrayList<TravelTimeRange>();
        timeRangeSelections.add(TravelTimeRange.RANGE_06_12);
        timeRangeSelections.add(TravelTimeRange.RANGE_12_18);
        TrafficRequirement trafficRequirement = new TrafficRequirement(TrafficToolType.TRAIN, timeRangeSelections);
        JsonObject jo = trafficRequirement.toJSON();
        assertEquals(Constants.JSONKeys.REQUIREMENT, jo.getString(Constants.JSONKeys.TYPE));
        assertEquals(TrafficToolType.TRAIN.enumValue(), jo.getInt(Constants.JSONKeys.TRAFFIC_TOOL_TYPE));
        assertTrue(jo.get(Constants.JSONKeys.DATETIME_RANGE_SELECTIONS) instanceof JsonArray);

        JsonArray selections = ((JsonArray) jo.get(Constants.JSONKeys.DATETIME_RANGE_SELECTIONS));
        assertEquals(2, selections.size());
        assertTrue(selections.get(0) instanceof JsonObject);

        JsonObject selection1 = (JsonObject) selections.get(0);
        assertEquals(6, selection1.getInt(Constants.JSONKeys.TIME_RANGE_START));
        assertEquals(12 - 6, selection1.getInt(Constants.JSONKeys.TIME_RANGE_OFFSET));

        JsonObject selection2 = (JsonObject) selections.get(1);
        assertEquals(12, selection2.getInt(Constants.JSONKeys.TIME_RANGE_START));
        assertEquals(18 - 12, selection2.getInt(Constants.JSONKeys.TIME_RANGE_OFFSET));

        assertEquals(false, trafficRequirement.isItinerary());
    }
}
