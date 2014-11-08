package com.free.walker.service.itinerary.req;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.free.walker.service.itinerary.basic.Introspection;
import com.free.walker.service.itinerary.basic.TravelLocation;
import com.ibm.icu.util.Calendar;

public class ItineraryRequirement extends BaseTravelRequirement implements TravelRequirement {
    private TravelLocation destinationLocation;
    private TravelLocation departureLocation;
    private List<Calendar> departureDateTimeSelections;

    public ItineraryRequirement() {
        super();
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
        resBuilder.add(Introspection.JSONKeys.TYPE, Introspection.JSONKeys.ITINERARY);
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

    public boolean isItinerary() {
        return true;
    }

    public TravelLocation getDestination() {
        return destinationLocation;
    }
}
