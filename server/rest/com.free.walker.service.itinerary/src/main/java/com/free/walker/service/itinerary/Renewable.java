package com.free.walker.service.itinerary;

import javax.json.JsonException;
import javax.json.JsonObject;

public interface Renewable {
    public Object newFromJSON(JsonObject jsObject) throws JsonException;
}
