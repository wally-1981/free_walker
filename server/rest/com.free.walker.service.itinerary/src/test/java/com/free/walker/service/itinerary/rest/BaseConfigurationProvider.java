package com.free.walker.service.itinerary.rest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.eclipse.jetty.http.HttpSchemes;
import org.eclipse.jetty.http.HttpStatus;

import com.free.walker.service.itinerary.primitive.Introspection;

public abstract class BaseConfigurationProvider implements ServiceConfigurationProvider {
    private static final Map<String, String> CREDENTIALS = new HashMap<String, String>();

    static {
        CREDENTIALS.put(Introspection.DefaultAccounts.ADMIN_ACCOUNT, "passw0rd");
        CREDENTIALS.put(Introspection.DefaultAccounts.DEFAULT_MASTER_ACCOUNT, "passw0rd");
        CREDENTIALS.put(Introspection.DefaultAccounts.DEFAULT_WECHAT_ACCOUNT, "passw0rd");
        CREDENTIALS.put(Introspection.DefaultAccounts.DEFAULT_AGENCY_ACCOUNT, "passw0rd");
    }

    private static class MyRedirectStrategy extends DefaultRedirectStrategy {
        private UsernamePasswordCredentials credentials;

        private static final String[] REDIRECT_METHODS = new String[] {
            HttpGet.METHOD_NAME,
            HttpPost.METHOD_NAME,
            HttpPut.METHOD_NAME,
            HttpDelete.METHOD_NAME
        };

        public MyRedirectStrategy(UsernamePasswordCredentials credentials) {
            super();

            if (credentials == null) {
                throw new NullPointerException();
            }

            this.credentials = credentials;
        }

        protected boolean isRedirectable(final String method) {
            for (final String m: REDIRECT_METHODS) {
                if (m.equalsIgnoreCase(method)) {
                    return true;
                }
            }
            return false;
        }

        public HttpUriRequest getRedirect(final HttpRequest request, final HttpResponse response,
            final HttpContext context) throws ProtocolException {
            String method = request.getRequestLine().getMethod();
            if (HttpPost.METHOD_NAME.equalsIgnoreCase(method) || HttpPut.METHOD_NAME.equalsIgnoreCase(method)) {
                /*
                 * The content length will be auto calculated in the original
                 * request, convey it into the redirect request will break the
                 * redirect process of HttpClient.
                 */
                request.removeHeaders(HttpHeaders.CONTENT_LENGTH);

                /*
                 * The content type may missing from some empty payload request,
                 * while the redirect process will add the HttpClient default
                 * content type, which mismatches with server side support.
                 */
                request.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
            }

            HttpUriRequest redirectedRequest = super.getRedirect(request, response, context);

            /*
             * Positively detect the authentication challenge to avoid
             * additional UNAUTHORIZED_401 round trip for the authentication.
             */
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            if (response.getStatusLine().getStatusCode() == HttpStatus.TEMPORARY_REDIRECT_307
                || HttpSchemes.HTTPS.equalsIgnoreCase(redirectedRequest.getURI().getScheme())
                || HttpSchemes.HTTP.equalsIgnoreCase(clientContext.getTargetHost().getSchemeName())) {
                StringBuffer subjectCredentialBuilder = new StringBuffer(credentials.getUserName()).append(":").append(
                    credentials.getPassword());
                subjectCredentialBuilder = new StringBuffer("Basic ").append(Base64
                    .encodeBase64String(subjectCredentialBuilder.toString().getBytes()));
                redirectedRequest.addHeader(HttpHeaders.AUTHORIZATION, subjectCredentialBuilder.toString());
            }

            return redirectedRequest;
        }
    }

    protected HttpClient adminClient = null;
    protected HttpClient userClient = null;
    protected HttpClient userWeChatClient = null;
    protected HttpClient agencyClient = null;
    protected HttpClient anonymousClient = null;

    public BaseConfigurationProvider() {
        if (userClient == null && agencyClient == null && adminClient == null && anonymousClient == null) {
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

    protected void initHttpClients() {
        {
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                Introspection.DefaultAccounts.ADMIN_ACCOUNT,
                CREDENTIALS.get(Introspection.DefaultAccounts.ADMIN_ACCOUNT));
            adminClient = generateClient(credentials);
        }

        {
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                Introspection.DefaultAccounts.DEFAULT_MASTER_ACCOUNT,
                CREDENTIALS.get(Introspection.DefaultAccounts.DEFAULT_MASTER_ACCOUNT));
            userClient = generateClient(credentials);
        }

        {
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                Introspection.DefaultAccounts.DEFAULT_WECHAT_ACCOUNT,
                CREDENTIALS.get(Introspection.DefaultAccounts.DEFAULT_WECHAT_ACCOUNT));
            userWeChatClient = generateClient(credentials);
        }

        {
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                Introspection.DefaultAccounts.DEFAULT_AGENCY_ACCOUNT,
                CREDENTIALS.get(Introspection.DefaultAccounts.DEFAULT_AGENCY_ACCOUNT));
            agencyClient = generateClient(credentials);
        }

        try {
            SSLContextBuilder sslCntxBuilder = SSLContexts.custom()
                .loadKeyMaterial(getSSLKeyStoreURL(), getSSLStorePassword(), getSSLKeyPassword())
                .loadTrustMaterial(getSSLTrustStoreURL(), getSSLStorePassword());
            anonymousClient = HttpClientBuilder.create()
                .setSslcontext(sslCntxBuilder.build())
                .setSSLHostnameVerifier(new DefaultHostnameVerifier())
                .build();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    protected HttpClient generateClient(UsernamePasswordCredentials credentials) {
        try {
            CredentialsProvider provider = new BasicCredentialsProvider();
            provider.setCredentials(AuthScope.ANY, credentials);

            SSLContextBuilder sslCntxBuilder = SSLContexts.custom()
                .loadKeyMaterial(getSSLKeyStoreURL(), getSSLStorePassword(), getSSLKeyPassword())
                .loadTrustMaterial(getSSLTrustStoreURL(), getSSLStorePassword());
            return HttpClientBuilder.create()
                .setSslcontext(sslCntxBuilder.build())
                .setSSLHostnameVerifier(new DefaultHostnameVerifier())
                .setRedirectStrategy(new MyRedirectStrategy(credentials))
                .setDefaultCredentialsProvider(provider)
                .build();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
