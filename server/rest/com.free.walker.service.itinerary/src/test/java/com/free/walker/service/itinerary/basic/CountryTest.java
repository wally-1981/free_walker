package com.free.walker.service.itinerary.basic;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.util.UuidUtil;

public class CountryTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() {
        new Country().load();
    }

    @Test
    public void testFromToJSON() throws InvalidTravelReqirementException {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add(Introspection.JSONKeys.UUID, UuidUtil.fromCmpUuidStr("af70a55ceb4c415c837588081716f8b8").toString());
        Country country = new Country().fromJSON(builder.build());

        JsonObject countryObj = country.toJSON();
        assertEquals(UuidUtil.fromCmpUuidStr("af70a55ceb4c415c837588081716f8b8").toString(),
            countryObj.getString(Introspection.JSONKeys.UUID));
        assertEquals("China", countryObj.getString(Introspection.JSONKeys.NAME));
        assertEquals("中国", countryObj.getString(Introspection.JSONKeys.CHINESE_NAME));
        assertEquals("zhongguo", countryObj.getString(Introspection.JSONKeys.PINYIN_NAME));
    }

    @Test
    public void testFromToJSONWithInvalidUuid() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        String invalidUuidStr = UUID.randomUUID().toString();
        builder.add(Introspection.JSONKeys.UUID, invalidUuidStr);
        thrown.expect(JsonException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
            Introspection.JSONKeys.UUID, invalidUuidStr));
        new Country().fromJSON(builder.build());
    }

    @Test
    public void testFromToJSONWithCmpUuid() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        String cmpUuidStr = "af70a55ceb4c415c837588081716f8b8";
        builder.add(Introspection.JSONKeys.UUID, cmpUuidStr);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(cmpUuidStr);
        new Country().fromJSON(builder.build());
    }
}
