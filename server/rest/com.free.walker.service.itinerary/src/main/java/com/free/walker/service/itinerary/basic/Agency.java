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
    private String name;

    public Agency() {
        ;
    }

    public JsonObject toJSON() {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Introspection.JSONKeys.UUID, agencyId.toString());
        resBuilder.add(Introspection.JSONKeys.NAME, name);
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

        String nameStr = jsObject.getString(Introspection.JSONKeys.NAME, null);
        if (nameStr == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.NAME, nameStr));
        } else {
            name = nameStr;
        }

        return this;
    }

    public String getUuid() {
        return agencyId.toString();
    }

    public void setUuid(String uuid) {
        if (UuidUtil.isCmpUuidStr(uuid)) {
            this.agencyId = UuidUtil.fromCmpUuidStr(uuid);
        } else {
            this.agencyId = UuidUtil.fromUuidStr(uuid);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.NULL;
    }
}
