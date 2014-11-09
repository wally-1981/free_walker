package com.free.walker.service.itinerary.basic;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.Serializable;
import com.free.walker.service.itinerary.primitive.Introspection;

public class City implements Serializable {
    private String name;
    private Country country;

    public City() {
        ;
    }

    public City(String name, Country country) {
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

    public City fromJSON(JsonObject jsObject) throws JsonException {
        String cityName = jsObject.getString(Introspection.JSONKeys.NAME);
        if (cityName == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.NAME, cityName));
        } else {
            name = cityName;
        }

        JsonObject countryObj = jsObject.getJsonObject(Introspection.JSONKeys.COUNTRY);
        if (countryObj == null) {            
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.COUNTRY, countryObj));
        } else {
            country = new Country().fromJSON(countryObj);
        }

        return this;
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.OBJECT;
    }
}
