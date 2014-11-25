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

public class InvalidTravelReqirementException extends IllegalAccessException implements Serializable {
    private static final long serialVersionUID = -8526202664364439050L;

    private String context;
    private ErrorCode errorCode;

    public InvalidTravelReqirementException(String message) {
        super(message);
    }

    public InvalidTravelReqirementException(String message, UUID travelRequirementId) {
        super(message);
        this.context = travelRequirementId.toString();
    }

    public InvalidTravelReqirementException(String message, String context) {
        super(message);
        this.context = context;
    }

    public InvalidTravelReqirementException(UUID travelRequirementId, MongoException e) {
        super(e.getMessage());
        this.context = travelRequirementId.toString();
        this.errorCode = new ErrorCode(ErrorCode.ErrorCodeType.MONGO_DB_ERROR, e.getCode());
    }

    public InvalidTravelReqirementException(UUID travelRequirementId, ErrorCode errorCode) {
        super();
        this.context = travelRequirementId.toString();
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

    public InvalidTravelReqirementException fromJSON(JsonObject jsObject) throws JsonException {
        throw new UnsupportedOperationException();
    }
    
    public ValueType getValueType() {
        return JsonValue.ValueType.OBJECT;
    }
}
