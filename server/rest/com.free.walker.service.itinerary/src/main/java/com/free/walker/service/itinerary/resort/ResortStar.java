package com.free.walker.service.itinerary.resort;

public class ResortStar {
    public static final ResortStar STD_5A = new ResortStar(5);
    public static final ResortStar STD_4A = new ResortStar(4);
    public static final ResortStar STD_3A = new ResortStar(3);
    public static final ResortStar STD_2A = new ResortStar(2);

    private int enumValue = 0;

    private ResortStar(int enumValue) {
        if (enumValue <= 0) {
            throw new IllegalArgumentException();
        }

        this.enumValue = enumValue;
    }

    public int enumValue() {
        return enumValue;
    }
}
