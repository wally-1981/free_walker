package com.free.walker.service.itinerary.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.json.Json;
import javax.json.JsonObject;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;

public class JsonObjectUtilTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testMergeConflict() throws InvalidTravelReqirementException {
        thrown.expect(IllegalArgumentException.class);
        JsonObjectUtil.merge(Json.createObjectBuilder().add("a", 1).add("ns", 2).build(), "", Json
            .createObjectBuilder().add("d", 3).add("e", 4).build());

        thrown.expect(IllegalArgumentException.class);
        JsonObjectUtil.merge(Json.createObjectBuilder().add("a", 1).add("ns", 2).build(), " ", Json
            .createObjectBuilder().add("d", 3).add("e", 4).build());

        thrown.expect(IllegalArgumentException.class);
        JsonObjectUtil.merge(Json.createObjectBuilder().add("a", 1).add("ns", 2).build(), "ns", Json
            .createObjectBuilder().add("d", 3).add("e", 4).build());
    }

    @Test
    public void testMergeNull() throws InvalidTravelReqirementException {
        thrown.expect(NullPointerException.class);
        JsonObjectUtil.merge(null, "ns", Json.createObjectBuilder().add("d", 3).add("e", 4).build());

        thrown.expect(NullPointerException.class);
        JsonObjectUtil.merge(Json.createObjectBuilder().add("a", 1).add("ns", 2).build(), "ns", null);

        thrown.expect(NullPointerException.class);
        JsonObjectUtil.merge(Json.createObjectBuilder().add("a", 1).add("ns", 2).build(), null, Json
            .createObjectBuilder().add("d", 3).add("e", 4).build());
    }

    @Test
    public void testMerge() throws InvalidTravelReqirementException {
        JsonObject mergedJs = JsonObjectUtil.merge(Json.createObjectBuilder().add("a", 1).add("b", 2).build(), "ns",
            Json.createObjectBuilder().add("d", 3).add("e", 4).build());

        assertNotNull(mergedJs);

        assertTrue(mergedJs.containsKey("a"));
        assertEquals(1, mergedJs.getInt("a"));

        assertTrue(mergedJs.containsKey("b"));
        assertEquals(2, mergedJs.getInt("b"));

        assertTrue(mergedJs.containsKey("ns"));
        assertNotNull(mergedJs.getJsonObject("ns"));

        assertFalse(mergedJs.containsKey("d"));
        assertFalse(mergedJs.containsKey("e"));
        assertTrue(mergedJs.getJsonObject("ns").containsKey("d"));
        assertTrue(mergedJs.getJsonObject("ns").containsKey("e"));
        assertEquals(3, mergedJs.getJsonObject("ns").getInt("d"));
        assertEquals(4, mergedJs.getJsonObject("ns").getInt("e"));
    }
}
