package com.free.walker.service.itinerary.req;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.junit.Test;

import com.free.walker.service.itinerary.basic.TravelLocation;
import com.free.walker.service.itinerary.basic.TravelLocation.LocationType;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.util.UuidUtil;

public class DestinationRequirementTest {
    @Test
    public void testToJSON() throws javax.json.JsonException {
        UUID uuid = UuidUtil.fromCmpUuidStr("34057abf82764f12808a5e62ced36233");
        TravelLocation cityLocation = new TravelLocation(uuid, LocationType.CITY);
        DestinationRequirement destinationRequirement = new DestinationRequirement(cityLocation);
        JsonObject jo = destinationRequirement.toJSON();
        assertNotNull(jo.getString(Introspection.JSONKeys.UUID));
        assertEquals(Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT, jo.getString(Introspection.JSONKeys.TYPE));
        assertEquals(DestinationRequirement.SUB_TYPE, jo.getString(Introspection.JSONKeys.SUB_TYPE));
        assertNotNull(jo.getJsonObject(Introspection.JSONKeys.DESTINATION));
        JsonObject destination = jo.getJsonObject(Introspection.JSONKeys.DESTINATION);
        JsonObject destinationCity = destination.getJsonObject(Introspection.JSONKeys.CITY);
        assertNotNull(destinationCity);
        String destinationUuid = destinationCity.getJsonArray(Introspection.JSONKeys.UUID).getString(0);
        assertEquals(uuid.toString(), destinationUuid);
        assertEquals(false, destinationRequirement.isItinerary());
        assertEquals(false, destinationRequirement.isProposal());
    }

    @Test
    public void testFromJSON() throws javax.json.JsonException {
        UUID location = UuidUtil.fromCmpUuidStr("34057abf82764f12808a5e62ced36233");
        JsonObjectBuilder requirement = Json.createObjectBuilder();
        UUID uuid = UUID.randomUUID();
        requirement.add(Introspection.JSONKeys.UUID, uuid.toString());
        requirement.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT);
        requirement.add(Introspection.JSONKeys.SUB_TYPE, DestinationRequirement.SUB_TYPE);
        JsonObjectBuilder destination = Json.createObjectBuilder();
        destination.add(Introspection.JSONKeys.LOCATION, location.toString());
        destination.add(Introspection.JSONKeys.LOCATION_TYPE, LocationType.CITY.name());
        requirement.add(Introspection.JSONKeys.DESTINATION, destination);
        DestinationRequirement destinationRequirement = new DestinationRequirement().fromJSON(requirement.build());
        assertNotNull(destinationRequirement);
        assertEquals(uuid, destinationRequirement.getUUID());
    }
}
