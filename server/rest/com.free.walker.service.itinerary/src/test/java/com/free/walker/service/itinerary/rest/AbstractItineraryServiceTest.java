package com.free.walker.service.itinerary.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.ProcessingException;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.free.walker.service.itinerary.primitive.Introspection;

public abstract class AbstractItineraryServiceTest extends BaseServiceUrlProvider {
    private HttpClient httpClient;

    private JsonObject proposal;
    private JsonObject itinerary;
    private JsonObject hotelReq;
    private JsonObject trafficReq;
    private JsonObjectBuilder updatedRequirementBuilder;

    private String proposalId;
    private String itineraryId1st;
    private String itineraryId2nd;
    private String requirementId1st;
    private String requirementId2nd;

    private JsonArray agencyIds;

    protected String itineraryServiceUrlStr;
    protected String platformServiceUrlStr;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() {
        httpClient = HttpClientBuilder.create().build();

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
        }

        {
            JsonObjectBuilder requirementBuilder = Json.createObjectBuilder();
            requirementBuilder.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY);

            JsonObjectBuilder destinationBuilder = Json.createObjectBuilder();
            JsonObjectBuilder destCityBuilder = Json.createObjectBuilder();
            destCityBuilder.add(Introspection.JSONKeys.UUID, "b4cef473-1ad7-46cd-8ea5-d50bfa3ca033");
            destinationBuilder.add(Introspection.JSONKeys.CITY, destCityBuilder);
            requirementBuilder.add(Introspection.JSONKeys.DESTINATION, destinationBuilder);

            JsonObjectBuilder departureBuilder = Json.createObjectBuilder();
            JsonObjectBuilder deptCityBuilder = Json.createObjectBuilder();
            deptCityBuilder.add(Introspection.JSONKeys.UUID, "79fd8642-a11d-4811-887d-ec4268097a82");
            departureBuilder.add(Introspection.JSONKeys.CITY, deptCityBuilder);
            requirementBuilder.add(Introspection.JSONKeys.DEPARTURE, departureBuilder);
            itinerary = requirementBuilder.build();
        }

        {
            JsonObjectBuilder requirementBuilder = Json.createObjectBuilder();
            requirementBuilder.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT);
            requirementBuilder.add(Introspection.JSONKeys.SUB_TYPE, Introspection.JSONValues.SUB_TYPE_HOTEL);
            requirementBuilder.add(Introspection.JSONKeys.NIGHT, 10);
            hotelReq = requirementBuilder.build();
        }

        {
            JsonObjectBuilder requirementBuilder = Json.createObjectBuilder();
            requirementBuilder.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT);
            requirementBuilder.add(Introspection.JSONKeys.SUB_TYPE, Introspection.JSONValues.SUB_TYPE_TRAFFIC);
            requirementBuilder.add(Introspection.JSONKeys.TRAFFIC_TOOL_TYPE,
                Introspection.JSONValues.TRAFFIC_TOOL_TYPE_FLIGHT.enumValue());

            JsonArrayBuilder dateTimeSelections = Json.createArrayBuilder();
            JsonObjectBuilder dateTime1 = Json.createObjectBuilder();
            dateTime1.add(Introspection.JSONKeys.TIME_RANGE_START,
                Introspection.JSONValues.TIME_RANGE_06_12.realValue());
            dateTime1.add(Introspection.JSONKeys.TIME_RANGE_OFFSET,
                Introspection.JSONValues.TIME_RANGE_06_12.imaginaryValue());
            JsonObjectBuilder dateTime2 = Json.createObjectBuilder();
            dateTime2.add(Introspection.JSONKeys.TIME_RANGE_START,
                Introspection.JSONValues.TIME_RANGE_18_24.realValue());
            dateTime2.add(Introspection.JSONKeys.TIME_RANGE_OFFSET,
                Introspection.JSONValues.TIME_RANGE_18_24.imaginaryValue());
            dateTimeSelections.add(dateTime1);
            dateTimeSelections.add(dateTime2);
            requirementBuilder.add(Introspection.JSONKeys.DATETIME_RANGE_SELECTIONS, dateTimeSelections);
            trafficReq = requirementBuilder.build();
        }

        {
            updatedRequirementBuilder = Json.createObjectBuilder();
            updatedRequirementBuilder.add(Introspection.JSONKeys.TYPE,
                Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT);
            updatedRequirementBuilder.add(Introspection.JSONKeys.SUB_TYPE, Introspection.JSONValues.SUB_TYPE_HOTEL);
            updatedRequirementBuilder.add(Introspection.JSONKeys.NIGHT, 12);
        }

        {
            HttpPost post = new HttpPost();

            try {
                InputStream is = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("com/free/walker/service/itinerary/rest/agencies.json");
                post.setEntity(new StringEntity(Json.createReader(is).readObject().toString(), ContentType.APPLICATION_JSON));
                post.setURI(new URI(platformServiceUrlStr + "agencies?batch=true"));
                post.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
                HttpResponse response = httpClient.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    agencyIds = Json.createReader(response.getEntity().getContent()).readObject()
                        .getJsonArray(Introspection.JSONKeys.UUID);
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
    }

    @Test
    public void testAll() throws URISyntaxException, InterruptedException {
        /*
         * 新建一个Proposal，同时添加一个初始的Itinerary。
         */
        {
            HttpPost post = new HttpPost();
            post.setEntity(new StringEntity(proposal.toString(), ContentType.APPLICATION_JSON));
            post.setURI(new URI(itineraryServiceUrlStr + "proposals/"));
            post.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = httpClient.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject proposal = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(proposal);
                    proposalId = proposal.getString(Introspection.JSONKeys.UUID);
                    assertNotNull(proposalId);
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
         * 获取刚新建的Proposal。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(itineraryServiceUrlStr + "proposals/" + proposalId));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject proposal = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(proposal);
                    assertEquals(proposalId, proposal.getString(Introspection.JSONKeys.UUID));
                    assertEquals(Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL,
                        proposal.getString(Introspection.JSONKeys.TYPE));

                    JsonArray requirements = proposal.getJsonArray(Introspection.JSONKeys.REQUIREMENTS);
                    assertEquals(0, requirements.size());
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
         * 获取Proposal中的所有Itinerary。此时，Proposal中包含初始的Itinerary。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(itineraryServiceUrlStr + "itineraries/" + proposalId + "?requirementType=proposal"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonArray itineries = Json.createReader(response.getEntity().getContent()).readArray();
                    assertNotNull(itineries);
                    assertEquals(1, itineries.size());

                    JsonObject itineraryRequirement = itineries.getJsonObject(0);
                    assertNotNull(itineraryRequirement);
                    itineraryId1st = itineraryRequirement.getString(Introspection.JSONKeys.UUID);
                    
                    JsonObject departure = itineraryRequirement.getJsonObject(Introspection.JSONKeys.DEPARTURE);
                    assertNotNull(departure);
                    JsonObject departureCity = departure.getJsonObject(Introspection.JSONKeys.CITY);
                    assertNotNull(departureCity);
                    assertEquals("Barcelona", departureCity.getString(Introspection.JSONKeys.NAME));
                    assertEquals("巴塞罗纳", departureCity.getString(Introspection.JSONKeys.CHINESE_NAME));
                    assertEquals("basailuona", departureCity.getString(Introspection.JSONKeys.PINYIN_NAME));
                    
                    JsonObject destination = itineraryRequirement.getJsonObject(Introspection.JSONKeys.DESTINATION);
                    assertNotNull(destination);
                    JsonObject destinationCity = destination.getJsonObject(Introspection.JSONKeys.CITY);
                    assertNotNull(destinationCity);
                    assertEquals("Taibei", destinationCity.getString(Introspection.JSONKeys.NAME));
                    assertEquals("台北", destinationCity.getString(Introspection.JSONKeys.CHINESE_NAME));
                    assertEquals("taibei", destinationCity.getString(Introspection.JSONKeys.PINYIN_NAME));
                }
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                get.abort();
            }
        }

        /*
         * 添加一个Itinerary。此时，为刚新建的Proposal添加一个Itinerary。
         */
        {
            HttpPost post = new HttpPost();
            post.setEntity(new StringEntity(itinerary.toString(), ContentType.APPLICATION_JSON));
            post.setURI(new URI(itineraryServiceUrlStr + "itineraries/" + proposalId));
            post.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = httpClient.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject itinerary = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(itinerary);
                    itineraryId2nd = itinerary.getString(Introspection.JSONKeys.UUID);
                    assertNotNull(itineraryId2nd);
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
         * 获取一个Itinerary。此时，即获取刚新建的Itinerary。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(itineraryServiceUrlStr + "itineraries/" + itineraryId2nd + "?requirementType=itinerary"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject itinerary = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(itinerary);
                    assertEquals(itineraryId2nd, itinerary.getString(Introspection.JSONKeys.UUID));
                    assertEquals(Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY,
                        itinerary.getString(Introspection.JSONKeys.TYPE));

                    JsonObject departure = itinerary.getJsonObject(Introspection.JSONKeys.DEPARTURE);
                    assertNotNull(departure);
                    JsonObject departureCity = departure.getJsonObject(Introspection.JSONKeys.CITY);
                    assertNotNull(departureCity);
                    assertEquals("Wuhan", departureCity.getString(Introspection.JSONKeys.NAME));
                    assertEquals("武汉", departureCity.getString(Introspection.JSONKeys.CHINESE_NAME));
                    assertEquals("wuhan", departureCity.getString(Introspection.JSONKeys.PINYIN_NAME));

                    JsonObject destination = itinerary.getJsonObject(Introspection.JSONKeys.DESTINATION);
                    assertNotNull(destination);
                    JsonObject destinationCity = destination.getJsonObject(Introspection.JSONKeys.CITY);
                    assertNotNull(destinationCity);
                    assertEquals("Guangzhou", destinationCity.getString(Introspection.JSONKeys.NAME));
                    assertEquals("广州", destinationCity.getString(Introspection.JSONKeys.CHINESE_NAME));
                    assertEquals("guangzhou", destinationCity.getString(Introspection.JSONKeys.PINYIN_NAME));
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
         * 为Proposal中的最后一个Itinerary添加一个Requirement。此时，即为刚新添的Itinerary添加一个Requirement。
         */
        {
            HttpPost post = new HttpPost();
            post.setEntity(new StringEntity(hotelReq.toString(), ContentType.APPLICATION_JSON));
            post.setURI(new URI(itineraryServiceUrlStr + "requirements/" + proposalId));
            post.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = httpClient.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject requirement = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(requirement);
                    requirementId1st = requirement.getString(Introspection.JSONKeys.UUID);
                    assertNotNull(requirementId1st);
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
         * 获取一个Requirement。此时，即获取刚添加的Requirement。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(itineraryServiceUrlStr + "requirements/" + requirementId1st));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject requirement = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(requirement);
                    assertEquals(requirementId1st, requirement.getString(Introspection.JSONKeys.UUID));
                    assertEquals(Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT,
                        requirement.getString(Introspection.JSONKeys.TYPE));
                    assertEquals(Introspection.JSONValues.SUB_TYPE_HOTEL,
                        requirement.getString(Introspection.JSONKeys.SUB_TYPE));
                    assertEquals(10, requirement.getInt(Introspection.JSONKeys.NIGHT));
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
         * 获取Proposal中的所有Itinerary。此时，Proposal中包含两个Itinerary。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(itineraryServiceUrlStr + "itineraries/" + proposalId + "?requirementType=proposal"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonArray itineries = Json.createReader(response.getEntity().getContent()).readArray();
                    assertNotNull(itineries);
                    assertEquals(2, itineries.size());

                    {
                        JsonObject itineraryRequirement = itineries.getJsonObject(0);
                        assertNotNull(itineraryRequirement);

                        assertEquals(itineraryId1st, itineraryRequirement.getString(Introspection.JSONKeys.UUID));

                        JsonObject departure = itineraryRequirement.getJsonObject(Introspection.JSONKeys.DEPARTURE);
                        assertNotNull(departure);
                        JsonObject departureCity = departure.getJsonObject(Introspection.JSONKeys.CITY);
                        assertNotNull(departureCity);
                        assertEquals("Barcelona", departureCity.getString(Introspection.JSONKeys.NAME));
                        assertEquals("巴塞罗纳", departureCity.getString(Introspection.JSONKeys.CHINESE_NAME));
                        assertEquals("basailuona", departureCity.getString(Introspection.JSONKeys.PINYIN_NAME));

                        JsonObject destination = itineraryRequirement.getJsonObject(Introspection.JSONKeys.DESTINATION);
                        assertNotNull(destination);
                        JsonObject destinationCity = destination.getJsonObject(Introspection.JSONKeys.CITY);
                        assertNotNull(destinationCity);
                        assertEquals("Taibei", destinationCity.getString(Introspection.JSONKeys.NAME));
                        assertEquals("台北", destinationCity.getString(Introspection.JSONKeys.CHINESE_NAME));
                        assertEquals("taibei", destinationCity.getString(Introspection.JSONKeys.PINYIN_NAME));
                    }

                    {
                        JsonObject itineraryRequirement = itineries.getJsonObject(1);
                        assertNotNull(itineraryRequirement);

                        assertEquals(itineraryId2nd, itineraryRequirement.getString(Introspection.JSONKeys.UUID));

                        JsonObject departure = itineraryRequirement.getJsonObject(Introspection.JSONKeys.DEPARTURE);
                        assertNotNull(departure);
                        JsonObject departureCity = departure.getJsonObject(Introspection.JSONKeys.CITY);
                        assertNotNull(departureCity);
                        assertEquals("Wuhan", departureCity.getString(Introspection.JSONKeys.NAME));
                        assertEquals("武汉", departureCity.getString(Introspection.JSONKeys.CHINESE_NAME));
                        assertEquals("wuhan", departureCity.getString(Introspection.JSONKeys.PINYIN_NAME));

                        JsonObject destination = itineraryRequirement.getJsonObject(Introspection.JSONKeys.DESTINATION);
                        assertNotNull(destination);
                        JsonObject destinationCity = destination.getJsonObject(Introspection.JSONKeys.CITY);
                        assertNotNull(destinationCity);
                        assertEquals("Guangzhou", destinationCity.getString(Introspection.JSONKeys.NAME));
                        assertEquals("广州", destinationCity.getString(Introspection.JSONKeys.CHINESE_NAME));
                        assertEquals("guangzhou", destinationCity.getString(Introspection.JSONKeys.PINYIN_NAME));
                    }
                }
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                get.abort();
            }
        }

        /*
         * 为Proposal中的第一个Itinerary添加一个Requirement。此时，即为初始的Itinerary添加一个Requirement。
         */
        {
            HttpPost post = new HttpPost();
            post.setEntity(new StringEntity(trafficReq.toString(), ContentType.APPLICATION_JSON));
            post.setURI(new URI(itineraryServiceUrlStr + "requirements/" + proposalId + "/" + itineraryId1st));
            post.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = httpClient.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject requirement = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(requirement);
                    requirementId2nd = requirement.getString(Introspection.JSONKeys.UUID);
                    assertNotNull(requirementId2nd);
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
         * 获取一个Requirement。此时，即获取刚添加的Requirement。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(itineraryServiceUrlStr + "requirements/" + requirementId2nd));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject requirement = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(requirement);
                    assertEquals(requirementId2nd, requirement.getString(Introspection.JSONKeys.UUID));
                    assertEquals(Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT,
                        requirement.getString(Introspection.JSONKeys.TYPE));
                    assertEquals(Introspection.JSONValues.SUB_TYPE_TRAFFIC,
                        requirement.getString(Introspection.JSONKeys.SUB_TYPE));
                    assertEquals(Introspection.JSONValues.TRAFFIC_TOOL_TYPE_FLIGHT.enumValue(),
                        requirement.getInt(Introspection.JSONKeys.TRAFFIC_TOOL_TYPE));

                    JsonArray datetimeRangeSelections = requirement
                        .getJsonArray(Introspection.JSONKeys.DATETIME_RANGE_SELECTIONS);
                    assertNotNull(datetimeRangeSelections);
                    assertEquals(2, datetimeRangeSelections.size());

                    JsonObject datetimeRangeSelection1 = datetimeRangeSelections.getJsonObject(0);
                    assertEquals(6, datetimeRangeSelection1.getInt(Introspection.JSONKeys.TIME_RANGE_START));
                    assertEquals(6, datetimeRangeSelection1.getInt(Introspection.JSONKeys.TIME_RANGE_OFFSET));

                    JsonObject datetimeRangeSelection2 = datetimeRangeSelections.getJsonObject(1);
                    assertEquals(18, datetimeRangeSelection2.getInt(Introspection.JSONKeys.TIME_RANGE_START));
                    assertEquals(6, datetimeRangeSelection2.getInt(Introspection.JSONKeys.TIME_RANGE_OFFSET));
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
         * 更新一个Requirement。此时，即更新第一个添加的Requirement。
         */
        {
            updatedRequirementBuilder.add(Introspection.JSONKeys.UUID, requirementId1st);
            HttpPut put = new HttpPut();
            put.setEntity(new StringEntity(updatedRequirementBuilder.build().toString(), ContentType.APPLICATION_JSON));
            put.setURI(new URI(itineraryServiceUrlStr + "requirements/"));
            put.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = httpClient.execute(put);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject requirement = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(requirement);
                    requirementId1st = requirement.getString(Introspection.JSONKeys.UUID);
                    assertNotNull(requirementId1st);
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
         * 获取指定Proposal和Itinerary的所有Requirement。此时，即获取第二个Itinerary的所有Requirement。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(itineraryServiceUrlStr + "requirements/" + proposalId + "/" + itineraryId2nd));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonArray requirements = Json.createReader(response.getEntity().getContent()).readArray();
                    assertNotNull(requirements);
                    assertEquals(1, requirements.size());

                    JsonObject requirement = requirements.getJsonObject(0);
                    assertEquals(requirementId1st, requirement.getString(Introspection.JSONKeys.UUID));
                    assertEquals(Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT,
                        requirement.getString(Introspection.JSONKeys.TYPE));
                    assertEquals(Introspection.JSONValues.SUB_TYPE_HOTEL,
                        requirement.getString(Introspection.JSONKeys.SUB_TYPE));
                    assertEquals(12, requirement.getInt(Introspection.JSONKeys.NIGHT));
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
         * 删除一个Requirement。
         */
        {
            HttpDelete delete = new HttpDelete();
            delete.setURI(new URI(itineraryServiceUrlStr + "requirements/" + proposalId + "/" + requirementId1st));
            delete.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = httpClient.execute(delete);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject requirement = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(requirement);
                    assertEquals(requirementId1st, requirement.getString(Introspection.JSONKeys.UUID));
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
         * 获取刚删除的Requirement。此时，需求已删除，无法获取。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(itineraryServiceUrlStr + "requirements/" + requirementId1st));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                assertEquals(HttpStatus.NOT_FOUND_404, statusCode);
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                get.abort();
            }
        }

        /*
         * 删除一个Itinerary,Itinerary所包含的所有Requirement也会被删除。此时，初始的Itinerary被删除。
         */
        {
            HttpDelete delete = new HttpDelete();
            delete.setURI(new URI(itineraryServiceUrlStr + "itineraries/" + proposalId + "/" + itineraryId1st));
            delete.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = httpClient.execute(delete);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject requirement = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(requirement);
                    assertEquals(itineraryId1st, requirement.getString(Introspection.JSONKeys.UUID));
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
         * 获取Proposal中的所有Itinerary。此时，Proposal中仅剩一个Itinerary。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(itineraryServiceUrlStr + "itineraries/" + proposalId + "?requirementType=proposal"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonArray itineries = Json.createReader(response.getEntity().getContent()).readArray();
                    assertNotNull(itineries);
                    assertEquals(1, itineries.size());

                    {
                        JsonObject itineraryRequirement = itineries.getJsonObject(0);
                        assertNotNull(itineraryRequirement);

                        assertEquals(itineraryId2nd, itineraryRequirement.getString(Introspection.JSONKeys.UUID));

                        JsonObject departure = itineraryRequirement.getJsonObject(Introspection.JSONKeys.DEPARTURE);
                        assertNotNull(departure);
                        JsonObject departureCity = departure.getJsonObject(Introspection.JSONKeys.CITY);
                        assertNotNull(departureCity);
                        assertEquals("Wuhan", departureCity.getString(Introspection.JSONKeys.NAME));
                        assertEquals("武汉", departureCity.getString(Introspection.JSONKeys.CHINESE_NAME));
                        assertEquals("wuhan", departureCity.getString(Introspection.JSONKeys.PINYIN_NAME));

                        JsonObject destination = itineraryRequirement.getJsonObject(Introspection.JSONKeys.DESTINATION);
                        assertNotNull(destination);
                        JsonObject destinationCity = destination.getJsonObject(Introspection.JSONKeys.CITY);
                        assertNotNull(destinationCity);
                        assertEquals("Guangzhou", destinationCity.getString(Introspection.JSONKeys.NAME));
                        assertEquals("广州", destinationCity.getString(Introspection.JSONKeys.CHINESE_NAME));
                        assertEquals("guangzhou", destinationCity.getString(Introspection.JSONKeys.PINYIN_NAME));
                    }
                }
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                get.abort();
            }
        }

        /*
         * 提交Proposal。
         */
        {
            HttpPost post = new HttpPost();
            post.setURI(new URI(itineraryServiceUrlStr + "proposals/agencies/" + proposalId + "?delayMins=1"));
            post.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = httpClient.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                assertEquals(HttpStatus.ACCEPTED_202, statusCode);
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                post.abort();
            }
        }

        /*
         * 查询我（游客）提交的Proposal。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(itineraryServiceUrlStr + "proposals/my"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonArray proposals = Json.createReader(response.getEntity().getContent()).readArray();
                    assertNotNull(proposals);
                    assertTrue(proposals.size() > 0);
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
         * 查看我（旅行社）被列为候选的Proposal（概述）。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(itineraryServiceUrlStr + "proposals/agencies/selected/"
                + agencyIds.getString(agencyIds.size() - 2)));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject proposalSummaries = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(proposalSummaries);
                    JsonArray uuid = proposalSummaries.getJsonArray(Introspection.JSONKeys.UUID);
                    assertNotNull(uuid);
                    assertEquals(1, uuid.size());
                    assertEquals(proposalId, uuid.getString(0));
                    JsonArray summary = proposalSummaries.getJsonArray(Introspection.JSONKeys.SUMMARY);
                    assertNotNull(summary);
                    assertEquals(1, summary.size());
                    assertEquals("测试提议", summary.getString(0));
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
         * 旅行社抢单Proposal。
         */
        {
            HttpPut put = new HttpPut();
            put.setURI(new URI(itineraryServiceUrlStr + "proposals/agencies/" + proposalId + "/"
                + agencyIds.getString(agencyIds.size() - 2)));
            put.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = httpClient.execute(put);
                int statusCode = response.getStatusLine().getStatusCode();
                assertEquals(HttpStatus.OK_200, statusCode);
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                put.abort();
            }
        }

        /*
         * 查看我（旅行社）被列为候选的Proposal（概述）。抢单成功后，此时应该少一个Proposal。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(itineraryServiceUrlStr + "proposals/agencies/selected/"
                + agencyIds.getString(agencyIds.size() - 2)));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject proposalSummaries = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(proposalSummaries);
                    JsonArray uuid = proposalSummaries.getJsonArray(Introspection.JSONKeys.UUID);
                    assertNotNull(uuid);
                    assertEquals(0, uuid.size());
                    JsonArray summary = proposalSummaries.getJsonArray(Introspection.JSONKeys.SUMMARY);
                    assertNotNull(summary);
                    assertEquals(0, summary.size());
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

        {
            Thread.sleep(1000 * 60);
        }

        /*
         * 查询我（旅行社）收到的Proposal。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(itineraryServiceUrlStr + "proposals/agencies/"
                + agencyIds.getString(agencyIds.size() - 2)));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonArray proposals = Json.createReader(response.getEntity().getContent()).readArray();
                    assertNotNull(proposals);
                    assertEquals(1, proposals.size());
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
         * 再次提交Proposal。
         */
        {
            ;
        }
    }

    @After
    public void after() {
        for (int i = 0; i < agencyIds.size(); i++) {
            HttpDelete delete = new HttpDelete();

            try {
                delete.setURI(new URI(platformServiceUrlStr + "agencies/" + agencyIds.getString(i)));
                delete.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
                HttpResponse response = httpClient.execute(delete);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    String agencyId = Json.createReader(response.getEntity().getContent()).readObject()
                        .getString(Introspection.JSONKeys.UUID);
                    assertNotNull(agencyId);
                    assertEquals(agencyIds.getString(i), agencyId);
                } else {
                    JsonObject error = Json.createReader(response.getEntity().getContent()).readObject();
                    throw new ProcessingException(error.toString());
                }
            } catch (IOException e) {
                throw new ProcessingException(e);
            } catch (URISyntaxException e) {
                throw new ProcessingException(e);
            } finally {
                delete.abort();
            }
        }
    }
}
