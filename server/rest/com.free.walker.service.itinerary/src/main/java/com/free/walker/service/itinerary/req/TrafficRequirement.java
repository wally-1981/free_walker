package com.free.walker.service.itinerary.req;

import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.commons.lang3.StringUtils;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.Flight;
import com.free.walker.service.itinerary.basic.Train;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.primitive.TrafficToolType;
import com.free.walker.service.itinerary.primitive.TravelTimeRange;
import com.free.walker.service.itinerary.util.UuidUtil;

public class TrafficRequirement extends BaseTravelRequirement implements TravelRequirement {
    public static final String SUB_TYPE;

    static {
        String[] names = StringUtils.splitByCharacterTypeCamelCase(TrafficRequirement.class.getSimpleName());
        SUB_TYPE = StringUtils.join(names, '_', 0, names.length - 1).toLowerCase();
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
        this(Introspection.JSONValues.TRAFFIC_TOOL_TYPE_FLIGHT);

        this.flight = flight;
    }

    public TrafficRequirement(Train train) {
        this(Introspection.JSONValues.TRAFFIC_TOOL_TYPE_TRAIN);

        this.train = train;
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Introspection.JSONKeys.UUID, getUUID().toString());
        resBuilder.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT);
        resBuilder.add(Introspection.JSONKeys.SUB_TYPE, SUB_TYPE);
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

    public TrafficRequirement fromJSON(JsonObject jsObject) throws JsonException {
        String requirementId = jsObject.getString(Introspection.JSONKeys.UUID, null);

        if (requirementId != null) {
            this.requirementId = UuidUtil.fromUuidStr(requirementId);
        } else {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.UUID, requirementId));
        }

        return newFromJSON(jsObject);
    }

    public TrafficRequirement newFromJSON(JsonObject jsObject) throws JsonException {
        String type = jsObject.getString(Introspection.JSONKeys.TYPE, null);
        if (type != null && !Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT.equals(type)) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.TYPE, type));
        }

        String subType = jsObject.getString(Introspection.JSONKeys.SUB_TYPE, null);
        if (subType != null && !SUB_TYPE.equals(subType)) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.SUB_TYPE, subType));
        }

        int trafficToolType = jsObject.getInt(Introspection.JSONKeys.TRAFFIC_TOOL_TYPE, 0);
        if (trafficToolType > 0) {
            try {
                this.trafficToolType = Introspection.JsonValueHelper.getTrafficToolType(trafficToolType);
            } catch (InvalidTravelReqirementException e) {
                throw new JsonException(e.getMessage(), e);
            }
        } else {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.TRAFFIC_TOOL_TYPE, trafficToolType));
        }

        JsonArray dateTimeSelections = jsObject.getJsonArray(Introspection.JSONKeys.DATETIME_RANGE_SELECTIONS);
        trafficToolTimeRangeSelections = new ArrayList<TravelTimeRange>();
        try {
            for (int i = 0; i < dateTimeSelections.size(); i++) {
                JsonObject dateTime = dateTimeSelections.getJsonObject(i);
                int start = dateTime.getInt(Introspection.JSONKeys.TIME_RANGE_START, -1);
                int offset = dateTime.getInt(Introspection.JSONKeys.TIME_RANGE_OFFSET, -1);
                trafficToolTimeRangeSelections.add(Introspection.JsonValueHelper.getTravelTimeRange(start, offset));
            }
        } catch (InvalidTravelReqirementException e) {
            throw new JsonException(e.getMessage(), e);
        }

        return this;
    }
}
