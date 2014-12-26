package com.free.walker.service.itinerary.util;

import java.util.UUID;

import com.free.walker.service.itinerary.LocalMessages;

public class UuidUtil {
    public static UUID fromUuidStr(final String uuidStr) {
        try {
            return UUID.fromString(uuidStr);
        } catch (Exception e) {
            throw new IllegalArgumentException(LocalMessages.getMessage(LocalMessages.illegal_uuid, uuidStr), e);
        }
    }

    public static boolean isCmpUuidStr(final String uuidStr) {
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

    public static UUID fromCmpUuidStr(final String uuidCmpStr) {
        if (uuidCmpStr == null) {
            throw new NullPointerException();
        }

        try {
            String uuidCmp = new StringBuffer(uuidCmpStr.substring(0, 8)).append("-").append(uuidCmpStr.substring(8, 12))
                .append("-").append(uuidCmpStr.substring(12, 16)).append("-").append(uuidCmpStr.substring(16, 20))
                .append("-").append(uuidCmpStr.substring(20)).toString();
            return UUID.fromString(uuidCmp);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                LocalMessages.getMessage(LocalMessages.illegal_compact_uuid, uuidCmpStr), e);
        }
    }

    public static String toCmpUuidStr(final String uuidStr) {
        if (uuidStr == null) {
            throw new NullPointerException();
        }

        return uuidStr.replaceAll("-", "");
    }
}
