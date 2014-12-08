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
import com.free.walker.service.itinerary.basic.Hotel;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.util.UuidUtil;
import com.ibm.icu.util.Calendar;

public class HotelItem extends TravelProductItem {
    public static final String SUB_TYPE;

    static {
        String[] names = StringUtils.splitByCharacterTypeCamelCase(HotelItem.class.getSimpleName());
        SUB_TYPE = StringUtils.join(names, '_', 0, names.length - 1).toLowerCase();
    }

    private Hotel hotel;
    private Calendar arrival;
    private Calendar departure;

    public HotelItem() {
        super();
    }

    public HotelItem(TravelProduct travelProduct, Hotel hotel, Calendar arrival, Calendar departure) {
        super(travelProduct);

        if (hotel == null || arrival == null || departure == null) {
            throw new NullPointerException();
        }

        if (arrival.fieldDifference(departure.getTime(), Calendar.DATE) < 1) {
            throw new IllegalArgumentException();
        }

        this.hotel = hotel;
        this.arrival = arrival;
        this.departure = departure;
    }

    public double getCost() {
        long nights = arrival.fieldDifference(departure.getTime(), Calendar.DATE);
        return super.getCost() + hotel.getPrice() * nights;
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
        resBuilder.add(Introspection.JSONKeys.ARRIVAL_DATETIME, arrival.getTimeInMillis());
        resBuilder.add(Introspection.JSONKeys.DEPARTURE_DATETIME, departure.getTimeInMillis());
        resBuilder.add(Introspection.JSONKeys.HOTEL, hotel.toJSON());
        return resBuilder.build();
    }

    public HotelItem newFromJSON(JsonObject jsObject) throws JsonException {
        String uuidStr = jsObject.getString(Introspection.JSONKeys.UUID, null);
        if (uuidStr == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.UUID, uuidStr));
        } else {
            uuid = UuidUtil.fromUuidStr(uuidStr);
        }

        return fromJSON(jsObject);
    }

    public HotelItem fromJSON(JsonObject jsObject) throws JsonException {
        String subType = jsObject.getString(Introspection.JSONKeys.SUB_TYPE, null);
        if (subType == null || !SUB_TYPE.equals(subType)) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.SUB_TYPE, subType));
        }

        JsonNumber arrivalDateTime = jsObject.getJsonNumber(Introspection.JSONKeys.ARRIVAL_DATETIME);
        if (arrivalDateTime == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.ARRIVAL_DATETIME, arrivalDateTime));
        } else {
            Calendar arrival = Calendar.getInstance();
            arrival.setTimeInMillis(arrivalDateTime.longValue());
            this.arrival = arrival;
        }

        JsonNumber departureDateTime = jsObject.getJsonNumber(Introspection.JSONKeys.DEPARTURE_DATETIME);
        if (departureDateTime == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.DEPARTURE_DATETIME, departureDateTime));
        } else {
            Calendar departure = Calendar.getInstance();
            departure.setTimeInMillis(departureDateTime.longValue());
            this.departure = departure;
        }

        JsonObject hotelJs = jsObject.getJsonObject(Introspection.JSONKeys.HOTEL);
        if (hotelJs == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.HOTEL, hotelJs));
        } else {
            this.hotel = new Hotel().fromJSON(hotelJs);
        }

        return this;
    }
}
