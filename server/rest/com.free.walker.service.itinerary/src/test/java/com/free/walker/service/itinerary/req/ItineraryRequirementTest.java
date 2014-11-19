package com.free.walker.service.itinerary.req;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
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
import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.TravelLocation;
import com.free.walker.service.itinerary.infra.PlatformInitializer;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.ibm.icu.util.Calendar;

public class ItineraryRequirementTest {
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
        JsonObject jo = itineraryRequirement.toJSON();
        assertEquals(Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY, jo.getString(Introspection.JSONKeys.TYPE));
        assertNotNull(jo.get(Introspection.JSONKeys.DESTINATION));
        assertNotNull(jo.get(Introspection.JSONKeys.DEPARTURE));

        assertEquals(true, itineraryRequirement.isItinerary());
    }

    @Test
    public void testToJSON4DateTimeSelection() throws JsonException {
        TravelLocation destinationLocation = new TravelLocation(Constants.TAIBEI);
        TravelLocation departureLocation = new TravelLocation(Constants.WUHAN);

        List<Calendar> departureDateTimeSelections = new ArrayList<Calendar>();
        Calendar departureDateTime1 = Calendar.getInstance();
        departureDateTime1.add(Calendar.DAY_OF_YEAR, 10);
        Calendar departureDateTime2 = Calendar.getInstance();
        departureDateTime2.add(Calendar.DAY_OF_YEAR, 11);
        departureDateTimeSelections.add(departureDateTime1);
        departureDateTimeSelections.add(departureDateTime2);

        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destinationLocation, departureLocation,
            departureDateTimeSelections);
        JsonObject jo = itineraryRequirement.toJSON();
        assertEquals(Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY, jo.getString(Introspection.JSONKeys.TYPE));
        assertNotNull(jo.get(Introspection.JSONKeys.DESTINATION));
        assertNotNull(jo.get(Introspection.JSONKeys.DEPARTURE));
        assertTrue(jo.get(Introspection.JSONKeys.DATETIME_SELECTIONS) instanceof JsonArray);
        
        JsonArray selections = (JsonArray) jo.get(Introspection.JSONKeys.DATETIME_SELECTIONS);
        assertEquals(2, selections.size());
        assertEquals(departureDateTime1.getTimeInMillis(), selections.getJsonNumber(0).longValue());
        assertEquals(departureDateTime2.getTimeInMillis(), selections.getJsonNumber(1).longValue());

        assertEquals(true, itineraryRequirement.isItinerary());
    }

    @Test
    public void testFromJSON() throws JsonException {
        JsonObjectBuilder itinerary = Json.createObjectBuilder();
        UUID uuid = UUID.randomUUID();
        itinerary.add(Introspection.JSONKeys.UUID, uuid.toString());
        itinerary.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY);

        thrown.expect(JsonException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
            Introspection.JSONKeys.DEPARTURE, null));
        new ItineraryRequirement().fromJSON(itinerary.build());
    }
}
