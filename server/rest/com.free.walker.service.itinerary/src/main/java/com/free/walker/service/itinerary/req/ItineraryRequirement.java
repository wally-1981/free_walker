package com.free.walker.service.itinerary.req;

import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.TravelLocation;
import com.ibm.icu.util.Calendar;

public class ItineraryRequirement extends BaseTravelRequirement implements TravelRequirement {
    private TravelLocation destinationLocation;
    private TravelLocation departureLocation;
    private List<Calendar> departureDateTimeSelections;

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

    public JSONObject toJSON() throws JSONException {
        JSONObject res = super.toJSON();
        res.put(Constants.JSONKeys.DESTINATION, destinationLocation.toJSON());
        res.put(Constants.JSONKeys.DEPARTURE, departureLocation.toJSON());

        if (departureDateTimeSelections != null) {
            JSONArray dateTimeSelections = new JSONArray();
            for (Calendar selection : departureDateTimeSelections) {
                dateTimeSelections.put(selection.getTimeInMillis());
            }
            res.put(Constants.JSONKeys.DATETIME_SELECTIONS, dateTimeSelections);
        }

        return res;
    }

    public boolean isItinerary() {
        return true;
    }

    public TravelLocation getDestination() {
        return destinationLocation;
    }
}
