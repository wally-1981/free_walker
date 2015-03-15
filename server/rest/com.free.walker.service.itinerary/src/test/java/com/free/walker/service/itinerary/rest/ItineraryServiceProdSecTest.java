package com.free.walker.service.itinerary.rest;

import org.junit.Before;

public class ItineraryServiceProdSecTest extends AbstractItineraryServiceTest {
    @Before
    public void before() {
        itineraryServiceUrlStr = getProdSecureServiceUrl(ItineraryService.class);
        platformServiceUrlStr = getProdSecureServiceUrl(PlatformService.class);
        super.before();
    }
}
