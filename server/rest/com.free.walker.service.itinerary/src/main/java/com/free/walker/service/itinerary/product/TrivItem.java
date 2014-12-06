package com.free.walker.service.itinerary.product;

import java.util.List;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.util.UuidUtil;

public class TrivItem extends TravelProductItem {
    public TrivItem(TravelProduct travelProduct) {
        super(travelProduct);
    }

    public double getCost() {
        return super.getCost();
    }

    public boolean isTriv() {
        return true;
    }

    public List<TravelProductItem> getTravelProductItems() {
        return travelProduct.getTravelProductItems();
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Introspection.JSONKeys.UUID, uuid.toString());
        return resBuilder.build();
    }

    public TrivItem fromJSON(JsonObject jsObject) throws JsonException {
        String uuidStr = jsObject.getString(Introspection.JSONKeys.UUID, null);
        if (uuidStr == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.UUID, uuidStr));
        } else {
            uuid = UuidUtil.fromUuidStr(uuidStr);
        }

        return this;
    }
}
