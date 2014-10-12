package com.free.walker.service.itinerary.exp;

import java.util.UUID;

public class InvalidTravelReqirementException extends IllegalAccessException {
    private static final long serialVersionUID = -8526202664364439050L;

    private UUID travelRequirementId;

    public InvalidTravelReqirementException(UUID travelRequirementId, String message) {
        super(message);
        this.travelRequirementId = travelRequirementId;
    }

    public UUID getTravelRequirementId() {
        return travelRequirementId;
    }
}
