package com.free.walker.service.itinerary.dao.db;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.script.ScriptService.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.dao.DAOConstants;
import com.free.walker.service.itinerary.dao.TravelResourceDAO;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.DependencyException;
import com.free.walker.service.itinerary.primitive.Introspection;
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

    public JsonObject searchResource(QueryTemplate template, Map<String, Object> templageParams)
        throws DatabaseAccessException {
        if (template == null || templageParams == null) {
            throw new NullPointerException();
        }

        SearchResponse response = esClient.prepareSearch(DAOConstants.elasticsearch_resource_index)
            .setTypes(DAOConstants.elasticsearch_resource_type)
            .setTemplateName(template.nameValue())
            .setTemplateType(ScriptType.INDEXED)
            .setTemplateParams(templageParams)
            .get();

        SearchHits hits = response.getHits();
        LOG.info(LocalMessages.getMessage(LocalMessages.resource_index_searched, template.nameValue(),
            templageParams.toString(), response.isTimedOut(), response.isTerminatedEarly(), response.getTookInMillis(),
            response.getTotalShards(), response.getSuccessfulShards(), response.getFailedShards()));

        JsonObjectBuilder resultBuilder = Json.createObjectBuilder();
        resultBuilder.add(Introspection.JSONKeys.TOTAL_HITS_NUMBER, hits.getTotalHits());
        JsonArrayBuilder resultArrayBuilder = Json.createArrayBuilder();
        Iterator<SearchHit> hitsIter = hits.iterator();
        while (hitsIter.hasNext()) {
            SearchHit searchHit = (SearchHit) hitsIter.next();

            LOG.info(LocalMessages.getMessage(LocalMessages.resource_search_hit, searchHit.id(), searchHit.getIndex(),
                searchHit.getType(), searchHit.getId(), searchHit.getVersion(), searchHit.getScore()));

            JsonObject hitSource = Json.createReader(new StringReader(searchHit.getSourceAsString())).readObject();
            resultArrayBuilder.add(hitSource);
        }
        resultBuilder.add(Introspection.JSONKeys.HITS, resultArrayBuilder);

        return resultBuilder.build();
    }

    public JsonObject synchrinizeResource(String providerId, boolean exhausted, Calendar calendar, boolean dryRun)
        throws DependencyException {
        ResourceProvider resourceProvider = resourceProviders.get(providerId);
        if (resourceProvider == null) {
            throw new DependencyException(LocalMessages.getMessage(LocalMessages.not_found_resource_provider,
                providerId));
        }

        if (!resourceProvider.ping()) {
            resourceProvider.sanitize();
            if (!resourceProvider.ping()) {
                throw new DependencyException(LocalMessages.getMessage(LocalMessages.ping_failed_resource_provider,
                    providerId));
            }
        }

        JsonArrayBuilder addedBuilder = Json.createArrayBuilder();
        JsonArrayBuilder updatedBuilder = Json.createArrayBuilder();
        JsonArrayBuilder deletedBuilder = Json.createArrayBuilder();

        Vector<JsonArray> syncResult = resourceProvider.sync(exhausted, calendar);
        JsonObjectBuilder builder = Json.createObjectBuilder();

        JsonArray addedRes = syncResult.get(0);
        for (int i = 0; !dryRun && i < addedRes.size(); i++) {
            JsonObject resource = addedRes.getJsonObject(i);
            String resourceCode = resource.getString(Introspection.JSONKeys.Resounce.CODE);
            String resourceId = resourceCode + '@' + providerId;

            LOG.info(LocalMessages.getMessage(LocalMessages.publish_added_resource, resourceCode, providerId));

            IndexRequestBuilder requestBuilder = esClient.prepareIndex(DAOConstants.elasticsearch_resource_index,
                DAOConstants.elasticsearch_resource_type, resourceId);
            IndexResponse response = requestBuilder.setSource(resource.toString()).execute().actionGet();

            LOG.info(LocalMessages.getMessage(response.isCreated() ? LocalMessages.resource_index_created
                : LocalMessages.resource_index_updated, resourceId, response.getIndex(), response.getType(), response
                .getId(), response.getVersion(), response.getHeaders(), response.isCreated()));

            if (response.isCreated()) {
                addedBuilder.add(response.getId());
            } else {
                updatedBuilder.add(response.getId());
            }
        }

        JsonArray updatedRes = syncResult.get(1);
        for (int i = 0; !dryRun && i < updatedRes.size(); i++) {
            JsonObject resource = updatedRes.getJsonObject(i);
            String resourceCode = resource.getString(Introspection.JSONKeys.Resounce.CODE);
            String resourceId = resourceCode + '@' + providerId;

            LOG.info(LocalMessages.getMessage(LocalMessages.publish_updated_resource, resourceCode, providerId));

            IndexRequestBuilder requestBuilder = esClient.prepareIndex(DAOConstants.elasticsearch_resource_index,
                DAOConstants.elasticsearch_resource_type, resourceId);
            IndexResponse response = requestBuilder.setSource(resource.toString()).execute().actionGet();

            LOG.info(LocalMessages.getMessage(response.isCreated() ? LocalMessages.resource_index_created
                : LocalMessages.resource_index_updated, resourceId, response.getIndex(), response.getType(), response
                .getId(), response.getVersion(), response.getHeaders(), response.isCreated()));

            if (response.isCreated()) {
                addedBuilder.add(response.getId());
            } else {
                updatedBuilder.add(response.getId());
            }
        }

        JsonArray deletedRes = syncResult.get(2);
        for (int i = 0; !dryRun && i < deletedRes.size(); i++) {
            JsonObject resource = deletedRes.getJsonObject(i);
            String resourceCode = resource.getString(Introspection.JSONKeys.Resounce.CODE);
            String resourceId = resourceCode + '@' + providerId;

            LOG.info(LocalMessages.getMessage(LocalMessages.publish_revoked_resource, resourceCode, providerId));

            DeleteRequestBuilder requestBuilder = esClient.prepareDelete(DAOConstants.elasticsearch_resource_index,
                DAOConstants.elasticsearch_resource_type, resourceId);
            DeleteResponse response = requestBuilder.execute().actionGet();
            if (response.isFound()) {
                LOG.info(LocalMessages.getMessage(LocalMessages.resource_index_deleted, resourceId,
                    response.getIndex(), response.getType(), response.getId(), response.getVersion(),
                    response.getHeaders()));
                deletedBuilder.add(response.getId());
            } else {
                LOG.info(LocalMessages.getMessage(LocalMessages.resource_index_not_found, resourceId,
                    response.getIndex(), response.getType(), response.getId(), response.getVersion(),
                    response.getHeaders()));
            }
        }

        builder.add(Introspection.JSONKeys.SYNC_ADD, addedBuilder);
        builder.add(Introspection.JSONKeys.SYNC_ADD_NUMBER, addedRes.size());
        builder.add(Introspection.JSONKeys.SYNC_UPDATE, updatedBuilder);
        builder.add(Introspection.JSONKeys.SYNC_UPDATE_NUMBER, updatedRes.size());
        builder.add(Introspection.JSONKeys.SYNC_DELETE, deletedBuilder);
        builder.add(Introspection.JSONKeys.SYNC_DELETE_NUMBER, deletedRes.size());

        return builder.build();
    }

    public ResourceProvider getResourceProvider(String providerId) {
        return resourceProviders.get(providerId);
    }
}
