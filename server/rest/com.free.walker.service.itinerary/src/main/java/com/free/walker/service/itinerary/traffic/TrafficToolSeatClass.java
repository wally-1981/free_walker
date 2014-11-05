package com.free.walker.service.itinerary.traffic;

public class TrafficToolSeatClass {
    public static final TrafficToolSeatClass CLASS_1ST = new TrafficToolSeatClass(30);
    public static final TrafficToolSeatClass CLASS_2ND = new TrafficToolSeatClass(20);
    public static final TrafficToolSeatClass CLASS_3RD = new TrafficToolSeatClass(10);

    private int enumValue = 0;

    private TrafficToolSeatClass(int enumValue) {
        if (enumValue <= 0) {
            throw new IllegalArgumentException();
        }

        this.enumValue = enumValue;
    }

    public int enumValue() {
        return enumValue;
    }
}
