package com.free.walker.service.itinerary.basic;


import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.free.walker.service.itinerary.Serializable;

public class Hotel implements Serializable {
    public JsonObject toJSON() {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        return resBuilder.build();
    }

    public Object fromJSON(JsonObject jsObject) throws JsonException {
        return this;
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.OBJECT;
    }
}
