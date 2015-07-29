package com.free.walker.service.itinerary.primitive;

import com.free.walker.service.itinerary.Enumable;

public class ContinentID implements Enumable {
    private int enumValue = 0;

    public static boolean isValid(int enumValue) {
        if (enumValue <= 0 || enumValue >= 8) {
            return false;
        }
        return true;
    }

    protected ContinentID(int enumValue) {
        if (!isValid(enumValue)) {
            throw new IllegalArgumentException();
        }

        this.enumValue = enumValue;
    }

    public int enumValue() {
        return enumValue;
    }
}
