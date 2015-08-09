package com.free.walker.service.itinerary.util;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;
import org.eclipse.jetty.http.HttpSchemes;

import com.free.walker.service.itinerary.rest.ServiceConfigurationProvider;

public class UriUtil {
    public static URI ensureSecureUri(URI uri, boolean enforceSecurity) throws URISyntaxException {
        URIBuilder secUriBuilder = new URIBuilder(uri);
        if (enforceSecurity) {
            secUriBuilder.setScheme(HttpSchemes.HTTPS);
            if (secUriBuilder.getPort() == Integer.valueOf(ServiceConfigurationProvider.PROD_PORT)) {
                secUriBuilder.setPort(Integer.valueOf(ServiceConfigurationProvider.PROD_SEC_PORT));
            } else if (secUriBuilder.getPort() == Integer.valueOf(ServiceConfigurationProvider.DEVO_PORT)) {
                secUriBuilder.setPort(Integer.valueOf(ServiceConfigurationProvider.DEVO_SEC_PORT));
            } else {
                ;
            }
        }
        return secUriBuilder.build();
    }

    public static String ensureSecureUriAsString(URI uri, boolean enforceSecurity) throws URISyntaxException {
        return ensureSecureUri(uri, enforceSecurity).toString();
    }
}
