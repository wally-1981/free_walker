package com.free.walker.service.itinerary.req;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.json.JsonException;
import javax.json.JsonObject;

import org.junit.Test;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.basic.Hotel;
import com.free.walker.service.itinerary.hotel.HotelStar;

public class HotelRequirementTest {
    @Test
    public void testToJSON4Nights() throws javax.json.JsonException {
        TravelRequirement hotelRequirement = new HotelRequirement(3);
        JsonObject jo = hotelRequirement.toJSON();
        assertEquals(Constants.JSONKeys.REQUIREMENT, jo.getString(Constants.JSONKeys.TYPE));
        assertEquals(jo.getInt(Constants.JSONKeys.NIGHT), 3);

        assertEquals(false, hotelRequirement.isItinerary());
    }

    @Test
    public void testToJSON4NightsAndStar() throws JsonException {
        TravelRequirement hotelRequirement = new HotelRequirement(4, HotelStar.LST_5);
        JsonObject jo = hotelRequirement.toJSON();
        assertEquals(Constants.JSONKeys.REQUIREMENT, jo.getString(Constants.JSONKeys.TYPE));
        assertEquals(4, jo.getInt(Constants.JSONKeys.NIGHT));
        assertEquals(5, jo.getInt(Constants.JSONKeys.STAR));

        assertEquals(false, hotelRequirement.isItinerary());
    }

    @Test
    public void testToJSON4NightsAndHotel() throws JsonException {
        TravelRequirement hotelRequirement = new HotelRequirement(5, new Hotel());
        JsonObject jo = hotelRequirement.toJSON();
        assertEquals(Constants.JSONKeys.REQUIREMENT, jo.getString(Constants.JSONKeys.TYPE));
        assertEquals(5, jo.getInt(Constants.JSONKeys.NIGHT));
        assertNotNull(jo.get(Constants.JSONKeys.HOTEL));

        assertEquals(false, hotelRequirement.isItinerary());
    }
}
