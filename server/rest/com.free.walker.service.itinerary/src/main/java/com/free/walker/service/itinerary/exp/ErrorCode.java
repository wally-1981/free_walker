package com.free.walker.service.itinerary.exp;

public class ErrorCode {
    public static final ErrorCode E1001 = new ErrorCode(1001);
    public static final ErrorCode E1002 = new ErrorCode(1002);
    public static final ErrorCode E1003 = new ErrorCode(1003);

    public static class ErrorCodeType {
        public static final ErrorCodeType MONGO_DB_ERROR = new ErrorCodeType(1);

        private int type;
        private ErrorCodeType(int type) {
            this.type = type;
        }

        public int enumValue() {
            return type;
        }
    }

    private ErrorCodeType type;
    private int code;

    private ErrorCode(int code) {
        this.code = code;
    }

    public ErrorCode(ErrorCodeType type, int code) {
        this.type = type;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public ErrorCodeType getType() {
        return type;
    }
}
