package com.free.walker.service.itinerary.basic;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.Serializable;
import com.free.walker.service.itinerary.primitive.Introspection;

public class TravelLocation implements Serializable {
    private City city;

    public TravelLocation() {
        ;
    }

    public TravelLocation(City city) {
        this.city = city;
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder res = Json.createObjectBuilder();
        res.add(Introspection.JSONKeys.CITY, city.toJSON());
        return res.build();
    }

    public Object fromJSON(JsonObject jsObject) throws JsonException {
        JsonObject cityObj = jsObject.getJsonObject(Introspection.JSONKeys.CITY);

        if (cityObj == null) {            
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.CITY, cityObj));
        } else {
            city = new City().fromJSON(cityObj);
        }

        return this;
    }

    public City getCity() {
        return city;
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.OBJECT;
    }
}
