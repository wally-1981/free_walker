package com.free.walker.service.itinerary.basic;

import java.util.Date;
import java.util.Iterator;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.Serializable;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.ibm.icu.text.MessageFormat;

public class Conversation implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(Conversation.class);

    private Session session;

    public Conversation() {
        ;
    }

    public Conversation(Session session) {
        if (session == null) {
            throw new NullPointerException();
        }
        this.session = session;
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.NULL;
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Introspection.JSONKeys.UUID, session.getId().toString());
        resBuilder.add(Introspection.JSONKeys.VIA, session.getHost());
        resBuilder.add(Introspection.JSONKeys.ACCESS, session.getLastAccessTime().getTime());
        resBuilder.add(Introspection.JSONKeys.SESSION_START, session.getStartTimestamp().getTime());
        resBuilder.add(Introspection.JSONKeys.TIMEOUT, session.getTimeout());

        JsonObjectBuilder attributesBuilder = Json.createObjectBuilder();
        Iterator<Object> sessionKeys = session.getAttributeKeys().iterator();
        while (sessionKeys.hasNext()) {
            Object key = sessionKeys.next();
            Object value = session.getAttribute(key);
            if (key instanceof String && value instanceof String) {
                attributesBuilder.add((String) key, (String) value);
            } else {
                LOG.debug(MessageFormat.format("The session attributes were dropped during persistence. {0}:{1}",
                    key.toString(), value.toString()));
            }
        }
        resBuilder.add(Introspection.JSONKeys.ATTRIBUTES, attributesBuilder);

        if (session instanceof SimpleSession && ((SimpleSession) session).getStopTimestamp() != null) {
            resBuilder.add(Introspection.JSONKeys.SESSION_STOP, ((SimpleSession) session).getStopTimestamp().getTime());
        }

        return resBuilder.build();
    }

    public Conversation fromJSON(JsonObject jsObject) throws JsonException {
        SimpleSession simpleSession = new SimpleSession();
        
        String id = jsObject.getString(Introspection.JSONKeys.UUID, null);
        if (id == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.UUID, id));
        } else {
            simpleSession.setId(id);
        }

        String viaHost = jsObject.getString(Introspection.JSONKeys.VIA, null);
        if (viaHost == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.VIA, viaHost));
        } else {
            simpleSession.setHost(viaHost);
        }

        JsonNumber access = jsObject.getJsonNumber(Introspection.JSONKeys.ACCESS);
        if (access == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.ACCESS, access));
        } else {
            simpleSession.setLastAccessTime(new Date(access.longValue()));
        }

        JsonNumber start = jsObject.getJsonNumber(Introspection.JSONKeys.SESSION_START);
        if (start == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.SESSION_START, start));
        } else {
            simpleSession.setStartTimestamp(new Date(start.longValue()));
        }

        JsonNumber stop = jsObject.getJsonNumber(Introspection.JSONKeys.SESSION_STOP);
        if (stop != null) {
            simpleSession.setStopTimestamp(new Date(stop.longValue()));
        }

        JsonNumber timeout = jsObject.getJsonNumber(Introspection.JSONKeys.TIMEOUT);
        if (timeout != null) {
            simpleSession.setTimeout(timeout.longValue());
        }

        JsonObject attributes = jsObject.getJsonObject(Introspection.JSONKeys.ATTRIBUTES);
        if (attributes != null) {
            Iterator<String> keys = attributes.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                simpleSession.setAttribute(key, attributes.getString(key));
            }
        }

        session = simpleSession;

        return this;
    }

    public Session getSession() {
        return session;
    }
}
