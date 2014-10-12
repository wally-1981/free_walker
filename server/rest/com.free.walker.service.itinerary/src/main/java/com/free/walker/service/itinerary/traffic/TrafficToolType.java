package com.free.walker.service.itinerary.traffic;

public class TrafficToolType {
    public static TrafficToolType FLIGHT = new TrafficToolType(60);
    public static TrafficToolType TRAIN = new TrafficToolType(50);

    private int enumValue = 0;

    private TrafficToolType(int enumValue) {
        if (enumValue <= 0) {
            throw new IllegalArgumentException();
        }

        this.enumValue = enumValue;
    }

    public int enumValue() {
        return enumValue;
    }
}
