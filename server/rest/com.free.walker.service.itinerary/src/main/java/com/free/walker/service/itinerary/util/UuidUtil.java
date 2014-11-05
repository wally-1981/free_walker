package com.free.walker.service.itinerary.util;

import java.util.UUID;

import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;

public class UuidUtil {
    public static UUID fromUuidStr(String uuidStr) throws InvalidTravelReqirementException {
        try {
            return UUID.fromString(uuidStr);
        } catch (Exception e) {
            throw new InvalidTravelReqirementException(e.getMessage(), uuidStr);
        }
    }
}
