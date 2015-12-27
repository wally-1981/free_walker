package com.free.walker.service.itinerary.rest;

import java.net.MalformedURLException;
import java.net.URL;

public interface ServiceConfigurationProvider {
    public static final String PROD_PORT = "9000";
    public static final String PROD_SEC_PORT = "9001";
    public static final String DEVO_PORT = "9010";
    public static final String DEVO_SEC_PORT = "9011";

    public static final String DEVO_CLIENT_PORT = "4200";
    public static final String DEVO_CLIENT_SEC_PORT = "4300";

    public static final String[] ALLOWED_ORIGINS = new String[] {
        "http://localhost",
        "https://localhost",
        "http://localhost:" + DEVO_CLIENT_PORT,
        "https://localhost:" + DEVO_CLIENT_SEC_PORT
    };

    public abstract String getProdServiceUrl(Class<?> aClass);

    public abstract String getDevoServiceUrl(Class<?> aClass);

    public abstract String getProdSecureServiceUrl(Class<?> aClass);

    public abstract String getDevoSecureServiceUrl(Class<?> aClass);

    public abstract URL getSSLKeyStoreURL() throws MalformedURLException;

    public abstract URL getSSLTrustStoreURL() throws MalformedURLException;

    public abstract char[] getSSLStorePassword();

    public abstract char[] getSSLKeyPassword();
}
