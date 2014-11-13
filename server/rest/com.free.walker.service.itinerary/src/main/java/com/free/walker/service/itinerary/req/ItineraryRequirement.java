package com.free.walker.service.itinerary.req;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.TravelLocation;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.util.UuidUtil;
import com.ibm.icu.util.Calendar;

public class ItineraryRequirement extends BaseTravelRequirement implements TravelRequirement {
    private TravelLocation destinationLocation;
    private TravelLocation departureLocation;
    private List<Calendar> departureDateTimeSelections;

    public ItineraryRequirement() {
        ;
    }

    public ItineraryRequirement(TravelLocation destinationLocation, TravelLocation departureLocation) {
        super();

        if (destinationLocation == null || departureLocation == null) {
            throw new NullPointerException();
        }

        this.destinationLocation = destinationLocation;
        this.departureLocation = departureLocation;
    }

    public ItineraryRequirement(TravelLocation destinationLocation, TravelLocation departureLocation,
        List<Calendar> departureDateTimeSelections) {
        this(destinationLocation, departureLocation);

        if (departureDateTimeSelections == null || departureDateTimeSelections.isEmpty()) {
            throw new IllegalArgumentException();
        }

        this.departureDateTimeSelections = departureDateTimeSelections;
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Introspection.JSONKeys.UUID, getUUID().toString());
        resBuilder.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY);
        resBuilder.add(Introspection.JSONKeys.DESTINATION, destinationLocation.toJSON());
        resBuilder.add(Introspection.JSONKeys.DEPARTURE, departureLocation.toJSON());

        if (departureDateTimeSelections != null) {
            JsonArrayBuilder dateTimeSelections = Json.createArrayBuilder();
            for (Calendar selection : departureDateTimeSelections) {
                dateTimeSelections.add(selection.getTimeInMillis());
            }
            resBuilder.add(Introspection.JSONKeys.DATETIME_SELECTIONS, dateTimeSelections);
        }

        return resBuilder.build();
    }

    public Object fromJSON(JsonObject jsObject) throws JsonException {
        String requirementId = jsObject.getString(Introspection.JSONKeys.UUID);

        if (requirementId != null) {
            try {
                this.requirementId = UuidUtil.fromUuidStr(requirementId);
            } catch (InvalidTravelReqirementException e) {
                throw new JsonException(e.getMessage(), e);
            }            
        }

        String type = jsObject.getString(Introspection.JSONKeys.TYPE);
        if (type != null && !Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY.equals(type)) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.TYPE, type));
        }

        JsonObject deptObj = jsObject.getJsonObject(Introspection.JSONKeys.DEPARTURE);
        if (deptObj != null) {
            this.departureLocation = (TravelLocation) new TravelLocation().fromJSON(deptObj);
        } else {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.DEPARTURE, deptObj));
        }

        JsonObject destObj = jsObject.getJsonObject(Introspection.JSONKeys.DESTINATION);
        if (destObj != null) {
            this.destinationLocation = (TravelLocation) new TravelLocation().fromJSON(destObj);
        } else {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.DESTINATION, destObj));
        }

        return this;
    }

    public boolean isItinerary() {
        return true;
    }

    public TravelLocation getDestination() {
        return destinationLocation;
    }
}
