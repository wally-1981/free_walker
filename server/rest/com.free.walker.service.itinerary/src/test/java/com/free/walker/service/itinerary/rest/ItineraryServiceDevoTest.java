package com.free.walker.service.itinerary.rest;

import org.junit.Before;

public class ItineraryServiceDevoTest extends AbstractItineraryServiceTest {
    @Before
    public void before() {
        itineraryServiceUrlStr = getDevoServiceUrl(ItineraryService.class);
        platformServiceUrlStr = getDevoServiceUrl(PlatformService.class);
        super.before();
    }
}
