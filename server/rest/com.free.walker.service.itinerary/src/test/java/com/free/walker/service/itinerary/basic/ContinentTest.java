package com.free.walker.service.itinerary.basic;

import static org.junit.Assert.assertEquals;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.primitive.Introspection.JSONKeys;

public class ContinentTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testFromToJSON() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add(Introspection.JSONKeys.UUID, Introspection.JSONValues.CONTINENT_ID_ASIA.enumValue());
        Continent continent = new Continent().fromJSON(builder.build());

        JsonObject asiaObj = continent.toJSON();
        assertEquals(Introspection.JSONValues.CONTINENT_ID_ASIA.enumValue(), asiaObj.getInt(Introspection.JSONKeys.UUID));
        assertEquals("Asia", asiaObj.getString(Introspection.JSONKeys.NAME));
        assertEquals("亚洲", asiaObj.getString(Introspection.JSONKeys.CHINESE_NAME));
        assertEquals("yazhou", asiaObj.getString(Introspection.JSONKeys.PINYIN_NAME));
    }

    @Test
    public void testFromJSONWithInt() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add(Introspection.JSONKeys.UUID, 3);
        Continent continent = new Continent().fromJSON(builder.build());

        JsonObject asiaObj = continent.toJSON();
        assertEquals(Introspection.JSONValues.CONTINENT_ID_NORTH_AMERICA.enumValue(), asiaObj.getInt(Introspection.JSONKeys.UUID));
        assertEquals("North America", asiaObj.getString(Introspection.JSONKeys.NAME));
        assertEquals("北美洲", asiaObj.getString(Introspection.JSONKeys.CHINESE_NAME));
        assertEquals("beimeizhou", asiaObj.getString(Introspection.JSONKeys.PINYIN_NAME));
    }

    @Test
    public void testToFromJSONWithInvalidID() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add(Introspection.JSONKeys.UUID, 100);

        thrown.expect(JsonException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value, JSONKeys.UUID, 100));
        new Continent().fromJSON(builder.build());
    }

    @Test
    public void testToFromJSONWithStringID() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add(Introspection.JSONKeys.UUID, "1");

        thrown.expect(JsonException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value, JSONKeys.UUID, 0));
        new Continent().fromJSON(builder.build());

        JsonObjectBuilder builder2 = Json.createObjectBuilder();
        builder2.add(Introspection.JSONKeys.UUID, "ABC");

        thrown.expect(JsonException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value, JSONKeys.UUID, "ABC"));
        new Continent().fromJSON(builder2.build());
    }
}
