package com.free.walker.service.itinerary.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.shiro.session.mgt.SimpleSession;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.primitive.Introspection;

public class ConversationTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private JsonObject sessionObj;

    @Before
    public void before() {
        Calendar sessionStart = Calendar.getInstance();
        sessionStart.add(Calendar.MINUTE, -89);

        Calendar sessionLastAccess = Calendar.getInstance();
        sessionLastAccess.add(Calendar.MINUTE, -28);

        Calendar sessionStop = Calendar.getInstance();
        sessionStop.add(Calendar.MINUTE, -6);

        JsonObjectBuilder builder = Json.createObjectBuilder();
        String sessionId = UUID.randomUUID().toString();
        builder.add(Introspection.JSONKeys.UUID, sessionId);
        builder.add(Introspection.JSONKeys.VIA, "89.76.123.16");
        builder.add(Introspection.JSONKeys.ACCESS, sessionLastAccess.getTimeInMillis());
        builder.add(Introspection.JSONKeys.SESSION_START, sessionStart.getTimeInMillis());
        builder.add(Introspection.JSONKeys.TIMEOUT, 720000);
        builder.add(Introspection.JSONKeys.SESSION_STOP, sessionStop.getTimeInMillis());

        JsonObjectBuilder attributesBuilder = Json.createObjectBuilder();
        attributesBuilder.add("K1", "V1");
        attributesBuilder.add("K2", "V2");
        builder.add(Introspection.JSONKeys.ATTRIBUTES, attributesBuilder);

        sessionObj = builder.build();
    }

    @Test
    public void testFromJSON() throws InvalidTravelReqirementException {
        Conversation conversation = new Conversation().fromJSON(sessionObj);

        assertTrue(conversation.getSession() instanceof SimpleSession);
        assertEquals(sessionObj.getString(Introspection.JSONKeys.UUID), conversation.getSession().getId());
        assertEquals(sessionObj.getString(Introspection.JSONKeys.VIA), conversation.getSession().getHost());
        assertEquals(sessionObj.getJsonNumber(Introspection.JSONKeys.ACCESS).longValue(),
            conversation.getSession().getLastAccessTime().getTime());
        assertEquals(sessionObj.getJsonNumber(Introspection.JSONKeys.SESSION_START).longValue(),
            conversation.getSession().getStartTimestamp().getTime());
        assertEquals(sessionObj.getJsonNumber(Introspection.JSONKeys.SESSION_STOP).longValue(),
            ((SimpleSession) conversation.getSession()).getStopTimestamp().getTime());
        assertEquals(sessionObj.getJsonNumber(Introspection.JSONKeys.TIMEOUT).longValue(),
            conversation.getSession().getTimeout());
        assertEquals(sessionObj.getJsonObject(Introspection.JSONKeys.ATTRIBUTES).getString("K1"),
            conversation.getSession().getAttribute("K1"));
        assertEquals(sessionObj.getJsonObject(Introspection.JSONKeys.ATTRIBUTES).getString("K2"),
            conversation.getSession().getAttribute("K2"));
    }

    @Test
    public void testToJSON() throws InvalidTravelReqirementException {
        Conversation conversation = new Conversation().fromJSON(sessionObj);
        JsonObject sessionJs = conversation.toJSON();

        assertTrue(conversation.getSession() instanceof SimpleSession);
        assertEquals(sessionObj.getString(Introspection.JSONKeys.UUID),
            sessionJs.getString(Introspection.JSONKeys.UUID));
        assertEquals(sessionObj.getString(Introspection.JSONKeys.VIA),
            sessionJs.getString(Introspection.JSONKeys.VIA));
        assertEquals(sessionObj.getJsonNumber(Introspection.JSONKeys.ACCESS).longValue(),
            sessionJs.getJsonNumber(Introspection.JSONKeys.ACCESS).longValue());
        assertEquals(sessionObj.getJsonNumber(Introspection.JSONKeys.SESSION_START).longValue(),
            sessionJs.getJsonNumber(Introspection.JSONKeys.SESSION_START).longValue());
        assertEquals(sessionObj.getJsonNumber(Introspection.JSONKeys.SESSION_STOP).longValue(),
            sessionJs.getJsonNumber(Introspection.JSONKeys.SESSION_STOP).longValue());
        assertEquals(sessionObj.getJsonNumber(Introspection.JSONKeys.TIMEOUT).longValue(),
            sessionJs.getJsonNumber(Introspection.JSONKeys.TIMEOUT).longValue());
        assertEquals(sessionObj.getJsonObject(Introspection.JSONKeys.ATTRIBUTES).getString("K1"),
            sessionJs.getJsonObject(Introspection.JSONKeys.ATTRIBUTES).getString("K1"));
        assertEquals(sessionObj.getJsonObject(Introspection.JSONKeys.ATTRIBUTES).getString("K2"),
            sessionJs.getJsonObject(Introspection.JSONKeys.ATTRIBUTES).getString("K2"));
    }
}
