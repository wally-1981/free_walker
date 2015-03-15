package com.free.walker.service.itinerary.rest;

import org.junit.Before;

public class ProductServiceProdSecTest extends AbstractProductServiceTest {
    @Before
    public void before() {
        itineraryServiceUrlStr = getProdSecureServiceUrl(ItineraryService.class);
        productServiceUrlStr = getProdSecureServiceUrl(ProductService.class);
        super.before();
    }
}
