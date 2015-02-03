package com.free.walker.service.itinerary.util;

import java.util.Iterator;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public class JsonObjectUtil {
    public static JsonObject merge(JsonObject primary, String secondaryKey, JsonObject secondary) {
        if (primary == null || secondary == null || secondaryKey == null) {
            throw new NullPointerException();
        }

        if (secondaryKey.trim().length() == 0 || primary.containsKey(secondaryKey)) {
            throw new IllegalArgumentException();
        }

        JsonObjectBuilder builder = Json.createObjectBuilder();

        Iterator<String> a = primary.keySet().iterator();
        while (a.hasNext()) {
            String k = a.next();
            JsonValue v = primary.get(k);
            builder.add(k, v);
        }

        builder.add(secondaryKey, secondary);

        return builder.build();
    }
}
