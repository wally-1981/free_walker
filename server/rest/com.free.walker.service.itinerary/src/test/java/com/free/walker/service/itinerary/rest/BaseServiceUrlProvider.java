package com.free.walker.service.itinerary.rest;

public abstract class BaseServiceUrlProvider implements ServiceUrlProvider {
    public String getProdServiceUrl(Class<?> aClass) {
        if (ProductService.class.equals(aClass)) {
            return ServiceUrlProvider.PRODUCT_PROD_LOCAL_URL;
        } else if (ItineraryService.class.equals(aClass)) {
            return ServiceUrlProvider.ITINERARY_PROD_LOCAL_URL;
        } else if (PlatformService.class.equals(aClass)) {
            return ServiceUrlProvider.PLATFORM_PROD_LOCAL_URL;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public String getDevoServiceUrl(Class<?> aClass) {
        if (ProductService.class.equals(aClass)) {
            return ServiceUrlProvider.PRODUCT_DEVO_LOCAL_URL;
        } else if (ItineraryService.class.equals(aClass)) {
            return ServiceUrlProvider.ITINERARY_DEVO_LOCAL_URL;
        } else if (PlatformService.class.equals(aClass)) {
            return ServiceUrlProvider.PLATFORM_DEVO_LOCAL_URL;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
