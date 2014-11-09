package com.free.walker.service.itinerary.req;

import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.commons.lang3.StringUtils;

import com.free.walker.service.itinerary.basic.Flight;
import com.free.walker.service.itinerary.basic.Train;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.primitive.TrafficToolType;
import com.free.walker.service.itinerary.primitive.TravelTimeRange;

public class TrafficRequirement extends BaseTravelRequirement implements TravelRequirement {
    public static final String SUB_TYPE;

    static {
        String[] names = StringUtils.splitByCharacterTypeCamelCase(TrafficRequirement.class.getSimpleName());
        SUB_TYPE = StringUtils.join(names, '_', 0, names.length - 1);
    }

    private TrafficToolType trafficToolType;
    private List<TravelTimeRange> trafficToolTimeRangeSelections;
    private Flight flight;
    private Train train;

    public TrafficRequirement() {
        ;
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

    public TrafficRequirement(Flight flight) {
        this(Introspection.JSONValues.TRAFFIC_TOOL_FLIGHT);

        this.flight = flight;
    }

    public TrafficRequirement(Train train) {
        this(Introspection.JSONValues.TRAFFIC_TOOL_TRAIN);

        this.train = train;
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Introspection.JSONKeys.UUID, getUUID().toString());
        resBuilder.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.REQUIREMENT);
        resBuilder.add(Introspection.JSONKeys.SUB_TYPE, getSubType());
        resBuilder.add(Introspection.JSONKeys.TRAFFIC_TOOL_TYPE, trafficToolType.enumValue());

        if (trafficToolTimeRangeSelections != null) {
            JsonArrayBuilder dateTimeSelections = Json.createArrayBuilder();
            for (TravelTimeRange selection : trafficToolTimeRangeSelections) {
                JsonObjectBuilder timeRange = Json.createObjectBuilder();
                timeRange.add(Introspection.JSONKeys.TIME_RANGE_START, selection.realValue());
                timeRange.add(Introspection.JSONKeys.TIME_RANGE_OFFSET, selection.imaginaryValue());
                dateTimeSelections.add(timeRange);
            }
            resBuilder.add(Introspection.JSONKeys.DATETIME_RANGE_SELECTIONS, dateTimeSelections);
        }

        if (flight != null) {
            resBuilder.add(Introspection.JSONKeys.FLIGHT, flight.toJSON());
        }

        if (train != null) {
            resBuilder.add(Introspection.JSONKeys.TRAIN, train.toJSON());
        }

        return resBuilder.build();
    }

    public Object fromJSON(JsonObject jsObject) throws JsonException {
        return null;
    }
}
