package com.free.walker.service.itinerary.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;

public class MySQLDbClientBuilder {
    private static final String MASTER_CONFIG = "com/free/walker/service/itinerary/dao/mybatis-config.xml";

    public static InputStream getMasterConfig() {
        try {
            return Resources.getResourceAsStream(MASTER_CONFIG);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
