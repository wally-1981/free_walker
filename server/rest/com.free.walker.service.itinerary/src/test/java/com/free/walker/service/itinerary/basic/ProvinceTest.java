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

public class ProvinceTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() {
        new Province().load();
    }

    @Test
    public void testFromToJSON() throws InvalidTravelReqirementException {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add(Introspection.JSONKeys.UUID, UuidUtil.fromCmpUuidStr("8a1dc8fe34c04380b241c19a61ad07be").toString());
        Province province = new Province().fromJSON(builder.build());

        JsonObject provinceObj = province.toJSON();
        assertEquals(UuidUtil.fromCmpUuidStr("8a1dc8fe34c04380b241c19a61ad07be").toString(),
            provinceObj.getString(Introspection.JSONKeys.UUID));
        assertEquals("Hongkong", provinceObj.getString(Introspection.JSONKeys.NAME));
        assertEquals("香港", provinceObj.getString(Introspection.JSONKeys.CHINESE_NAME));
        assertEquals("xianggang", provinceObj.getString(Introspection.JSONKeys.PINYIN_NAME));
    }

    @Test
    public void testFromToJSONWithInvalidUuid() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        String invalidUuidStr = UUID.randomUUID().toString();
        builder.add(Introspection.JSONKeys.UUID, invalidUuidStr);
        thrown.expect(JsonException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
            Introspection.JSONKeys.UUID, invalidUuidStr));
        new Province().fromJSON(builder.build());
    }

    @Test
    public void testFromToJSONWithCmpUuid() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        String cmpUuidStr = "8a1dc8fe34c04380b241c19a61ad07be";
        builder.add(Introspection.JSONKeys.UUID, cmpUuidStr);
        thrown.expect(JsonException.class);
        new Province().fromJSON(builder.build());
    }
}
