package com.free.walker.service.itinerary.rest;

public interface ServiceUrlProvider {
    public static final String PLATFORM_PROD_LOCAL_URL = "http://localhost:9000/service/platform/";
    public static final String PLATFORM_DEVO_LOCAL_URL = "http://localhost:9010/service/platform/";

    public static final String ITINERARY_PROD_LOCAL_URL = "http://localhost:9000/service/itinerary/";
    public static final String ITINERARY_DEVO_LOCAL_URL = "http://localhost:9010/service/itinerary/";

    public static final String PRODUCT_PROD_LOCAL_URL = "http://localhost:9000/service/product/";
    public static final String PRODUCT_DEVO_LOCAL_URL = "http://localhost:9010/service/product/";

    public abstract String getServiceUrl();
}
