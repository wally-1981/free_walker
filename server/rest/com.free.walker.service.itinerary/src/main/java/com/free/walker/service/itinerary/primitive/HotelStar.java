package com.free.walker.service.itinerary.primitive;

import com.free.walker.service.itinerary.Enumable;

public class HotelStar implements Enumable {
    private int enumValue = 0;

    protected HotelStar(int enumValue) {
        if (enumValue <= 0) {
            throw new IllegalArgumentException();
        }

        this.enumValue = enumValue;
    }

    public int enumValue() {
        return enumValue;
    }
}
