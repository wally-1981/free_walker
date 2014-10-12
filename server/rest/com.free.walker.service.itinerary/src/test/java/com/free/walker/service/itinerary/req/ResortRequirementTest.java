package com.free.walker.service.itinerary.req;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.TravelTimeRange;
import com.free.walker.service.itinerary.basic.Resort;
import com.free.walker.service.itinerary.resort.ResortStar;

public class ResortRequirementTest {
    @Test
    public void testToJSON4TimeRange() throws JSONException {
        TravelRequirement resortRequirement = new ResortRequirement(TravelTimeRange.RANGE_12_18);
        JSONObject jo = resortRequirement.toJSON();
        assertEquals(12, jo.getInt(Constants.JSONKeys.TIME_RANGE_START));
        assertEquals(18 - 12, jo.getInt(Constants.JSONKeys.TIME_RANGE_OFFSET));

        assertEquals(false, resortRequirement.isItinerary());
    }

    @Test
    public void testToJSON4TimeRangeAndStar() throws JSONException {
        TravelRequirement resortRequirement = new ResortRequirement(TravelTimeRange.RANGE_06_12, ResortStar.STD_4A);
        JSONObject jo = resortRequirement.toJSON();
        assertEquals(6, jo.getInt(Constants.JSONKeys.TIME_RANGE_START));
        assertEquals(12 - 6, jo.getInt(Constants.JSONKeys.TIME_RANGE_OFFSET));
        assertEquals(ResortStar.STD_4A.enumValue(), jo.get(Constants.JSONKeys.STAR));

        assertEquals(false, resortRequirement.isItinerary());
    }

    @Test
    public void testToJSON4TimeRangeAndResort() throws JSONException {
        TravelRequirement resortRequirement = new ResortRequirement(TravelTimeRange.RANGE_06_12, new Resort());
        JSONObject jo = resortRequirement.toJSON();
        assertEquals(6, jo.getInt(Constants.JSONKeys.TIME_RANGE_START));
        assertEquals(12 - 6, jo.getInt(Constants.JSONKeys.TIME_RANGE_OFFSET));
        assertNotNull(jo.get(Constants.JSONKeys.RESORT));

        assertEquals(false, resortRequirement.isItinerary());
    }
}
