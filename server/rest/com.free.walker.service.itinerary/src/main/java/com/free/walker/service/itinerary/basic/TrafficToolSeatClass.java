package com.free.walker.service.itinerary.basic;

import com.free.walker.service.itinerary.Enumable;

public class TrafficToolSeatClass implements Enumable {
    private int enumValue = 0;

    protected TrafficToolSeatClass(int enumValue) {
        if (enumValue <= 0) {
            throw new IllegalArgumentException();
        }

        this.enumValue = enumValue;
    }

    public int enumValue() {
        return enumValue;
    }
}
