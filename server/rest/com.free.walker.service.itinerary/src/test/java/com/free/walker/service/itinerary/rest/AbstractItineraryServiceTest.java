package com.free.walker.service.itinerary.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.util.UuidUtil;

public abstract class AbstractItineraryServiceTest {
    private HttpClient httpClient;
    private String itineraryServiceUrlStr;

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

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() throws InvalidTravelReqirementException {
        httpClient = HttpClientBuilder.create().build();
        itineraryServiceUrlStr = getServiceUrl();

        {
            JsonObjectBuilder requirementBuilder = Json.createObjectBuilder();
            requirementBuilder.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY);

            JsonObjectBuilder destinationBuilder = Json.createObjectBuilder();
            JsonObjectBuilder destCityBuilder = Json.createObjectBuilder();
            destCityBuilder.add(Introspection.JSONKeys.UUID,
                UuidUtil.fromUuidStr("02515d41-f141-4175-9a11-9e68b9cfe687").toString());
            destinationBuilder.add(Introspection.JSONKeys.CITY, destCityBuilder);
            requirementBuilder.add(Introspection.JSONKeys.DESTINATION, destinationBuilder);

            JsonObjectBuilder departureBuilder = Json.createObjectBuilder();
            JsonObjectBuilder deptCityBuilder = Json.createObjectBuilder();
            deptCityBuilder.add(Introspection.JSONKeys.UUID,
                UuidUtil.fromUuidStr("84844276-3036-47dd-90e0-f095cfa98da5").toString());
            departureBuilder.add(Introspection.JSONKeys.CITY, deptCityBuilder);
            requirementBuilder.add(Introspection.JSONKeys.DEPARTURE, departureBuilder);

            JsonObjectBuilder proposalBuilder = Json.createObjectBuilder();
            proposalBuilder.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL);
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
            destCityBuilder.add(Introspection.JSONKeys.UUID,
                UuidUtil.fromUuidStr("79fd8642-a11d-4811-887d-ec4268097a82").toString());
            destinationBuilder.add(Introspection.JSONKeys.CITY, destCityBuilder);
            requirementBuilder.add(Introspection.JSONKeys.DESTINATION, destinationBuilder);

            JsonObjectBuilder departureBuilder = Json.createObjectBuilder();
            JsonObjectBuilder deptCityBuilder = Json.createObjectBuilder();
            deptCityBuilder.add(Introspection.JSONKeys.UUID,
                UuidUtil.fromUuidStr("46e46912-f856-49ce-b9f8-cac99fe9211e").toString());
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
                Introspection.JSONValues.TIME_RANGE_18_23.realValue());
            dateTime2.add(Introspection.JSONKeys.TIME_RANGE_OFFSET,
                Introspection.JSONValues.TIME_RANGE_18_23.imaginaryValue());
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
    }

    @Test
    public void testAll() throws URISyntaxException {
        /*
         * Retrieve service introspection.
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(itineraryServiceUrlStr + "introspection/"));
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

        /*
         * Create a proposal with an initial itinerary.
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
         * Get the newly created proposal.
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
         * Get all itineraries by proposal id. Only the initial one will be
         * retrieved at this case moment.
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
         * Add a new itinerary.
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
         * Get the newly added itinerary by itinerary id.
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
                    assertEquals("Geneva", departureCity.getString(Introspection.JSONKeys.NAME));
                    assertEquals("日内瓦", departureCity.getString(Introspection.JSONKeys.CHINESE_NAME));
                    assertEquals("rineiwa", departureCity.getString(Introspection.JSONKeys.PINYIN_NAME));

                    JsonObject destination = itinerary.getJsonObject(Introspection.JSONKeys.DESTINATION);
                    assertNotNull(destination);
                    JsonObject destinationCity = destination.getJsonObject(Introspection.JSONKeys.CITY);
                    assertNotNull(destinationCity);
                    assertEquals("Wuhan", destinationCity.getString(Introspection.JSONKeys.NAME));
                    assertEquals("武汉", destinationCity.getString(Introspection.JSONKeys.CHINESE_NAME));
                    assertEquals("wuhan", destinationCity.getString(Introspection.JSONKeys.PINYIN_NAME));
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
         * Add a requirement to the last itinerary of the proposal.
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
         * Get the newly added requirement.
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
         * Get all itineraries by proposal id again. There should be two
         * itineraries at this case moment.
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
                        assertEquals("Geneva", departureCity.getString(Introspection.JSONKeys.NAME));
                        assertEquals("日内瓦", departureCity.getString(Introspection.JSONKeys.CHINESE_NAME));
                        assertEquals("rineiwa", departureCity.getString(Introspection.JSONKeys.PINYIN_NAME));

                        JsonObject destination = itineraryRequirement.getJsonObject(Introspection.JSONKeys.DESTINATION);
                        assertNotNull(destination);
                        JsonObject destinationCity = destination.getJsonObject(Introspection.JSONKeys.CITY);
                        assertNotNull(destinationCity);
                        assertEquals("Wuhan", destinationCity.getString(Introspection.JSONKeys.NAME));
                        assertEquals("武汉", destinationCity.getString(Introspection.JSONKeys.CHINESE_NAME));
                        assertEquals("wuhan", destinationCity.getString(Introspection.JSONKeys.PINYIN_NAME));
                    }
                }
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                get.abort();
            }
        }

        /*
         * Add a requirement to the first itinerary of the proposal.
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
         * Get the newly added requirement.
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
         * Update an existing requirement.
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
         * Retrieve all requirments of the proposal and itinerary.
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
         * Delete a requirement.
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
         * Retrieve the deleted requirement. It can not be found at this case moment.
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(itineraryServiceUrlStr + "requirements/" + proposalId + "/" + itineraryId2nd));
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
         * Delete an itinerary.
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
         * Get all itineraries by proposal id. There is remain at this case
         * moment.
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
                        assertEquals("Geneva", departureCity.getString(Introspection.JSONKeys.NAME));
                        assertEquals("日内瓦", departureCity.getString(Introspection.JSONKeys.CHINESE_NAME));
                        assertEquals("rineiwa", departureCity.getString(Introspection.JSONKeys.PINYIN_NAME));

                        JsonObject destination = itineraryRequirement.getJsonObject(Introspection.JSONKeys.DESTINATION);
                        assertNotNull(destination);
                        JsonObject destinationCity = destination.getJsonObject(Introspection.JSONKeys.CITY);
                        assertNotNull(destinationCity);
                        assertEquals("Wuhan", destinationCity.getString(Introspection.JSONKeys.NAME));
                        assertEquals("武汉", destinationCity.getString(Introspection.JSONKeys.CHINESE_NAME));
                        assertEquals("wuhan", destinationCity.getString(Introspection.JSONKeys.PINYIN_NAME));
                    }
                }
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                get.abort();
            }
        }
    }

    protected abstract String getServiceUrl();
}
