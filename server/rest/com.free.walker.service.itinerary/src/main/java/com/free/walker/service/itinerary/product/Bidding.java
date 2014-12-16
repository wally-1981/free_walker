package com.free.walker.service.itinerary.product;

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
import com.free.walker.service.itinerary.Serializable;
import com.free.walker.service.itinerary.primitive.Introspection;

public class Bidding extends TravelProductItem {
    private BiddingItem[] biddingItems;

    public Bidding(){
        super();
    }

    public Bidding(TravelProduct travelProduct) {
        super(travelProduct);
    }

    public Bidding(TravelProduct travelProduct, BiddingItem[] biddingItems) {
        super(travelProduct);

        if (travelProduct == null || biddingItems == null) {
            throw new NullPointerException();
        }

        if (biddingItems.length == 0) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < biddingItems.length; i++) {
            if (i + 1 < biddingItems.length && biddingItems[i].getMax() >= biddingItems[i + 1].getMin()) {
                throw new IllegalArgumentException(LocalMessages.getMessage(LocalMessages.conflict_bidding_item_range,
                    biddingItems[i].getMax(), biddingItems[i + 1].getMin()));
            }
        }

        this.biddingItems = biddingItems;
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.NULL;
    }

    public String getType() {
        return "";
    }

    public UUID getUUID() {
        return null;
    }

    public List<TravelProductItem> getTravelProductItems() {
        return travelProduct.getTravelProductItems();
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();

        JsonArrayBuilder itemsBuilder = Json.createArrayBuilder();
        for (int i = 0; i < biddingItems.length; i++) {
            itemsBuilder.add(biddingItems[i].toJSON());
        }
        resBuilder.add(Introspection.JSONKeys.BIDDING, itemsBuilder);

        return resBuilder.build();
    }

    public Bidding newFromJSON(JsonObject jsObject) throws JsonException {
        return fromJSON(jsObject);
    }

    public Bidding fromJSON(JsonObject jsObject) throws JsonException {
        JsonArray biddingJs = jsObject.getJsonArray(Introspection.JSONKeys.BIDDING);
        if (biddingJs == null) {
            this.biddingItems = new BiddingItem[] {};
        } else {
            this.biddingItems = new BiddingItem[biddingJs.size()];
            for (int i = 0; i < biddingJs.size(); i++) {
                JsonObject item = biddingJs.getJsonObject(i);
                if (item.getJsonString(Introspection.JSONKeys.TITLE) == null) {
                    this.biddingItems[i] = new BiddingItem().fromJSON(item);
                } else {
                    this.biddingItems[i] = new BiddingExtra().fromJSON(item);
                }
            }
        }

        return this;
    }

    public static class BiddingItem implements Serializable {
        protected int min;
        protected int max;
        protected double price;

        private BiddingItem() {
            ;
        }

        public BiddingItem(double price) {
            this(1, Integer.MAX_VALUE, price);
        }

        public BiddingItem(int min, double price) {
            this(min, Integer.MAX_VALUE, price);
        }

        public BiddingItem(int min, int max, double price) {
            if (min < 1 || max < 1 || price <= 0) {
                throw new IllegalArgumentException();
            }

            if (min > max) {
                throw new IllegalArgumentException(LocalMessages.getMessage(LocalMessages.invalid_bidding_item_range,
                    min, max));
            }

            this.min = min;
            this.max = max;
            this.price = price;
        }

        public ValueType getValueType() {
            return JsonValue.ValueType.NULL;
        }

        public JsonObject toJSON() throws JsonException {
            JsonObjectBuilder resBuilder = Json.createObjectBuilder();
            resBuilder.add(Introspection.JSONKeys.MIN, min);
            resBuilder.add(Introspection.JSONKeys.MAX, max);
            resBuilder.add(Introspection.JSONKeys.PRICE, price);
            return resBuilder.build();
        }

        public BiddingItem fromJSON(JsonObject jsObject) throws JsonException {
            int min = jsObject.getInt(Introspection.JSONKeys.MIN, 0);
            if (min < 1) {
                this.min = 1;
            } else {
                this.min = min;
            }

            int max = jsObject.getInt(Introspection.JSONKeys.MAX, 0);
            if (max < 1) {
                this.max = Integer.MAX_VALUE;
            } else {
                this.max = max;
            }

            if (this.min > this.max) {
                throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_bidding_item_range, min, max));
            }

            JsonNumber price = jsObject.getJsonNumber(Introspection.JSONKeys.PRICE);
            if (price == null) {
                throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                    Introspection.JSONKeys.PRICE, price));
            } else if (price.doubleValue() <= 0) {
                throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                    Introspection.JSONKeys.PRICE, price.doubleValue()));
            } else {
                this.price = price.doubleValue();
            }

            return this;
        }

        private int getMin() {
            return min;
        }

        private int getMax() {
            return max;
        }
    }

    public static class BiddingExtra extends BiddingItem {
        private String title;

        public BiddingExtra() {
            ;
        }

        public BiddingExtra(String title, double price) {
            super(1, Integer.MAX_VALUE, Math.abs(price));

            if (title == null) {
                throw new NullPointerException();
            }

            if (title.trim().length() == 0) {
                throw new IllegalArgumentException();
            }

            this.title = title;
            this.price = price;
        }

        public ValueType getValueType() {
            return JsonValue.ValueType.NULL;
        }

        public JsonObject toJSON() throws JsonException {
            JsonObjectBuilder resBuilder = Json.createObjectBuilder();
            resBuilder.add(Introspection.JSONKeys.TITLE, title);
            resBuilder.add(Introspection.JSONKeys.PRICE, price);
            return resBuilder.build();
        }

        public BiddingExtra fromJSON(JsonObject jsObject) throws JsonException {
            String title = jsObject.getString(Introspection.JSONKeys.TITLE, null);
            if (title == null || title.trim().length() == 0) {
                throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                    Introspection.JSONKeys.TITLE, title));
            } else {
                this.title = title;
            }

            JsonNumber price = jsObject.getJsonNumber(Introspection.JSONKeys.PRICE);
            if (price == null) {
                throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                    Introspection.JSONKeys.PRICE, price));
            } else {
                this.price = price.doubleValue();
            }

            this.min = 1;
            this.max = Integer.MAX_VALUE;

            return this;
        }
    }
}
