package com.free.walker.service.itinerary.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LocalNetworkInterfaceUtil {
    public static String getLocalIPAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new IllegalStateException(e);
        }
    }
}
