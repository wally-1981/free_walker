package com.free.walker.service.itinerary.product;

import java.util.List;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.commons.lang3.StringUtils;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.util.UuidUtil;

public class TrivItem extends TravelProductItem {
    public static final String SUB_TYPE;

    static {
        String[] names = StringUtils.splitByCharacterTypeCamelCase(TrivItem.class.getSimpleName());
        SUB_TYPE = StringUtils.join(names, '_', 0, names.length - 1).toLowerCase();
    }

    public TrivItem() {
        super();
    }

    public TrivItem(TravelProduct travelProduct) {
        super(travelProduct);
    }

    public double getCost() {
        return super.getCost();
    }

    public String getType() {
        return SUB_TYPE;
    }

    public boolean isTriv() {
        return true;
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
        return resBuilder.build();
    }

    public TrivItem newFromJSON(JsonObject jsObject) throws JsonException {
        String uuidStr = jsObject.getString(Introspection.JSONKeys.UUID, null);
        if (uuidStr == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.UUID, uuidStr));
        } else {
            uuid = UuidUtil.fromUuidStr(uuidStr);
        }

        return fromJSON(jsObject);
    }

    public TrivItem fromJSON(JsonObject jsObject) throws JsonException {
        String subType = jsObject.getString(Introspection.JSONKeys.SUB_TYPE, null);
        if (subType == null || !SUB_TYPE.equals(subType)) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.SUB_TYPE, subType));
        }

        return this;
    }
}
