package com.free.walker.service.itinerary.req;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.traffic.TrafficToolSeatClass;

public class TrafficToolSeatRequirement extends BaseTravelRequirement implements TravelRequirement {
    private TrafficToolSeatClass trafficToolSeatClass;

    public TrafficToolSeatRequirement(TrafficToolSeatClass trafficToolSeatClass) {
        super();

        if (trafficToolSeatClass == null) {
            throw new NullPointerException();
        }

        this.trafficToolSeatClass = trafficToolSeatClass;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject res = super.toJSON();
        res.put(Constants.JSONKeys.TRAFFIC_TOOL_SEAT_CLASS, trafficToolSeatClass.enumValue());
        return res;
    }
}
