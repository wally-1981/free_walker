package com.free.walker.service.itinerary;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.free.walker.service.itinerary.rest.ItineraryServiceDevoSecTest;
import com.free.walker.service.itinerary.rest.PlatformServiceDevoSecTest;
import com.free.walker.service.itinerary.rest.ProductServiceDevoSecTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    PlatformServiceDevoSecTest.class,
    ItineraryServiceDevoSecTest.class,
    ProductServiceDevoSecTest.class,
})
public class AllDevoSecureServiceTestSuite {}
