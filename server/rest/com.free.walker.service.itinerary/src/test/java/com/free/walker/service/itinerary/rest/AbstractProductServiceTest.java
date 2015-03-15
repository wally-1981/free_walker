package com.free.walker.service.itinerary.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.ProcessingException;

import org.apache.cxf.helpers.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.primitive.Introspection;
import com.ibm.icu.util.Calendar;

public abstract class AbstractProductServiceTest extends BaseServiceUrlProvider {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractProductServiceTest.class);

    private HttpClient httpClient;

    private JsonObject proposal;
    private JsonObject product;
    private JsonObject newHotel;
    private JsonObject newTraffic;
    private JsonObject newResort;
    private JsonObject newTriv;
    private JsonObject bidding;
    private Calendar enrollmentDeadlineDatetime;
    private Calendar departureDatetime;
    private Calendar trafficDatetime;
    private Calendar arrivalHotelDatetime;
    private Calendar resortDatetime;
    private Calendar departureHotelDatetime;

    private String proposalId;
    private String productId;
    private String hotelItemId;
    private String trafficItemId;
    private String resortItemId;
    private String trivItemId;

    protected String itineraryServiceUrlStr;
    protected String productServiceUrlStr;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() {
        try {
            SSLContextBuilder sslCntxBuilder = SSLContexts.custom()
                .loadKeyMaterial(getSSLKeyStoreURL(), getSSLStorePassword(), getSSLKeyPassword())
                .loadTrustMaterial(getSSLTrustStoreURL(), getSSLStorePassword());
            httpClient = HttpClientBuilder.create().setSslcontext(sslCntxBuilder.build())
                .setSSLHostnameVerifier(new DefaultHostnameVerifier()).build();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        {
            JsonObjectBuilder requirementBuilder = Json.createObjectBuilder();
            requirementBuilder.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY);

            JsonObjectBuilder destinationBuilder = Json.createObjectBuilder();
            JsonObjectBuilder destCityBuilder = Json.createObjectBuilder();
            destCityBuilder.add(Introspection.JSONKeys.UUID, "02515d41-f141-4175-9a11-9e68b9cfe687");
            destinationBuilder.add(Introspection.JSONKeys.CITY, destCityBuilder);
            requirementBuilder.add(Introspection.JSONKeys.DESTINATION, destinationBuilder);

            JsonObjectBuilder departureBuilder = Json.createObjectBuilder();
            JsonObjectBuilder deptCityBuilder = Json.createObjectBuilder();
            deptCityBuilder.add(Introspection.JSONKeys.UUID, "84844276-3036-47dd-90e0-f095cfa98da5");
            departureBuilder.add(Introspection.JSONKeys.CITY, deptCityBuilder);
            requirementBuilder.add(Introspection.JSONKeys.DEPARTURE, departureBuilder);

            JsonObjectBuilder proposalBuilder = Json.createObjectBuilder();
            proposalBuilder.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL);
            proposalBuilder.add(Introspection.JSONKeys.AUTHOR, "3b3e4dcf-e353-4418-adfb-3c9af7a54992");
            proposalBuilder.add(Introspection.JSONKeys.TITLE, "测试提议");
            JsonArrayBuilder requirementsBuilder = Json.createArrayBuilder();
            requirementsBuilder.add(requirementBuilder);
            proposalBuilder.add(Introspection.JSONKeys.REQUIREMENTS, requirementsBuilder);
            proposal = proposalBuilder.build();

            HttpPost post = new HttpPost();
            try {
                post.setEntity(new StringEntity(proposal.toString(), ContentType.APPLICATION_JSON));
                post.setURI(new URI(itineraryServiceUrlStr + "proposals/"));
                post.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
                post.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_ACCOUNT);
                HttpResponse response = httpClient.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject proposal = Json.createReader(response.getEntity().getContent()).readObject();
                    proposalId = proposal.getString(Introspection.JSONKeys.UUID);
                } else if (statusCode == HttpStatus.UNAUTHORIZED_401) {
                    LOG.error(IOUtils.toString(response.getEntity().getContent()));
                    assertTrue(false);
                } else {
                    JsonObject error = Json.createReader(response.getEntity().getContent()).readObject();
                    throw new ProcessingException(error.toString());
                }
            } catch (IOException e) {
                throw new ProcessingException(e);
            } catch (URISyntaxException e) {
                throw new ProcessingException(e);
            } finally {
                post.abort();
            }
        }

        {
            enrollmentDeadlineDatetime = Calendar.getInstance();
            enrollmentDeadlineDatetime.add(Calendar.DAY_OF_MONTH, 30);

            departureDatetime = Calendar.getInstance();
            departureDatetime.add(Calendar.DAY_OF_MONTH, 37);

            trafficDatetime = Calendar.getInstance();
            trafficDatetime.add(Calendar.DAY_OF_MONTH, 37);

            arrivalHotelDatetime = Calendar.getInstance();
            arrivalHotelDatetime.add(Calendar.DAY_OF_MONTH, 38);

            resortDatetime = Calendar.getInstance();
            resortDatetime.add(Calendar.DAY_OF_MONTH, 42);

            departureHotelDatetime = Calendar.getInstance();
            departureHotelDatetime.add(Calendar.DAY_OF_MONTH, 50);

            JsonObjectBuilder productBuilder = Json.createObjectBuilder();
            productBuilder.add(Introspection.JSONKeys.REF_UUID, proposalId);
            productBuilder.add(Introspection.JSONKeys.GROUP_CAPACITY, 60);
            productBuilder.add(Introspection.JSONKeys.DEADLINE_DATETIME, enrollmentDeadlineDatetime.getTimeInMillis());
            productBuilder.add(Introspection.JSONKeys.DEPARTURE_DATETIME, departureDatetime.getTimeInMillis());
            JsonObjectBuilder departureBuilder = Json.createObjectBuilder();
            JsonObjectBuilder deptCityBuilder = Json.createObjectBuilder();
            deptCityBuilder.add(Introspection.JSONKeys.UUID, "84844276-3036-47dd-90e0-f095cfa98da5");
            departureBuilder.add(Introspection.JSONKeys.CITY, deptCityBuilder);
            productBuilder.add(Introspection.JSONKeys.DEPARTURE, departureBuilder);
            productBuilder.add(Introspection.JSONKeys.STATUS, Introspection.JSONValues.DRAFT_PRODUCT.enumValue());

            JsonObjectBuilder hotelItemBuilder = Json.createObjectBuilder();
            hotelItemBuilder.add(Introspection.JSONKeys.SUB_TYPE, Introspection.JSONValues.SUB_TYPE_HOTEL_ITEM);
            hotelItemBuilder.add(Introspection.JSONKeys.ARRIVAL_DATETIME, arrivalHotelDatetime.getTimeInMillis());
            hotelItemBuilder.add(Introspection.JSONKeys.DEPARTURE_DATETIME, departureHotelDatetime.getTimeInMillis());
            JsonObjectBuilder hotelBuilder = Json.createObjectBuilder();
            hotelItemBuilder.add(Introspection.JSONKeys.HOTEL, hotelBuilder);

            JsonObjectBuilder trafficItemBuilder = Json.createObjectBuilder();
            trafficItemBuilder.add(Introspection.JSONKeys.SUB_TYPE, Introspection.JSONValues.SUB_TYPE_TRAFFIC_ITEM);
            trafficItemBuilder.add(Introspection.JSONKeys.DATE, trafficDatetime.getTimeInMillis());
            JsonObjectBuilder flightBuilder = Json.createObjectBuilder();
            flightBuilder.add(Introspection.JSONKeys.TRAFFIC_TOOL_TYPE,
                Introspection.JSONValues.TRAFFIC_TOOL_TYPE_FLIGHT.enumValue());
            trafficItemBuilder.add(Introspection.JSONKeys.TRAFFIC, flightBuilder);

            JsonObjectBuilder resortItemBuilder = Json.createObjectBuilder();
            resortItemBuilder.add(Introspection.JSONKeys.SUB_TYPE, Introspection.JSONValues.SUB_TYPE_RESORT_ITEM);
            resortItemBuilder.add(Introspection.JSONKeys.DATE, resortDatetime.getTimeInMillis());
            JsonObjectBuilder resortBuilder = Json.createObjectBuilder();
            resortItemBuilder.add(Introspection.JSONKeys.RESORT, resortBuilder);

            JsonObjectBuilder trivItemBuilder = Json.createObjectBuilder();
            trivItemBuilder.add(Introspection.JSONKeys.SUB_TYPE, Introspection.JSONValues.SUB_TYPE_TRIV_ITEM);

            JsonArrayBuilder productItemsBuilder = Json.createArrayBuilder();
            productItemsBuilder.add(hotelItemBuilder);
            productItemsBuilder.add(trafficItemBuilder);
            productItemsBuilder.add(resortItemBuilder);
            productItemsBuilder.add(trivItemBuilder);
            productBuilder.add(Introspection.JSONKeys.ITEMS, productItemsBuilder);

            product = productBuilder.build();
        }

        {
            JsonObjectBuilder hotelItemBuilder = Json.createObjectBuilder();
            hotelItemBuilder.add(Introspection.JSONKeys.SUB_TYPE, Introspection.JSONValues.SUB_TYPE_HOTEL_ITEM);
            hotelItemBuilder.add(Introspection.JSONKeys.ARRIVAL_DATETIME, arrivalHotelDatetime.getTimeInMillis() + 1);
            hotelItemBuilder.add(Introspection.JSONKeys.DEPARTURE_DATETIME, departureHotelDatetime.getTimeInMillis());
            JsonObjectBuilder hotelBuilder = Json.createObjectBuilder();
            hotelItemBuilder.add(Introspection.JSONKeys.HOTEL, hotelBuilder);
            newHotel = hotelItemBuilder.build();
        }

        {
            JsonObjectBuilder trafficItemBuilder = Json.createObjectBuilder();
            trafficItemBuilder.add(Introspection.JSONKeys.SUB_TYPE, Introspection.JSONValues.SUB_TYPE_TRAFFIC_ITEM);
            trafficItemBuilder.add(Introspection.JSONKeys.DATE, trafficDatetime.getTimeInMillis() + 1);
            JsonObjectBuilder flightBuilder = Json.createObjectBuilder();
            flightBuilder.add(Introspection.JSONKeys.TRAFFIC_TOOL_TYPE,
                Introspection.JSONValues.TRAFFIC_TOOL_TYPE_FLIGHT.enumValue());
            trafficItemBuilder.add(Introspection.JSONKeys.TRAFFIC, flightBuilder);
            newTraffic = trafficItemBuilder.build();
        }

        {
            JsonObjectBuilder resortItemBuilder = Json.createObjectBuilder();
            resortItemBuilder.add(Introspection.JSONKeys.SUB_TYPE, Introspection.JSONValues.SUB_TYPE_RESORT_ITEM);
            resortItemBuilder.add(Introspection.JSONKeys.DATE, resortDatetime.getTimeInMillis() + 1);
            JsonObjectBuilder resortBuilder = Json.createObjectBuilder();
            resortItemBuilder.add(Introspection.JSONKeys.RESORT, resortBuilder);
            newResort = resortItemBuilder.build();
        }

        {
            JsonObjectBuilder trivItemBuilder = Json.createObjectBuilder();
            trivItemBuilder.add(Introspection.JSONKeys.SUB_TYPE, Introspection.JSONValues.SUB_TYPE_TRIV_ITEM);
            JsonObjectBuilder biddingBuilder = Json.createObjectBuilder();
            biddingBuilder.add(Introspection.JSONKeys.TITLE, "随身Wifi");
            biddingBuilder.add(Introspection.JSONKeys.PRICE, 108.8);
            trivItemBuilder.add(Introspection.JSONKeys.BIDDING, biddingBuilder);
            newTriv = trivItemBuilder.build();
        }

        {
            JsonObjectBuilder biddingBuilder = Json.createObjectBuilder();
            JsonArrayBuilder biddingItemsBuilder = Json.createArrayBuilder();

            JsonObjectBuilder biddingItemBuilder1 = Json.createObjectBuilder();
            biddingItemBuilder1.add(Introspection.JSONKeys.MIN, 1);
            biddingItemBuilder1.add(Introspection.JSONKeys.MAX, 10);
            biddingItemBuilder1.add(Introspection.JSONKeys.PRICE, 2000);
            biddingItemsBuilder.add(biddingItemBuilder1);

            JsonObjectBuilder biddingItemBuilder2 = Json.createObjectBuilder();
            biddingItemBuilder2.add(Introspection.JSONKeys.MIN, 11);
            biddingItemBuilder2.add(Introspection.JSONKeys.MAX, 30);
            biddingItemBuilder2.add(Introspection.JSONKeys.PRICE, 1600);
            biddingItemsBuilder.add(biddingItemBuilder2);

            JsonObjectBuilder biddingItemBuilder3 = Json.createObjectBuilder();
            biddingItemBuilder3.add(Introspection.JSONKeys.MIN, 31);
            biddingItemBuilder3.add(Introspection.JSONKeys.MAX, 80);
            biddingItemBuilder3.add(Introspection.JSONKeys.PRICE, 1200);
            biddingItemsBuilder.add(biddingItemBuilder3);

            JsonObjectBuilder biddingItemBuilder4 = Json.createObjectBuilder();
            biddingItemBuilder4.add(Introspection.JSONKeys.TITLE, "Child Price");
            biddingItemBuilder4.add(Introspection.JSONKeys.PRICE, -300.01);
            biddingItemsBuilder.add(biddingItemBuilder4);

            JsonObjectBuilder biddingItemBuilder5 = Json.createObjectBuilder();
            biddingItemBuilder5.add(Introspection.JSONKeys.TITLE, "Single Room Price");
            biddingItemBuilder5.add(Introspection.JSONKeys.PRICE, 388);
            biddingItemsBuilder.add(biddingItemBuilder5);

            biddingBuilder.add(Introspection.JSONKeys.BIDDING, biddingItemsBuilder);

            bidding = biddingBuilder.build();
        }
    }

    @Test
    public void testAll() throws URISyntaxException, InterruptedException {
        /*
         * 新建一个Product，同时添加初始的ProductItem。
         */
        {
            HttpPost post = new HttpPost();
            post.setEntity(new StringEntity(product.toString(), ContentType.APPLICATION_JSON));
            post.setURI(new URI(productServiceUrlStr + "products/"));
            post.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            post.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject product = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(product);
                    productId = product.getString(Introspection.JSONKeys.UUID);
                    assertNotNull(productId);
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
                post.abort();
            }
        }

        /*
         * 获取刚新建的Product。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(productServiceUrlStr + "products/" + productId));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject product = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(product);
                    assertEquals(productId, product.getString(Introspection.JSONKeys.UUID));
                    assertEquals(60, product.getInt(Introspection.JSONKeys.GROUP_CAPACITY));
                    assertEquals(enrollmentDeadlineDatetime.getTimeInMillis(),
                        product.getJsonNumber(Introspection.JSONKeys.DEADLINE_DATETIME).longValue());
                    assertEquals(departureDatetime.getTimeInMillis(),
                        product.getJsonNumber(Introspection.JSONKeys.DEPARTURE_DATETIME).longValue());

                    JsonArray productItems = product.getJsonArray(Introspection.JSONKeys.ITEMS);
                    assertEquals(0, productItems.size());
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
                get.abort();
            }
        }

        /*
         * 根据Proposal获取刚新建的Product。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(productServiceUrlStr + "products/" + proposalId + "?idType=proposal"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonArray products = Json.createReader(response.getEntity().getContent()).readArray();
                    assertNotNull(products);
                    assertEquals(1, products.size());

                    {
                        JsonObject product = products.getJsonObject(0);
                        assertNotNull(product);

                        assertEquals(productId, product.getString(Introspection.JSONKeys.UUID));
                        assertEquals(60, product.getInt(Introspection.JSONKeys.GROUP_CAPACITY));
                        assertEquals(enrollmentDeadlineDatetime.getTimeInMillis(),
                            product.getJsonNumber(Introspection.JSONKeys.DEADLINE_DATETIME).longValue());
                        assertEquals(departureDatetime.getTimeInMillis(),
                            product.getJsonNumber(Introspection.JSONKeys.DEPARTURE_DATETIME).longValue());

                        JsonArray productItems = product.getJsonArray(Introspection.JSONKeys.ITEMS);
                        assertEquals(0, productItems.size());
                    }
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
                get.abort();
            }
        }

        /*
         * 获取刚新建的Product的HotelItem。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(productServiceUrlStr + "products/" + productId + "/hotels"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonArray productItems = Json.createReader(response.getEntity().getContent()).readArray();
                    assertNotNull(productItems);
                    assertEquals(1, productItems.size());

                    {
                        JsonObject hotelItem = productItems.getJsonObject(0);
                        assertNotNull(hotelItem);

                        assertNotNull(hotelItemId = hotelItem.getString(Introspection.JSONKeys.UUID));
                        assertEquals(Introspection.JSONValues.SUB_TYPE_HOTEL_ITEM,
                            hotelItem.getString(Introspection.JSONKeys.SUB_TYPE));
                        assertEquals(arrivalHotelDatetime.getTimeInMillis(),
                            hotelItem.getJsonNumber(Introspection.JSONKeys.ARRIVAL_DATETIME).longValue());
                        assertEquals(departureHotelDatetime.getTimeInMillis(),
                            hotelItem.getJsonNumber(Introspection.JSONKeys.DEPARTURE_DATETIME).longValue());
                        assertNotNull(hotelItem.getJsonObject(Introspection.JSONKeys.HOTEL));
                    }
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
                get.abort();
            }
        }

        /*
         * 获取刚新建的Product的TrafficItem。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(productServiceUrlStr + "products/" + productId + "/traffics"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonArray productItems = Json.createReader(response.getEntity().getContent()).readArray();
                    assertNotNull(productItems);
                    assertEquals(1, productItems.size());

                    {
                        JsonObject trafficItem = productItems.getJsonObject(0);
                        assertNotNull(trafficItem);

                        assertNotNull(trafficItemId = trafficItem.getString(Introspection.JSONKeys.UUID));
                        assertEquals(Introspection.JSONValues.SUB_TYPE_TRAFFIC_ITEM,
                            trafficItem.getString(Introspection.JSONKeys.SUB_TYPE));
                        assertEquals(trafficDatetime.getTimeInMillis(),
                            trafficItem.getJsonNumber(Introspection.JSONKeys.DATE).longValue());
                        assertNotNull(trafficItem.getJsonObject(Introspection.JSONKeys.TRAFFIC));
                    }
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
                get.abort();
            }
        }

        /*
         * 获取刚新建的Product的ResortItem。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(productServiceUrlStr + "products/" + productId + "/resorts"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonArray productItems = Json.createReader(response.getEntity().getContent()).readArray();
                    assertNotNull(productItems);
                    assertEquals(1, productItems.size());

                    {
                        JsonObject resortItem = productItems.getJsonObject(0);
                        assertNotNull(resortItem);

                        assertNotNull(resortItemId = resortItem.getString(Introspection.JSONKeys.UUID));
                        assertEquals(Introspection.JSONValues.SUB_TYPE_RESORT_ITEM,
                            resortItem.getString(Introspection.JSONKeys.SUB_TYPE));
                        assertEquals(resortDatetime.getTimeInMillis(),
                            resortItem.getJsonNumber(Introspection.JSONKeys.DATE).longValue());
                        assertNotNull(resortItem.getJsonObject(Introspection.JSONKeys.RESORT));
                    }
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
                get.abort();
            }
        }

        /*
         * 获取刚新建的Product的Item。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(productServiceUrlStr + "products/" + productId + "/items"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonArray productItems = Json.createReader(response.getEntity().getContent()).readArray();
                    assertNotNull(productItems);
                    assertEquals(1, productItems.size());

                    {
                        JsonObject trivItem = productItems.getJsonObject(0);
                        assertNotNull(trivItem);

                        assertNotNull(trivItemId = trivItem.getString(Introspection.JSONKeys.UUID));
                        assertEquals(Introspection.JSONValues.SUB_TYPE_TRIV_ITEM,
                            trivItem.getString(Introspection.JSONKeys.SUB_TYPE));
                    }
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
                get.abort();
            }
        }

        /*
         * 获取刚新建的Product的Bidding；此时，该产品没有设置bidding信息，所以返回404。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(productServiceUrlStr + "products/" + productId + "/bidding"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                assertEquals(HttpStatus.NOT_FOUND_404, statusCode);
                JsonObject error = Json.createReader(response.getEntity().getContent()).readObject();
                assertNotNull(error);
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                get.abort();
            }
        }

        /*
         * 为刚新建的Product添加一个HotelItem。
         */
        {
            HttpPost post = new HttpPost();
            post.setEntity(new StringEntity(newHotel.toString(), ContentType.APPLICATION_JSON));
            post.setURI(new URI(productServiceUrlStr + "products/" + productId + "/hotels"));
            post.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            post.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject hotelItem = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(hotelItem);
                    assertNotNull(hotelItem.getString(Introspection.JSONKeys.UUID));
                    assertFalse(hotelItem.getString(Introspection.JSONKeys.UUID).equals(hotelItemId));
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
                post.abort();
            }
        }

        /*
         * 为刚新建的Product添加一个TrafficItem。
         */
        {
            HttpPost post = new HttpPost();
            post.setEntity(new StringEntity(newTraffic.toString(), ContentType.APPLICATION_JSON));
            post.setURI(new URI(productServiceUrlStr + "products/" + productId + "/traffics"));
            post.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            post.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject trafficItem = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(trafficItem);
                    assertNotNull(trafficItem.getString(Introspection.JSONKeys.UUID));
                    assertFalse(trafficItem.getString(Introspection.JSONKeys.UUID).equals(trafficItemId));
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
                post.abort();
            }
        }

        /*
         * 为刚新建的Product添加一个ResortItem。
         */
        {
            HttpPost post = new HttpPost();
            post.setEntity(new StringEntity(newResort.toString(), ContentType.APPLICATION_JSON));
            post.setURI(new URI(productServiceUrlStr + "products/" + productId + "/resorts"));
            post.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            post.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject resortItem = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(resortItem);
                    assertNotNull(resortItem.getString(Introspection.JSONKeys.UUID));
                    assertFalse(resortItem.getString(Introspection.JSONKeys.UUID).equals(resortItemId));
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
                post.abort();
            }
        }

        /*
         * 为刚新建的Product添加一个TrivItem。
         */
        {
            HttpPost post = new HttpPost();
            post.setEntity(new StringEntity(newTriv.toString(), ContentType.APPLICATION_JSON));
            post.setURI(new URI(productServiceUrlStr + "products/" + productId + "/items"));
            post.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            post.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject trivItem = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(trivItem);
                    assertNotNull(trivItem.getString(Introspection.JSONKeys.UUID));
                    assertFalse(trivItem.getString(Introspection.JSONKeys.UUID).equals(trivItemId));
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
                post.abort();
            }
        }

        /*
         * 获取新添加的HotelItem。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(productServiceUrlStr + "products/" + productId + "/hotels"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonArray productItems = Json.createReader(response.getEntity().getContent()).readArray();
                    assertNotNull(productItems);
                    assertEquals(2, productItems.size());

                    {
                        JsonObject hotelItem = productItems.getJsonObject(0);
                        assertNotNull(hotelItem);

                        assertNotNull(hotelItem.getString(Introspection.JSONKeys.UUID));
                        assertEquals(Introspection.JSONValues.SUB_TYPE_HOTEL_ITEM,
                            hotelItem.getString(Introspection.JSONKeys.SUB_TYPE));
                        assertEquals(arrivalHotelDatetime.getTimeInMillis(),
                            hotelItem.getJsonNumber(Introspection.JSONKeys.ARRIVAL_DATETIME).longValue());
                        assertEquals(departureHotelDatetime.getTimeInMillis(),
                            hotelItem.getJsonNumber(Introspection.JSONKeys.DEPARTURE_DATETIME).longValue());
                        assertNotNull(hotelItem.getJsonObject(Introspection.JSONKeys.HOTEL));
                    }

                    {
                        JsonObject hotelItem = productItems.getJsonObject(1);
                        assertNotNull(hotelItem);

                        assertNotNull(hotelItem.getString(Introspection.JSONKeys.UUID));
                        assertEquals(Introspection.JSONValues.SUB_TYPE_HOTEL_ITEM,
                            hotelItem.getString(Introspection.JSONKeys.SUB_TYPE));
                        assertEquals(arrivalHotelDatetime.getTimeInMillis() + 1,
                            hotelItem.getJsonNumber(Introspection.JSONKeys.ARRIVAL_DATETIME).longValue());
                        assertEquals(departureHotelDatetime.getTimeInMillis(),
                            hotelItem.getJsonNumber(Introspection.JSONKeys.DEPARTURE_DATETIME).longValue());
                        assertNotNull(hotelItem.getJsonObject(Introspection.JSONKeys.HOTEL));
                    }
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
                get.abort();
            }
        }

        /*
         * 获取新添加的TrafficItem。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(productServiceUrlStr + "products/" + productId + "/traffics"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonArray productItems = Json.createReader(response.getEntity().getContent()).readArray();
                    assertNotNull(productItems);
                    assertEquals(2, productItems.size());

                    {
                        JsonObject trafficItem = productItems.getJsonObject(0);
                        assertNotNull(trafficItem);

                        assertNotNull(trafficItem.getString(Introspection.JSONKeys.UUID));
                        assertEquals(Introspection.JSONValues.SUB_TYPE_TRAFFIC_ITEM,
                            trafficItem.getString(Introspection.JSONKeys.SUB_TYPE));
                        assertEquals(trafficDatetime.getTimeInMillis(),
                            trafficItem.getJsonNumber(Introspection.JSONKeys.DATE).longValue());
                        assertNotNull(trafficItem.getJsonObject(Introspection.JSONKeys.TRAFFIC));
                    }

                    {
                        JsonObject trafficItem = productItems.getJsonObject(1);
                        assertNotNull(trafficItem);

                        assertNotNull(trafficItem.getString(Introspection.JSONKeys.UUID));
                        assertEquals(Introspection.JSONValues.SUB_TYPE_TRAFFIC_ITEM,
                            trafficItem.getString(Introspection.JSONKeys.SUB_TYPE));
                        assertEquals(trafficDatetime.getTimeInMillis() + 1,
                            trafficItem.getJsonNumber(Introspection.JSONKeys.DATE).longValue());
                        assertNotNull(trafficItem.getJsonObject(Introspection.JSONKeys.TRAFFIC));
                    }
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
                get.abort();
            }
        }

        /*
         * 获取新添加的ResortItem。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(productServiceUrlStr + "products/" + productId + "/resorts"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonArray productItems = Json.createReader(response.getEntity().getContent()).readArray();
                    assertNotNull(productItems);
                    assertEquals(2, productItems.size());

                    {
                        JsonObject resortItem = productItems.getJsonObject(0);
                        assertNotNull(resortItem);

                        assertNotNull(resortItem.getString(Introspection.JSONKeys.UUID));
                        assertEquals(Introspection.JSONValues.SUB_TYPE_RESORT_ITEM,
                            resortItem.getString(Introspection.JSONKeys.SUB_TYPE));
                        assertEquals(resortDatetime.getTimeInMillis(),
                            resortItem.getJsonNumber(Introspection.JSONKeys.DATE).longValue());
                        assertNotNull(resortItem.getJsonObject(Introspection.JSONKeys.RESORT));
                    }

                    {
                        JsonObject resortItem = productItems.getJsonObject(1);
                        assertNotNull(resortItem);

                        assertNotNull(resortItem.getString(Introspection.JSONKeys.UUID));
                        assertEquals(Introspection.JSONValues.SUB_TYPE_RESORT_ITEM,
                            resortItem.getString(Introspection.JSONKeys.SUB_TYPE));
                        assertEquals(resortDatetime.getTimeInMillis() + 1,
                            resortItem.getJsonNumber(Introspection.JSONKeys.DATE).longValue());
                        assertNotNull(resortItem.getJsonObject(Introspection.JSONKeys.RESORT));
                    }
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
                get.abort();
            }
        }

        /*
         * 获取新添加的Item。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(productServiceUrlStr + "products/" + productId + "/items"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonArray productItems = Json.createReader(response.getEntity().getContent()).readArray();
                    assertNotNull(productItems);
                    assertEquals(2, productItems.size());

                    {
                        JsonObject trivItem = productItems.getJsonObject(0);
                        assertNotNull(trivItem);

                        assertNotNull(trivItem.getString(Introspection.JSONKeys.UUID));
                        assertEquals(Introspection.JSONValues.SUB_TYPE_TRIV_ITEM,
                            trivItem.getString(Introspection.JSONKeys.SUB_TYPE));
                    }

                    {
                        JsonObject trivItem = productItems.getJsonObject(1);
                        assertNotNull(trivItem);

                        assertNotNull(trivItem.getString(Introspection.JSONKeys.UUID));
                        assertEquals(Introspection.JSONValues.SUB_TYPE_TRIV_ITEM,
                            trivItem.getString(Introspection.JSONKeys.SUB_TYPE));
                    }
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
                get.abort();
            }
        }

        /*
         * 设置产品Bidding。
         */
        {
            HttpPost post = new HttpPost();
            post.setEntity(new StringEntity(bidding.toString(), ContentType.APPLICATION_JSON));
            post.setURI(new URI(productServiceUrlStr + "products/" + productId + "/bidding"));
            post.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            post.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject biddingItem = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(biddingItem);
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
                post.abort();
            }
        }

        /*
         * 删除初始的HotelItem；但产品Bidding存在，删除失败。
         */
        {
            HttpDelete delete = new HttpDelete();
            delete.setURI(new URI(productServiceUrlStr + "products/" + productId + "/hotels/" + hotelItemId));
            delete.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            delete.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(delete);
                int statusCode = response.getStatusLine().getStatusCode();
                assertEquals(HttpStatus.BAD_REQUEST_400, statusCode);
                JsonObject error = Json.createReader(response.getEntity().getContent()).readObject();
                assertNotNull(error);
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                delete.abort();
            }
        }

        /*
         * 获取刚新建的Product的Bidding。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(productServiceUrlStr + "products/" + productId + "/bidding"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject bidding = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(bidding);
                    JsonArray biddingItems = bidding.getJsonArray(Introspection.JSONKeys.BIDDING);
                    assertNotNull(biddingItems);
                    assertEquals(5, biddingItems.size());
                    
                }
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                get.abort();
            }
        }

        /*
         * 取消产品Bidding。
         */
        {
            HttpDelete delete = new HttpDelete();
            delete.setURI(new URI(productServiceUrlStr + "products/" + productId + "/bidding"));
            delete.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            delete.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(delete);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject bidding = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(bidding);
                    assertEquals(productId, bidding.getString(Introspection.JSONKeys.UUID));
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
                delete.abort();
            }
        }

        /*
         * 删除初始的HotelItem。
         */
        {
            HttpDelete delete = new HttpDelete();
            delete.setURI(new URI(productServiceUrlStr + "products/" + productId + "/hotels/" + hotelItemId));
            delete.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            delete.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(delete);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject hotelItem = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(hotelItem);
                    assertEquals(hotelItemId, hotelItem.getString(Introspection.JSONKeys.UUID));
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
                delete.abort();
            }
        }

        /*
         * 删除初始的TrafficItem。
         */
        {
            HttpDelete delete = new HttpDelete();
            delete.setURI(new URI(productServiceUrlStr + "products/" + productId + "/traffics/" + trafficItemId));
            delete.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            delete.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(delete);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject trafficItem = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(trafficItem);
                    assertEquals(trafficItemId, trafficItem.getString(Introspection.JSONKeys.UUID));
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
                delete.abort();
            }
        }

        /*
         * 删除初始的ResortItem。
         */
        {
            HttpDelete delete = new HttpDelete();
            delete.setURI(new URI(productServiceUrlStr + "products/" + productId + "/resorts/" + resortItemId));
            delete.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            delete.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(delete);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject resortItem = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(resortItem);
                    assertEquals(resortItemId, resortItem.getString(Introspection.JSONKeys.UUID));
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
                delete.abort();
            }
        }

        /*
         * 删除初始的TrivItem。
         */
        {
            HttpDelete delete = new HttpDelete();
            delete.setURI(new URI(productServiceUrlStr + "products/" + productId + "/items/" + trivItemId));
            delete.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            delete.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(delete);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject trivItem = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(trivItem);
                    assertEquals(trivItemId, trivItem.getString(Introspection.JSONKeys.UUID));
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
                delete.abort();
            }
        }

        /*
         * 获取Product剩余的HotelItem。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(productServiceUrlStr + "products/" + productId + "/hotels"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonArray productItems = Json.createReader(response.getEntity().getContent()).readArray();
                    assertNotNull(productItems);
                    assertEquals(1, productItems.size());

                    {
                        JsonObject hotelItem = productItems.getJsonObject(0);
                        assertNotNull(hotelItem);

                        assertNotNull(hotelItem.getString(Introspection.JSONKeys.UUID));
                        assertEquals(Introspection.JSONValues.SUB_TYPE_HOTEL_ITEM,
                            hotelItem.getString(Introspection.JSONKeys.SUB_TYPE));
                        assertEquals(arrivalHotelDatetime.getTimeInMillis() + 1,
                            hotelItem.getJsonNumber(Introspection.JSONKeys.ARRIVAL_DATETIME).longValue());
                        assertEquals(departureHotelDatetime.getTimeInMillis(),
                            hotelItem.getJsonNumber(Introspection.JSONKeys.DEPARTURE_DATETIME).longValue());
                        assertNotNull(hotelItem.getJsonObject(Introspection.JSONKeys.HOTEL));
                        assertFalse(hotelItem.getString(Introspection.JSONKeys.UUID).equals(hotelItemId));
                    }
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
                get.abort();
            }
        }

        /*
         * 获取Product剩余的TrafficItem。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(productServiceUrlStr + "products/" + productId + "/traffics"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonArray productItems = Json.createReader(response.getEntity().getContent()).readArray();
                    assertNotNull(productItems);
                    assertEquals(1, productItems.size());

                    {
                        JsonObject trafficItem = productItems.getJsonObject(0);
                        assertNotNull(trafficItem);

                        assertNotNull(trafficItem.getString(Introspection.JSONKeys.UUID));
                        assertEquals(Introspection.JSONValues.SUB_TYPE_TRAFFIC_ITEM,
                            trafficItem.getString(Introspection.JSONKeys.SUB_TYPE));
                        assertEquals(trafficDatetime.getTimeInMillis() + 1,
                            trafficItem.getJsonNumber(Introspection.JSONKeys.DATE).longValue());
                        assertNotNull(trafficItem.getJsonObject(Introspection.JSONKeys.TRAFFIC));
                        assertFalse(trafficItem.getString(Introspection.JSONKeys.UUID).equals(trafficItemId));
                    }
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
                get.abort();
            }
        }

        /*
         * 获取Product剩余的ResortItem。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(productServiceUrlStr + "products/" + productId + "/resorts"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonArray productItems = Json.createReader(response.getEntity().getContent()).readArray();
                    assertNotNull(productItems);
                    assertEquals(1, productItems.size());

                    {
                        JsonObject resortItem = productItems.getJsonObject(0);
                        assertNotNull(resortItem);

                        assertNotNull(resortItem.getString(Introspection.JSONKeys.UUID));
                        assertEquals(Introspection.JSONValues.SUB_TYPE_RESORT_ITEM,
                            resortItem.getString(Introspection.JSONKeys.SUB_TYPE));
                        assertEquals(resortDatetime.getTimeInMillis() + 1,
                            resortItem.getJsonNumber(Introspection.JSONKeys.DATE).longValue());
                        assertNotNull(resortItem.getJsonObject(Introspection.JSONKeys.RESORT));
                        assertFalse(resortItem.getString(Introspection.JSONKeys.UUID).equals(resortItemId));
                    }
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
                get.abort();
            }
        }

        /*
         * 获取Product剩余的Item。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(productServiceUrlStr + "products/" + productId + "/items"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonArray productItems = Json.createReader(response.getEntity().getContent()).readArray();
                    assertNotNull(productItems);
                    assertEquals(1, productItems.size());

                    {
                        JsonObject trivItem = productItems.getJsonObject(0);
                        assertNotNull(trivItem);

                        assertNotNull(trivItem.getString(Introspection.JSONKeys.UUID));
                        assertEquals(Introspection.JSONValues.SUB_TYPE_TRIV_ITEM,
                            trivItem.getString(Introspection.JSONKeys.SUB_TYPE));
                        assertFalse(trivItem.getString(Introspection.JSONKeys.UUID).equals(trivItemId));
                        JsonObject bidding = trivItem.getJsonObject(Introspection.JSONKeys.BIDDING);
                        assertNotNull(bidding);
                        assertNotNull(bidding.getString(Introspection.JSONKeys.TITLE));
                        assertNotNull(bidding.getJsonNumber(Introspection.JSONKeys.PRICE));
                        assertEquals("随身Wifi", bidding.getString(Introspection.JSONKeys.TITLE));
                        assertEquals(108.8, bidding.getJsonNumber(Introspection.JSONKeys.PRICE).doubleValue(), 0.1);
                    }
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
                get.abort();
            }
        }

        /*
         * 发布Draft状态的Product，必然失败。
         */
        {
            HttpPost post = new HttpPost();
            post.setURI(new URI(productServiceUrlStr + "products/public/" + productId));
            post.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            post.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                assertEquals(HttpStatus.CONFLICT_409, statusCode);
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                post.abort();
            }
        }

        /*
         * 根据Proposal Owner获取Draft状态的Product
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(productServiceUrlStr + "products/my/"
                + Introspection.JSONValues.DRAFT_PRODUCT.enumValue()));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                assertEquals(HttpStatus.CONFLICT_409, statusCode);
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                get.abort();
            }
        }

        /*
         * 根据Product Owner获取Draft状态的Product
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(productServiceUrlStr + "products/my/"
                + Introspection.JSONValues.DRAFT_PRODUCT.enumValue()));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonArray products = Json.createReader(response.getEntity().getContent()).readArray();
                    assertNotNull(products);
                    assertTrue(products.size() >= 1);
                    JsonObject product = products.getJsonObject(0);
                    assertEquals(productId, product.getString(Introspection.JSONKeys.UUID));
                    assertEquals(60, product.getInt(Introspection.JSONKeys.GROUP_CAPACITY));
                    assertEquals(enrollmentDeadlineDatetime.getTimeInMillis(),
                        product.getJsonNumber(Introspection.JSONKeys.DEADLINE_DATETIME).longValue());
                    assertEquals(departureDatetime.getTimeInMillis(),
                        product.getJsonNumber(Introspection.JSONKeys.DEPARTURE_DATETIME).longValue());
                    JsonArray productItems = product.getJsonArray(Introspection.JSONKeys.ITEMS);
                    assertEquals(0, productItems.size());
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
                get.abort();
            }
        }

        /*
         * 提交Product，Product状态从Draft自动更新为Private。
         */
        {
            HttpPut put = new HttpPut();
            put.setURI(new URI(productServiceUrlStr + "products/" + productId));
            put.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            put.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(put);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject product = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(product);
                    String productUuid = product.getString(Introspection.JSONKeys.UUID);
                    assertNotNull(productUuid);
                    assertEquals(productId, productUuid);
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

        /*
         * 根据Proposal Owner获取Private状态的Product
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(productServiceUrlStr + "products/my/"
                + Introspection.JSONValues.PRIVATE_PRODUCT.enumValue()));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                assertEquals(HttpStatus.CONFLICT_409, statusCode);
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                get.abort();
            }
        }

        /*
         * 根据Product Owner获取Private状态的Product
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(productServiceUrlStr + "products/my/"
                + Introspection.JSONValues.PRIVATE_PRODUCT.enumValue()));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonArray products = Json.createReader(response.getEntity().getContent()).readArray();
                    assertNotNull(products);
                    assertTrue(products.size() >= 1);
                    JsonObject product = products.getJsonObject(0);
                    assertEquals(productId, product.getString(Introspection.JSONKeys.UUID));
                    assertEquals(60, product.getInt(Introspection.JSONKeys.GROUP_CAPACITY));
                    assertEquals(enrollmentDeadlineDatetime.getTimeInMillis(),
                        product.getJsonNumber(Introspection.JSONKeys.DEADLINE_DATETIME).longValue());
                    assertEquals(departureDatetime.getTimeInMillis(),
                        product.getJsonNumber(Introspection.JSONKeys.DEPARTURE_DATETIME).longValue());
                    JsonArray productItems = product.getJsonArray(Introspection.JSONKeys.ITEMS);
                    assertEquals(0, productItems.size());
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
                get.abort();
            }
        }

        /*
         * 发布Product。
         */
        {
            HttpPost post = new HttpPost();
            post.setURI(new URI(productServiceUrlStr + "products/public/" + productId));
            post.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            post.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject product = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(product);
                    String productUuid = product.getString(Introspection.JSONKeys.UUID);
                    assertNotNull(productUuid);
                    assertEquals(productId, productUuid);
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
                post.abort();
            }
        }

        /*
         * Sleep for 3 seconds and let the search engine ready for query of the
         * newly published product.
         */
        Thread.sleep(3000);

        /*
         * 检索所有Product。
         */
        {
            JsonObjectBuilder searchCriteria = Json.createObjectBuilder();
            searchCriteria.add(Introspection.JSONKeys.TERM, "*");
            searchCriteria.add(Introspection.JSONKeys.TEMPLATE, Introspection.JSONValues.TEST_TEMPLACE_AS_INT);
            searchCriteria.add(Introspection.JSONKeys.PAGE_NUM, 0);
            searchCriteria.add(Introspection.JSONKeys.PAGE_SIZE, 2);

            HttpPut put = new HttpPut();
            put.setEntity(new StringEntity(searchCriteria.build().toString(), ContentType.APPLICATION_JSON));
            put.setURI(new URI(productServiceUrlStr + "products/"));
            put.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            put.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(put);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject products = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(products);
                    long hitsNumber = products.getJsonNumber(Introspection.JSONKeys.TOTAL_HITS_NUMBER).longValue();
                    JsonArray hits = products.getJsonArray(Introspection.JSONKeys.HITS);
                    assertTrue(hitsNumber > 0);
                    assertNotNull(hits);
                    assertTrue(hits.size() > 0);
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

        /*
         * 根据Product Owner获取Public状态的Product
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(productServiceUrlStr + "products/my/"
                + Introspection.JSONValues.PUBLIC_PRODUCT.enumValue()));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonArray products = Json.createReader(response.getEntity().getContent()).readArray();
                    assertNotNull(products);
                    assertTrue(products.size() >= 1);
                    JsonObject product = products.getJsonObject(0);
                    assertEquals(productId, product.getString(Introspection.JSONKeys.UUID));
                    assertEquals(60, product.getInt(Introspection.JSONKeys.GROUP_CAPACITY));
                    assertEquals(enrollmentDeadlineDatetime.getTimeInMillis(),
                        product.getJsonNumber(Introspection.JSONKeys.DEADLINE_DATETIME).longValue());
                    assertEquals(departureDatetime.getTimeInMillis(),
                        product.getJsonNumber(Introspection.JSONKeys.DEPARTURE_DATETIME).longValue());
                    JsonArray productItems = product.getJsonArray(Introspection.JSONKeys.ITEMS);
                    assertEquals(4, productItems.size());
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
                get.abort();
            }
        }

        /*
         * 根据Proposal Owner获取Public状态的Product
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(productServiceUrlStr + "products/my/"
                + Introspection.JSONValues.PUBLIC_PRODUCT.enumValue()));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonArray products = Json.createReader(response.getEntity().getContent()).readArray();
                    assertNotNull(products);
                    assertTrue(products.size() >= 1);
                    JsonObject product = products.getJsonObject(0);
                    assertEquals(productId, product.getString(Introspection.JSONKeys.UUID));
                    assertEquals(60, product.getInt(Introspection.JSONKeys.GROUP_CAPACITY));
                    assertEquals(enrollmentDeadlineDatetime.getTimeInMillis(),
                        product.getJsonNumber(Introspection.JSONKeys.DEADLINE_DATETIME).longValue());
                    assertEquals(departureDatetime.getTimeInMillis(),
                        product.getJsonNumber(Introspection.JSONKeys.DEPARTURE_DATETIME).longValue());
                    JsonArray productItems = product.getJsonArray(Introspection.JSONKeys.ITEMS);
                    assertEquals(4, productItems.size());
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
                get.abort();
            }
        }

        /*
         * 根据目的地检索Product。
         */
        {
            JsonObjectBuilder searchCriteria = Json.createObjectBuilder();
            searchCriteria.add(Introspection.JSONKeys.TERM, "Taibei");
            searchCriteria.add(Introspection.JSONKeys.TEMPLATE,
                Introspection.JSONValues.PRODUCT_DESTINATION_TEMPLATE_AS_INT);
            searchCriteria.add(Introspection.JSONKeys.PAGE_NUM, 0);
            searchCriteria.add(Introspection.JSONKeys.PAGE_SIZE, 2);

            HttpPut put = new HttpPut();
            put.setEntity(new StringEntity(searchCriteria.build().toString(), ContentType.APPLICATION_JSON));
            put.setURI(new URI(productServiceUrlStr + "products/"));
            put.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            put.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(put);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject products = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(products);
                    long hitsNumber = products.getJsonNumber(Introspection.JSONKeys.TOTAL_HITS_NUMBER).longValue();
                    JsonArray hits = products.getJsonArray(Introspection.JSONKeys.HITS);
                    assertTrue(hitsNumber > 0);
                    assertNotNull(hits);
                    assertTrue(hits.size() > 0);
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

        /*
         * 根据目的地拼音检索Product。
         */
        {
            JsonObjectBuilder searchCriteria = Json.createObjectBuilder();
            searchCriteria.add(Introspection.JSONKeys.TERM, "taibei");
            searchCriteria.add(Introspection.JSONKeys.TEMPLATE,
                Introspection.JSONValues.PRODUCT_DESTINATION_TEMPLATE_AS_INT);

            HttpPut put = new HttpPut();
            put.setEntity(new StringEntity(searchCriteria.build().toString(), ContentType.APPLICATION_JSON));
            put.setURI(new URI(productServiceUrlStr + "products/"));
            put.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            put.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(put);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject products = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(products);
                    long hitsNumber = products.getJsonNumber(Introspection.JSONKeys.TOTAL_HITS_NUMBER).longValue();
                    JsonArray hits = products.getJsonArray(Introspection.JSONKeys.HITS);
                    assertTrue(hitsNumber > 0);
                    assertNotNull(hits);
                    assertTrue(hits.size() > 0);
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

        /*
         * 根据目的地中文名检索Product。
         */
        {
            JsonObjectBuilder searchCriteria = Json.createObjectBuilder();
            searchCriteria.add(Introspection.JSONKeys.TERM, "台北");
            searchCriteria.add(Introspection.JSONKeys.TEMPLATE,
                Introspection.JSONValues.PRODUCT_DESTINATION_TEMPLATE_AS_INT);
            searchCriteria.add(Introspection.JSONKeys.PAGE_NUM, 0);
            searchCriteria.add(Introspection.JSONKeys.PAGE_SIZE, 2);
            searchCriteria.add(Introspection.JSONKeys.SORT_KEY, Introspection.JSONKeys.DEPARTURE_DATETIME);
            searchCriteria.add(Introspection.JSONKeys.SORT_ORDER, Introspection.JSONValues.SORT_ASC_ORDER_AS_INT);
            searchCriteria.add(Introspection.JSONKeys.SORT_TYPE, Introspection.JSONValues.SORT_LONG_TYPE_AS_INT);

            HttpPut put = new HttpPut();
            put.setEntity(new StringEntity(searchCriteria.build().toString(), ContentType.APPLICATION_JSON));
            put.setURI(new URI(productServiceUrlStr + "products/"));
            put.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            put.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(put);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject products = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(products);
                    long hitsNumber = products.getJsonNumber(Introspection.JSONKeys.TOTAL_HITS_NUMBER).longValue();
                    JsonArray hits = products.getJsonArray(Introspection.JSONKeys.HITS);
                    assertTrue(hitsNumber > 0);
                    assertNotNull(hits);
                    assertTrue(hits.size() > 0);
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

        /*
         * 根据出发地检索Product。
         */
        {
            JsonObjectBuilder searchCriteria = Json.createObjectBuilder();
            searchCriteria.add(Introspection.JSONKeys.TERM, "Barcelona");
            searchCriteria.add(Introspection.JSONKeys.TEMPLATE,
                Introspection.JSONValues.PRODUCT_DEPARTURE_TEMPLATE_AS_STR);
            searchCriteria.add(Introspection.JSONKeys.PAGE_NUM, 0);
            searchCriteria.add(Introspection.JSONKeys.PAGE_SIZE, 2);

            HttpPut put = new HttpPut();
            put.setEntity(new StringEntity(searchCriteria.build().toString(), ContentType.APPLICATION_JSON));
            put.setURI(new URI(productServiceUrlStr + "products/"));
            put.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            put.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(put);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject products = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(products);
                    long hitsNumber = products.getJsonNumber(Introspection.JSONKeys.TOTAL_HITS_NUMBER).longValue();
                    JsonArray hits = products.getJsonArray(Introspection.JSONKeys.HITS);
                    assertTrue(hitsNumber > 0);
                    assertNotNull(hits);
                    assertTrue(hits.size() > 0);
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

        /*
         * 根据出发地拼音检索Product。
         */
        {
            JsonObjectBuilder searchCriteria = Json.createObjectBuilder();
            searchCriteria.add(Introspection.JSONKeys.TERM, "basailuona");
            searchCriteria.add(Introspection.JSONKeys.TEMPLATE,
                Introspection.JSONValues.PRODUCT_DEPARTURE_TEMPLATE_AS_STR);

            HttpPut put = new HttpPut();
            put.setEntity(new StringEntity(searchCriteria.build().toString(), ContentType.APPLICATION_JSON));
            put.setURI(new URI(productServiceUrlStr + "products/"));
            put.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            put.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(put);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject products = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(products);
                    long hitsNumber = products.getJsonNumber(Introspection.JSONKeys.TOTAL_HITS_NUMBER).longValue();
                    JsonArray hits = products.getJsonArray(Introspection.JSONKeys.HITS);
                    assertTrue(hitsNumber > 0);
                    assertNotNull(hits);
                    assertTrue(hits.size() > 0);
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

        /*
         * 根据出发地中文名检索Product。
         */
        {
            JsonObjectBuilder searchCriteria = Json.createObjectBuilder();
            searchCriteria.add(Introspection.JSONKeys.TERM, "巴塞罗纳");
            searchCriteria.add(Introspection.JSONKeys.TEMPLATE,
                Introspection.JSONValues.PRODUCT_DEPARTURE_TEMPLATE_AS_STR);
            searchCriteria.add(Introspection.JSONKeys.PAGE_NUM, 0);
            searchCriteria.add(Introspection.JSONKeys.PAGE_SIZE, 2);
            searchCriteria.add(Introspection.JSONKeys.SORT_KEY, Introspection.JSONKeys.DEPARTURE_DATETIME);
            searchCriteria.add(Introspection.JSONKeys.SORT_ORDER, Introspection.JSONValues.SORT_ASC_ORDER_AS_STR);
            searchCriteria.add(Introspection.JSONKeys.SORT_TYPE, Introspection.JSONValues.SORT_LONG_TYPE_AS_STR);

            HttpPut put = new HttpPut();
            put.setEntity(new StringEntity(searchCriteria.build().toString(), ContentType.APPLICATION_JSON));
            put.setURI(new URI(productServiceUrlStr + "products/"));
            put.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            put.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(put);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject products = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(products);
                    long hitsNumber = products.getJsonNumber(Introspection.JSONKeys.TOTAL_HITS_NUMBER).longValue();
                    JsonArray hits = products.getJsonArray(Introspection.JSONKeys.HITS);
                    assertTrue(hitsNumber > 0);
                    assertNotNull(hits);
                    assertTrue(hits.size() > 0);
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

        /*
         * 根据出发地中文名检索Product, 太阳作为出发地，无法匹配任何产品。
         */
        {
            JsonObjectBuilder searchCriteria = Json.createObjectBuilder();
            searchCriteria.add(Introspection.JSONKeys.TERM, "太阳");
            searchCriteria.add(Introspection.JSONKeys.TEMPLATE,
                Introspection.JSONValues.PRODUCT_DEPARTURE_TEMPLATE_AS_STR);

            HttpPut put = new HttpPut();
            put.setEntity(new StringEntity(searchCriteria.build().toString(), ContentType.APPLICATION_JSON));
            put.setURI(new URI(productServiceUrlStr + "products/"));
            put.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            put.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(put);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject products = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(products);
                    long hitsNumber = products.getJsonNumber(Introspection.JSONKeys.TOTAL_HITS_NUMBER).longValue();
                    JsonArray hits = products.getJsonArray(Introspection.JSONKeys.HITS);
                    assertTrue(hitsNumber == 0 || hitsNumber == 1000);
                    assertNotNull(hits);
                    assertTrue(hits.size() == 0 || hits.size() == 1);
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

        /*
         * 根据出发地中文名检索Product，无检索词导致无效搜索请求。
         */
        {
            JsonObjectBuilder searchCriteria = Json.createObjectBuilder();
            searchCriteria.add(Introspection.JSONKeys.TEMPLATE,
                Introspection.JSONValues.PRODUCT_DEPARTURE_TEMPLATE_AS_STR);

            HttpPut put = new HttpPut();
            put.setEntity(new StringEntity(searchCriteria.build().toString(), ContentType.APPLICATION_JSON));
            put.setURI(new URI(productServiceUrlStr + "products/"));
            put.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            put.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(put);
                int statusCode = response.getStatusLine().getStatusCode();
                assertEquals(HttpStatus.BAD_REQUEST_400, statusCode);
                JsonObject error = Json.createReader(response.getEntity().getContent()).readObject();
                assertNotNull(error);
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                put.abort();
            }
        }

        /*
         * 根据出发地中文名检索Product，无效检索模板导致无效搜索请求。
         */
        {
            JsonObjectBuilder searchCriteria = Json.createObjectBuilder();
            searchCriteria.add(Introspection.JSONKeys.TERM, "巴塞罗纳");
            searchCriteria.add(Introspection.JSONKeys.TEMPLATE, "missing_template");

            HttpPut put = new HttpPut();
            put.setEntity(new StringEntity(searchCriteria.build().toString(), ContentType.APPLICATION_JSON));
            put.setURI(new URI(productServiceUrlStr + "products/"));
            put.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            put.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(put);
                int statusCode = response.getStatusLine().getStatusCode();
                assertEquals(HttpStatus.BAD_REQUEST_400, statusCode);
                JsonObject error = Json.createReader(response.getEntity().getContent()).readObject();
                assertNotNull(error);
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                put.abort();
            }
        }

        /*
         * 根据出发地中文名检索Product，非法排序类型导致无效搜索请求。
         */
        {
            JsonObjectBuilder searchCriteria = Json.createObjectBuilder();
            searchCriteria.add(Introspection.JSONKeys.TERM, "巴塞罗纳");
            searchCriteria.add(Introspection.JSONKeys.TEMPLATE,
                Introspection.JSONValues.PRODUCT_DEPARTURE_TEMPLATE_AS_STR);
            searchCriteria.add(Introspection.JSONKeys.SORT_TYPE, "string");

            HttpPut put = new HttpPut();
            put.setEntity(new StringEntity(searchCriteria.build().toString(), ContentType.APPLICATION_JSON));
            put.setURI(new URI(productServiceUrlStr + "products/"));
            put.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            put.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(put);
                int statusCode = response.getStatusLine().getStatusCode();
                assertEquals(HttpStatus.BAD_REQUEST_400, statusCode);
                JsonObject error = Json.createReader(response.getEntity().getContent()).readObject();
                assertNotNull(error);
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                put.abort();
            }
        }

        /*
         * 取消发布Product。
         */
        {
            HttpDelete delete = new HttpDelete();
            delete.setURI(new URI(productServiceUrlStr + "products/public/" + productId));
            delete.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            delete.setHeader(HttpHeaders.AUTHORIZATION, Introspection.TestValues.DEFAULT_AGENCY_ACCOUNT);
            try {
                HttpResponse response = httpClient.execute(delete);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject product = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(product);
                    String productUuid = product.getString(Introspection.JSONKeys.UUID);
                    assertNotNull(productUuid);
                    assertEquals(productId, productUuid);
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
                delete.abort();
            }
        }
    }
}
