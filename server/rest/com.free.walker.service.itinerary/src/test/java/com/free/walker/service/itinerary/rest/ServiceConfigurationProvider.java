package com.free.walker.service.itinerary.rest;

import java.net.MalformedURLException;
import java.net.URL;

import com.free.walker.service.itinerary.util.LocalNetworkInterfaceUtil;
import com.ibm.icu.text.MessageFormat;

public interface ServiceConfigurationProvider {
    public static final String PROD_PORT = "9000";
    public static final String PROD_SEC_PORT = "9001";
    public static final String DEVO_PORT = "9010";
    public static final String DEVO_SEC_PORT = "9011";

    public static final String PLATFORM_PROD_LOCAL_URL = MessageFormat.format("http://{0}:{1}/service/platform/",
        LocalNetworkInterfaceUtil.getLocalIPAddress(), PROD_PORT);
    public static final String PLATFORM_PROD_LOCAL_SEC_URL = MessageFormat.format("https://{0}:{1}/service/platform/",
        LocalNetworkInterfaceUtil.getLocalIPAddress(), PROD_SEC_PORT);
    public static final String PLATFORM_DEVO_LOCAL_URL = MessageFormat.format("http://{0}:{1}/service/platform/",
        LocalNetworkInterfaceUtil.getLocalIPAddress(), DEVO_PORT);
    public static final String PLATFORM_DEVO_LOCAL_SEC_URL = MessageFormat.format("https://{0}:{1}/service/platform/",
        LocalNetworkInterfaceUtil.getLocalIPAddress(), DEVO_SEC_PORT);

    public static final String ITINERARY_PROD_LOCAL_URL = MessageFormat.format("http://{0}:{1}/service/itinerary/",
        LocalNetworkInterfaceUtil.getLocalIPAddress(), PROD_PORT);
    public static final String ITINERARY_PROD_LOCAL_SEC_URL = MessageFormat.format("https://{0}:{1}/service/itinerary/",
        LocalNetworkInterfaceUtil.getLocalIPAddress(), PROD_SEC_PORT);
    public static final String ITINERARY_DEVO_LOCAL_URL = MessageFormat.format("http://{0}:{1}/service/itinerary/",
        LocalNetworkInterfaceUtil.getLocalIPAddress(), DEVO_PORT);
    public static final String ITINERARY_DEVO_LOCAL_SEC_URL = MessageFormat.format("https://{0}:{1}/service/itinerary/",
        LocalNetworkInterfaceUtil.getLocalIPAddress(), DEVO_SEC_PORT);

    public static final String PRODUCT_PROD_LOCAL_URL = MessageFormat.format("http://{0}:{1}/service/product/",
        LocalNetworkInterfaceUtil.getLocalIPAddress(), PROD_PORT);
    public static final String PRODUCT_PROD_LOCAL_SEC_URL = MessageFormat.format("https://{0}:{1}/service/product/",
        LocalNetworkInterfaceUtil.getLocalIPAddress(), PROD_SEC_PORT);
    public static final String PRODUCT_DEVO_LOCAL_URL = MessageFormat.format("http://{0}:{1}/service/product/",
        LocalNetworkInterfaceUtil.getLocalIPAddress(), DEVO_PORT);
    public static final String PRODUCT_DEVO_LOCAL_SEC_URL = MessageFormat.format("https://{0}:{1}/service/product/",
        LocalNetworkInterfaceUtil.getLocalIPAddress(), DEVO_SEC_PORT);

    public abstract String getProdServiceUrl(Class<?> aClass);

    public abstract String getDevoServiceUrl(Class<?> aClass);

    public abstract String getProdSecureServiceUrl(Class<?> aClass);

    public abstract String getDevoSecureServiceUrl(Class<?> aClass);

    public abstract URL getSSLKeyStoreURL() throws MalformedURLException;

    public abstract URL getSSLTrustStoreURL() throws MalformedURLException;

    public abstract char[] getSSLStorePassword();

    public abstract char[] getSSLKeyPassword();
}
