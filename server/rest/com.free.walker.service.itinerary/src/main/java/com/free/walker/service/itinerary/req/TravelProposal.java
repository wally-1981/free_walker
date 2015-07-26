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
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.util.JsonObjectHelper;
import com.free.walker.service.itinerary.util.UuidUtil;
import com.ibm.icu.util.Calendar;

public class TravelProposal extends BaseTravelRequirement implements TravelRequirement {
    private String authorAccountId;
    private String proposalTitle;
    private List<TravelRequirement> travelRequirements;
    private Set<String> proposalTags;
    private int unit;
    private Calendar arrivalDatetime;
    private Calendar departureDatetime;
    private double budget;

    public TravelProposal() {
        this.travelRequirements = new ArrayList<TravelRequirement>();
        this.proposalTags = new HashSet<String>();
        this.proposalTitle = Constants.NEW_PROPOSAL;
        this.unit = 0;
        this.budget = 0;
        this.authorAccountId = Constants.DEFAULT_USER_ACCOUNT.getUuid();
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
        this.unit = 0;
        this.budget = 0;
        this.authorAccountId = Constants.DEFAULT_USER_ACCOUNT.getUuid();
    }

    public TravelProposal(String proposalTitle, ItineraryRequirement itineraryRequirement, List<String> proposalTags) {
        this(proposalTitle, itineraryRequirement);
        this.proposalTags = distill(proposalTags);
    }

    public TravelProposal(ItineraryRequirement itineraryRequirement) {
        this(Constants.NEW_PROPOSAL, itineraryRequirement);
        String dest = itineraryRequirement.getDestination().getChineseName();
        String dept = itineraryRequirement.getDeparture().getChineseName();
        this.proposalTitle = dept + "=>" + dest;
    }

    public TravelProposal(ItineraryRequirement itineraryRequirement, List<String> proposalTags) {
        this(Constants.NEW_PROPOSAL, itineraryRequirement, proposalTags);
        String dest = itineraryRequirement.getDestination().getChineseName();
        String dept = itineraryRequirement.getDeparture().getChineseName();
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
        resBuilder.add(Introspection.JSONKeys.AUTHOR, authorAccountId);
        resBuilder.add(Introspection.JSONKeys.TITLE, proposalTitle);
        resBuilder.add(Introspection.JSONKeys.UNIT, unit);
        resBuilder.add(Introspection.JSONKeys.BUDGET, budget);

        if (arrivalDatetime != null) {
            resBuilder.add(Introspection.JSONKeys.ARRIVAL_DATETIME, arrivalDatetime.getTimeInMillis());
        }

        if (departureDatetime != null) {
            resBuilder.add(Introspection.JSONKeys.DEPARTURE_DATETIME, departureDatetime.getTimeInMillis());
        }

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
        String requirementId = jsObject.getString(Introspection.JSONKeys.UUID, null);

        if (requirementId != null) {
            this.requirementId = UuidUtil.fromUuidStr(requirementId);
        } else {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.UUID, requirementId));
        }

        return newFromJSON(jsObject);
    }

    public TravelProposal newFromJSON(JsonObject jsObject) throws JsonException {
        String type = jsObject.getString(Introspection.JSONKeys.TYPE, null);
        if (type != null && !Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL.equals(type)) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.TYPE, type));
        }

        String title = jsObject.getString(Introspection.JSONKeys.TITLE, null);
        if (title == null || title.trim().length() == 0) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.TITLE, title));
        } else {
            proposalTitle = title;
        }

        JsonNumber unit = jsObject.getJsonNumber(Introspection.JSONKeys.UNIT);
        if (unit != null) {
            this.unit = unit.intValue();
        }

        JsonNumber departureDt = jsObject.getJsonNumber(Introspection.JSONKeys.DEPARTURE_DATETIME);
        if (departureDt != null) {
            this.arrivalDatetime = Calendar.getInstance();
            this.arrivalDatetime.setTimeInMillis(departureDt.longValue());
        }

        JsonNumber arrivalDt = jsObject.getJsonNumber(Introspection.JSONKeys.ARRIVAL_DATETIME);
        if (arrivalDt != null) {
            this.arrivalDatetime = Calendar.getInstance();
            this.arrivalDatetime.setTimeInMillis(arrivalDt.longValue());
        }

        JsonNumber budget = jsObject.getJsonNumber(Introspection.JSONKeys.BUDGET);
        if (budget != null) {
            this.budget = budget.doubleValue();
        }

        String author = jsObject.getString(Introspection.JSONKeys.AUTHOR, null);
        if (author == null || author.trim().length() == 0) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.AUTHOR, author));
        } else {
            authorAccountId = author;
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
