package com.free.walker.service.itinerary.exp;

import java.util.UUID;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.SerializableJSON;

public class InvalidTravelReqirementException extends IllegalAccessException implements SerializableJSON {
    private static final long serialVersionUID = -8526202664364439050L;

    private String context;
    private ErrorCode errorCode;

    public InvalidTravelReqirementException(String message, UUID travelRequirementId) {
        super(message);
        this.context = travelRequirementId.toString();
    }

    public InvalidTravelReqirementException(String message, String context) {
        super(message);
        this.context = context;
    }

    public InvalidTravelReqirementException(UUID travelRequirementId, ErrorCode errorCode) {
        super();
        this.context = travelRequirementId.toString();
        this.errorCode = errorCode;
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder res = Json.createObjectBuilder();

        if (errorCode != null) {
            res.add(Constants.JSONKeys.ERROR_CODE, errorCode.getCode());
        }

        if (context != null) {
            res.add(Constants.JSONKeys.ERROR_CNTX, context);
        }

        if (super.getMessage() != null) {
            res.add(Constants.JSONKeys.ERROR_DESC, super.getMessage());
        }

        return res.build();
    }
    
    public ValueType getValueType() {
        return JsonValue.ValueType.OBJECT;
    }
}
