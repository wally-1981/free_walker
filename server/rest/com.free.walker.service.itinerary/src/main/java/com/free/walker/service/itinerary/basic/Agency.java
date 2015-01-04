package com.free.walker.service.itinerary.basic;

import java.util.UUID;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.Renewable;
import com.free.walker.service.itinerary.Serializable;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.util.UuidUtil;

public class Agency implements Serializable, Renewable {
    private UUID uuid;
    private String name;
    private String title;
    private int rating;
    private int hmd;
    private long exp;

    public Agency() {
        this.uuid = UUID.randomUUID();
    }

    public JsonObject toJSON() {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Introspection.JSONKeys.UUID, uuid.toString());
        resBuilder.add(Introspection.JSONKeys.NAME, name);
        resBuilder.add(Introspection.JSONKeys.TITLE, title);
        resBuilder.add(Introspection.JSONKeys.STAR, rating);
        resBuilder.add(Introspection.JSONKeys.HMD, hmd);
        resBuilder.add(Introspection.JSONKeys.EXP, exp);
        return resBuilder.build();
    }

    public Agency newFromJSON(JsonObject jsObject) throws JsonException {
        String uuid = jsObject.getString(Introspection.JSONKeys.UUID, null);
        if (uuid == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.UUID, uuid));
        } else {
            this.uuid = UuidUtil.fromUuidStr(uuid);
        }
        return fromJSON(jsObject);
    }

    public Agency fromJSON(JsonObject jsObject) throws JsonException {
        String name = jsObject.getString(Introspection.JSONKeys.NAME, null);
        if (name == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.NAME, name));
        } else {
            this.name = name;
        }

        String title = jsObject.getString(Introspection.JSONKeys.TITLE, null);
        if (title == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.TITLE, title));
        } else {
            this.title = title;
        }

        int rating = jsObject.getInt(Introspection.JSONKeys.STAR, -1);
        if (rating < 0) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.STAR, rating));
        } else {
            this.rating = rating;
        }

        int hmd = jsObject.getInt(Introspection.JSONKeys.HMD, 0);
        if (hmd > 100 || hmd < 0) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.HMD, hmd));
        } else {
            this.hmd = hmd;
        }

        JsonNumber exp = jsObject.getJsonNumber(Introspection.JSONKeys.EXP);
        if (exp == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.HMD, hmd));
        } else {
            this.exp = exp.longValue();
        }

        return this;
    }

    public String getUuid() {
        return uuid.toString();
    }

    public void setUuid(String uuid) {
        if (UuidUtil.isCmpUuidStr(uuid)) {
            this.uuid = UuidUtil.fromCmpUuidStr(uuid);
        } else {
            this.uuid = UuidUtil.fromUuidStr(uuid);
        }
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getHmd() {
        return hmd;
    }

    public void setHmd(int hmd) {
        if (hmd > 100 || hmd < 0) {
            throw new IllegalArgumentException();
        }

        this.hmd = hmd;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.NULL;
    }
}
