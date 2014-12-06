package com.free.walker.service.itinerary.util;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.dao.DAOConstants;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

public class MongoDbClientBuilder {
    private static Logger LOG = LoggerFactory.getLogger(MongoDbClientBuilder.class);

    private static final String AT = "@";
    private static final String ADDRESS_SPLITTER = ";";
    private static final String HOST_PORT_SPLITTER = ":";

    private static final int DEFAULT_DB_PORT = 27017;

    private MongoClient client;

    public DB build(String dbName, Properties config) throws UnknownHostException {
        if (config == null || config.getProperty(DAOConstants.mongo_database_url) == null || dbName == null
            || dbName.trim().length() == 0) {
            throw new NullPointerException();
        }

        String mongoDatabaseUrl = config.getProperty(DAOConstants.mongo_database_url);
        mongoDatabaseUrl = new StringBuffer(dbName).append(mongoDatabaseUrl).toString();

        List<ServerAddress> hostAddresses = new ArrayList<ServerAddress>();
        String dbHosts = mongoDatabaseUrl.substring(mongoDatabaseUrl.indexOf(AT) + AT.length());
        String[] hosts = dbHosts.split(ADDRESS_SPLITTER);
        for (int i = 0; i < hosts.length; i++) {
            String[] host = hosts[i].split(HOST_PORT_SPLITTER);
            if (host.length == 1 && host[0].length() > 0) {
                hostAddresses.add(new ServerAddress(host[0], DEFAULT_DB_PORT));
            } else if (host.length == 2 && host[0].length() > 0) {
                hostAddresses.add(new ServerAddress(host[0], Integer.parseInt(host[1])));
            } else if (host.length == 0 && host.length > 2) {
                LOG.warn(LocalMessages.getMessage(LocalMessages.invalid_db_host_address, hosts[i]));
            } else {
                LOG.warn(LocalMessages.getMessage(LocalMessages.invalid_db_host_address, host[0]));
            }
        }

        if (hostAddresses.isEmpty() || dbName == null || dbName.length() == 0) {
            throw new IllegalArgumentException();
        } else {
            if (client == null) {
                client = new MongoClient(hostAddresses);
            }
            return client.getDB(dbName);
        }
    }
}
