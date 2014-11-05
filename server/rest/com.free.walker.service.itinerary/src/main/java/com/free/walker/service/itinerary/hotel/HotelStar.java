package com.free.walker.service.itinerary.hotel;

public class HotelStar {
    public static final HotelStar STD_5 = new HotelStar(50);
    public static final HotelStar STD_4 = new HotelStar(40);
    public static final HotelStar STD_3 = new HotelStar(30);
    public static final HotelStar STD_2 = new HotelStar(20);

    public static final HotelStar LST_5 = new HotelStar(5);
    public static final HotelStar LST_4 = new HotelStar(4);
    public static final HotelStar LST_3 = new HotelStar(3);

    private int enumValue = 0;

    private HotelStar(int enumValue) {
        if (enumValue <= 0) {
            throw new IllegalArgumentException();
        }

        this.enumValue = enumValue;
    }

    public int enumValue() {
        return enumValue;
    }
}
