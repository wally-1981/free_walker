package com.free.walker.service.itinerary;

import com.free.walker.service.itinerary.basic.City;
import com.free.walker.service.itinerary.basic.Country;
import com.ibm.icu.util.ULocale;

public class Constants {
    public static final Country CHINA = new Country(ULocale.CHINA);
    public static final Country US = new Country(ULocale.US);
    public static final Country UK = new Country(ULocale.UK);
    public static final Country CANADA = new Country(ULocale.CANADA);

    public static final City BEIJING = new City("Beijing", CHINA);
    public static final City WUHAN = new City("Wuhan", CHINA);
    public static final City LONDON = new City("London", UK);
    public static final City LA = new City("LA", US);
    public static final City BOSTON = new City("BOSTON", US);
}
