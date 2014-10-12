package com.free.walker.service.itinerary.basic;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.SerializableJSON;

public class City implements SerializableJSON {
    public final static City BEIJING = new City("Beijing", Country.CHINA);
    public final static City WUHAN = new City("Wuhan", Country.CHINA);
    public final static City LONDON = new City("London", Country.UK);
    public final static City LA = new City("LA", Country.US);
    public final static City BOSTON = new City("BOSTON", Country.US);

    private String name;
    private Country country;

    private City(String name, Country country) {
        if (name == null || country == null) {
            throw new NullPointerException();
        }

        this.name = name;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject res = new JSONObject();
        res.put(Constants.JSONKeys.NAME, name);
        res.put(Constants.JSONKeys.COUNTRY, country);
        return res;
    }
}
