package com.free.walker.service.itinerary.req;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.TravelTimeRange;
import com.free.walker.service.itinerary.basic.Flight;
import com.free.walker.service.itinerary.basic.Train;
import com.free.walker.service.itinerary.traffic.TrafficToolType;

public class TrafficRequirement extends BaseTravelRequirement implements TravelRequirement {
    private TrafficToolType trafficToolType;
    private List<TravelTimeRange> trafficToolTimeRangeSelections;
    private Flight flight;
    private Train train;

    public TrafficRequirement(TrafficToolType trafficToolType) {
        super();

        if (trafficToolType == null) {
            throw new NullPointerException();
        }

        this.trafficToolType = trafficToolType;
    }

    public TrafficRequirement(TrafficToolType trafficToolType, TravelTimeRange trafficToolTimeRange) {
        this(trafficToolType);

        if (trafficToolTimeRange == null) {
            throw new NullPointerException();
        }

        this.trafficToolTimeRangeSelections = new ArrayList<TravelTimeRange>();
        this.trafficToolTimeRangeSelections.add(trafficToolTimeRange);
    }

    public TrafficRequirement(TrafficToolType trafficToolType, List<TravelTimeRange> trafficToolTimeRangeSelections) {
        this(trafficToolType);

        if (trafficToolTimeRangeSelections == null || trafficToolTimeRangeSelections.isEmpty()) {
            throw new NullPointerException();
        }
        this.trafficToolTimeRangeSelections = trafficToolTimeRangeSelections;
    }

    public TrafficRequirement(TrafficToolType trafficToolType, Flight flight) {
        this(trafficToolType);

        this.flight = flight;
    }

    public TrafficRequirement(TrafficToolType trafficToolType, Train train) {
        this(trafficToolType);

        this.train = train;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject res = super.toJSON();
        res.put(Constants.JSONKeys.TRAFFIC_TOOL_TYPE, trafficToolType.enumValue());

        if (trafficToolTimeRangeSelections != null) {
            JSONArray dateTimeSelections = new JSONArray();
            for (TravelTimeRange selection : trafficToolTimeRangeSelections) {
                JSONObject timeRange = new JSONObject();
                timeRange.put(Constants.JSONKeys.TIME_RANGE_START, selection.getStart());
                timeRange.put(Constants.JSONKeys.TIME_RANGE_OFFSET, selection.getOffset());
                dateTimeSelections.put(timeRange);
            }
            res.put(Constants.JSONKeys.DATETIME_RANGE_SELECTIONS, dateTimeSelections);
        }

        if (flight != null) {
            res.put(Constants.JSONKeys.FLIGHT, flight.toJSON());
        }

        if (train != null) {
            res.put(Constants.JSONKeys.TRAIN, train.toJSON());
        }

        return res;
    }
}
