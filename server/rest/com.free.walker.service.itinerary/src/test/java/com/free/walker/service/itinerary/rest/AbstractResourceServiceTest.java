package com.free.walker.service.itinerary.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.ProcessingException;

import org.apache.cxf.helpers.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.primitive.Introspection;

public abstract class AbstractResourceServiceTest extends BaseConfigurationProvider {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractResourceServiceTest.class);

    protected String resourceServiceUrlStr;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() {}

    @Test
    public void testAll() throws URISyntaxException {
        /*
         * 同步未知供应商资源。
         */
        {
            HttpPut put = new HttpPut();
            put.setURI(new URI(resourceServiceUrlStr + "resources/0000000?dryRun=true"));
            put.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = adminClient.execute(put);
                int statusCode = response.getStatusLine().getStatusCode();
                assertEquals(HttpStatus.BAD_REQUEST_400, statusCode);
                assertTrue(IOUtils.toString(response.getEntity().getContent()).isEmpty());
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                put.abort();
            }
        }

        /*
         * 同步有效供应商资源。
         */
        {
            HttpPut put = new HttpPut();
            put.setURI(new URI(resourceServiceUrlStr + "resources/0000001?dryRun=true"));
            put.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = adminClient.execute(put);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject syncResult = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(syncResult);
                    JsonObject syncMeta = syncResult.getJsonObject(Introspection.JSONKeys.SYNC_META);
                    assertNotNull(syncMeta);
                    long before = syncMeta.getJsonNumber(Introspection.JSONKeys.SYNC_DATE_BEFORE).longValue();
                    long after = syncMeta.getJsonNumber(Introspection.JSONKeys.SYNC_DATE_AFTER).longValue();
                    assertTrue(before < after);
                    assertTrue(syncResult.getInt(Introspection.JSONKeys.SYNC_ADD) >= 0);
                    assertTrue(syncResult.getInt(Introspection.JSONKeys.SYNC_UPDATE) >= 0);
                    assertTrue(syncResult.getInt(Introspection.JSONKeys.SYNC_DELETE) >= 0);
                } else if (statusCode == HttpStatus.UNAUTHORIZED_401) {
                    LOG.error(IOUtils.toString(response.getEntity().getContent()));
                    assertTrue(false);
                } else {
                    JsonObject error = Json.createReader(response.getEntity().getContent()).readObject();
                    throw new ProcessingException(error.toString());
                }
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                put.abort();
            }
        }
    }
}
