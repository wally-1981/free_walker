package com.free.walker.service.itinerary.basic;

import com.free.walker.service.itinerary.Enumable;

public class TravelTimeRange implements Enumable {
    private int start;
    private int offset;

    protected TravelTimeRange(int start, int duration) {
        if (start < 0 || start > 23) {
            throw new IllegalArgumentException();
        }

        if (duration < 0 || duration > 24 || start + duration > 24) {
            throw new IllegalArgumentException();
        }

        this.start = start;
        this.offset = duration;
    }

    public int getStart() {
        return start;
    }

    public int getOffset() {
        return offset;
    }

    public int enumValue() {
        return start;
    }
}
