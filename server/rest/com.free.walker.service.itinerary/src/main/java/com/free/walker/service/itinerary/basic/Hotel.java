package com.free.walker.service.itinerary.basic;


import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.free.walker.service.itinerary.SerializableJSON;

public class Hotel implements SerializableJSON {
    public JsonObject toJSON() {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        return resBuilder.build();
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.OBJECT;
    }
}
