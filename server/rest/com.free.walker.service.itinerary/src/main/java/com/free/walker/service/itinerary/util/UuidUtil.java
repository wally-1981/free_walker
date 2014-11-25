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

    public static boolean isCmpUuidStr(String uuidStr) {
        if (uuidStr == null) {
            throw new NullPointerException();
        }

        if (uuidStr.charAt(8) == '-' && uuidStr.charAt(13) == '-' && uuidStr.charAt(18) == '-'
            && uuidStr.charAt(23) == '-') {
            return false;
        } else {
            if (uuidStr.contains("-")) {
                return false;
            } else {
                return true;
            }
        }
    }

    public static UUID fromCmpUuidStr(String uuidCmpStr) throws InvalidTravelReqirementException {
        if (uuidCmpStr == null) {
            throw new NullPointerException();
        }

        try {
            uuidCmpStr = new StringBuffer(uuidCmpStr.substring(0, 8)).append("-").append(uuidCmpStr.substring(8, 12))
                .append("-").append(uuidCmpStr.substring(12, 16)).append("-").append(uuidCmpStr.substring(16, 20))
                .append("-").append(uuidCmpStr.substring(20)).toString();
            return UUID.fromString(uuidCmpStr);
        } catch (Exception e) {
            throw new InvalidTravelReqirementException(e.getMessage(), uuidCmpStr);
        }
    }

    public static String toCmpUuidStr(String uuidStr) {
        if (uuidStr == null) {
            throw new NullPointerException();
        }

        return uuidStr.replaceAll("-", "");
    }
}
