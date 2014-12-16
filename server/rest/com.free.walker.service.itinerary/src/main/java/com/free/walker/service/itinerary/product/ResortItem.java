package com.free.walker.service.itinerary.product;

import java.util.List;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.commons.lang3.StringUtils;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.Resort;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.util.UuidUtil;
import com.ibm.icu.util.Calendar;

public class ResortItem extends TravelProductItem {
    public static final String SUB_TYPE;

    static {
        String[] names = StringUtils.splitByCharacterTypeCamelCase(ResortItem.class.getSimpleName());
        SUB_TYPE = StringUtils.join(names, '_', 0, names.length - 1).toLowerCase();
    }

    private Resort resort;
    private Calendar date;

    public ResortItem() {
        super();
    }

    public ResortItem(TravelProduct travelProduct, Resort resort, Calendar date) {
        super(travelProduct);

        if (resort == null || date == null) {
            throw new NullPointerException();
        }

        this.resort = resort;
        this.date = date;
    }

    public double getCost() {
        return super.getCost() + resort.getPrice();
    }

    public String getType() {
        return SUB_TYPE;
    }

    public UUID getUUID() {
        return uuid;
    }

    public List<TravelProductItem> getTravelProductItems() {
        return travelProduct.getTravelProductItems();
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Introspection.JSONKeys.UUID, uuid.toString());
        resBuilder.add(Introspection.JSONKeys.SUB_TYPE, SUB_TYPE);
        resBuilder.add(Introspection.JSONKeys.DATE, date.getTimeInMillis());
        resBuilder.add(Introspection.JSONKeys.RESORT, resort.toJSON());
        return resBuilder.build();
    }

    public ResortItem fromJSON(JsonObject jsObject) throws JsonException {
        String uuidStr = jsObject.getString(Introspection.JSONKeys.UUID, null);
        if (uuidStr == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.UUID, uuidStr));
        } else {
            uuid = UuidUtil.fromUuidStr(uuidStr);
        }

        return newFromJSON(jsObject);
    }

    public ResortItem newFromJSON(JsonObject jsObject) throws JsonException {
        String subType = jsObject.getString(Introspection.JSONKeys.SUB_TYPE, null);
        if (subType == null || !SUB_TYPE.equals(subType)) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.SUB_TYPE, subType));
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

        JsonObject resortJs = jsObject.getJsonObject(Introspection.JSONKeys.RESORT);
        if (resortJs == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.HOTEL, resortJs));
        } else {
            this.resort = new Resort().fromJSON(resortJs);
        }

        return this;
    }
}
