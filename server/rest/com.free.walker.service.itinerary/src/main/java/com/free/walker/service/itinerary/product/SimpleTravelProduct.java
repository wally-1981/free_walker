package com.free.walker.service.itinerary.product;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.Renewable;
import com.free.walker.service.itinerary.exp.InvalidTravelProductException;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.util.JsonObjectHelper;
import com.free.walker.service.itinerary.util.UuidUtil;
import com.ibm.icu.util.Calendar;

public class SimpleTravelProduct implements TravelProduct, Renewable {
    private List<TravelProductItem> travelProductItems;
    private UUID productId;
    private UUID proposalId;
    private int sizeUpperLimit;
    private Calendar enrolmentDeadlineDateTime;
    private Calendar departureDateTime;

    public SimpleTravelProduct() {
        this.productId = UUID.randomUUID();
        this.travelProductItems = new LinkedList<TravelProductItem>();
    }

    public SimpleTravelProduct(UUID travelProposal) {
        this();

        if (travelProposal == null) {
            throw new NullPointerException();
        }

        this.proposalId = travelProposal;
    }

    public double getCost() {
        return 0;
    }

    public List<TravelProductItem> getTravelProductItems() {
        return travelProductItems;
    }

    public UUID getTravelProposal() {
        return proposalId;
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Introspection.JSONKeys.UUID, productId.toString());
        resBuilder.add(Introspection.JSONKeys.REF_UUID, proposalId.toString());

        JsonArrayBuilder items = Json.createArrayBuilder();
        for (TravelProductItem travelProductItem : travelProductItems) {
            items.add(travelProductItem.toJSON());
        }
        resBuilder.add(Introspection.JSONKeys.ITEMS, items);

        if (sizeUpperLimit > 0) {
            resBuilder.add(Introspection.JSONKeys.GROUP_CAPACITY, sizeUpperLimit);
        }

        if (enrolmentDeadlineDateTime != null) {
            resBuilder.add(Introspection.JSONKeys.DEADLINE_DATETIME, enrolmentDeadlineDateTime.getTimeInMillis());
        }

        if (departureDateTime != null) {
            resBuilder.add(Introspection.JSONKeys.DEPARTURE_DATETIME, departureDateTime.getTimeInMillis());
        }

        return resBuilder.build();
    }

    public TravelProduct fromJSON(JsonObject jsObject) throws JsonException {
        String productId = jsObject.getString(Introspection.JSONKeys.UUID, null);

        if (productId != null) {
            this.productId = UuidUtil.fromUuidStr(productId);
        } else {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.UUID, productId));
        }

        return newFromJSON(jsObject);
    }

    public TravelProduct newFromJSON(JsonObject jsObject) throws JsonException {
        String proposalId = jsObject.getString(Introspection.JSONKeys.REF_UUID, null);

        if (proposalId != null) {
            this.proposalId = UuidUtil.fromUuidStr(proposalId);
        } else {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.REF_UUID, proposalId));
        }

        JsonArray items = jsObject.getJsonArray(Introspection.JSONKeys.ITEMS);
        if (items != null && items.size() != 0) {
            try {
                for (int i = 0; i < items.size(); i++) {
                    JsonObject item = items.getJsonObject(i);
                    travelProductItems.add(JsonObjectHelper.toProductItem(item));
                }
            } catch (InvalidTravelProductException e) {
                throw new JsonException(e.getMessage(), e);
            }
        }

        int sizeUpperLimit = jsObject.getInt(Introspection.JSONKeys.GROUP_CAPACITY, 0);
        if (sizeUpperLimit > 0) {
            this.sizeUpperLimit = sizeUpperLimit;
        }
        
        JsonNumber enrolmentDeadlineDateTime = jsObject.getJsonNumber(Introspection.JSONKeys.DEADLINE_DATETIME);
        if (enrolmentDeadlineDateTime != null) {
            this.enrolmentDeadlineDateTime = Calendar.getInstance();
            this.enrolmentDeadlineDateTime.setTimeInMillis(enrolmentDeadlineDateTime.longValue());
        }

        JsonNumber departureDateTime = jsObject.getJsonNumber(Introspection.JSONKeys.DEPARTURE_DATETIME);
        if (departureDateTime != null) {
            this.departureDateTime = Calendar.getInstance();
            this.departureDateTime.setTimeInMillis(departureDateTime.longValue());
        }

        return this;
    }

    public TravelProduct getCore() {
        return this;
    }

    public UUID getProductUUID() {
        return productId;
    }

    public UUID getProposalUUID() {
        return proposalId;
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.NULL;
    }
}
