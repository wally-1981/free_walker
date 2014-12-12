package com.free.walker.service.itinerary.rest;

import org.junit.Before;

public class ProductServiceProdTest extends AbstractProductServiceTest {
    @Before
    public void before() {
        itineraryServiceUrlStr = getProdServiceUrl(ItineraryService.class);
        productServiceUrlStr = getProdServiceUrl(ProductService.class);
        super.before();
    }
}
