package com.free.walker.service.itinerary.primitive;

public enum AccountType {
    MASTER, WeChat, QQ, AliPay, WeiBo;

    public static AccountType valueOf(int ordinal) {
        if (ordinal == MASTER.ordinal()) {
            return MASTER;
        } else if (ordinal == WeChat.ordinal()) {
            return WeChat;
        } else if (ordinal == QQ.ordinal()) {
            return QQ;
        } else if (ordinal == AliPay.ordinal()) {
            return AliPay;
        } else if (ordinal == WeiBo.ordinal()) {
            return WeiBo;
        } else {
            return MASTER;
        }
    }
}
