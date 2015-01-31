package com.free.walker.service.itinerary.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.dao.DAOConstants;

public class ElasticSearchClientBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchClientBuilder.class);

    private static final String AT = "@";
    private static final String ADDRESS_SPLITTER = ";";
    private static final String HOST_PORT_SPLITTER = ":";

    private static final String DEFAULT_CLUSTER_NAME = "elasticsearch";
    private static final int DEFAULT_CLUSTER_PORT = 9300;

    public Client build(Properties config) {
        if (config == null || config.getProperty(DAOConstants.elasticsearch_url) == null) {
            throw new NullPointerException();
        }

        String elasticSearchUrl = config.getProperty(DAOConstants.elasticsearch_url);
        List<InetSocketTransportAddress> hostAddresses = new ArrayList<InetSocketTransportAddress>();
        String clusterName = elasticSearchUrl.substring(0, elasticSearchUrl.indexOf(AT));
        String esHosts = elasticSearchUrl.substring(elasticSearchUrl.indexOf(AT) + AT.length());

        if (clusterName == null || clusterName.trim().length() == 0) {
            clusterName = DEFAULT_CLUSTER_NAME;
        }

        String[] hosts = esHosts.split(ADDRESS_SPLITTER);
        for (int i = 0; i < hosts.length; i++) {
            String[] host = hosts[i].split(HOST_PORT_SPLITTER);
            if (host.length == 1 && host[0].length() > 0) {
                hostAddresses.add(new InetSocketTransportAddress(host[0], DEFAULT_CLUSTER_PORT));
            } else if (host.length == 2 && host[0].length() > 0) {
                hostAddresses.add(new InetSocketTransportAddress(host[0], Integer.parseInt(host[1])));
            } else if (host.length == 0 && host.length > 2) {
                LOG.warn(LocalMessages.getMessage(LocalMessages.invalid_db_host_address, hosts[i]));
            } else {
                LOG.warn(LocalMessages.getMessage(LocalMessages.invalid_db_host_address, host[0]));
            }
        }

        if (hostAddresses.isEmpty()) {
            throw new IllegalArgumentException();
        } else {
            Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", clusterName).build();
            TransportClient tClient = new TransportClient(settings);
            tClient.addTransportAddresses(hostAddresses.toArray(new TransportAddress[] {}));
            return tClient;
        }
    }
}
