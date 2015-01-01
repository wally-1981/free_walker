package com.free.walker.service.itinerary.infra;

import com.free.walker.service.itinerary.Loadable;
import com.free.walker.service.itinerary.basic.City;
import com.free.walker.service.itinerary.basic.Country;
import com.free.walker.service.itinerary.basic.Province;
import com.free.walker.service.itinerary.basic.Region;
import com.free.walker.service.itinerary.basic.Tag;
import com.free.walker.service.itinerary.task.TagRefreshTask;

public class PlatformInitializer {
    public static void init() {
        Loadable regionLoader = new Region();
        if (!regionLoader.load()) {
            throw new IllegalStateException();
        }

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

        Loadable tagLoader = new Tag();
        if (!tagLoader.load()) {
            throw new IllegalStateException();
        }

        try {
            TagRefreshTask.schedule();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
