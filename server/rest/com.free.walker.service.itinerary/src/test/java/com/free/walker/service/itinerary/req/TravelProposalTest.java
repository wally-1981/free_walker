package com.free.walker.service.itinerary.req;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;

import org.junit.Test;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.basic.City;
import com.free.walker.service.itinerary.basic.TravelLocation;

public class TravelProposalTest {
    @Test
    public void testToJSON() throws JsonException {
        TravelLocation destinationLocation = new TravelLocation(City.BEIJING);
        TravelLocation departureLocation = new TravelLocation(City.LONDON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destinationLocation, departureLocation);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);
        JsonObject jo = travelProposal.toJSON();
        assertEquals(Constants.JSONKeys.PROPOSAL, jo.getString(Constants.JSONKeys.TYPE));
        assertTrue(jo.get(Constants.JSONKeys.REQUIREMENTS) instanceof JsonArray);

        JsonArray requirements = (JsonArray) jo.get(Constants.JSONKeys.REQUIREMENTS);
        assertEquals(1, requirements.size());
        assertNotNull(requirements.get(0));
    }
}
