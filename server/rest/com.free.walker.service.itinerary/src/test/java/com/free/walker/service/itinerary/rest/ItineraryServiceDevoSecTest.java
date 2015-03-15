package com.free.walker.service.itinerary.rest;

import org.junit.Before;

public class ItineraryServiceDevoSecTest extends AbstractItineraryServiceTest {
    @Before
    public void before() {
        itineraryServiceUrlStr = getDevoSecureServiceUrl(ItineraryService.class);
        platformServiceUrlStr = getDevoSecureServiceUrl(PlatformService.class);
        super.before();
    }
}
