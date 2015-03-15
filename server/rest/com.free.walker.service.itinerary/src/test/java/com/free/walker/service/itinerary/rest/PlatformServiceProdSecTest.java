package com.free.walker.service.itinerary.rest;

import org.junit.Before;

public class PlatformServiceProdSecTest extends AbstractPlatformServiceTest {
    @Before
    public void before() {
        platformServiceUrlStr = getProdSecureServiceUrl(PlatformService.class);
        super.before();
    }
}
