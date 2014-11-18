package com.free.walker.service.itinerary.req;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;

import org.junit.Before;
import org.junit.Test;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.basic.TravelLocation;
import com.free.walker.service.itinerary.infra.PlatformInitializer;
import com.free.walker.service.itinerary.primitive.Introspection;

public class TravelProposalTest {
    @Before
    public void before() {
        PlatformInitializer.init();
    }

    @Test
    public void testToJSON() throws JsonException {
        TravelLocation destinationLocation = new TravelLocation(Constants.TAIBEI);
        TravelLocation departureLocation = new TravelLocation(Constants.WUHAN);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destinationLocation, departureLocation);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);
        JsonObject jo = travelProposal.toJSON();
        assertEquals(Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL, jo.getString(Introspection.JSONKeys.TYPE));
        assertTrue(jo.get(Introspection.JSONKeys.REQUIREMENTS) instanceof JsonArray);

        JsonArray requirements = (JsonArray) jo.get(Introspection.JSONKeys.REQUIREMENTS);
        assertEquals(1, requirements.size());
        assertNotNull(requirements.get(0));
    }

    @Test
    public void testFromJSON() throws JsonException {
        ;
    }
}
