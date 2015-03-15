package com.free.walker.service.itinerary.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.LocalMessages;

public class SystemConfigUtil {
    private static final Logger LOG = LoggerFactory.getLogger(SystemConfigUtil.class);

    private static final String DEFAULT_CONFIG = "com/free/walker/service/itinerary/dao/config.properties";
    private static final String DEFAULT_CONFIG_SPRING = "config/ServiceConfig.xml";
    private static final String CONFIG_DIR = new File(System.getProperty("user.dir"), "conf").toString();
    private static final String CONFIG_FILE = "config.properties";
    private static final String SPRING_CONFIG_FILE = "ServiceConfig.xml";

    private static final String DEFAULT_LOG_CONFIG = "log4j.properties";

    public static Properties getApplicationConfig() throws FileNotFoundException, IOException {
        Properties config = new Properties();
        File configFile = new File(CONFIG_DIR, CONFIG_FILE);
        if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
            config.load(new FileInputStream(configFile));
        } else {
            LOG.info(LocalMessages.getMessage(LocalMessages.fallback_config_path, configFile.toString()));
            config.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(DEFAULT_CONFIG));
        }
        return config;
    }

    public static String getApplicationSpringConfig() {
        File configFile = new File(CONFIG_DIR, SPRING_CONFIG_FILE);
        if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
            return configFile.getPath();
        } else {
            LOG.info(LocalMessages.getMessage(LocalMessages.fallback_config_path, configFile.toString()));
            return DEFAULT_CONFIG_SPRING;
        }
    }

    public static Properties getLogConfig() throws IOException {
        Properties config = new Properties();
        config.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(DEFAULT_LOG_CONFIG));
        return config;
    }
}
