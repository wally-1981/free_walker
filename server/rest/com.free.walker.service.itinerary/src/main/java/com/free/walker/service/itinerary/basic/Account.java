package com.free.walker.service.itinerary.basic;

import java.util.UUID;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.Serializable;
import com.free.walker.service.itinerary.primitive.AccountType;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.util.UuidUtil;

public class Account implements Serializable {
    private UUID accountId;
    private AccountType accountType;
    private String login;
    private String mobile;
    private String email;
    private String displayName;
    private String imageUri;

    public Account() {
        accountId = UUID.randomUUID();
    }

    public JsonObject toJSON() {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Introspection.JSONKeys.UUID, accountId.toString());
        resBuilder.add(Introspection.JSONKeys.TYPE, accountType.ordinal());
        resBuilder.add(Introspection.JSONKeys.LOGIN, login);
        resBuilder.add(Introspection.JSONKeys.MOBILE, mobile);
        resBuilder.add(Introspection.JSONKeys.EMAIL, email);
        resBuilder.add(Introspection.JSONKeys.NAME, displayName);
        resBuilder.add(Introspection.JSONKeys.REF_LINK, imageUri);
        return resBuilder.build();
    }

    public Account fromJSON(JsonObject jsObject) throws JsonException {
        String uuidStr = jsObject.getString(Introspection.JSONKeys.UUID, null);
        if (uuidStr == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.UUID, uuidStr));
        } else {
            accountId = UuidUtil.fromUuidStr(uuidStr);
        }

        int type = jsObject.getInt(Introspection.JSONKeys.TYPE, -1);
        if (type < 0) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.TYPE, type));
        } else {
            accountType = AccountType.valueOf(type);
        }

        String login = jsObject.getString(Introspection.JSONKeys.LOGIN, null);
        if (login == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.LOGIN, login));
        } else {
            this.login = login;
        }

        String mobile = jsObject.getString(Introspection.JSONKeys.MOBILE, null);
        if (mobile == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.MOBILE, mobile));
        } else {
            this.mobile = mobile;
        }

        String email = jsObject.getString(Introspection.JSONKeys.EMAIL, null);
        if (email == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.EMAIL, email));
        } else {
            this.email = email;
        }

        String name = jsObject.getString(Introspection.JSONKeys.NAME, null);
        if (name == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.NAME, name));
        } else {
            displayName = name;
        }

        String imageUri = jsObject.getString(Introspection.JSONKeys.REF_LINK, null);
        if (imageUri != null) {
            this.imageUri = imageUri;
        }

        return this;
    }

    public String getUuid() {
        return accountId.toString();
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.NULL;
    }
}
