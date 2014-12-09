package com.free.walker.service.itinerary;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.free.walker.service.itinerary.rest.ItineraryServiceDevoTest;
import com.free.walker.service.itinerary.rest.ItineraryServiceProdTest;
import com.free.walker.service.itinerary.rest.PlatformServiceDevoTest;
import com.free.walker.service.itinerary.rest.PlatformServiceProdTest;
import com.free.walker.service.itinerary.rest.ProductServiceDevoTest;
import com.free.walker.service.itinerary.rest.ProductServiceProdTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    PlatformServiceProdTest.class,
    PlatformServiceDevoTest.class,

    ItineraryServiceProdTest.class,
    ItineraryServiceDevoTest.class,

    ProductServiceProdTest.class,
    ProductServiceDevoTest.class,
})
public class AllServiceTestSuite {}
