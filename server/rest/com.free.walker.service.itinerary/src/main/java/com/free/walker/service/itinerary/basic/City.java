package com.free.walker.service.itinerary.basic;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.free.walker.service.itinerary.Serializable;

public class City implements Serializable {
    public static final City BEIJING = new City("Beijing", Country.CHINA);
    public static final City WUHAN = new City("Wuhan", Country.CHINA);
    public static final City LONDON = new City("London", Country.UK);
    public static final City LA = new City("LA", Country.US);
    public static final City BOSTON = new City("BOSTON", Country.US);

    private String name;
    private Country country;

    private City(String name, Country country) {
        if (name == null || country == null) {
            throw new NullPointerException();
        }

        this.name = name;
        this.country = country;
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Introspection.JSONKeys.NAME, name);
        resBuilder.add(Introspection.JSONKeys.COUNTRY, country.toJSON());
        return resBuilder.build();
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.OBJECT;
    }
}
