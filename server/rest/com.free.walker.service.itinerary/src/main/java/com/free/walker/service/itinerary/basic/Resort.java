package com.free.walker.service.itinerary.basic;

import org.codehaus.jettison.json.JSONObject;

import com.free.walker.service.itinerary.SerializableJSON;

public class Resort implements SerializableJSON {
    public JSONObject toJSON() {
        return new JSONObject();
    }
}
