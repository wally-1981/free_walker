package com.free.walker.service.itinerary.primitive;

import com.free.walker.service.itinerary.Enumable;

public class ContinentID implements Enumable {
    private int enumValue = 0;

    protected ContinentID(int enumValue) {
        if (enumValue <= 0 || enumValue >= 8) {
            throw new IllegalArgumentException();
        }

        this.enumValue = enumValue;
    }

    public int enumValue() {
        return enumValue;
    }
}
