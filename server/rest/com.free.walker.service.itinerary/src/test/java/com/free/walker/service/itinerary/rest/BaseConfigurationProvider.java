package com.free.walker.service.itinerary.rest;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;

import com.free.walker.service.itinerary.primitive.Introspection;

public abstract class BaseConfigurationProvider implements ServiceConfigurationProvider {
    protected HttpClient adminClient = null;
    protected HttpClient userClient = null;
    protected HttpClient userWeChatClient = null;
    protected HttpClient agencyClient = null;

    public BaseConfigurationProvider() {
        if (userClient == null && agencyClient == null && adminClient == null) {
            initHttpClients();
        }
    }

    public String getProdServiceUrl(Class<?> aClass) {
        if (ProductService.class.equals(aClass)) {
            return ServiceConfigurationProvider.PRODUCT_PROD_LOCAL_URL;
        } else if (ItineraryService.class.equals(aClass)) {
            return ServiceConfigurationProvider.ITINERARY_PROD_LOCAL_URL;
        } else if (PlatformService.class.equals(aClass)) {
            return ServiceConfigurationProvider.PLATFORM_PROD_LOCAL_URL;
        } else if (AccountService.class.equals(aClass)) {
            return ServiceConfigurationProvider.ACCOUNT_PROD_LOCAL_URL;
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
        } else if (AccountService.class.equals(aClass)) {
            return ServiceConfigurationProvider.ACCOUNT_PROD_LOCAL_SEC_URL;
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
        return "cspass".toCharArray(); // Please refer to ServiceConfig.xml
    }

    public char[] getSSLKeyPassword() {
        return "ckpass".toCharArray(); // Please refer to ServiceConfig.xml
    }

    public String genBasicAuthString(String subject) {
        String credential = getCredential(subject);
        byte[] subjectCredential = new StringBuffer(subject).append(":").append(credential).toString().getBytes();
        String basicAuthorization = Base64.encodeBase64String(subjectCredential);
        return new StringBuffer("Basic ").append(basicAuthorization).toString();
    }

    protected void initHttpClients() {
        try {
            SSLContextBuilder sslCntxBuilder = SSLContexts.custom()
                .loadKeyMaterial(getSSLKeyStoreURL(), getSSLStorePassword(), getSSLKeyPassword())
                .loadTrustMaterial(getSSLTrustStoreURL(), getSSLStorePassword());
            adminClient = HttpClientBuilder.create()
                .setSslcontext(sslCntxBuilder.build())
                .setSSLHostnameVerifier(new DefaultHostnameVerifier())
                .build();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        try {
            SSLContextBuilder sslCntxBuilder = SSLContexts.custom()
                .loadKeyMaterial(getSSLKeyStoreURL(), getSSLStorePassword(), getSSLKeyPassword())
                .loadTrustMaterial(getSSLTrustStoreURL(), getSSLStorePassword());
            userClient = HttpClientBuilder.create()
                .setSslcontext(sslCntxBuilder.build())
                .setSSLHostnameVerifier(new DefaultHostnameVerifier())
                .build();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        try {
            SSLContextBuilder sslCntxBuilder = SSLContexts.custom()
                .loadKeyMaterial(getSSLKeyStoreURL(), getSSLStorePassword(), getSSLKeyPassword())
                .loadTrustMaterial(getSSLTrustStoreURL(), getSSLStorePassword());
            userWeChatClient = HttpClientBuilder.create()
                .setSslcontext(sslCntxBuilder.build())
                .setSSLHostnameVerifier(new DefaultHostnameVerifier())
                .build();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        try {
            SSLContextBuilder sslCntxBuilder = SSLContexts.custom()
                .loadKeyMaterial(getSSLKeyStoreURL(), getSSLStorePassword(), getSSLKeyPassword())
                .loadTrustMaterial(getSSLTrustStoreURL(), getSSLStorePassword());
            agencyClient = HttpClientBuilder.create()
                .setSslcontext(sslCntxBuilder.build())
                .setSSLHostnameVerifier(new DefaultHostnameVerifier())
                .build();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private String getCredential(String subject) {
        if (Introspection.TestValues.ADMIN_ACCOUNT.equals(subject)) {
            return "passw0rd"; // Please refer to shiro.ini
        } else if (Introspection.TestValues.DEFAULT_ACCOUNT.equals(subject)) {
            return "passw0rd"; // Please refer to shiro.ini
        } else if (Introspection.TestValues.DEFAULT_WECHAT_ACCOUNT.equals(subject)) {
            return "passw0rd"; // Please refer to shiro.ini
        }  else if (Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT.equals(subject)) {
            return "passw0rd"; // Please refer to shiro.ini
        } else {
            return "";
        }
    }
}
