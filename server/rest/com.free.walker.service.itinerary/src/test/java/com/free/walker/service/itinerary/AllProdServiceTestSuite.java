package com.free.walker.service.itinerary;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.free.walker.service.itinerary.rest.AccountServiceProdTest;
import com.free.walker.service.itinerary.rest.ItineraryServiceProdTest;
import com.free.walker.service.itinerary.rest.PlatformServiceProdTest;
import com.free.walker.service.itinerary.rest.ProductServiceProdTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    AccountServiceProdTest.class,
    PlatformServiceProdTest.class,
    ItineraryServiceProdTest.class,
    ProductServiceProdTest.class,
})
public class AllProdServiceTestSuite {}
