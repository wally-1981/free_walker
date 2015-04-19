package com.free.walker.service.itinerary.rest;

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
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.primitive.AccountType;
import com.free.walker.service.itinerary.primitive.Introspection;

public class ItineraryServiceProdCustSecTest extends AbstractItineraryServiceTest {
    private static final Logger LOG = LoggerFactory.getLogger(ItineraryServiceProdCustSecTest.class);

    private String accountServiceUrlStr;
    private JsonObject userAccountJs = null;
    private JsonObject agencyAccountJs = null;

    @Before
    public void before() {
        accountServiceUrlStr = getProdSecureServiceUrl(AccountService.class);
        itineraryServiceUrlStr = getProdSecureServiceUrl(ItineraryService.class);
        platformServiceUrlStr = getProdSecureServiceUrl(PlatformService.class);

        {
            String login = UUID.randomUUID().toString();
            JsonObjectBuilder userAccountJsBuilder = Json.createObjectBuilder();
            userAccountJsBuilder.add(Introspection.JSONKeys.LOGIN, login);
            userAccountJsBuilder.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.ACCOUNT_TYPE_MASTER);
            userAccountJsBuilder.add(Introspection.JSONKeys.STATUS, Introspection.JSONValues.ACCOUNT_STATUS_ACTIVE);
            userAccountJsBuilder.add(Introspection.JSONKeys.PASSWORD, "hashedpassword4peterdotli");
            userAccountJsBuilder.add(Introspection.JSONKeys.EMAIL, login + "@yahoo.com");
            userAccountJsBuilder.add(Introspection.JSONKeys.NAME, "peter.li");
            userAccountJsBuilder.add(Introspection.JSONKeys.REF_LINK, "http://www.yahoo.com");
            userAccountJsBuilder.add(Introspection.JSONKeys.ROLE,
                Json.createArrayBuilder().add(AccountType.getDefaultRole(AccountType.MASTER).ordinal()));
            userAccountJs = userAccountJsBuilder.build();

            HttpPost post = new HttpPost();
            post.setEntity(new StringEntity(userAccountJs.toString(), ContentType.APPLICATION_JSON));
            post.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                post.setURI(new URI(accountServiceUrlStr + "accounts/"));
                HttpResponse response = anonymousClient.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject accountJs = Json.createReader(response.getEntity().getContent()).readObject();
                    this.userAccountJs = accountJs;
                }  else if (statusCode == HttpStatus.UNAUTHORIZED_401) {
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
            String login = UUID.randomUUID().toString();
            JsonObjectBuilder agencyAccountJsBuilder = Json.createObjectBuilder();
            agencyAccountJsBuilder.add(Introspection.JSONKeys.LOGIN, login);
            agencyAccountJsBuilder.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.ACCOUNT_TYPE_AGENCY);
            agencyAccountJsBuilder.add(Introspection.JSONKeys.STATUS, Introspection.JSONValues.ACCOUNT_STATUS_ACTIVE);
            agencyAccountJsBuilder.add(Introspection.JSONKeys.PASSWORD, "hashedpassword4chinainternational");
            agencyAccountJsBuilder.add(Introspection.JSONKeys.EMAIL, login + "@china.gov");
            agencyAccountJsBuilder.add(Introspection.JSONKeys.NAME, "china_international");
            agencyAccountJsBuilder.add(Introspection.JSONKeys.REF_LINK, "http://www.china.gov");
            agencyAccountJsBuilder.add(Introspection.JSONKeys.ROLE,
                Json.createArrayBuilder().add(AccountType.getDefaultRole(AccountType.AGENCY).ordinal()));
            agencyAccountJs = agencyAccountJsBuilder.build();

            HttpPost post = new HttpPost();
            post.setEntity(new StringEntity(agencyAccountJs.toString(), ContentType.APPLICATION_JSON));
            post.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try {
                post.setURI(new URI(accountServiceUrlStr + "accounts/"));
                HttpResponse response = anonymousClient.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.OK_200) {
                    JsonObject accountJs = Json.createReader(response.getEntity().getContent()).readObject();
                    this.agencyAccountJs = accountJs;
                }  else if (statusCode == HttpStatus.UNAUTHORIZED_401) {
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

        userClient = generateClient(new UsernamePasswordCredentials(
            userAccountJs.getString(Introspection.JSONKeys.LOGIN),
            "hashedpassword4peterdotli"));
        agencyClient = generateClient(new UsernamePasswordCredentials(
            agencyAccountJs.getString(Introspection.JSONKeys.LOGIN),
            "hashedpassword4chinainternational"));

        super.before();
    }
}
