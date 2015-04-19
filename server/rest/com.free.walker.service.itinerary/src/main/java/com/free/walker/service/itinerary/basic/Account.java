package com.free.walker.service.itinerary.basic;

import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.Serializable;
import com.free.walker.service.itinerary.primitive.AccountStatus;
import com.free.walker.service.itinerary.primitive.AccountType;
import com.free.walker.service.itinerary.primitive.AccountType.Role;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.util.UuidUtil;

public class Account implements Serializable {
    private UUID accountId;
    private AccountType accountType;
    private AccountStatus accountStatus;
    private String login;
    private String mobile;
    private String email;
    private String name;
    private String imageUri;
    private String password;
    private AccountType.Role[] roles;

    public Account() {
        accountId = UUID.randomUUID();
    }

    public JsonObject toJSON() {
        return toJSON(false);
    }

    public JsonObject toJSON(boolean maskPwd) {
        JsonArrayBuilder rolesBuilder = Json.createArrayBuilder();
        for (int i = 0; roles != null && i < roles.length; i++) {
            rolesBuilder.add(roles[i].ordinal());
        }

        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Introspection.JSONKeys.UUID, accountId.toString());
        resBuilder.add(Introspection.JSONKeys.TYPE, accountType.ordinal());
        resBuilder.add(Introspection.JSONKeys.ROLE, rolesBuilder);
        resBuilder.add(Introspection.JSONKeys.STATUS, accountStatus.ordinal());
        resBuilder.add(Introspection.JSONKeys.LOGIN, login);
        resBuilder.add(Introspection.JSONKeys.PASSWORD, maskPwd ? "******" : password);
        if (mobile != null) resBuilder.add(Introspection.JSONKeys.MOBILE, mobile);
        if (email != null) resBuilder.add(Introspection.JSONKeys.EMAIL, email);
        resBuilder.add(Introspection.JSONKeys.NAME, name);
        if (imageUri != null) resBuilder.add(Introspection.JSONKeys.REF_LINK, imageUri);
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

        return newFromJSON(jsObject);
    }

    public Account newFromJSON(JsonObject jsObject) throws JsonException {
        int type = jsObject.getInt(Introspection.JSONKeys.TYPE, -1);
        if (type < 0) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.TYPE, type));
        } else {
            accountType = AccountType.valueOf(type);
        }

        JsonArray roleArray = jsObject.getJsonArray(Introspection.JSONKeys.ROLE);
        if (roleArray != null) {
            roles = new Role[roleArray.size()];
            for (int i = 0; i < roleArray.size(); i++) {
                roles[i] = AccountType.Role.valueOf(roleArray.getInt(i));
            }
        } else {
            roles = new Role[0];
        }

        int status = jsObject.getInt(Introspection.JSONKeys.STATUS, -1);
        if (status < 0) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.STATUS, status));
        } else {
            accountStatus = AccountStatus.valueOf(status);
        }

        String login = jsObject.getString(Introspection.JSONKeys.LOGIN, null);
        if (login == null || login.trim().isEmpty()) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.LOGIN, login));
        } else {
            this.login = login;
        }

        String pwd = jsObject.getString(Introspection.JSONKeys.PASSWORD, null);
        if (pwd == null || pwd.trim().isEmpty()) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.PASSWORD, pwd));
        } else {
            this.password = pwd;
        }

        String mobile = jsObject.getString(Introspection.JSONKeys.MOBILE, null);
        if (mobile != null) {
            this.mobile = mobile;
        }

        String email = jsObject.getString(Introspection.JSONKeys.EMAIL, null);
        if (email != null) {
            this.email = email.toLowerCase();
        }

        String name = jsObject.getString(Introspection.JSONKeys.NAME, null);
        if (name == null || name.trim().isEmpty()) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.NAME, name));
        } else {
            this.name = name;
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

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getName() {
        return name;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public String getPassword() {
        return password;
    }

    public String getImageUri() {
        return imageUri;
    }

    public Role[] getRoles() {
        return roles;
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.NULL;
    }
}
