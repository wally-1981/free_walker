package com.free.walker.service.itinerary.basic;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.free.walker.service.itinerary.Serializable;

public class TravelLocation implements Serializable {
    private City city;

    public TravelLocation(City city) {
        this.city = city;
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder res = Json.createObjectBuilder();
        res.add(Introspection.JSONKeys.CITY, city.toJSON());
        return res.build();
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.OBJECT;
    }
}
