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
import com.free.walker.service.itinerary.basic.TravelLocation;
import com.free.walker.service.itinerary.exp.InvalidTravelProductException;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.primitive.ProductStatus;
import com.free.walker.service.itinerary.util.JsonObjectHelper;
import com.free.walker.service.itinerary.util.UuidUtil;
import com.ibm.icu.util.Calendar;

public class SimpleTravelProduct implements TravelProduct, Renewable {
    private List<TravelProductItem> travelProductItems;
    private UUID productId;
    private UUID proposalId;
    private int capacity;
    private Calendar deadline;
    private Calendar departure;
    private TravelLocation departureLocation;
    private ProductStatus productStatus;

    public SimpleTravelProduct() {
        this.productId = UUID.randomUUID();
        this.travelProductItems = new LinkedList<TravelProductItem>();
    }

    public SimpleTravelProduct(UUID proposalId, int capacity, Calendar deadline, Calendar departure,
        TravelLocation departureLocation) {
        this();

        if (proposalId == null || deadline == null || departure == null) {
            throw new NullPointerException();
        }

        if (capacity <= 0) {
            throw new IllegalArgumentException(LocalMessages.getMessage(LocalMessages.illegal_product_capacity,
                capacity, productId));
        }

        Calendar now = Calendar.getInstance();
        if (deadline.compareTo(now) < 0) {
            throw new IllegalArgumentException(LocalMessages.getMessage(LocalMessages.illegal_product_capacity,
                deadline, productId, now));
        }

        if (departure.compareTo(deadline) < 0) {
            throw new IllegalArgumentException(LocalMessages.getMessage(LocalMessages.illegal_product_capacity,
                departure, productId, deadline));
        }

        this.proposalId = proposalId;
        this.capacity = capacity;
        this.deadline = deadline;
        this.departure = departure;
        this.departureLocation = departureLocation;
        this.productStatus = Introspection.JSONValues.DRAFT_PRODUCT;
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

        if (capacity > 0) {
            resBuilder.add(Introspection.JSONKeys.GROUP_CAPACITY, capacity);
        }

        if (deadline != null) {
            resBuilder.add(Introspection.JSONKeys.DEADLINE_DATETIME, deadline.getTimeInMillis());
        }

        if (departure != null) {
            resBuilder.add(Introspection.JSONKeys.DEPARTURE_DATETIME, departure.getTimeInMillis());
        }

        if (departureLocation != null) {
            resBuilder.add(Introspection.JSONKeys.DEPARTURE, departureLocation.toJSON());
        }

        if (productStatus != null) {
            resBuilder.add(Introspection.JSONKeys.STATUS, productStatus.enumValue());
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
            this.capacity = sizeUpperLimit;
        }

        JsonNumber enrolmentDeadlineDateTime = jsObject.getJsonNumber(Introspection.JSONKeys.DEADLINE_DATETIME);
        if (enrolmentDeadlineDateTime != null) {
            this.deadline = Calendar.getInstance();
            this.deadline.setTimeInMillis(enrolmentDeadlineDateTime.longValue());
        }

        JsonNumber departureDateTime = jsObject.getJsonNumber(Introspection.JSONKeys.DEPARTURE_DATETIME);
        if (departureDateTime != null) {
            this.departure = Calendar.getInstance();
            this.departure.setTimeInMillis(departureDateTime.longValue());
        }

        JsonObject departureLocation = jsObject.getJsonObject(Introspection.JSONKeys.DEPARTURE);
        if (departureLocation == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.DEPARTURE, productId));
        } else {
            this.departureLocation = new TravelLocation().fromJSON(departureLocation);
        }

        int productEnum = jsObject.getInt(Introspection.JSONKeys.STATUS, 0);
        ProductStatus productStatus = ProductStatus.valueOf(productEnum);
        if (productStatus != null) {
            this.productStatus = productStatus;
        } else {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.STATUS, productEnum));
        }

        return this;
    }

    public TravelProduct getCore() {
        return this;
    }

    public ProductStatus getStatus() {
        return productStatus;
    }

    public void setStatus(ProductStatus productStatus) {
        this.productStatus = productStatus;
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

    public Object adapt(String attributeName, Class<?> attributeType) {
        if (attributeName == null || attributeName.trim().isEmpty() || attributeType == null) {
            return null;
        }

        if (Introspection.JSONKeys.DEPARTURE.equals(attributeName)
            && attributeType.isAssignableFrom(departureLocation.getClass())) {
            return departureLocation;
        } else if (Introspection.JSONKeys.DEADLINE_DATETIME.equals(attributeName)
            && attributeType.isAssignableFrom(deadline.getClass())) {
            return deadline;
        } else {
            return null;
        }
    }
}
