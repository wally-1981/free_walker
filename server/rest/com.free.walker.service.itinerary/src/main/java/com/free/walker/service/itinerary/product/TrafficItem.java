package com.free.walker.service.itinerary.product;

import java.util.List;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.commons.lang3.StringUtils;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.Flight;
import com.free.walker.service.itinerary.basic.Train;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.traffic.TrafficTool;
import com.free.walker.service.itinerary.util.UuidUtil;
import com.ibm.icu.util.Calendar;

public class TrafficItem extends TravelProductItem {
    public static final String SUB_TYPE;

    static {
        String[] names = StringUtils.splitByCharacterTypeCamelCase(TrafficItem.class.getSimpleName());
        SUB_TYPE = StringUtils.join(names, '_', 0, names.length - 1).toLowerCase();
    }

    private TrafficTool trafficTool;
    private Calendar date;

    public TrafficItem(TravelProduct travelProduct, TrafficTool trafficTool, Calendar date) {
        super(travelProduct);

        if (trafficTool == null || date == null) {
            throw new NullPointerException();
        }

        this.trafficTool = trafficTool;
        this.date = date;
    }

    public double getCost() {
        return super.getCost() + trafficTool.getTotalFee();
    }

    public String getType() {
        return SUB_TYPE;
    }

    public List<TravelProductItem> getTravelProductItems() {
        return travelProduct.getTravelProductItems();
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Introspection.JSONKeys.UUID, uuid.toString());
        resBuilder.add(Introspection.JSONKeys.DATE, date.getTimeInMillis());
        resBuilder.add(Introspection.JSONKeys.TRAFFIC, trafficTool.toJSON());
        return resBuilder.build();
    }

    public Object fromJSON(JsonObject jsObject) throws JsonException {
        String uuidStr = jsObject.getString(Introspection.JSONKeys.UUID, null);
        if (uuidStr == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.UUID, uuidStr));
        } else {
            uuid = UuidUtil.fromUuidStr(uuidStr);
        }

        JsonNumber dateJs = jsObject.getJsonNumber(Introspection.JSONKeys.DATE);
        if (dateJs == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.DATE, dateJs));
        } else {
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(dateJs.longValue());
            this.date = date;
        }

        JsonObject trafficJs = jsObject.getJsonObject(Introspection.JSONKeys.TRAFFIC);
        if (trafficJs == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.TRAFFIC, trafficJs));
        } else {
            if (Introspection.JSONValues.TRAFFIC_TOOL_TYPE_FLIGHT.equals(trafficTool.getType())) {
                this.trafficTool = new Flight().fromJSON(trafficJs);
            } else if (Introspection.JSONValues.TRAFFIC_TOOL_TYPE_TRAIN.equals(trafficTool.getType())) {
                this.trafficTool = new Train().fromJSON(trafficJs);
            } else {
                throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                    Introspection.JSONKeys.TRAFFIC, trafficJs));
            }
        }

        return this;
    }
}
