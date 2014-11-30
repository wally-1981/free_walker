package com.free.walker.service.itinerary.req;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.util.JsonObjectHelper;
import com.free.walker.service.itinerary.util.UuidUtil;

public class TravelProposal extends BaseTravelRequirement implements TravelRequirement {
    private static final String NEW_PROPOSAL = "New Proposal";

    private String proposalTitle;
    private List<TravelRequirement> travelRequirements;
    private Set<String> proposalTags;

    public TravelProposal() {
        this.travelRequirements = new ArrayList<TravelRequirement>();
        this.proposalTags = new HashSet<String>();
        this.proposalTitle = NEW_PROPOSAL;
    }

    public TravelProposal(String proposalTitle, ItineraryRequirement itineraryRequirement) {
        super();

        if (proposalTitle == null || itineraryRequirement == null) {
            throw new NullPointerException();
        }

        if (proposalTitle.trim().length() == 0) {
            throw new IllegalArgumentException();
        }

        this.travelRequirements = new ArrayList<TravelRequirement>();
        this.travelRequirements.add(itineraryRequirement);
        this.proposalTags = new HashSet<String>();
        this.proposalTitle = proposalTitle;
    }

    public TravelProposal(String proposalTitle, ItineraryRequirement itineraryRequirement, List<String> proposalTags) {
        this(proposalTitle, itineraryRequirement);

        this.proposalTags = distill(proposalTags);
        this.proposalTitle = proposalTitle;
    }

    public TravelProposal(ItineraryRequirement itineraryRequirement) {
        this(NEW_PROPOSAL, itineraryRequirement);
        String dest = itineraryRequirement.getDestination().getCity().getChineseName();
        String dept = itineraryRequirement.getDeparture().getCity().getChineseName();
        this.proposalTitle = dept + "=>" + dest;
    }

    public TravelProposal(ItineraryRequirement itineraryRequirement, List<String> proposalTags) {
        this(NEW_PROPOSAL, itineraryRequirement, proposalTags);
        String dest = itineraryRequirement.getDestination().getCity().getChineseName();
        String dept = itineraryRequirement.getDeparture().getCity().getChineseName();
        this.proposalTitle = dept + "=>" + dest;
    }

    public boolean isProposal() {
        return true;
    }

    public String getTitle() {
        return proposalTitle;
    }

    public List<TravelRequirement> getTravelRequirements() {
        return travelRequirements;
    }

    public Set<String> getTags() {
        return proposalTags;
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Introspection.JSONKeys.UUID, getUUID().toString());
        resBuilder.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL);
        resBuilder.add(Introspection.JSONKeys.TITLE, proposalTitle);

        JsonArrayBuilder requirements = Json.createArrayBuilder();
        for (TravelRequirement travelRequirement : travelRequirements) {
            requirements.add(travelRequirement.toJSON());
        }
        resBuilder.add(Introspection.JSONKeys.REQUIREMENTS, requirements);

        JsonArrayBuilder tags = Json.createArrayBuilder();
        Iterator<String> tagsIter = proposalTags.iterator();
        while (tagsIter.hasNext()) {
            tags.add(tagsIter.next());
        }
        resBuilder.add(Introspection.JSONKeys.TAGS, tags);

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

        String title = jsObject.getString(Introspection.JSONKeys.TITLE);
        if (title == null || title.trim().length() == 0) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.TITLE, title));
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
        }

        JsonArray tags = jsObject.getJsonArray(Introspection.JSONKeys.TAGS);
        proposalTags = distill(tags);

        return this;
    }

    private Set<String> distill(List<?> tags) {
        Map<String, Integer> distilled = new HashMap<String, Integer>();
        if (tags != null && tags.size() != 0) {
            for (int i = 0; i < tags.size(); i++) {
                Object tag = tags.get(i);
                if (tag instanceof JsonValue) {
                    String tagStr = tags.get(i).toString();
                    distilled.put(tagStr.substring(1, tagStr.length() - 1).trim(), 1);
                } else {
                    distilled.put(((String) tags.get(i)).trim(), 1);
                }
            }
        }
        return distilled.keySet();
    }
}