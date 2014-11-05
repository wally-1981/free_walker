package com.free.walker.service.itinerary.req;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;

import org.junit.Test;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.basic.City;
import com.free.walker.service.itinerary.basic.TravelLocation;
import com.ibm.icu.util.Calendar;

public class ItineraryRequirementTest {
    @Test
    public void testToJSON() throws JsonException {
        TravelLocation destinationLocation = new TravelLocation(City.BEIJING);
        TravelLocation departureLocation = new TravelLocation(City.LONDON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destinationLocation, departureLocation);
        JsonObject jo = itineraryRequirement.toJSON();
        assertEquals(Constants.JSONKeys.ITINERARY, jo.getString(Constants.JSONKeys.TYPE));
        assertNotNull(jo.get(Constants.JSONKeys.DESTINATION));
        assertNotNull(jo.get(Constants.JSONKeys.DEPARTURE));

        assertEquals(true, itineraryRequirement.isItinerary());
    }

    @Test
    public void testToJSON4DateTimeSelection() throws JsonException {
        TravelLocation destinationLocation = new TravelLocation(City.BEIJING);
        TravelLocation departureLocation = new TravelLocation(City.LONDON);

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
        assertEquals(Constants.JSONKeys.ITINERARY, jo.getString(Constants.JSONKeys.TYPE));
        assertNotNull(jo.get(Constants.JSONKeys.DESTINATION));
        assertNotNull(jo.get(Constants.JSONKeys.DEPARTURE));
        assertTrue(jo.get(Constants.JSONKeys.DATETIME_SELECTIONS) instanceof JsonArray);
        
        JsonArray selections = (JsonArray) jo.get(Constants.JSONKeys.DATETIME_SELECTIONS);
        assertEquals(2, selections.size());
        assertEquals(departureDateTime1.getTimeInMillis(), selections.getJsonNumber(0).longValue());
        assertEquals(departureDateTime2.getTimeInMillis(), selections.getJsonNumber(1).longValue());

        assertEquals(true, itineraryRequirement.isItinerary());
    }
}
