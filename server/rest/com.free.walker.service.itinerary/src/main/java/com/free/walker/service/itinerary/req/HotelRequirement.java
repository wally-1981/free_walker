package com.free.walker.service.itinerary.req;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.commons.lang3.StringUtils;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.Hotel;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.primitive.HotelStar;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.util.UuidUtil;
import com.ibm.icu.util.Calendar;

public class HotelRequirement extends BaseTravelRequirement implements TravelRequirement {
    public static final String SUB_TYPE;

    static {
        String[] names = StringUtils.splitByCharacterTypeCamelCase(HotelRequirement.class.getSimpleName());
        SUB_TYPE = StringUtils.join(names, '_', 0, names.length - 1).toLowerCase();
    }

    private int nights;
    private HotelStar star;
    private Hotel hotel;
    private Calendar arrivalDateTime;

    public HotelRequirement() {
        ;
    }

    public HotelRequirement(int nights) {
        super();

        if (nights <= 0) {
            throw new IllegalArgumentException(LocalMessages.getMessage(LocalMessages.illegal_hotel_nights, nights));
        }

        this.nights = nights;
    }

    public HotelRequirement(int nights, HotelStar star) {
        this(nights);

        if (star == null) {
            throw new NullPointerException();
        }

        this.star = star;
    }

    public HotelRequirement(int nights, Hotel hotel, Calendar arrivalDateTime) {
        this(nights);

        if (hotel == null || arrivalDateTime == null) {
            throw new NullPointerException();
        }

        this.hotel = hotel;
        this.arrivalDateTime = arrivalDateTime;
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Introspection.JSONKeys.UUID, getUUID().toString());
        resBuilder.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT);

        resBuilder.add(Introspection.JSONKeys.SUB_TYPE, SUB_TYPE);
        resBuilder.add(Introspection.JSONKeys.NIGHT, nights);

        if (star != null) {
            resBuilder.add(Introspection.JSONKeys.STAR, star.enumValue());
        }

        if (hotel != null) {
            JsonObject hotelJSON = hotel.toJSON();
            resBuilder.add(Introspection.JSONKeys.HOTEL, hotelJSON);
        }

        if (arrivalDateTime != null) {
            resBuilder.add(Introspection.JSONKeys.ARRIVAL_DATETIME, arrivalDateTime.getTimeInMillis());
        }

        return resBuilder.build();
    }

    public HotelRequirement fromJSON(JsonObject jsObject) throws JsonException {
        String requirementId = jsObject.getString(Introspection.JSONKeys.UUID, null);

        if (requirementId != null) {
            this.requirementId = UuidUtil.fromUuidStr(requirementId);
        } else {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.UUID, requirementId));
        }

        return newFromJSON(jsObject);
    }

    public HotelRequirement newFromJSON(JsonObject jsObject) throws JsonException {
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

        int nights = jsObject.getInt(Introspection.JSONKeys.NIGHT, 0);
        if (nights > 0) {
            this.nights = nights;
        } else {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.NIGHT, nights));
        }

        int star = jsObject.getInt(Introspection.JSONKeys.STAR, 0);
        if (star > 0) {
            try {
                this.star = Introspection.JsonValueHelper.getHotelStar(star);
            } catch (InvalidTravelReqirementException e) {
                throw new JsonException(e.getMessage(), e);
            }
        }

        JsonObject hotelObj = jsObject.getJsonObject(Introspection.JSONKeys.HOTEL);
        if (hotelObj != null) {
            this.hotel = (Hotel) new Hotel().fromJSON(hotelObj);
        }

        JsonNumber arrivalDateTime = jsObject.getJsonNumber(Introspection.JSONKeys.ARRIVAL_DATETIME);
        if (arrivalDateTime != null) {
            this.arrivalDateTime = Calendar.getInstance();
            this.arrivalDateTime.setTimeInMillis(arrivalDateTime.longValue());
        }

        return this;
    }
}
