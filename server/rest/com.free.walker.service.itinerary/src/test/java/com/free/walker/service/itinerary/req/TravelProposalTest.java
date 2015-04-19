package com.free.walker.service.itinerary.req;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
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
        assertEquals(Introspection.DefaultAccounts.DEFAULT_MASTER_ACCOUNT_UUID,
            jo.getString(Introspection.JSONKeys.AUTHOR));
        assertTrue(jo.get(Introspection.JSONKeys.REQUIREMENTS) instanceof JsonArray);

        JsonArray requirements = (JsonArray) jo.get(Introspection.JSONKeys.REQUIREMENTS);
        assertEquals(1, requirements.size());
        assertNotNull(requirements.get(0));
    }

    @Test
    public void testToJSONWithTags() throws JsonException {
        TravelLocation destinationLocation = new TravelLocation(Constants.TAIBEI);
        TravelLocation departureLocation = new TravelLocation(Constants.WUHAN);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destinationLocation, departureLocation);
        List<String> travelTags = new ArrayList<String>();
        travelTags.add("摄影 ");
        travelTags.add(" 蜜月");
        travelTags.add(" 摄影");
        TravelProposal travelProposal = new TravelProposal("测试提议", itineraryRequirement, travelTags);
        JsonObject jo = travelProposal.toJSON();
        assertEquals(Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL, jo.getString(Introspection.JSONKeys.TYPE));
        assertEquals(Introspection.DefaultAccounts.DEFAULT_MASTER_ACCOUNT_UUID,
            jo.getString(Introspection.JSONKeys.AUTHOR));
        assertEquals("测试提议", jo.getString(Introspection.JSONKeys.TITLE));

        assertTrue(jo.get(Introspection.JSONKeys.REQUIREMENTS) instanceof JsonArray);
        JsonArray requirements = (JsonArray) jo.get(Introspection.JSONKeys.REQUIREMENTS);
        assertEquals(1, requirements.size());
        assertNotNull(requirements.get(0));

        assertTrue(jo.get(Introspection.JSONKeys.TAGS) instanceof JsonArray);
        JsonArray tags = (JsonArray) jo.get(Introspection.JSONKeys.TAGS);
        assertEquals(2, tags.size());
        assertNotNull(tags.getString(0));
        assertNotNull(tags.getString(1));
        assertTrue(tags.getString(0).equals("摄影") || tags.getString(0).equals("蜜月"));
        assertTrue(tags.getString(1).equals("摄影") || tags.getString(1).equals("蜜月"));
    }

    @Test
    public void testFromJSON() throws JsonException {
        JsonObjectBuilder proposal = Json.createObjectBuilder();
        UUID uuid = UUID.randomUUID();
        proposal.add(Introspection.JSONKeys.UUID, uuid.toString());
        proposal.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL);
        proposal.add(Introspection.JSONKeys.AUTHOR, Constants.DEFAULT_USER_ACCOUNT.getUuid());
        proposal.add(Introspection.JSONKeys.TITLE, "测试提议");
        JsonArray requirements = Json.createArrayBuilder().build();
        proposal.add(Introspection.JSONKeys.REQUIREMENTS, requirements);

        TravelProposal travelProposal = new TravelProposal().fromJSON(proposal.build());
        assertNotNull(travelProposal);
        assertEquals(uuid, travelProposal.getUUID());
        assertNotNull(travelProposal.getTravelRequirements());
        assertEquals(0, travelProposal.getTravelRequirements().size());
    }

    @Test
    public void testFromJSONWithTags() throws JsonException {
        JsonObjectBuilder proposal = Json.createObjectBuilder();
        UUID uuid = UUID.randomUUID();
        proposal.add(Introspection.JSONKeys.UUID, uuid.toString());
        proposal.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL);
        proposal.add(Introspection.JSONKeys.AUTHOR, Constants.DEFAULT_USER_ACCOUNT.getUuid());
        JsonArray requirements = Json.createArrayBuilder().build();
        proposal.add(Introspection.JSONKeys.TITLE, "测试提议");
        proposal.add(Introspection.JSONKeys.REQUIREMENTS, requirements);
        JsonArrayBuilder tagsBuilder = Json.createArrayBuilder();
        tagsBuilder.add("户外");
        proposal.add(Introspection.JSONKeys.TAGS, tagsBuilder.build());

        TravelProposal travelProposal = new TravelProposal().fromJSON(proposal.build());
        assertNotNull(travelProposal);
        assertEquals(uuid, travelProposal.getUUID());
        assertNotNull(travelProposal.getTravelRequirements());
        assertEquals(0, travelProposal.getTravelRequirements().size());
        assertNotNull(travelProposal.getTags());
        assertEquals(1, travelProposal.getTags().size());
        assertEquals("户外", travelProposal.getTags().iterator().next());
    }
}
