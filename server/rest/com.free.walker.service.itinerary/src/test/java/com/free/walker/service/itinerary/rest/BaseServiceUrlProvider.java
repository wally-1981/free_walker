package com.free.walker.service.itinerary.rest;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class BaseServiceUrlProvider implements ServiceConfigurationProvider {
    public String getProdServiceUrl(Class<?> aClass) {
        if (ProductService.class.equals(aClass)) {
            return ServiceConfigurationProvider.PRODUCT_PROD_LOCAL_URL;
        } else if (ItineraryService.class.equals(aClass)) {
            return ServiceConfigurationProvider.ITINERARY_PROD_LOCAL_URL;
        } else if (PlatformService.class.equals(aClass)) {
            return ServiceConfigurationProvider.PLATFORM_PROD_LOCAL_URL;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public String getDevoServiceUrl(Class<?> aClass) {
        if (ProductService.class.equals(aClass)) {
            return ServiceConfigurationProvider.PRODUCT_DEVO_LOCAL_URL;
        } else if (ItineraryService.class.equals(aClass)) {
            return ServiceConfigurationProvider.ITINERARY_DEVO_LOCAL_URL;
        } else if (PlatformService.class.equals(aClass)) {
            return ServiceConfigurationProvider.PLATFORM_DEVO_LOCAL_URL;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public String getProdSecureServiceUrl(Class<?> aClass) {
        if (ProductService.class.equals(aClass)) {
            return ServiceConfigurationProvider.PRODUCT_PROD_LOCAL_SEC_URL;
        } else if (ItineraryService.class.equals(aClass)) {
            return ServiceConfigurationProvider.ITINERARY_PROD_LOCAL_SEC_URL;
        } else if (PlatformService.class.equals(aClass)) {
            return ServiceConfigurationProvider.PLATFORM_PROD_LOCAL_SEC_URL;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public String getDevoSecureServiceUrl(Class<?> aClass) {
        if (ProductService.class.equals(aClass)) {
            return ServiceConfigurationProvider.PRODUCT_DEVO_LOCAL_SEC_URL;
        } else if (ItineraryService.class.equals(aClass)) {
            return ServiceConfigurationProvider.ITINERARY_DEVO_LOCAL_SEC_URL;
        } else if (PlatformService.class.equals(aClass)) {
            return ServiceConfigurationProvider.PLATFORM_DEVO_LOCAL_SEC_URL;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public URL getSSLKeyStoreURL() throws MalformedURLException {
        return new URL("file:keystore/ClientKeystore.jks");
    }

    public URL getSSLTrustStoreURL() throws MalformedURLException {
        return new URL("file:keystore/ClientKeystore.jks");
    }

    public char[] getSSLStorePassword() {
        return "cspass".toCharArray();
    }

    public char[] getSSLKeyPassword() {
        return "ckpass".toCharArray();
    }
}
