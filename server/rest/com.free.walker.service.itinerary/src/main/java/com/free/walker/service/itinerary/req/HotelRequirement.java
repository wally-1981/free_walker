package com.free.walker.service.itinerary.req;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.Hotel;
import com.free.walker.service.itinerary.hotel.HotelStar;

public class HotelRequirement extends BaseTravelRequirement implements TravelRequirement {
    private int nights;
    private HotelStar hotelStar;
    private Hotel hotel;

    public HotelRequirement() {
        super();
    }

    public HotelRequirement(int nights) {
        super();

        if (nights <= 0) {
            throw new IllegalArgumentException(LocalMessages.getMessage(LocalMessages.illegal_hotel_nights, nights));
        }

        this.nights = nights;
    }

    public HotelRequirement(int nights, HotelStar hotelStar) {
        this(nights);

        if (hotelStar == null) {
            throw new NullPointerException();
        }

        this.hotelStar = hotelStar;
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
        resBuilder.add(Constants.JSONKeys.UUID, requirementId.toString());
        resBuilder.add(Constants.JSONKeys.TYPE, Constants.JSONKeys.REQUIREMENT);
        resBuilder.add(Constants.JSONKeys.NIGHT, nights);

        if (hotelStar != null) {
            resBuilder.add(Constants.JSONKeys.STAR, hotelStar.enumValue());
        }

        if (hotel != null) {
            JsonObject hotelJSON = hotel.toJSON();
            resBuilder.add(Constants.JSONKeys.HOTEL, hotelJSON);
        }

        return resBuilder.build();
    }
}
