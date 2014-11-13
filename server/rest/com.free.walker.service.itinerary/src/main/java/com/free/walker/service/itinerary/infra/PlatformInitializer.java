package com.free.walker.service.itinerary.infra;

import com.free.walker.service.itinerary.Loadable;
import com.free.walker.service.itinerary.basic.City;
import com.free.walker.service.itinerary.basic.Country;
import com.free.walker.service.itinerary.basic.Province;

public class PlatformInitializer {
    public static void init() {
        Loadable countryLoader = new Country();
        if (!countryLoader.load()) {
            throw new IllegalStateException();
        }

        Loadable provinceLoader = new Province();
        if (!provinceLoader.load()) {
            throw new IllegalStateException();
        }

        Loadable cityLoader = new City();
        if (!cityLoader.load()) {
            throw new IllegalStateException();
        }
    }
}
