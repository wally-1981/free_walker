package com.free.walker.service.itinerary.req;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.TravelLocation;
import com.free.walker.service.itinerary.basic.City;

public class TravelProposalTest {
    @Test
    public void testToJSON() throws JSONException {
        TravelLocation destinationLocation = new TravelLocation(City.BEIJING);
        TravelLocation departureLocation = new TravelLocation(City.LONDON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destinationLocation, departureLocation);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);
        JSONObject jo = travelProposal.toJSON();
        assertTrue(jo.get(Constants.JSONKeys.PROPOSAL) instanceof JSONArray);

        JSONArray requirements = (JSONArray) jo.get(Constants.JSONKeys.PROPOSAL);
        assertEquals(1, requirements.length());
        assertNotNull(requirements.get(0));
    }
}
