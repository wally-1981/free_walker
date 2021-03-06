package com.free.walker.service.itinerary.exp;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.free.walker.service.itinerary.Serializable;
import com.free.walker.service.itinerary.primitive.Introspection;

public class DependencyException extends Exception implements Serializable {
    private static final long serialVersionUID = 7127810896672235241L;

    private String context;
    private ErrorCode errorCode;

    public DependencyException(String msg) {
        super(msg);
    }

    public DependencyException(Exception e) {
        super(e);
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.NULL;
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder res = Json.createObjectBuilder();

        if (errorCode != null) {
            res.add(Introspection.JSONKeys.ERROR_CODE, errorCode.getCode());
        }

        if (context != null) {
            res.add(Introspection.JSONKeys.ERROR_CNTX, context);
        }

        if (super.getMessage() != null) {
            res.add(Introspection.JSONKeys.ERROR_DESC, super.getMessage());
        }

        return res.build();
    }

    public Object fromJSON(JsonObject jsObject) throws JsonException {
        throw new UnsupportedOperationException();
    }
}
