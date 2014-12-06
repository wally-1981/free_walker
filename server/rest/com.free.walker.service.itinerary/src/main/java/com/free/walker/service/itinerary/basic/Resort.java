package com.free.walker.service.itinerary.basic;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.free.walker.service.itinerary.Serializable;

public class Resort implements Serializable {
    private double price;

    public JsonObject toJSON() {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        return resBuilder.build();
    }

    public Resort fromJSON(JsonObject jsObject) throws JsonException {
        return this;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.NULL;
    }
}
