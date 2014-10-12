package com.free.walker.service.itinerary.req;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.free.walker.service.itinerary.Constants;

public class TravelProposal extends BaseTravelRequirement implements TravelRequirement {
    private List<TravelRequirement> travelRequirements;

    public TravelProposal(ItineraryRequirement itineraryRequirement) {
        super();

        if (itineraryRequirement == null) {
            throw new NullPointerException();
        }

        this.travelRequirements = new ArrayList<TravelRequirement>();
        this.travelRequirements.add(itineraryRequirement);
    }

    public List<TravelRequirement> getTravelRequirements() {
        return travelRequirements;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject proposal = super.toJSON();
        JSONArray requirements = new JSONArray();
        for (TravelRequirement travelRequirement : travelRequirements) {
            requirements.put(travelRequirement.toJSON());
        }
        proposal.put(Constants.JSONKeys.PROPOSAL, requirements);
        return proposal;
    }
}
