package com.free.walker.service.itinerary.util;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;
import org.eclipse.jetty.http.HttpSchemes;

import com.free.walker.service.itinerary.rest.ServiceConfigurationProvider;

public class UriUtil {
    public static URI ensureSecureUri(URI uri) throws URISyntaxException {
        URIBuilder secUriBuilder = new URIBuilder(uri);
        secUriBuilder.setScheme(HttpSchemes.HTTPS);
        if (secUriBuilder.getPort() == Integer.valueOf(ServiceConfigurationProvider.PROD_PORT)) {
            secUriBuilder.setPort(Integer.valueOf(ServiceConfigurationProvider.PROD_SEC_PORT));
        } else if (secUriBuilder.getPort() == Integer.valueOf(ServiceConfigurationProvider.DEVO_PORT)) {
            secUriBuilder.setPort(Integer.valueOf(ServiceConfigurationProvider.DEVO_SEC_PORT));
        } else {
            ;
        }
        return secUriBuilder.build();
    }

    public static String ensureSecureUriAsString(URI uri) throws URISyntaxException {
        return ensureSecureUri(uri).toString();
    }
}
