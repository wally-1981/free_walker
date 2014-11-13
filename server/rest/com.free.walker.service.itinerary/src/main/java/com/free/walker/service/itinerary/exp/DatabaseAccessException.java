package com.free.walker.service.itinerary.exp;

public class DatabaseAccessException extends Exception {
    private static final long serialVersionUID = 2570052914730752469L;

    public DatabaseAccessException(Exception e) {
        super(e);
    }
}
