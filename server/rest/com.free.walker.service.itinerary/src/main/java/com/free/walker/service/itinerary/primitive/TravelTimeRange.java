package com.free.walker.service.itinerary.primitive;

import com.free.walker.service.itinerary.Imaginable;

public class TravelTimeRange implements Imaginable {
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

    public int realValue() {
        return start;
    }

    public int imaginaryValue() {
        return offset;
    }
}
