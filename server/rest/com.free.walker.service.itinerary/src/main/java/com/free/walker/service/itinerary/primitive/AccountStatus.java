package com.free.walker.service.itinerary.primitive;

public enum AccountStatus {
    ACTIVE, IN_ACTIVE, LOCKED, REVOKED;

    public static AccountStatus valueOf(int ordinal) {
        if (ordinal == ACTIVE.ordinal()) {
            return ACTIVE;
        } else if (ordinal == IN_ACTIVE.ordinal()) {
            return IN_ACTIVE;
        } else if (ordinal == LOCKED.ordinal()) {
            return LOCKED;
        } else if (ordinal == REVOKED.ordinal()) {
            return REVOKED;
        } else {
            return null;
        }
    }
}
