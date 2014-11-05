package com.free.walker.service.itinerary;

import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonValue;

public interface SerializableJSON extends JsonValue {
    public JsonObject toJSON() throws JsonException;
}
