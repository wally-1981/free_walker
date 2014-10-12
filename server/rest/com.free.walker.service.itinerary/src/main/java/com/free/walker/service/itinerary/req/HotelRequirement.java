package com.free.walker.service.itinerary.req;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.Hotel;
import com.free.walker.service.itinerary.hotel.HotelStar;

public class HotelRequirement extends BaseTravelRequirement implements TravelRequirement {
    private int nights;
    private HotelStar hotelStar;
    private Hotel hotel;

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

    public JSONObject toJSON() throws JSONException {
        JSONObject res = super.toJSON();
        res.put(Constants.JSONKeys.NIGHT, nights);

        if (hotelStar != null) {
            res.put(Constants.JSONKeys.STAR, hotelStar.enumValue());
        }

        if (hotel != null) {
            JSONObject hotelJSON = hotel.toJSON();
            res.put(Constants.JSONKeys.HOTEL, hotelJSON);
        }

        return res;
    }
}
