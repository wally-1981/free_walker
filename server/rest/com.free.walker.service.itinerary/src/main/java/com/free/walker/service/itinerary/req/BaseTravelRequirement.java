package com.free.walker.service.itinerary.req;

import java.util.UUID;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.TravelLocation;

public abstract class BaseTravelRequirement implements TravelRequirement {
    protected UUID requirementId;

    public BaseTravelRequirement() {
        this.requirementId = UUID.randomUUID();
    }

    public boolean isItinerary() {
        return false;
    }

    public UUID getUUID() {
        return requirementId;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put(Constants.JSONKeys.UUID, requirementId);
        return jo;
    }

    public TravelLocation getDestination() {
        return null;
    }
}
