package com.free.walker.service.itinerary;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.free.walker.service.itinerary.rest.AccountServiceProdTest;
import com.free.walker.service.itinerary.rest.ItineraryServiceProdCustTest;
import com.free.walker.service.itinerary.rest.ItineraryServiceProdTest;
import com.free.walker.service.itinerary.rest.PlatformServiceProdTest;
import com.free.walker.service.itinerary.rest.ProductServiceProdCustTest;
import com.free.walker.service.itinerary.rest.ProductServiceProdTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    AccountServiceProdTest.class,
    PlatformServiceProdTest.class,
    ItineraryServiceProdTest.class,
    ProductServiceProdTest.class,

    ItineraryServiceProdCustTest.class,
    ProductServiceProdCustTest.class,
})
public class AllProdServiceTestSuite {}
