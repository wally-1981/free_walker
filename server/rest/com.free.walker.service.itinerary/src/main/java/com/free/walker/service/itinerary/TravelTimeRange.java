package com.free.walker.service.itinerary;

public class TravelTimeRange {
    public static TravelTimeRange RANGE_00_06 = new TravelTimeRange(0, 6);
    public static TravelTimeRange RANGE_06_12 = new TravelTimeRange(6, 6);
    public static TravelTimeRange RANGE_12_18 = new TravelTimeRange(12, 6);
    public static TravelTimeRange RANGE_18_23 = new TravelTimeRange(18, 6);

    private int start;
    private int offset;

    private TravelTimeRange(int start, int duration) {
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
}
