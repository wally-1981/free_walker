package com.free.walker.service.itinerary.rest;

import com.free.walker.service.itinerary.util.LocalNetworkInterfaceUtil;
import com.ibm.icu.text.MessageFormat;

public interface ServiceUrlProvider {
    public static final String PLATFORM_PROD_LOCAL_URL = MessageFormat.format("http://{0}:9000/service/platform/",
        LocalNetworkInterfaceUtil.getLocalIPAddress());
    public static final String PLATFORM_DEVO_LOCAL_URL = MessageFormat.format("http://{0}:9010/service/platform/",
        LocalNetworkInterfaceUtil.getLocalIPAddress());

    public static final String ITINERARY_PROD_LOCAL_URL = MessageFormat.format("http://{0}:9000/service/itinerary/",
        LocalNetworkInterfaceUtil.getLocalIPAddress());
    public static final String ITINERARY_DEVO_LOCAL_URL = MessageFormat.format("http://{0}:9010/service/itinerary/",
        LocalNetworkInterfaceUtil.getLocalIPAddress());

    public static final String PRODUCT_PROD_LOCAL_URL = MessageFormat.format("http://{0}:9000/service/product/",
        LocalNetworkInterfaceUtil.getLocalIPAddress());
    public static final String PRODUCT_DEVO_LOCAL_URL = MessageFormat.format("http://{0}:9010/service/product/",
        LocalNetworkInterfaceUtil.getLocalIPAddress());

    public abstract String getProdServiceUrl(Class<?> aClass);

    public abstract String getDevoServiceUrl(Class<?> aClass);
}
