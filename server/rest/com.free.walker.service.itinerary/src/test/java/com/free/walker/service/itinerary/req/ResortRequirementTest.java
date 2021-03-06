package com.free.walker.service.itinerary.req;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.junit.Test;

import com.free.walker.service.itinerary.basic.Resort;
import com.free.walker.service.itinerary.primitive.Introspection;

public class ResortRequirementTest {
    @Test
    public void testToJSON4TimeRange() throws JsonException {
        TravelRequirement resortRequirement = new ResortRequirement(Introspection.JSONValues.TIME_RANGE_12_18);
        JsonObject jo = resortRequirement.toJSON();
        assertEquals(Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT, jo.getString(Introspection.JSONKeys.TYPE));
        assertEquals(12, jo.getInt(Introspection.JSONKeys.TIME_RANGE_START));
        assertEquals(18 - 12, jo.getInt(Introspection.JSONKeys.TIME_RANGE_OFFSET));

        assertEquals(false, resortRequirement.isItinerary());
    }

    @Test
    public void testToJSON4TimeRangeAndStar() throws JsonException {
        TravelRequirement resortRequirement = new ResortRequirement(Introspection.JSONValues.TIME_RANGE_06_12,
            Introspection.JSONValues.RESORT_STAR_STD_4A);
        JsonObject jo = resortRequirement.toJSON();
        assertEquals(Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT, jo.getString(Introspection.JSONKeys.TYPE));
        assertEquals(6, jo.getInt(Introspection.JSONKeys.TIME_RANGE_START));
        assertEquals(12 - 6, jo.getInt(Introspection.JSONKeys.TIME_RANGE_OFFSET));
        assertEquals(Introspection.JSONValues.RESORT_STAR_STD_4A.enumValue(), jo.getInt(Introspection.JSONKeys.STAR));

        assertEquals(false, resortRequirement.isItinerary());
    }

    @Test
    public void testToJSON4TimeRangeAndResort() throws JsonException {
        TravelRequirement resortRequirement = new ResortRequirement(Introspection.JSONValues.TIME_RANGE_06_12,
            new Resort());
        JsonObject jo = resortRequirement.toJSON();
        assertEquals(Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT, jo.getString(Introspection.JSONKeys.TYPE));
        assertEquals(6, jo.getInt(Introspection.JSONKeys.TIME_RANGE_START));
        assertEquals(12 - 6, jo.getInt(Introspection.JSONKeys.TIME_RANGE_OFFSET));
        assertNotNull(jo.get(Introspection.JSONKeys.RESORT));

        assertEquals(false, resortRequirement.isItinerary());
    }

    @Test
    public void testFromJSON() throws JsonException {
        JsonObjectBuilder requirement = Json.createObjectBuilder();
        UUID uuid = UUID.randomUUID();
        requirement.add(Introspection.JSONKeys.UUID, uuid.toString());
        requirement.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT);
        requirement.add(Introspection.JSONKeys.SUB_TYPE, ResortRequirement.SUB_TYPE);
        requirement.add(Introspection.JSONKeys.STAR, Introspection.JSONValues.RESORT_STAR_STD_4A.enumValue());
        requirement.add(Introspection.JSONKeys.TIME_RANGE_START, Introspection.JSONValues.TIME_RANGE_12_18.realValue());
        requirement.add(Introspection.JSONKeys.TIME_RANGE_OFFSET,
            Introspection.JSONValues.TIME_RANGE_12_18.imaginaryValue());
        TravelRequirement resortRequirement = new ResortRequirement().fromJSON(requirement.build());
        assertNotNull(resortRequirement);
        assertEquals(uuid, resortRequirement.getUUID());
    }
}
