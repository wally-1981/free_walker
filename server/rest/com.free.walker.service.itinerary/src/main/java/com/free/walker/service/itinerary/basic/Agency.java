package com.free.walker.service.itinerary.basic;

import java.util.UUID;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.Serializable;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.util.UuidUtil;

public class Agency implements Serializable {
    private UUID agencyId;

    public Agency() {
        agencyId = UUID.randomUUID();
    }

    public Agency(UUID agencyId) {
        if (agencyId == null) {
            throw new NullPointerException();
        }

        this.agencyId = agencyId;
    }

    public JsonObject toJSON() {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Introspection.JSONKeys.UUID, agencyId.toString());
        return resBuilder.build();
    }

    public Agency fromJSON(JsonObject jsObject) throws JsonException {
        String uuidStr = jsObject.getString(Introspection.JSONKeys.UUID, null);
        if (uuidStr == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.UUID, uuidStr));
        } else {
            agencyId = UuidUtil.fromUuidStr(uuidStr);
        }

        return this;
    }

    public String getUuid() {
        return agencyId.toString();
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.NULL;
    }
}
