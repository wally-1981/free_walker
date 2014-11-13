package com.free.walker.service.itinerary.req;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;

import org.junit.Test;

import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.primitive.TravelTimeRange;

public class TrafficRequirementTest {
    @Test
    public void testToJSON4ToolType() throws JsonException {
        TravelRequirement trafficRequirement = new TrafficRequirement(Introspection.JSONValues.TRAFFIC_TOOL_TYPE_FLIGHT);
        JsonObject jo = trafficRequirement.toJSON();
        assertEquals(Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT, jo.getString(Introspection.JSONKeys.TYPE));
        assertEquals(Introspection.JSONValues.TRAFFIC_TOOL_TYPE_FLIGHT.enumValue(),
            jo.getInt(Introspection.JSONKeys.TRAFFIC_TOOL_TYPE));

        assertEquals(false, trafficRequirement.isItinerary());
    }

    @Test
    public void testToJSON4ToolTypeAndTimeRange() throws JsonException {
        TrafficRequirement trafficRequirement = new TrafficRequirement(Introspection.JSONValues.TRAFFIC_TOOL_TYPE_TRAIN,
            Introspection.JSONValues.TIME_RANGE_06_12);
        JsonObject jo = trafficRequirement.toJSON();
        assertEquals(Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT, jo.getString(Introspection.JSONKeys.TYPE));
        assertEquals(Introspection.JSONValues.TRAFFIC_TOOL_TYPE_TRAIN.enumValue(),
            jo.getInt(Introspection.JSONKeys.TRAFFIC_TOOL_TYPE));
        assertTrue(jo.get(Introspection.JSONKeys.DATETIME_RANGE_SELECTIONS) instanceof JsonArray);

        JsonArray selections = ((JsonArray) jo.get(Introspection.JSONKeys.DATETIME_RANGE_SELECTIONS));
        assertEquals(1, selections.size());
        assertTrue(selections.get(0) instanceof JsonObject);

        JsonObject selection = (JsonObject) selections.get(0);
        assertEquals(6, selection.getInt(Introspection.JSONKeys.TIME_RANGE_START));
        assertEquals(12 - 6, selection.getInt(Introspection.JSONKeys.TIME_RANGE_OFFSET));

        assertEquals(false, trafficRequirement.isItinerary());
    }

    @Test
    public void testToJSON4ToolTypeAndTimeRanges() throws JsonException {
        List<TravelTimeRange> timeRangeSelections = new ArrayList<TravelTimeRange>();
        timeRangeSelections.add(Introspection.JSONValues.TIME_RANGE_06_12);
        timeRangeSelections.add(Introspection.JSONValues.TIME_RANGE_12_18);
        TrafficRequirement trafficRequirement = new TrafficRequirement(Introspection.JSONValues.TRAFFIC_TOOL_TYPE_TRAIN,
            timeRangeSelections);
        JsonObject jo = trafficRequirement.toJSON();
        assertEquals(Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT, jo.getString(Introspection.JSONKeys.TYPE));
        assertEquals(Introspection.JSONValues.TRAFFIC_TOOL_TYPE_TRAIN.enumValue(),
            jo.getInt(Introspection.JSONKeys.TRAFFIC_TOOL_TYPE));
        assertTrue(jo.get(Introspection.JSONKeys.DATETIME_RANGE_SELECTIONS) instanceof JsonArray);

        JsonArray selections = ((JsonArray) jo.get(Introspection.JSONKeys.DATETIME_RANGE_SELECTIONS));
        assertEquals(2, selections.size());
        assertTrue(selections.get(0) instanceof JsonObject);

        JsonObject selection1 = (JsonObject) selections.get(0);
        assertEquals(6, selection1.getInt(Introspection.JSONKeys.TIME_RANGE_START));
        assertEquals(12 - 6, selection1.getInt(Introspection.JSONKeys.TIME_RANGE_OFFSET));

        JsonObject selection2 = (JsonObject) selections.get(1);
        assertEquals(12, selection2.getInt(Introspection.JSONKeys.TIME_RANGE_START));
        assertEquals(18 - 12, selection2.getInt(Introspection.JSONKeys.TIME_RANGE_OFFSET));

        assertEquals(false, trafficRequirement.isItinerary());
    }
}
