package com.free.walker.service.itinerary.primitive;

public enum AccountType {
    ADMIN, MASTER, AGENCY, WeChat, QQ, AliPay, WeiBo;

    public static AccountType valueOf(int ordinal) {
        if (ordinal == ADMIN.ordinal()) {
            return ADMIN;
        } else if (ordinal == MASTER.ordinal()) {
            return MASTER;
        } else if (ordinal == AGENCY.ordinal()) {
            return AGENCY;
        } else if (ordinal == WeChat.ordinal()) {
            return WeChat;
        } else if (ordinal == QQ.ordinal()) {
            return QQ;
        } else if (ordinal == AliPay.ordinal()) {
            return AliPay;
        } else if (ordinal == WeiBo.ordinal()) {
            return WeiBo;
        } else {
            return null;
        }
    }

    public static boolean isTouristAccount(int ordinal) {
        if (ordinal == ADMIN.ordinal()) {
            return false;
        } else if (ordinal == MASTER.ordinal()) {
            return true;
        } else if (ordinal == AGENCY.ordinal()) {
            return false;
        } else if (ordinal == WeChat.ordinal()) {
            return true;
        } else if (ordinal == QQ.ordinal()) {
            return true;
        } else if (ordinal == AliPay.ordinal()) {
            return true;
        } else if (ordinal == WeiBo.ordinal()) {
            return true;
        } else {
            return false;
        }
    }
}
