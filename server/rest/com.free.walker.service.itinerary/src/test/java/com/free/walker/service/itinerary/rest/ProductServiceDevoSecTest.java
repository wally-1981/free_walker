package com.free.walker.service.itinerary.rest;

import org.junit.Before;

public class ProductServiceDevoSecTest extends AbstractProductServiceTest {
    @Before
    public void before() {
        itineraryServiceUrlStr = getDevoSecureServiceUrl(ItineraryService.class);
        productServiceUrlStr = getDevoSecureServiceUrl(ProductService.class);
        super.before();
    }
}
