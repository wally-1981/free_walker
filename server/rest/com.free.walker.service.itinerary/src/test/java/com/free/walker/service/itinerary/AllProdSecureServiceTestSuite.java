package com.free.walker.service.itinerary;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.free.walker.service.itinerary.rest.AccountServiceProdSecTest;
import com.free.walker.service.itinerary.rest.ItineraryServiceProdCustSecTest;
import com.free.walker.service.itinerary.rest.ItineraryServiceProdSecTest;
import com.free.walker.service.itinerary.rest.PlatformServiceProdSecTest;
import com.free.walker.service.itinerary.rest.ProductServiceProdCustSecTest;
import com.free.walker.service.itinerary.rest.ProductServiceProdSecTest;
import com.free.walker.service.itinerary.rest.ResourceServiceProdSecTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    AccountServiceProdSecTest.class,
    ResourceServiceProdSecTest.class,
    PlatformServiceProdSecTest.class,
    ItineraryServiceProdSecTest.class,
    ProductServiceProdSecTest.class,

    ItineraryServiceProdCustSecTest.class,
    ProductServiceProdCustSecTest.class,
})
public class AllProdSecureServiceTestSuite {}
