package com.free.walker.service.itinerary;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.free.walker.service.itinerary.rest.ItineraryServiceDevoTest;
import com.free.walker.service.itinerary.rest.ItineraryServiceProdTest;
import com.free.walker.service.itinerary.rest.ProductServiceDevoTest;
import com.free.walker.service.itinerary.rest.ProductServiceProdTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    ItineraryServiceProdTest.class,
    ItineraryServiceDevoTest.class,

    ProductServiceProdTest.class,
    ProductServiceDevoTest.class,
})
public class AllServiceTestSuite {}
