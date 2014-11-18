package com.free.walker.service.itinerary.req;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.junit.Test;

import com.free.walker.service.itinerary.basic.Hotel;
import com.free.walker.service.itinerary.primitive.Introspection;

public class HotelRequirementTest {
    @Test
    public void testToJSON4Nights() throws javax.json.JsonException {
        TravelRequirement hotelRequirement = new HotelRequirement(3);
        JsonObject jo = hotelRequirement.toJSON();
        assertEquals(Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT, jo.getString(Introspection.JSONKeys.TYPE));
        assertEquals(jo.getInt(Introspection.JSONKeys.NIGHT), 3);

        assertEquals(false, hotelRequirement.isItinerary());
    }

    @Test
    public void testToJSON4NightsAndStar() throws JsonException {
        TravelRequirement hotelRequirement = new HotelRequirement(4, Introspection.JSONValues.HOTEL_STAR_LST_5);
        JsonObject jo = hotelRequirement.toJSON();
        assertEquals(Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT, jo.getString(Introspection.JSONKeys.TYPE));
        assertEquals(4, jo.getInt(Introspection.JSONKeys.NIGHT));
        assertEquals(50, jo.getInt(Introspection.JSONKeys.STAR));

        assertEquals(false, hotelRequirement.isItinerary());
    }

    @Test
    public void testToJSON4NightsAndHotel() throws JsonException {
        TravelRequirement hotelRequirement = new HotelRequirement(5, new Hotel());
        JsonObject jo = hotelRequirement.toJSON();
        assertEquals(Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT, jo.getString(Introspection.JSONKeys.TYPE));
        assertEquals(5, jo.getInt(Introspection.JSONKeys.NIGHT));
        assertNotNull(jo.get(Introspection.JSONKeys.HOTEL));

        assertEquals(false, hotelRequirement.isItinerary());
    }

    @Test
    public void testFromJSON() throws JsonException {
        JsonObjectBuilder requirement = Json.createObjectBuilder();
        UUID uuid = UUID.randomUUID();
        requirement.add(Introspection.JSONKeys.UUID, uuid.toString());
        requirement.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT);
        requirement.add(Introspection.JSONKeys.SUB_TYPE, HotelRequirement.SUB_TYPE);
        requirement.add(Introspection.JSONKeys.NIGHT, 12);
        requirement.add(Introspection.JSONKeys.STAR, Introspection.JSONValues.HOTEL_STAR_LST_4.enumValue());
        TravelRequirement hotelRequirement = new HotelRequirement().fromJSON(requirement.build());
        assertNotNull(hotelRequirement);
        assertEquals(uuid.toString(), hotelRequirement.getUUID().toString());
    }
}
