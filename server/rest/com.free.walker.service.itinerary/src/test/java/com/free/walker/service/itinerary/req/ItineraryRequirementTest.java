package com.free.walker.service.itinerary.req;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.TravelLocation;
import com.free.walker.service.itinerary.basic.City;
import com.ibm.icu.util.Calendar;

public class ItineraryRequirementTest {
    @Test
    public void testToJSON() throws JSONException {
        TravelLocation destinationLocation = new TravelLocation(City.BEIJING);
        TravelLocation departureLocation = new TravelLocation(City.LONDON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destinationLocation, departureLocation);
        JSONObject jo = itineraryRequirement.toJSON();
        assertNotNull(jo.get(Constants.JSONKeys.DESTINATION));
        assertNotNull(jo.get(Constants.JSONKeys.DEPARTURE));

        assertEquals(true, itineraryRequirement.isItinerary());
    }

    @Test
    public void testToJSON4DateTimeSelection() throws JSONException {
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
        JSONObject jo = itineraryRequirement.toJSON();
        assertNotNull(jo.get(Constants.JSONKeys.DESTINATION));
        assertNotNull(jo.get(Constants.JSONKeys.DEPARTURE));
        assertTrue(jo.get(Constants.JSONKeys.DATETIME_SELECTIONS) instanceof JSONArray);

        JSONArray selections = (JSONArray) jo.get(Constants.JSONKeys.DATETIME_SELECTIONS);
        assertEquals(2, selections.length());
        assertEquals(departureDateTime1.getTimeInMillis(), selections.getLong(0));
        assertEquals(departureDateTime2.getTimeInMillis(), selections.getLong(1));

        assertEquals(true, itineraryRequirement.isItinerary());
    }
}
