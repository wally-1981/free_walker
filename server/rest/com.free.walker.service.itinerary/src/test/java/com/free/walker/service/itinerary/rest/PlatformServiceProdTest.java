package com.free.walker.service.itinerary.rest;

import org.junit.Before;

public class PlatformServiceProdTest extends AbstractPlatformServiceTest {
    @Before
    public void before() {
        platformServiceUrlStr = getProdServiceUrl(PlatformService.class);
        super.before();
    }
}
