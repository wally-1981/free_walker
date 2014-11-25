package com.free.walker.service.itinerary.req;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.basic.TravelLocation;
import com.free.walker.service.itinerary.infra.PlatformInitializer;
import com.free.walker.service.itinerary.primitive.Introspection;

public class TravelProposalTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

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
        JsonObjectBuilder proposal = Json.createObjectBuilder();
        UUID uuid = UUID.randomUUID();
        proposal.add(Introspection.JSONKeys.UUID, uuid.toString());
        proposal.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL);
        JsonArray requirements = Json.createArrayBuilder().build();
        proposal.add(Introspection.JSONKeys.REQUIREMENTS, requirements);

        TravelProposal travelProposal = new TravelProposal().fromJSON(proposal.build());
        assertNotNull(travelProposal);
        assertEquals(uuid, travelProposal.getUUID());
        assertNotNull(travelProposal.getTravelRequirements());
        assertEquals(0, travelProposal.getTravelRequirements().size());
        
    }
}
