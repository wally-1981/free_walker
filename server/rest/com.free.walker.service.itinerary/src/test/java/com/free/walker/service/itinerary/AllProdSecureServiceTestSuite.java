package com.free.walker.service.itinerary;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.free.walker.service.itinerary.rest.AccountServiceProdSecTest;
import com.free.walker.service.itinerary.rest.ItineraryServiceProdSecTest;
import com.free.walker.service.itinerary.rest.PlatformServiceProdSecTest;
import com.free.walker.service.itinerary.rest.ProductServiceProdSecTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    AccountServiceProdSecTest.class,
    PlatformServiceProdSecTest.class,
    ItineraryServiceProdSecTest.class,
    ProductServiceProdSecTest.class,
})
public class AllProdSecureServiceTestSuite {}
