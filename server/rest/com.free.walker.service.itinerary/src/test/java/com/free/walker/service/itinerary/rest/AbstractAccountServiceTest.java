package com.free.walker.service.itinerary.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.ProcessingException;

import org.apache.cxf.helpers.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.primitive.AccountType;
import com.free.walker.service.itinerary.primitive.Introspection;

public abstract class AbstractAccountServiceTest extends BaseConfigurationProvider {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractAccountServiceTest.class);

    protected String accountServiceUrlStr;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private JsonObject accountJs = null;
    private JsonObjectBuilder updatedAccountJsBuilder = null;

    @Before
    public void before() {
        String login = UUID.randomUUID().toString();

        JsonObjectBuilder accountJsBuilder = Json.createObjectBuilder();
        accountJsBuilder.add(Introspection.JSONKeys.LOGIN, login);
        accountJsBuilder.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.ACCOUNT_TYPE_ALIPAY);
        accountJsBuilder.add(Introspection.JSONKeys.STATUS, Introspection.JSONValues.ACCOUNT_STATUS_LOCKED);
        accountJsBuilder.add(Introspection.JSONKeys.PASSWORD, "hashedpassword");
        accountJsBuilder.add(Introspection.JSONKeys.EMAIL, "");
        accountJsBuilder.add(Introspection.JSONKeys.NAME, "nick_name");
        accountJsBuilder.add(Introspection.JSONKeys.REF_LINK, "http://www.china.gov");
        accountJsBuilder.add(Introspection.JSONKeys.ROLE,
            Json.createArrayBuilder().add(AccountType.getDefaultRole(AccountType.MASTER).ordinal()));
        accountJs = accountJsBuilder.build();

        updatedAccountJsBuilder = Json.createObjectBuilder();
        updatedAccountJsBuilder.add(Introspection.JSONKeys.LOGIN, login);
        updatedAccountJsBuilder.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.ACCOUNT_TYPE_ALIPAY);
        updatedAccountJsBuilder.add(Introspection.JSONKeys.STATUS, Introspection.JSONValues.ACCOUNT_STATUS_LOCKED);
        updatedAccountJsBuilder.add(Introspection.JSONKeys.PASSWORD, "hashedpassword");
        updatedAccountJsBuilder.add(Introspection.JSONKeys.MOBILE, UUID.randomUUID().toString());
        updatedAccountJsBuilder.add(Introspection.JSONKeys.EMAIL, UUID.randomUUID().toString() + "@china.gov");
        updatedAccountJsBuilder.add(Introspection.JSONKeys.NAME, "2016_success");
        updatedAccountJsBuilder.add(Introspection.JSONKeys.REF_LINK, "http://www.china.gov.cn");
        updatedAccountJsBuilder.add(Introspection.JSONKeys.ROLE,
            Json.createArrayBuilder().add(AccountType.getDefaultRole(AccountType.MASTER).ordinal()));
    }

    @Test
    public void testAll() throws URISyntaxException {
        /*
         * 注册一个新的Account。
         */
        {
            HttpPost post = new HttpPost();
            post.setEntity(new StringEntity(accountJs.toString(), ContentType.APPLICATION_JSON));
            post.setURI(new URI(accountServiceUrlStr + "accounts/"));
            post.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = userClient.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject accountJs = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(accountJs);
                    assertNotNull(accountJs.getString(Introspection.JSONKeys.UUID));
                    assertEquals(this.accountJs.getInt(Introspection.JSONKeys.STATUS),
                        accountJs.getInt(Introspection.JSONKeys.STATUS));
                    assertEquals(this.accountJs.getInt(Introspection.JSONKeys.TYPE),
                        accountJs.getInt(Introspection.JSONKeys.TYPE));
                    assertEquals(this.accountJs.getString(Introspection.JSONKeys.LOGIN),
                        accountJs.getString(Introspection.JSONKeys.LOGIN));
                    assertEquals(this.accountJs.getString(Introspection.JSONKeys.EMAIL),
                        accountJs.getString(Introspection.JSONKeys.EMAIL));
                    assertNull(accountJs.getString(Introspection.JSONKeys.MOBILE, null));
                    assertEquals(this.accountJs.getString(Introspection.JSONKeys.NAME),
                        accountJs.getString(Introspection.JSONKeys.NAME));
                    assertEquals("******", accountJs.getString(Introspection.JSONKeys.PASSWORD));
                    assertEquals(this.accountJs.getString(Introspection.JSONKeys.REF_LINK),
                        accountJs.getString(Introspection.JSONKeys.REF_LINK));
                    this.accountJs = accountJs;
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
         * 更新一个Account。
         */
        {
            JsonObject updatedAccountJs = updatedAccountJsBuilder.add(Introspection.JSONKeys.UUID,
                accountJs.getString(Introspection.JSONKeys.UUID)).build();

            HttpPut put = new HttpPut();
            put.setEntity(new StringEntity(updatedAccountJs.toString(), ContentType.APPLICATION_JSON));
            put.setURI(new URI(accountServiceUrlStr + "accounts/"));
            put.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = userClient.execute(put);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject accountJs = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(accountJs);
                    assertNotNull(accountJs.getString(Introspection.JSONKeys.UUID));
                    assertEquals(updatedAccountJs.getInt(Introspection.JSONKeys.STATUS),
                        accountJs.getInt(Introspection.JSONKeys.STATUS));
                    assertEquals(updatedAccountJs.getInt(Introspection.JSONKeys.TYPE),
                        accountJs.getInt(Introspection.JSONKeys.TYPE));
                    assertEquals(updatedAccountJs.getString(Introspection.JSONKeys.LOGIN),
                        accountJs.getString(Introspection.JSONKeys.LOGIN));
                    assertEquals(updatedAccountJs.getString(Introspection.JSONKeys.EMAIL),
                        accountJs.getString(Introspection.JSONKeys.EMAIL));
                    assertEquals(updatedAccountJs.getString(Introspection.JSONKeys.MOBILE),
                        accountJs.getString(Introspection.JSONKeys.MOBILE));
                    assertEquals(updatedAccountJs.getString(Introspection.JSONKeys.NAME),
                        accountJs.getString(Introspection.JSONKeys.NAME));
                    assertEquals("******", accountJs.getString(Introspection.JSONKeys.PASSWORD));
                    assertEquals(updatedAccountJs.getString(Introspection.JSONKeys.REF_LINK),
                        accountJs.getString(Introspection.JSONKeys.REF_LINK));
                    this.accountJs = accountJs;
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
         * 修改Account密码。
         */
        {
            JsonObjectBuilder credentialChange = Json.createObjectBuilder();
            credentialChange.add(Introspection.JSONKeys.ORIGINAL, "hashedpassword");
            credentialChange.add(Introspection.JSONKeys.PASSWORD, "hashedpassw0rd");

            HttpPut put = new HttpPut();
            put.setEntity(new StringEntity(credentialChange.build().toString(), ContentType.APPLICATION_JSON));
            put.setURI(new URI(accountServiceUrlStr + "accounts/" + accountJs.getString(Introspection.JSONKeys.LOGIN)));
            put.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = userClient.execute(put);
                int statusCode = response.getStatusLine().getStatusCode();
                assertEquals(HttpStatus.OK_200, statusCode);
                assertTrue(IOUtils.toString(response.getEntity().getContent()).isEmpty());
            } catch (IOException e) {
                throw new ProcessingException(e);
            } finally {
                put.abort();
            }
        }

        /*
         * 激活一个Account。
         */
        {
            HttpPut put = new HttpPut();
            put.setURI(new URI(accountServiceUrlStr + "accounts/" + accountJs.getString(Introspection.JSONKeys.LOGIN)
                + "/" + Introspection.JSONValues.ACCOUNT_STATUS_ACTIVE));
            put.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = userClient.execute(put);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject accountJs = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(accountJs);
                    assertNotNull(accountJs.getString(Introspection.JSONKeys.UUID));
                    assertEquals(Introspection.JSONValues.ACCOUNT_STATUS_ACTIVE,
                        accountJs.getInt(Introspection.JSONKeys.STATUS));
                    assertEquals(this.accountJs.getInt(Introspection.JSONKeys.TYPE),
                        accountJs.getInt(Introspection.JSONKeys.TYPE));
                    assertEquals(this.accountJs.getString(Introspection.JSONKeys.LOGIN),
                        accountJs.getString(Introspection.JSONKeys.LOGIN));
                    assertEquals(this.accountJs.getString(Introspection.JSONKeys.EMAIL),
                        accountJs.getString(Introspection.JSONKeys.EMAIL));
                    assertEquals(this.accountJs.getString(Introspection.JSONKeys.MOBILE),
                        accountJs.getString(Introspection.JSONKeys.MOBILE));
                    assertEquals(this.accountJs.getString(Introspection.JSONKeys.NAME),
                        accountJs.getString(Introspection.JSONKeys.NAME));
                    assertEquals("******", accountJs.getString(Introspection.JSONKeys.PASSWORD));
                    assertEquals(this.accountJs.getString(Introspection.JSONKeys.REF_LINK),
                        accountJs.getString(Introspection.JSONKeys.REF_LINK));
                    this.accountJs = accountJs;
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
         * 检索一个Account。
         */
        {
            HttpGet get = new HttpGet();
            get.setURI(new URI(accountServiceUrlStr + "accounts/" + accountJs.getString(Introspection.JSONKeys.LOGIN)));
            get.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                HttpResponse response = userClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject accountJs = Json.createReader(response.getEntity().getContent()).readObject();
                    assertNotNull(accountJs);
                    assertNotNull(accountJs.getString(Introspection.JSONKeys.UUID));
                    assertEquals(Introspection.JSONValues.ACCOUNT_STATUS_ACTIVE,
                        accountJs.getInt(Introspection.JSONKeys.STATUS));
                    assertEquals(this.accountJs.getInt(Introspection.JSONKeys.TYPE),
                        accountJs.getInt(Introspection.JSONKeys.TYPE));
                    assertEquals(this.accountJs.getString(Introspection.JSONKeys.LOGIN),
                        accountJs.getString(Introspection.JSONKeys.LOGIN));
                    assertEquals(this.accountJs.getString(Introspection.JSONKeys.EMAIL),
                        accountJs.getString(Introspection.JSONKeys.EMAIL));
                    assertEquals(this.accountJs.getString(Introspection.JSONKeys.MOBILE),
                        accountJs.getString(Introspection.JSONKeys.MOBILE));
                    assertEquals(this.accountJs.getString(Introspection.JSONKeys.NAME),
                        accountJs.getString(Introspection.JSONKeys.NAME));
                    assertEquals("******", accountJs.getString(Introspection.JSONKeys.PASSWORD));
                    assertEquals(this.accountJs.getString(Introspection.JSONKeys.REF_LINK),
                        accountJs.getString(Introspection.JSONKeys.REF_LINK));
                    this.accountJs = accountJs;
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
