package com.free.walker.service.itinerary.req;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.basic.Hotel;
import com.free.walker.service.itinerary.hotel.HotelStar;

public class HotelRequirementTest {
    @Test
    public void testToJSON4Nights() throws JSONException {
        TravelRequirement hotelRequirement = new HotelRequirement(3);
        JSONObject jo = hotelRequirement.toJSON();
        assertEquals(jo.get(Constants.JSONKeys.NIGHT), 3);

        assertEquals(false, hotelRequirement.isItinerary());
    }

    @Test
    public void testToJSON4NightsAndStar() throws JSONException {
        TravelRequirement hotelRequirement = new HotelRequirement(4, HotelStar.LST_5);
        JSONObject jo = hotelRequirement.toJSON();
        assertEquals(4, jo.get(Constants.JSONKeys.NIGHT));
        assertEquals(5, jo.get(Constants.JSONKeys.STAR));

        assertEquals(false, hotelRequirement.isItinerary());
    }

    @Test
    public void testToJSON4NightsAndHotel() throws JSONException {
        TravelRequirement hotelRequirement = new HotelRequirement(5, new Hotel());
        JSONObject jo = hotelRequirement.toJSON();
        assertEquals(5, jo.get(Constants.JSONKeys.NIGHT));
        assertNotNull(jo.get(Constants.JSONKeys.HOTEL));

        assertEquals(false, hotelRequirement.isItinerary());
    }
}
