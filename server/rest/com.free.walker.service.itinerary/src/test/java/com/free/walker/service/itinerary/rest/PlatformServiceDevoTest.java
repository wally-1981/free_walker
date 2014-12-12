package com.free.walker.service.itinerary.rest;

import org.junit.Before;

public class PlatformServiceDevoTest extends AbstractPlatformServiceTest {
    @Before
    public void before() {
        platformServiceUrlStr = getDevoServiceUrl(PlatformService.class);
        super.before();
    }
}
