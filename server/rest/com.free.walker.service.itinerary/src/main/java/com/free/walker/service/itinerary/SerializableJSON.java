package com.free.walker.service.itinerary;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public interface SerializableJSON {
    public JSONObject toJSON() throws JSONException;
}
