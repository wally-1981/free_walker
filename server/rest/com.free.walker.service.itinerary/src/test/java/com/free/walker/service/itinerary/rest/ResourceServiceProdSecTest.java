package com.free.walker.service.itinerary.rest;

import org.junit.Before;

public class ResourceServiceProdSecTest extends AbstractResourceServiceTest {
    @Before
    public void before() {
        resourceServiceUrlStr = getProdSecureServiceUrl(ResourceService.class);
        super.before();
    }
}
