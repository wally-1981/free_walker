package com.free.walker.service.itinerary.rest;

import org.junit.Before;

public class ItineraryServiceProdTest extends AbstractItineraryServiceTest {
    @Before
    public void before() {
        itineraryServiceUrlStr = getProdServiceUrl(ItineraryService.class);
        super.before();
    }
}
