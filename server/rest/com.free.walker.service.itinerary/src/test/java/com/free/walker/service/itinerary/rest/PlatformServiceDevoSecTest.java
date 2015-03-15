package com.free.walker.service.itinerary.rest;

import org.junit.Before;

public class PlatformServiceDevoSecTest extends AbstractPlatformServiceTest {
    @Before
    public void before() {
        platformServiceUrlStr = getDevoSecureServiceUrl(PlatformService.class);
        super.before();
    }
}
