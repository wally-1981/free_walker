package com.free.walker.service.itinerary.exp;

import java.util.UUID;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.free.walker.service.itinerary.Serializable;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.mongodb.MongoException;

public class InvalidAccountException extends IllegalAccessException implements Serializable {
    private static final long serialVersionUID = -8526202664364439050L;

    private String context;
    private ErrorCode errorCode;

    public InvalidAccountException(String message) {
        super(message);
    }

    public InvalidAccountException(String message, UUID travelProductId) {
        super(message);
        this.context = travelProductId.toString();
    }

    public InvalidAccountException(String message, String context) {
        super(message);
        this.context = context;
    }

    public InvalidAccountException(UUID travelProductId, MongoException e) {
        super(e.getMessage());
        this.context = travelProductId.toString();
        this.errorCode = new ErrorCode(ErrorCode.ErrorCodeType.MONGO_DB_ERROR, e.getCode());
    }

    public InvalidAccountException(UUID travelProductId, ErrorCode errorCode) {
        super();
        this.context = travelProductId.toString();
        this.errorCode = errorCode;
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder res = Json.createObjectBuilder();

        if (errorCode != null) {
            res.add(Introspection.JSONKeys.ERROR_CODE, errorCode.getCode());

            if (errorCode.getType() != null) {
                res.add(Introspection.JSONKeys.ERROR_TYPE, errorCode.getType().enumValue());
            }
        }

        if (context != null) {
            res.add(Introspection.JSONKeys.ERROR_CNTX, context);
        }

        if (super.getMessage() != null) {
            res.add(Introspection.JSONKeys.ERROR_DESC, super.getMessage());
        }

        return res.build();
    }

    public InvalidAccountException fromJSON(JsonObject jsObject) throws JsonException {
        throw new UnsupportedOperationException();
    }
    
    public ValueType getValueType() {
        return JsonValue.ValueType.NULL;
    }
}
