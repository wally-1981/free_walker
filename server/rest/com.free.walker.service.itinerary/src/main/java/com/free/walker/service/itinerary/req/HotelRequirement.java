package com.free.walker.service.itinerary.req;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.commons.lang3.StringUtils;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.Hotel;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.primitive.HotelStar;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.util.UuidUtil;
public class HotelRequirement extends BaseTravelRequirement implements TravelRequirement {
    public static final String SUB_TYPE;

    static {
        String[] names = StringUtils.splitByCharacterTypeCamelCase(HotelRequirement.class.getSimpleName());
        SUB_TYPE = StringUtils.join(names, '_', 0, names.length - 1).toLowerCase();
    }

    private int nights;
    private HotelStar star;
    private Hotel hotel;

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

    public HotelRequirement(int nights, Hotel hotel) {
        this(nights);

        if (hotel == null) {
            throw new NullPointerException();
        }

        this.hotel = hotel;
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

        return resBuilder.build();
    }

    public Object fromJSON(JsonObject jsObject) throws JsonException {
        String requirementId = jsObject.getString(Introspection.JSONKeys.UUID);

        if (requirementId != null) {
            try {
                this.requirementId = UuidUtil.fromUuidStr(requirementId);
            } catch (InvalidTravelReqirementException e) {
                throw new JsonException(e.getMessage(), e);
            }            
        }

        String type = jsObject.getString(Introspection.JSONKeys.TYPE);
        if (type != null && !Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT.equals(type)) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.TYPE, type));
        }

        String subType = jsObject.getString(Introspection.JSONKeys.SUB_TYPE);
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
        if (hotel != null) {
            this.hotel = (Hotel) new Hotel().fromJSON(hotelObj);
        }

        return this;
    }
}
