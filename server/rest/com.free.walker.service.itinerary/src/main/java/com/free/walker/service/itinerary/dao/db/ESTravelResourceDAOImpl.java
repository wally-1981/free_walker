package com.free.walker.service.itinerary.dao.db;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.json.JsonObject;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.dao.DAOConstants;
import com.free.walker.service.itinerary.dao.TravelResourceDAO;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.DependencyException;
import com.free.walker.service.itinerary.primitive.QueryTemplate;
import com.free.walker.service.itinerary.res.ResourceProvider;
import com.free.walker.service.itinerary.util.ElasticSearchClientBuilder;
import com.free.walker.service.itinerary.util.ResourceProviderBuilder;
import com.free.walker.service.itinerary.util.SystemConfigUtil;
import com.ibm.icu.util.Calendar;

public class ESTravelResourceDAOImpl implements TravelResourceDAO {
    private static final Logger LOG = LoggerFactory.getLogger(ESTravelResourceDAOImpl.class);

    private Map<String, ResourceProvider> resourceProviders;

    private Client esClient;
    private String esUrl;

    private static class SingletonHolder {
        private static final TravelResourceDAO INSTANCE = new ESTravelResourceDAOImpl();
    }

    public static TravelResourceDAO getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public ESTravelResourceDAOImpl() {
        try {
            Properties config = SystemConfigUtil.getApplicationConfig();

            resourceProviders = new HashMap<String, ResourceProvider>();

            String[] providerIds = config.getProperty(Constants.provider_ids).split(Constants.dot_sep);
            for (int i = 0; providerIds != null && i < providerIds.length; i++) {
                if (providerIds[i] != null && !providerIds[i].trim().isEmpty()) {
                    ResourceProvider provider = ResourceProviderBuilder.build(providerIds[i], config);
                    if (provider != null) resourceProviders.put(providerIds[i], provider);
                }
            }

            if (resourceProviders.isEmpty()) {
                throw new IllegalStateException(LocalMessages.getMessage(LocalMessages.missing_resource_provider));
            }

            esClient = new ElasticSearchClientBuilder().build(config);
            esUrl = config.getProperty(DAOConstants.elasticsearch_url);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            if (!pingPersistence()){
                if (esClient != null) {
                    esClient.close();
                    esClient = null;
                }

                throw new IllegalStateException();
            }
        }
    }

    public boolean pingPersistence() {
        boolean result = false;

        try {
            Collection<ResourceProvider> providers = resourceProviders.values();
            Iterator<ResourceProvider> providerIter = providers.iterator();
            while (providerIter.hasNext()) {
                ResourceProvider provider = providerIter.next();
                if (!provider.ping()) {
                    return false;
                } else {
                    result = true;
                }
            }
        } catch (Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.resource_provider_init_failure), e);
            return false;
        }

        try {
            ClusterHealthStatus esHealthStatus = esClient.admin().cluster().health(new ClusterHealthRequest()).get()
                .getStatus();
            if (ClusterHealthStatus.RED.equals(esHealthStatus)) {
                LOG.error(LocalMessages.getMessage(LocalMessages.elasticsearch_abnormal_status, esHealthStatus));
                return false;
            } else if (ClusterHealthStatus.YELLOW.equals(esHealthStatus)) {
                LOG.warn(LocalMessages.getMessage(LocalMessages.elasticsearch_abnormal_status, esHealthStatus));
                return true;
            } else {
                result = result && true;
            }
        } catch (InterruptedException | ExecutionException | RuntimeException e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_init_failure, esUrl, Client.class.getName()), e);
            return false;
        }

        return result;
    }

    public JsonObject searchResources(QueryTemplate queryTemplate, Map<String, String> templageParams)
        throws DatabaseAccessException {
        return null;
    }

    public boolean synchrinizeResources(String providerId, boolean exhausted, Calendar calendar)
        throws DependencyException {
        ResourceProvider resourceProvider = resourceProviders.get(providerId);
        if (resourceProvider == null) {
            LOG.warn(LocalMessages.getMessage(LocalMessages.not_found_resource_provider, providerId));
            return false;
        }

        if (!resourceProvider.ping()) {
            resourceProvider.sanitize();
            if (!resourceProvider.ping()) {
                throw new DependencyException(LocalMessages.getMessage(LocalMessages.ping_failed_resource_provider,
                    providerId));
            }
        }

        return resourceProvider.sync(exhausted, calendar);
    }

    public ResourceProvider getResourceProvider(String providerId) {
        return resourceProviders.get(providerId);
    }
}
