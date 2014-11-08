package com.free.walker.service.itinerary.basic;

import com.free.walker.service.itinerary.Enumable;

public class ResortStar implements Enumable {
    private int enumValue = 0;

    protected ResortStar(int enumValue) {
        if (enumValue <= 0) {
            throw new IllegalArgumentException();
        }

        this.enumValue = enumValue;
    }

    public int enumValue() {
        return enumValue;
    }
}
