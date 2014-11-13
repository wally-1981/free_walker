package com.free.walker.service.itinerary.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

public class CityTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() {
        new City().load();
    }

    @Test
    public void testFromToJSON() throws InvalidTravelReqirementException {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add(Introspection.JSONKeys.UUID, UuidUtil.fromCmpUuidStr("34057abf82764f12808a5e62ced36233").toString());
        City city = new City().fromJSON(builder.build());

        JsonObject cityObj = city.toJSON();
        assertEquals(UuidUtil.fromCmpUuidStr("34057abf82764f12808a5e62ced36233").toString(),
            cityObj.getJsonArray(Introspection.JSONKeys.UUID).getString(0));
        assertEquals(UuidUtil.fromCmpUuidStr("237db8e728c941469c6009d004057caf").toString(),
            cityObj.getJsonArray(Introspection.JSONKeys.UUID).getString(1));
        assertEquals(UuidUtil.fromCmpUuidStr("af70a55ceb4c415c837588081716f8b8").toString(),
            cityObj.getJsonArray(Introspection.JSONKeys.UUID).getString(2));
        assertEquals(String.valueOf(Introspection.JSONValues.CONTINENT_ID_ASIA.enumValue()),
            cityObj.getJsonArray(Introspection.JSONKeys.UUID).getString(3));
        assertEquals("Dali", cityObj.getString(Introspection.JSONKeys.NAME));
        assertEquals("大理", cityObj.getString(Introspection.JSONKeys.CHINESE_NAME));
        assertEquals("dali", cityObj.getString(Introspection.JSONKeys.PINYIN_NAME));
    }

    @Test
    public void testNewCityFromUUID() throws InvalidTravelReqirementException {
        City dali = new City(UuidUtil.fromCmpUuidStr("34057abf82764f12808a5e62ced36233"));
        assertNotNull(dali);
        assertEquals(UuidUtil.fromCmpUuidStr("34057abf82764f12808a5e62ced36233").toString(), dali.getUuid().toString());
        assertEquals(UuidUtil.fromCmpUuidStr("237db8e728c941469c6009d004057caf").toString(), dali.getProvinceUuid()
            .toString());
        assertEquals(UuidUtil.fromCmpUuidStr("af70a55ceb4c415c837588081716f8b8").toString(), dali.getCountryUuid()
            .toString());
        assertEquals(Introspection.JSONValues.CONTINENT_ID_ASIA.enumValue(), dali.getContinentId());
        assertEquals("Dali", dali.getName());
        assertEquals("大理", dali.getChineseName());
        assertEquals("dali", dali.getPinyinName());
    }

    @Test
    public void testFromToJSONWithInvalidUuid() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        String invalidUuidStr = UUID.randomUUID().toString();
        builder.add(Introspection.JSONKeys.UUID, invalidUuidStr);
        thrown.expect(JsonException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
            Introspection.JSONKeys.UUID, invalidUuidStr));
        new City().fromJSON(builder.build());
    }

    @Test
    public void testFromToJSONWithCmpUuid() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        String cmpUuidStr = "34057abf82764f12808a5e62ced36233";
        builder.add(Introspection.JSONKeys.UUID, cmpUuidStr);
        thrown.expect(JsonException.class);
        new City().fromJSON(builder.build());
    }
}
