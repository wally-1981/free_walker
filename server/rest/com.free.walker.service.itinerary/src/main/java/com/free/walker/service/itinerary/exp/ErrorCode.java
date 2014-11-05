package com.free.walker.service.itinerary.exp;

public class ErrorCode {
    public static final ErrorCode E1001 = new ErrorCode(1001);
    public static final ErrorCode E1002 = new ErrorCode(1001);
    public static final ErrorCode E1003 = new ErrorCode(1001);

    private int code;

    private ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
