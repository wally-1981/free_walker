package com.free.walker.service.itinerary;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.free.walker.service.itinerary.rest.ItineraryServiceDevoTest;
import com.free.walker.service.itinerary.rest.PlatformServiceDevoTest;
import com.free.walker.service.itinerary.rest.ProductServiceDevoTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    PlatformServiceDevoTest.class,
    ItineraryServiceDevoTest.class,
    ProductServiceDevoTest.class,
})
public class AllDevoServiceTestSuite {}
