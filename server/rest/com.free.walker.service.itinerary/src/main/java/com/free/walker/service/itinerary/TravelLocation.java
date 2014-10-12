package com.free.walker.service.itinerary;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.free.walker.service.itinerary.basic.City;

public class TravelLocation implements SerializableJSON {
    private City city;

    public TravelLocation(City city) {
        this.city = city;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject res = new JSONObject();
        res.putOpt(Constants.JSONKeys.CITY, city.toJSON());
        return res;
    }
}
