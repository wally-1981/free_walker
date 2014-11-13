package com.free.walker.service.itinerary.util;

import static org.junit.Assert.assertNotNull;

import javax.json.JsonObject;

import org.junit.Test;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.basic.TravelLocation;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.req.ItineraryRequirement;

public class JsonObjectHelperTest {
    @Test
    public void testToRequirement() throws InvalidTravelReqirementException {
        TravelLocation destinationLocation = new TravelLocation(Constants.TAIBEI);
        TravelLocation departureLocation = new TravelLocation(Constants.WUHAN);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destinationLocation, departureLocation);
        JsonObject jo = itineraryRequirement.toJSON();
        assertNotNull(JsonObjectHelper.toRequirement(jo));
    }
}
