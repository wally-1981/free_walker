package com.free.walker.service.itinerary.req;

import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

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

    public TrafficRequirement() {
        super();
    }

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

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Constants.JSONKeys.UUID, requirementId.toString());
        resBuilder.add(Constants.JSONKeys.TYPE, Constants.JSONKeys.REQUIREMENT);
        resBuilder.add(Constants.JSONKeys.TRAFFIC_TOOL_TYPE, trafficToolType.enumValue());

        if (trafficToolTimeRangeSelections != null) {
            JsonArrayBuilder dateTimeSelections = Json.createArrayBuilder();
            for (TravelTimeRange selection : trafficToolTimeRangeSelections) {
                JsonObjectBuilder timeRange = Json.createObjectBuilder();
                timeRange.add(Constants.JSONKeys.TIME_RANGE_START, selection.getStart());
                timeRange.add(Constants.JSONKeys.TIME_RANGE_OFFSET, selection.getOffset());
                dateTimeSelections.add(timeRange);
            }
            resBuilder.add(Constants.JSONKeys.DATETIME_RANGE_SELECTIONS, dateTimeSelections);
        }

        if (flight != null) {
            resBuilder.add(Constants.JSONKeys.FLIGHT, flight.toJSON());
        }

        if (train != null) {
            resBuilder.add(Constants.JSONKeys.TRAIN, train.toJSON());
        }

        return resBuilder.build();
    }
}
