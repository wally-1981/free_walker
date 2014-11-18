package com.free.walker.service.itinerary.req;

import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.util.JsonObjectHelper;
import com.free.walker.service.itinerary.util.UuidUtil;

public class TravelProposal extends BaseTravelRequirement implements TravelRequirement {
    private List<TravelRequirement> travelRequirements;

    public TravelProposal() {
        travelRequirements = new ArrayList<TravelRequirement>();
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
        resBuilder.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL);
        JsonArrayBuilder requirements = Json.createArrayBuilder();
        for (TravelRequirement travelRequirement : travelRequirements) {
            requirements.add(travelRequirement.toJSON());
        }
        resBuilder.add(Introspection.JSONKeys.REQUIREMENTS, requirements);

        return resBuilder.build();
    }

    public TravelProposal fromJSON(JsonObject jsObject) throws JsonException {
        String requirementId = jsObject.getString(Introspection.JSONKeys.UUID);

        if (requirementId != null) {
            try {
                this.requirementId = UuidUtil.fromUuidStr(requirementId);
            } catch (InvalidTravelReqirementException e) {
                throw new JsonException(e.getMessage(), e);
            }            
        }

        return newFromJSON(jsObject);
    }

    public TravelProposal newFromJSON(JsonObject jsObject) throws JsonException {
        String type = jsObject.getString(Introspection.JSONKeys.TYPE);
        if (type != null && !Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL.equals(type)) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.TYPE, type));
        }

        JsonArray requirements = jsObject.getJsonArray(Introspection.JSONKeys.REQUIREMENTS);
        if (requirements != null && requirements.size() != 0) {
            try {
                for (int i = 0; i < requirements.size(); i++) {
                    JsonObject requirement = requirements.getJsonObject(i);
                    travelRequirements.add(JsonObjectHelper.toRequirement(requirement));
                }
            } catch (InvalidTravelReqirementException e) {
                throw new JsonException(e.getMessage(), e);
            }
        } else {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.REQUIREMENTS, requirements));
        }

        return this;
    }
}
