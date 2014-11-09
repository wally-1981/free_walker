package com.free.walker.service.itinerary.req;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.json.JsonException;
import javax.json.JsonObject;

import org.junit.Test;

import com.free.walker.service.itinerary.basic.Hotel;
import com.free.walker.service.itinerary.primitive.Introspection;

public class HotelRequirementTest {
    @Test
    public void testToJSON4Nights() throws javax.json.JsonException {
        TravelRequirement hotelRequirement = new HotelRequirement(3);
        JsonObject jo = hotelRequirement.toJSON();
        assertEquals(Introspection.JSONValues.REQUIREMENT, jo.getString(Introspection.JSONKeys.TYPE));
        assertEquals(jo.getInt(Introspection.JSONKeys.NIGHT), 3);

        assertEquals(false, hotelRequirement.isItinerary());
    }

    @Test
    public void testToJSON4NightsAndStar() throws JsonException {
        TravelRequirement hotelRequirement = new HotelRequirement(4, Introspection.JSONValues.HOTEL_LST_5);
        JsonObject jo = hotelRequirement.toJSON();
        assertEquals(Introspection.JSONValues.REQUIREMENT, jo.getString(Introspection.JSONKeys.TYPE));
        assertEquals(4, jo.getInt(Introspection.JSONKeys.NIGHT));
        assertEquals(50, jo.getInt(Introspection.JSONKeys.STAR));

        assertEquals(false, hotelRequirement.isItinerary());
    }

    @Test
    public void testToJSON4NightsAndHotel() throws JsonException {
        TravelRequirement hotelRequirement = new HotelRequirement(5, new Hotel());
        JsonObject jo = hotelRequirement.toJSON();
        assertEquals(Introspection.JSONValues.REQUIREMENT, jo.getString(Introspection.JSONKeys.TYPE));
        assertEquals(5, jo.getInt(Introspection.JSONKeys.NIGHT));
        assertNotNull(jo.get(Introspection.JSONKeys.HOTEL));

        assertEquals(false, hotelRequirement.isItinerary());
    }
}
