package com.free.walker.service.itinerary.rest;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.ProcessingException;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public abstract class AbstractPlatformServiceTest extends BaseServiceUrlProvider {
    private HttpClient httpClient;

    protected String platformServiceUrlStr;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() {
        httpClient = HttpClientBuilder.create().build();
        platformServiceUrlStr = getProdServiceUrl(PlatformService.class);
    }

    @Test
    public void testAll() throws URISyntaxException {
        /*
         * 获取Web服务自省。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(platformServiceUrlStr + "introspection/"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject introspection = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(introspection);
                } else {
                    JsonObject error = Json.createReader(response.getEntity().getContent()).readObject();
                    throw new ProcessingException(error.toString());
                }
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                get.abort();
            }
        }
    }
}
