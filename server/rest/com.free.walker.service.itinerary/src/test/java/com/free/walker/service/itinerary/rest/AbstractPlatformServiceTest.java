package com.free.walker.service.itinerary.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.ProcessingException;

import org.apache.cxf.helpers.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.primitive.Introspection;

public abstract class AbstractPlatformServiceTest extends BaseConfigurationProvider {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractPlatformServiceTest.class);

    private JsonObject agency;

    private String agencyId;
    private String sendLocationId = "2";
    private String recvLocationId = "03161e050c2448378eb863bfcbe744f3";

    protected String platformServiceUrlStr;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() {
        {
            JsonObjectBuilder agencyBuilder = Json.createObjectBuilder();
            agencyBuilder.add(Introspection.JSONKeys.NAME, "旅行社名称（正式）");
            agencyBuilder.add(Introspection.JSONKeys.TITLE, "旅行社名称");
            agencyBuilder.add(Introspection.JSONKeys.STAR, 2);
            agencyBuilder.add(Introspection.JSONKeys.HMD, 87);
            agencyBuilder.add(Introspection.JSONKeys.EXP, 9999);
            agency = agencyBuilder.build();
        }
    }

    @Test
    public void testAll() throws URISyntaxException {
        /*
         * CORS Support with HTTP
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(platformServiceUrlStr + "introspection/"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setHeader("Origin", "http://localhost");
            try {
                HttpResponse response = anonymousClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                assertEquals(HttpStatus.OK_200, statusCode);
                assertEquals("true", response.getFirstHeader("Access-Control-Allow-Credentials").getValue());
                assertEquals("http://localhost", response.getFirstHeader("Access-Control-Allow-Origin").getValue());
                assertFalse(IOUtils.toString(response.getEntity().getContent()).isEmpty());
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                get.abort();
            }
        }

        /*
         * CORS Support with HTTPS
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(platformServiceUrlStr + "introspection/"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setHeader("Origin", "https://localhost");
            try {
                HttpResponse response = anonymousClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                assertEquals(HttpStatus.OK_200, statusCode);
                assertEquals("true", response.getFirstHeader("Access-Control-Allow-Credentials").getValue());
                assertEquals("https://localhost", response.getFirstHeader("Access-Control-Allow-Origin").getValue());
                assertFalse(IOUtils.toString(response.getEntity().getContent()).isEmpty());
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                get.abort();
            }
        }

        /*
         * CORS Support with HTTP and PORT (Rejection Expected)
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(platformServiceUrlStr + "introspection/"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setHeader("Origin", "http://localhost:8080");
            try {
                HttpResponse response = anonymousClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                assertEquals(HttpStatus.OK_200, statusCode);
                assertNull(response.getFirstHeader("Access-Control-Allow-Credentials"));
                assertNull(response.getFirstHeader("Access-Control-Allow-Origin"));
                assertFalse(IOUtils.toString(response.getEntity().getContent()).isEmpty());
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                get.abort();
            }
        }

        /*
         * CORS Support with HTTPS and PORT (Rejection Expected)
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(platformServiceUrlStr + "introspection/"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            get.setHeader("Origin", "https://localhost:8080");
            try {
                HttpResponse response = anonymousClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                assertEquals(HttpStatus.OK_200, statusCode);
                assertNull(response.getFirstHeader("Access-Control-Allow-Credentials"));
                assertNull(response.getFirstHeader("Access-Control-Allow-Origin"));
                assertFalse(IOUtils.toString(response.getEntity().getContent()).isEmpty());
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                get.abort();
            }
        }

        /*
         * 未认证用户访问公开服务
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(platformServiceUrlStr + "introspection/"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = anonymousClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                assertEquals(HttpStatus.OK_200, statusCode);
                assertFalse(IOUtils.toString(response.getEntity().getContent()).isEmpty());
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                get.abort();
            }
        }

        /*
         * 未认证用户访问非公开服务
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(platformServiceUrlStr + "tags/top/2"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = anonymousClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                assertEquals(HttpStatus.UNAUTHORIZED_401, statusCode);
                assertFalse(IOUtils.toString(response.getEntity().getContent()).isEmpty());
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                get.abort();
            }
        }

        /*
         * 获取Web服务自省。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(platformServiceUrlStr + "introspection/"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = adminClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject introspection = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(introspection);
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
         * 获取热点标签，获取最热的两个。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(platformServiceUrlStr + "tags/top/2"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = adminClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonArray tags = Json.createReader(response.getEntity().getContent()).readArray();
                    assertNotNull(tags);
                    assertEquals(2, tags.size());
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
         * 获取国内城市。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(platformServiceUrlStr + "cities?isDomestic=Y"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = adminClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonArray cityGroups = Json.createReader(response.getEntity().getContent()).readArray();
                    assertNotNull(cityGroups);
                    assertTrue(cityGroups.size() <= 26);
                    assertTrue(cityGroups.size() == 21);
                    JsonObject firstGroup = (JsonObject) cityGroups.get(0);
                    JsonObject lastGroup = (JsonObject) cityGroups.get(cityGroups.size() - 1);
                    assertNotNull(firstGroup);
                    assertNotNull(lastGroup);
                    assertEquals("A", firstGroup.getString(Introspection.JSONKeys.ABBR));
                    assertEquals("Y", lastGroup.getString(Introspection.JSONKeys.ABBR));
                    JsonArray citiesWithLeadingA = firstGroup.getJsonArray(Introspection.JSONKeys.CITIES);
                    JsonArray citiesWithLeadingY = lastGroup.getJsonArray(Introspection.JSONKeys.CITIES);
                    assertNotNull(citiesWithLeadingA);
                    assertNotNull(citiesWithLeadingY);
                    assertEquals(20, citiesWithLeadingA.size());
                    for (int i = 0; i < citiesWithLeadingA.size(); i++) {
                        JsonObject city = citiesWithLeadingA.getJsonObject(i);
                        String pinyinName = city.getString(Introspection.JSONKeys.PINYIN_NAME);
                        String countryUuid = city.getString(Introspection.JSONKeys.COUNTRY);
                        assertTrue(pinyinName.startsWith("A") || pinyinName.startsWith("a"));
                        assertEquals("af70a55c-eb4c-415c-8375-88081716f8b8", countryUuid);
                    }
                    assertEquals(52, citiesWithLeadingY.size());
                    for (int i = 0; i < citiesWithLeadingY.size(); i++) {
                        JsonObject city = citiesWithLeadingY.getJsonObject(i);
                        String pinyinName = city.getString(Introspection.JSONKeys.PINYIN_NAME);
                        String countryUuid = city.getString(Introspection.JSONKeys.COUNTRY);
                        assertTrue(pinyinName.startsWith("Y") || pinyinName.startsWith("y"));
                        assertEquals("af70a55c-eb4c-415c-8375-88081716f8b8", countryUuid);
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
         * 获取国外城市。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(platformServiceUrlStr + "cities?isDomestic=N"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = adminClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonArray cityGroups = Json.createReader(response.getEntity().getContent()).readArray();
                    assertNotNull(cityGroups);
                    assertTrue(cityGroups.size() <= 26);
                    assertTrue(cityGroups.size() == 15);
                    JsonObject firstGroup = (JsonObject) cityGroups.get(0);
                    JsonObject lastGroup = (JsonObject) cityGroups.get(cityGroups.size() - 1);
                    assertNotNull(firstGroup);
                    assertNotNull(lastGroup);
                    assertEquals("A", firstGroup.getString(Introspection.JSONKeys.ABBR));
                    assertEquals("X", lastGroup.getString(Introspection.JSONKeys.ABBR));
                    JsonArray citiesWithLeadingA = firstGroup.getJsonArray(Introspection.JSONKeys.CITIES);
                    JsonArray citiesWithLeadingX = lastGroup.getJsonArray(Introspection.JSONKeys.CITIES);
                    assertNotNull(citiesWithLeadingA);
                    assertNotNull(citiesWithLeadingX);
                    assertEquals(1, citiesWithLeadingA.size());
                    for (int i = 0; i < citiesWithLeadingA.size(); i++) {
                        JsonObject city = citiesWithLeadingA.getJsonObject(i);
                        String pinyinName = city.getString(Introspection.JSONKeys.PINYIN_NAME);
                        String countryUuid = city.getString(Introspection.JSONKeys.COUNTRY);
                        assertTrue(pinyinName.startsWith("A") || pinyinName.startsWith("a"));
                        assertNotEquals("af70a55c-eb4c-415c-8375-88081716f8b8", countryUuid);
                    }
                    assertEquals(1, citiesWithLeadingX.size());
                    for (int i = 0; i < citiesWithLeadingX.size(); i++) {
                        JsonObject city = citiesWithLeadingX.getJsonObject(i);
                        String pinyinName = city.getString(Introspection.JSONKeys.PINYIN_NAME);
                        String countryUuid = city.getString(Introspection.JSONKeys.COUNTRY);
                        assertTrue(pinyinName.startsWith("X") || pinyinName.startsWith("x"));
                        assertNotEquals("af70a55c-eb4c-415c-8375-88081716f8b8", countryUuid);
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
         * 获取所有城市。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(platformServiceUrlStr + "cities"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = adminClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonArray cityGroups = Json.createReader(response.getEntity().getContent()).readArray();
                    assertNotNull(cityGroups);
                    assertTrue(cityGroups.size() <= 26);
                    assertTrue(cityGroups.size() == 21);
                    JsonObject firstGroup = (JsonObject) cityGroups.get(0);
                    JsonObject lastGroup = (JsonObject) cityGroups.get(cityGroups.size() - 1);
                    assertNotNull(firstGroup);
                    assertNotNull(lastGroup);
                    assertEquals("A", firstGroup.getString(Introspection.JSONKeys.ABBR));
                    assertEquals("Y", lastGroup.getString(Introspection.JSONKeys.ABBR));
                    JsonArray citiesWithLeadingA = firstGroup.getJsonArray(Introspection.JSONKeys.CITIES);
                    JsonArray citiesWithLeadingY = lastGroup.getJsonArray(Introspection.JSONKeys.CITIES);
                    assertNotNull(citiesWithLeadingA);
                    assertNotNull(citiesWithLeadingY);
                    assertEquals(21, citiesWithLeadingA.size());
                    for (int i = 0; i < citiesWithLeadingA.size(); i++) {
                        JsonObject city = citiesWithLeadingA.getJsonObject(i);
                        String pinyinName = city.getString(Introspection.JSONKeys.PINYIN_NAME);
                        assertTrue(pinyinName.startsWith("A") || pinyinName.startsWith("a"));
                    }
                    assertEquals(53, citiesWithLeadingY.size());
                    for (int i = 0; i < citiesWithLeadingY.size(); i++) {
                        JsonObject city = citiesWithLeadingY.getJsonObject(i);
                        String pinyinName = city.getString(Introspection.JSONKeys.PINYIN_NAME);
                        assertTrue(pinyinName.startsWith("Y") || pinyinName.startsWith("y"));
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
         * 添加一个旅行社
         */
        {
            HttpPost post = new HttpPost();
            post.setEntity(new StringEntity(agency.toString(), ContentType.APPLICATION_JSON));
            post.setURI(new URI(platformServiceUrlStr + "agencies/"));
            post.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = adminClient.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject agency = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(agency);
                    agencyId = agency.getString(Introspection.JSONKeys.UUID);
                    assertNotNull(agencyId);
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
         * 为旅行社关联发团地
         */
        {
            HttpPost post = new HttpPost();
            post.setURI(new URI(platformServiceUrlStr + "agencies/" + agencyId + "/locations/send/" + sendLocationId));
            post.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = adminClient.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                assertEquals(HttpStatus.OK_200, statusCode);
                assertTrue(IOUtils.toString(response.getEntity().getContent()).isEmpty());
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                post.abort();
            }
        }

        /*
         * 为旅行社关联接团地
         */
        {
            HttpPost post = new HttpPost();
            post.setURI(new URI(platformServiceUrlStr + "agencies/" + agencyId + "/locations/recv/" + recvLocationId));
            post.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = adminClient.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                assertEquals(HttpStatus.OK_200, statusCode);
                assertTrue(IOUtils.toString(response.getEntity().getContent()).isEmpty());
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                post.abort();
            }
        }

        /*
         * 获取新建的旅行社
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(platformServiceUrlStr + "agencies/" + agencyId));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = adminClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject agency = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(agency);
                    assertEquals(agencyId, agency.getString(Introspection.JSONKeys.UUID));
                    assertEquals("旅行社名称（正式）", agency.getString(Introspection.JSONKeys.NAME));
                    assertEquals("旅行社名称", agency.getString(Introspection.JSONKeys.TITLE));
                    assertEquals(2, agency.getInt(Introspection.JSONKeys.STAR));
                    assertEquals(87, agency.getInt(Introspection.JSONKeys.HMD));
                    assertEquals(9999, agency.getInt(Introspection.JSONKeys.EXP));
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
         * 查询旅行社的发团地
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(platformServiceUrlStr + "agencies/" + agencyId + "/locations?sendRecv=0"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = adminClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject locationJs = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(locationJs);
                    JsonArray locationIds = locationJs.getJsonArray(Introspection.JSONKeys.LOCATION);
                    assertNotNull(locationIds);
                    assertEquals(1, locationIds.size());
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
         * 查询旅行社的接团地
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(platformServiceUrlStr + "agencies/" + agencyId + "/locations?sendRecv=1"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = adminClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject locationJs = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(locationJs);
                    JsonArray locationIds = locationJs.getJsonArray(Introspection.JSONKeys.LOCATION);
                    assertNotNull(locationIds);
                    assertEquals(1, locationIds.size());
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
         * 为旅行社删除接团地
         */
        {
            HttpDelete delete = new HttpDelete();
            delete.setURI(new URI(platformServiceUrlStr + "agencies/" + agencyId + "/locations/recv/" + recvLocationId));
            delete.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = adminClient.execute(delete);
                int statusCode = response.getStatusLine().getStatusCode();
                assertEquals(HttpStatus.OK_200, statusCode);
                assertTrue(IOUtils.toString(response.getEntity().getContent()).isEmpty());
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                delete.abort();
            }
        }

        /*
         * 查询旅行社的接团地,验证删除成功。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(platformServiceUrlStr + "agencies/" + agencyId + "/locations?sendRecv=1"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = adminClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject locationJs = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(locationJs);
                    JsonArray locationIds = locationJs.getJsonArray(Introspection.JSONKeys.LOCATION);
                    assertNotNull(locationIds);
                    assertEquals(0, locationIds.size());
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
         * 删除一个旅行社
         */
        {
            HttpDelete delete = new HttpDelete();
            delete.setURI(new URI(platformServiceUrlStr + "agencies/" + agencyId));
            delete.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = adminClient.execute(delete);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    assertTrue(IOUtils.toString(response.getEntity().getContent()).isEmpty());
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
         * 获取删除的旅行社，验证删除成功。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(platformServiceUrlStr + "agencies/" + agencyId));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = adminClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                assertEquals(HttpStatus.NOT_FOUND_404, statusCode);
                assertTrue(IOUtils.toString(response.getEntity().getContent()).isEmpty());
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                get.abort();
            }
        }

        /*
         * 获取旅行社的发团地和接团地，验证删除成功。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(platformServiceUrlStr + "agencies/" + agencyId + "/locations?sendRecv=0"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = adminClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject locationJs = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(locationJs);
                    JsonArray locationIds = locationJs.getJsonArray(Introspection.JSONKeys.LOCATION);
                    assertNotNull(locationIds);
                    assertEquals(0, locationIds.size());
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
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(platformServiceUrlStr + "agencies/" + agencyId + "/locations?sendRecv=1"));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = adminClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject locationJs = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(locationJs);
                    JsonArray locationIds = locationJs.getJsonArray(Introspection.JSONKeys.LOCATION);
                    assertNotNull(locationIds);
                    assertEquals(0, locationIds.size());
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
    }
}
