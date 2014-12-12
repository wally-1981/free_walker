package com.free.walker.service.itinerary.rest;

import org.junit.Before;

public class ProductServiceDevoTest extends AbstractProductServiceTest {
    @Before
    public void before() {
        itineraryServiceUrlStr = getDevoServiceUrl(ItineraryService.class);
        productServiceUrlStr = getDevoServiceUrl(ProductService.class);
        super.before();
    }
}
