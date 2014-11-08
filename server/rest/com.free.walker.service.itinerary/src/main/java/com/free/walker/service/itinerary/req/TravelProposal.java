package com.free.walker.service.itinerary.req;

import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.free.walker.service.itinerary.basic.Introspection;

public class TravelProposal extends BaseTravelRequirement implements TravelRequirement {
    private List<TravelRequirement> travelRequirements;

    public TravelProposal() {
        super();
    }

    public TravelProposal(ItineraryRequirement itineraryRequirement) {
        super();

        if (itineraryRequirement == null) {
            throw new NullPointerException();
        }

        this.travelRequirements = new ArrayList<TravelRequirement>();
        this.travelRequirements.add(itineraryRequirement);
    }

    public boolean isProposal() {
        return true;
    }

    public List<TravelRequirement> getTravelRequirements() {
        return travelRequirements;
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Introspection.JSONKeys.UUID, getUUID().toString());
        resBuilder.add(Introspection.JSONKeys.TYPE, Introspection.JSONKeys.PROPOSAL);
        JsonArrayBuilder requirements = Json.createArrayBuilder();
        for (TravelRequirement travelRequirement : travelRequirements) {
            requirements.add(travelRequirement.toJSON());
        }
        resBuilder.add(Introspection.JSONKeys.REQUIREMENTS, requirements);

        return resBuilder.build();
    }
}
