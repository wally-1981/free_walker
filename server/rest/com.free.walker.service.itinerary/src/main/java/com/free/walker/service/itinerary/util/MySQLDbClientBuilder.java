package com.free.walker.service.itinerary.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.ibatis.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.LocalMessages;

public class MySQLDbClientBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(MySQLDbClientBuilder.class);

    private static final String MASTER_CONFIG = "com/free/walker/service/itinerary/dao/mybatis-config.xml";
    private static final String DEFAULT_CONFIG = "com/free/walker/service/itinerary/dao/config.properties";
    private static final String CONFIG_DIR = new File(System.getProperty("user.dir"), "conf").toString();
    private static final String CONFIG_FILE = "config.properties";

    public static InputStream getMasterConfig() {
        try {
            return Resources.getResourceAsStream(MASTER_CONFIG);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Properties getConfig() throws FileNotFoundException, IOException {
        Properties config = new Properties();
        File configFile = new File(CONFIG_DIR, CONFIG_FILE);
        if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
            config.load(new FileInputStream(configFile));
        } else {
            config.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(DEFAULT_CONFIG));
            LOG.info(LocalMessages.getMessage(LocalMessages.fallback_config_path_mysql, configFile.toString()));
        }
        return config;
    }
}
