package com.free.walker.service.itinerary;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.free.walker.service.itinerary.rest.ItineraryServiceDevoTest;
import com.free.walker.service.itinerary.rest.ItineraryServiceProdTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    ItineraryServiceProdTest.class,
    ItineraryServiceDevoTest.class,
})
public class AllServiceTestSuite {}
