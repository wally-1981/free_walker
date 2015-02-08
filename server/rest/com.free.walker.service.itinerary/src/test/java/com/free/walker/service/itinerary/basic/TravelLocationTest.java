package com.free.walker.service.itinerary.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import org.junit.Before;
import org.junit.Test;

import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.util.UuidUtil;

public class TravelLocationTest {
    @Before
    public void before() {
        new Country().load();
    }

    @Test
    public void testGetRelatedLocationsWithNoRelLocations() {
        {
            Object[] locations = new TravelLocation().getRelatedLocations();
            assertNotNull(locations);
            assertTrue(locations.length == 0);
        }

        {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add(Introspection.JSONKeys.UUID, UuidUtil.fromCmpUuidStr("af70a55ceb4c415c837588081716f8b8").toString());
            Country country = new Country().fromJSON(builder.build());

            Object[] locations = new TravelLocation(country).getRelatedLocations();
            assertNotNull(locations);
            assertTrue(locations.length == 0);
        }
    }

    @Test
    public void testGetRelatedLocationsWithRelLocation() {
        City dali = new City(UuidUtil.fromCmpUuidStr("34057abf82764f12808a5e62ced36233"));
        Object[] locations = new TravelLocation(dali).getRelatedLocations();
        assertNotNull(locations);
        assertTrue(locations.length == 3);
        assertEquals("237db8e728c941469c6009d004057caf", UuidUtil.toCmpUuidStr(((UUID) locations[0]).toString()));
        assertEquals("af70a55ceb4c415c837588081716f8b8", UuidUtil.toCmpUuidStr(((UUID) locations[1]).toString()));
        assertEquals("1", ((Continent) locations[2]).getUuid());
    }
}
