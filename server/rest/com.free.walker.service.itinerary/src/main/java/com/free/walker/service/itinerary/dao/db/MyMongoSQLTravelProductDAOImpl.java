package com.free.walker.service.itinerary.dao.db;

import java.io.IOException;
import java.io.StringReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.commons.lang3.StringUtils;
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
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.Account;
import com.free.walker.service.itinerary.basic.Continent;
import com.free.walker.service.itinerary.basic.StringTriple;
import com.free.walker.service.itinerary.basic.TravelLocation;
import com.free.walker.service.itinerary.dao.AccountDAO;
import com.free.walker.service.itinerary.dao.DAOConstants;
import com.free.walker.service.itinerary.dao.DAOFactory;
import com.free.walker.service.itinerary.dao.TravelBasicDAO;
import com.free.walker.service.itinerary.dao.TravelProductDAO;
import com.free.walker.service.itinerary.dao.TravelRequirementDAO;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidAccountException;
import com.free.walker.service.itinerary.exp.InvalidTravelProductException;
import com.free.walker.service.itinerary.primitive.AccountType;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.primitive.ProductStatus;
import com.free.walker.service.itinerary.primitive.QueryTemplate;
import com.free.walker.service.itinerary.primitive.SortType;
import com.free.walker.service.itinerary.product.Bidding;
import com.free.walker.service.itinerary.product.HotelItem;
import com.free.walker.service.itinerary.product.ResortItem;
import com.free.walker.service.itinerary.product.SimpleTravelProduct;
import com.free.walker.service.itinerary.product.TrafficItem;
import com.free.walker.service.itinerary.product.TravelProduct;
import com.free.walker.service.itinerary.product.TravelProductItem;
import com.free.walker.service.itinerary.product.TrivItem;
import com.free.walker.service.itinerary.req.ItineraryRequirement;
import com.free.walker.service.itinerary.req.TravelProposal;
import com.free.walker.service.itinerary.req.TravelRequirement;
import com.free.walker.service.itinerary.util.ElasticSearchClientBuilder;
import com.free.walker.service.itinerary.util.JsonObjectHelper;
import com.free.walker.service.itinerary.util.JsonObjectUtil;
import com.free.walker.service.itinerary.util.MongoDbClientBuilder;
import com.free.walker.service.itinerary.util.SystemConfigUtil;
import com.free.walker.service.itinerary.util.UuidUtil;
import com.ibm.icu.util.Calendar;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.QueryBuilder;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

public class MyMongoSQLTravelProductDAOImpl implements TravelProductDAO {
    private static final Logger LOG = LoggerFactory.getLogger(MyMongoSQLTravelProductDAOImpl.class);
    private static final DBObject ID_FIELD = new BasicDBObjectBuilder().add(DAOConstants.mongo_database_pk, true).get();

    private MongoClient mdbClient;
    private String productMongoDbUrl;

    private Client esClient;
    private String esUrl;

    private DB productDb;

    private AccountDAO accountDao;
    private TravelBasicDAO travelBasicDao;
    private TravelRequirementDAO travelRequirementDao;

    private static class SingletonHolder {
        private static final TravelProductDAO INSTANCE = new MyMongoSQLTravelProductDAOImpl();
    }

    public static TravelProductDAO getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private MyMongoSQLTravelProductDAOImpl() {
        try {
            Properties config = SystemConfigUtil.getApplicationConfig();

            mdbClient = new MongoDbClientBuilder().build(config);
            productMongoDbUrl = StringUtils.join(DAOConstants.product_mongo_database,
                config.getProperty(DAOConstants.mongo_database_url));
            productDb = mdbClient.getDB(DAOConstants.product_mongo_database);

            esClient = new ElasticSearchClientBuilder().build(config);
            esUrl = config.getProperty(DAOConstants.elasticsearch_url);
        } catch (UnknownHostException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            if (!pingPersistence()){
                if (mdbClient != null) {
                    mdbClient.close();
                    mdbClient = null;
                }

                if (esClient != null) {
                    esClient.close();
                    esClient = null;
                }
                throw new IllegalStateException();
            }
        }

        accountDao = DAOFactory.getAccountDAO(MyMongoSQLAccountDAOImpl.class.getName());
        travelBasicDao = DAOFactory.getTravelBasicDAO();
        travelRequirementDao = DAOFactory.getTravelRequirementDAO(MyMongoSQLTravelRequirementDAOImpl.class.getName());
    }

    public boolean pingPersistence() {
        boolean result = false;

        DBObject ping = new BasicDBObject("ping", "1");
        try {
            CommandResult cr = productDb.command(ping);
            if (!cr.ok()) {
                return false;
            } else {
                result = true;
            }
        } catch (RuntimeException e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_init_failure, productMongoDbUrl,
                MongoClient.class.getName()), e);
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

    public UUID createProduct(Account account, TravelProduct travelProduct) throws InvalidTravelProductException,
        DatabaseAccessException {
        if (account == null || travelProduct == null) {
            throw new NullPointerException();
        }

        DBCollection productColls = productDb.getCollection(DAOConstants.PRODUCT_COLL_NAME);
        if (productColls.findOne(travelProduct.getProductUUID().toString(), ID_FIELD) != null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.existed_travel_product,
                travelProduct.getProductUUID()), travelProduct.getProductUUID());
        }

        DBCollection productHotelColls = productDb.getCollection(DAOConstants.PRODUCT_HOTEL_COLL_NAME);
        if (productHotelColls.findOne(travelProduct.getProductUUID().toString(), ID_FIELD) != null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.existed_travel_product,
                travelProduct.getProductUUID()), travelProduct.getProductUUID());
        }

        DBCollection productTrafficColls = productDb.getCollection(DAOConstants.PRODUCT_TRAFFIC_COLL_NAME);
        if (productTrafficColls.findOne(travelProduct.getProductUUID().toString(), ID_FIELD) != null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.existed_travel_product,
                travelProduct.getProductUUID()), travelProduct.getProductUUID());
        }

        DBCollection productResortColls = productDb.getCollection(DAOConstants.PRODUCT_RESORT_COLL_NAME);
        if (productResortColls.findOne(travelProduct.getProductUUID().toString(), ID_FIELD) != null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.existed_travel_product,
                travelProduct.getProductUUID()), travelProduct.getProductUUID());
        }

        DBCollection productTrivColls = productDb.getCollection(DAOConstants.PRODUCT_TRIV_COLL_NAME);
        if (productTrivColls.findOne(travelProduct.getProductUUID().toString(), ID_FIELD) != null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.existed_travel_product,
                travelProduct.getProductUUID()), travelProduct.getProductUUID());
        }

        JsonArrayBuilder hotelItemsJs = Json.createArrayBuilder();
        JsonArrayBuilder trafficItemsJs = Json.createArrayBuilder();
        JsonArrayBuilder resortItemsJs = Json.createArrayBuilder();
        JsonArrayBuilder trivItemsJs = Json.createArrayBuilder();
        for (int i = 0; i < travelProduct.getTravelProductItems().size(); i++) {
            TravelProductItem item = travelProduct.getTravelProductItems().get(i);
            if (item.isTriv()) {
                trivItemsJs.add(item.toJSON());
            } else {
                if (HotelItem.SUB_TYPE.equals(item.getType())) {
                    hotelItemsJs.add(item.toJSON());
                } else if (TrafficItem.SUB_TYPE.equals(item.getType())) {
                    trafficItemsJs.add(item.toJSON());
                } else if (ResortItem.SUB_TYPE.equals(item.getType())) {
                    resortItemsJs.add(item.toJSON());
                } else {
                    continue;
                }
            }
        }

        travelProduct.getTravelProductItems().clear();
        JsonObject productJs = travelProduct.getCore().toJSON();

        try {
            WriteResult wr = storeProduct(productJs, account.getUuid());
            LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_create_record, wr.toString()));
        } catch (MongoException e) {
            throw new DatabaseAccessException(e);
        }

        try {
            WriteResult wr = storeProductItems(travelProduct.getProductUUID(), HotelItem.SUB_TYPE, hotelItemsJs.build());
            LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
        } catch (MongoException e) {
            throw new DatabaseAccessException(e);
        }

        try {
            WriteResult wr = storeProductItems(travelProduct.getProductUUID(), TrafficItem.SUB_TYPE, trafficItemsJs.build());
            LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
        } catch (MongoException e) {
            throw new DatabaseAccessException(e);
        }

        try {
            WriteResult wr = storeProductItems(travelProduct.getProductUUID(), ResortItem.SUB_TYPE, resortItemsJs.build());
            LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
        } catch (MongoException e) {
            throw new DatabaseAccessException(e);
        }

        try {
            WriteResult wr = storeProductItems(travelProduct.getProductUUID(), TrivItem.SUB_TYPE, trivItemsJs.build());
            LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
        } catch (MongoException e) {
            throw new DatabaseAccessException(e);
        }

        return travelProduct.getProductUUID();
    }

    public UUID addItem(UUID productId, TravelProductItem travelProductItem) throws InvalidTravelProductException,
        DatabaseAccessException {
        if (productId ==null || travelProductItem == null) {
            throw new NullPointerException();
        }

        DBCollection productColls = productDb.getCollection(DAOConstants.PRODUCT_COLL_NAME);
        if (productColls.findOne(productId.toString(), ID_FIELD) == null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.missing_travel_product,
                productId), productId);
        }

        DBCollection productItemColls;
        if (HotelItem.SUB_TYPE.equals(travelProductItem.getType())) {
            productItemColls = productDb.getCollection(DAOConstants.PRODUCT_HOTEL_COLL_NAME);
        } else if (TrafficItem.SUB_TYPE.equals(travelProductItem.getType())) {
            productItemColls = productDb.getCollection(DAOConstants.PRODUCT_TRAFFIC_COLL_NAME);
        } else if (ResortItem.SUB_TYPE.equals(travelProductItem.getType())) {
            productItemColls = productDb.getCollection(DAOConstants.PRODUCT_RESORT_COLL_NAME);
        } else {
            productItemColls = productDb.getCollection(DAOConstants.PRODUCT_TRIV_COLL_NAME);
        }

        DBObject productItemBs = productItemColls.findOne(productId.toString());
        JsonArrayBuilder itemsBuilder = Json.createArrayBuilder();
        if (productItemBs == null) {
            itemsBuilder.add(travelProductItem.toJSON());
        } else {
            JsonObject productHotels = Json.createReader(new StringReader(productItemBs.toString())).readObject();
            JsonArray hotels = productHotels.getJsonArray(Introspection.JSONKeys.ITEMS);
            for (int i = 0; i < hotels.size(); i++) {
                itemsBuilder.add(hotels.get(i));
            }
            itemsBuilder.add(travelProductItem.toJSON());
        }

        try {
            WriteResult wr = storeProductItems(productId, travelProductItem.getType(), itemsBuilder.build());
            LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
        } catch (MongoException e) {
            throw new DatabaseAccessException(e);
        }

        return travelProductItem.getUUID();
    }

    public UUID setBidding(UUID productId, Bidding bidding) throws InvalidTravelProductException,
        DatabaseAccessException {
        if (productId == null || bidding == null) {
            throw new NullPointerException();
        }

        DBCollection productColls = productDb.getCollection(DAOConstants.PRODUCT_COLL_NAME);
        if (productColls.findOne(productId.toString(), ID_FIELD) == null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.missing_travel_product,
                productId), productId);
        }

        DBCollection productBiddingColls = productDb.getCollection(DAOConstants.PRODUCT_BIDDING_COLL_NAME);
        if (productBiddingColls.findOne(productId.toString(), ID_FIELD) != null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.existed_product_bidding,
                productId), productId);
        }

        try {
            WriteResult wr = storeProductBidding(productId, bidding.toJSON());
            LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_create_record, wr.toString()));
        } catch (MongoException e) {
            throw new DatabaseAccessException(e);
        }

        return productId;
    }

    public TravelProduct getProduct(UUID productId) throws InvalidTravelProductException, DatabaseAccessException {
        if (productId == null) {
            throw new NullPointerException();
        }

        DBCollection productColls = productDb.getCollection(DAOConstants.PRODUCT_COLL_NAME);
        DBObject productBs = productColls.findOne(productId.toString());
        if (productBs == null) {
            return null;
        } else {
            JsonObject product = Json.createReader(new StringReader(productBs.toString())).readObject();
            TravelProduct travelProduct = new SimpleTravelProduct().fromJSON(product);
            return travelProduct;
        }
    }

    public List<TravelProduct> getProducts(UUID proposalId) throws InvalidTravelProductException,
        DatabaseAccessException {
        if (proposalId == null) {
            throw new NullPointerException();
        }

        List<TravelProduct> result = new ArrayList<TravelProduct>();
        DBCollection productColls = productDb.getCollection(DAOConstants.PRODUCT_COLL_NAME);
        DBCursor productsCr = productColls.find(new BasicDBObject(Introspection.JSONKeys.REF_UUID, proposalId
            .toString()));
        try {
            while (productsCr.hasNext()) {
                JsonObject product = Json.createReader(new StringReader(productsCr.next().toString())).readObject();
                result.add(JsonObjectHelper.toProduct(product, true));
            }
        } finally {
            productsCr.close();
        }

        return result;
    }

    public List<TravelProduct> getProducts(Account account, ProductStatus status) throws InvalidTravelProductException,
        DatabaseAccessException {
        if (account == null || status == null) {
            throw new NullPointerException();
        }

        if (ProductStatus.DRAFT_PRODUCT.equals(status) || ProductStatus.PRIVATE_PRODUCT.equals(status)) {
            Calendar since = Calendar.getInstance();
            since.add(Calendar.MONTH, -6);

            List<TravelProduct> result = new ArrayList<TravelProduct>();
            DBCollection productColls = productDb.getCollection(DAOConstants.PRODUCT_COLL_NAME);
            BasicDBObject query1 = new BasicDBObject(Introspection.JSONKeys.STATUS, status.enumValue());
            BasicDBObject query2 = new BasicDBObject(Introspection.JSONKeys.OWNER, account.getUuid().toString());
            BasicDBObject query3 = new BasicDBObject(Introspection.JSONKeys.DEADLINE_DATETIME, new BasicDBObject(
                "$gte", since.getTimeInMillis()));
            BasicDBObject productQuery = new BasicDBObject("$and", new BasicDBObject[] { query1, query2, query3 });
            BasicDBObject sorter = new BasicDBObject(Introspection.JSONKeys.DEADLINE_DATETIME, -1);
            DBCursor productsCr = productColls.find(productQuery).sort(sorter).limit(300);
            try {
                while (productsCr.hasNext()) {
                    JsonObject product = Json.createReader(new StringReader(productsCr.next().toString())).readObject();
                    result.add(JsonObjectHelper.toProduct(product, true));
                }
            } finally {
                productsCr.close();
            }
            return result;
        } else {
            Map<String, Object> templageParams = new HashMap<String, Object>();
            templageParams.put(DAOConstants.elasticsearch_product_status, String.valueOf(status.enumValue()));
            templageParams.put(DAOConstants.elasticsearch_from, String.valueOf(0));
            templageParams.put(DAOConstants.elasticsearch_sort_key, Introspection.JSONKeys.DEADLINE_DATETIME);
            templageParams.put(DAOConstants.elasticsearch_sort_order, SortOrder.DESC.toString());
            templageParams.put(DAOConstants.elasticsearch_sort_type, SortType.LONG.nameValue());

            SearchResponse response = null;
            QueryTemplate template = null;
            if (AccountType.isTouristAccount(account.getAccountType())) {
                templageParams.put(DAOConstants.elasticsearch_size, String.valueOf(100));
                templageParams.put(DAOConstants.elasticsearch_proposal_owner, account.getUuid());
                template = QueryTemplate.getQueryTemplate(Introspection.JSONValues.PROPOSAL_OWNER_TEMPLATE_AS_INT);
                response = esClient.prepareSearch(DAOConstants.elasticsearch_product_index)
                    .setTypes(DAOConstants.elasticsearch_product_type)
                    .setTemplateName(template.nameValue())
                    .setTemplateType(ScriptType.INDEXED)
                    .setTemplateParams(templageParams)
                    .get();
            } else {
                templageParams.put(DAOConstants.elasticsearch_size, String.valueOf(500));
                templageParams.put(DAOConstants.elasticsearch_product_owner, account.getUuid());
                template = QueryTemplate.getQueryTemplate(Introspection.JSONValues.PRODUCT_OWNER_TEMPLATE_AS_INT);
                response = esClient.prepareSearch(DAOConstants.elasticsearch_product_index)
                    .setTypes(DAOConstants.elasticsearch_product_type)
                    .setTemplateName(template.nameValue())
                    .setTemplateType(ScriptType.INDEXED)
                    .setTemplateParams(templageParams)
                    .get();
            }

            SearchHits hits = response.getHits();
            LOG.info(LocalMessages.getMessage(LocalMessages.product_index_searched, template.nameValue(),
                templageParams.toString(), response.isTimedOut(), response.isTerminatedEarly(),
                response.getTookInMillis(), response.getTotalShards(), response.getSuccessfulShards(),
                response.getFailedShards()));

            List<TravelProduct> result = new ArrayList<TravelProduct>();
            Iterator<SearchHit> hitsIter = hits.iterator();
            while (hitsIter.hasNext()) {
                SearchHit searchHit = (SearchHit) hitsIter.next();

                LOG.info(LocalMessages.getMessage(LocalMessages.product_search_hit, searchHit.id(),
                    searchHit.getIndex(), searchHit.getType(), searchHit.getId(), searchHit.getVersion(),
                    searchHit.getScore()));

                JsonObject hitSource = Json.createReader(new StringReader(searchHit.getSourceAsString())).readObject();
                result.add(JsonObjectHelper.toProduct(hitSource, true));
            }
            return result;
        }
    }

    public List<TravelProductItem> getItems(UUID productId, String itemType) throws InvalidTravelProductException,
        DatabaseAccessException {
        if (productId == null) {
            throw new NullPointerException();
        }

        DBCollection productColls = productDb.getCollection(DAOConstants.PRODUCT_COLL_NAME);
        if (productColls.findOne(productId.toString(), ID_FIELD) == null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.missing_travel_product,
                productId), productId);
        }

        DBCollection productHotelColls = productDb.getCollection(DAOConstants.PRODUCT_HOTEL_COLL_NAME);
        DBCollection productTrafficColls = productDb.getCollection(DAOConstants.PRODUCT_TRAFFIC_COLL_NAME);
        DBCollection productResortColls = productDb.getCollection(DAOConstants.PRODUCT_RESORT_COLL_NAME);
        DBCollection productTrivColls = productDb.getCollection(DAOConstants.PRODUCT_TRIV_COLL_NAME);

        List<TravelProductItem> result = new ArrayList<TravelProductItem>();

        if (HotelItem.SUB_TYPE.equals(itemType)) {
            DBObject productHotelsBs = productHotelColls.findOne(productId.toString());
            if (productHotelsBs != null) {
                JsonObject productHotels = Json.createReader(new StringReader(productHotelsBs.toString())).readObject();
                JsonArray hotelsJs = productHotels.getJsonArray(Introspection.JSONKeys.ITEMS);
                for (int i = 0; i < hotelsJs.size(); i++) {
                    result.add(JsonObjectHelper.toProductItem(hotelsJs.getJsonObject(i), true));
                }
            }
        }

        if (TrafficItem.SUB_TYPE.equals(itemType)) {
            DBObject productTrafficsBs = productTrafficColls.findOne(productId.toString());
            if (productTrafficsBs != null) {
                JsonObject productTraffics = Json.createReader(new StringReader(productTrafficsBs.toString())).readObject();
                JsonArray trafficsJs = productTraffics.getJsonArray(Introspection.JSONKeys.ITEMS);
                for (int i = 0; i < trafficsJs.size(); i++) {
                    result.add(JsonObjectHelper.toProductItem(trafficsJs.getJsonObject(i), true));
                }
            }
        }

        if (ResortItem.SUB_TYPE.equals(itemType)) {
            DBObject productResortsBs = productResortColls.findOne(productId.toString());
            if (productResortsBs != null) {
                JsonObject productResorts = Json.createReader(new StringReader(productResortsBs.toString())).readObject();
                JsonArray resortsJs = productResorts.getJsonArray(Introspection.JSONKeys.ITEMS);
                for (int i = 0; i < resortsJs.size(); i++) {
                    result.add(JsonObjectHelper.toProductItem(resortsJs.getJsonObject(i), true));
                }
            }
        }

        if (TrivItem.SUB_TYPE.equals(itemType)) {
            DBObject productTrivsBs = productTrivColls.findOne(productId.toString());
            if (productTrivsBs != null) {
                JsonObject productTrivs = Json.createReader(new StringReader(productTrivsBs.toString())).readObject();
                JsonArray trivsJs = productTrivs.getJsonArray(Introspection.JSONKeys.ITEMS);
                for (int i = 0; i < trivsJs.size(); i++) {
                    result.add(JsonObjectHelper.toProductItem(trivsJs.getJsonObject(i), true));
                }
            }
        }

        return result;
    }

    public Bidding getBidding(UUID productId) throws InvalidTravelProductException, DatabaseAccessException {
        if (productId == null) {
            throw new NullPointerException();
        }

        DBCollection productBiddingColls = productDb.getCollection(DAOConstants.PRODUCT_BIDDING_COLL_NAME);
        DBObject productBs = productBiddingColls.findOne(productId.toString());
        if (productBs == null) {
            return null;
        } else {
            JsonObject bidding = Json.createReader(new StringReader(productBs.toString())).readObject();
            return new Bidding().fromJSON(bidding);
        }
    }

    public UUID removeHotelItem(UUID productId, UUID hotelItemId) throws InvalidTravelProductException,
        DatabaseAccessException {
        if (productId == null || hotelItemId == null) {
            throw new NullPointerException();
        }

        DBCollection productColls = productDb.getCollection(DAOConstants.PRODUCT_COLL_NAME);
        if (productColls.findOne(productId.toString(), ID_FIELD) == null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.missing_travel_product,
                productId), productId);
        }

        DBCollection productBiddingColls = productDb.getCollection(DAOConstants.PRODUCT_BIDDING_COLL_NAME);
        if (productBiddingColls.findOne(productId.toString(), ID_FIELD) != null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(
                LocalMessages.illegal_remove_product_item_operation, productId), productId);
        }

        DBCollection productHotelColls = productDb.getCollection(DAOConstants.PRODUCT_HOTEL_COLL_NAME);
        DBObject productHotelsBs = productHotelColls.findOne(productId.toString());
        if (productHotelsBs != null) {
            JsonArrayBuilder hotelsBuilder = Json.createArrayBuilder();
            boolean removing = false;
            JsonObject productHotels = Json.createReader(new StringReader(productHotelsBs.toString())).readObject();
            JsonArray hotelsJs = productHotels.getJsonArray(Introspection.JSONKeys.ITEMS);
            for (int i = 0; i < hotelsJs.size(); i++) {
                JsonObject hotelJs = hotelsJs.getJsonObject(i);
                if (hotelItemId.toString().equals(hotelJs.getString(Introspection.JSONKeys.UUID, null))) {
                    removing = true;
                } else {
                    hotelsBuilder.add(hotelJs);
                }
            }

            if (removing) {
                try {
                    WriteResult wr = storeProductItems(productId, HotelItem.SUB_TYPE, hotelsBuilder.build());
                    LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
                } catch (MongoException e) {
                    throw new DatabaseAccessException(e);
                }
                return hotelItemId;
            } else {
                return null;
            }
        }

        return null;
    }

    public UUID removeTrafficItem(UUID productId, UUID trafficItemId) throws InvalidTravelProductException,
        DatabaseAccessException {
        if (productId == null || trafficItemId == null) {
            throw new NullPointerException();
        }

        DBCollection productColls = productDb.getCollection(DAOConstants.PRODUCT_COLL_NAME);
        if (productColls.findOne(productId.toString(), ID_FIELD) == null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.missing_travel_product,
                productId), productId);
        }

        DBCollection productBiddingColls = productDb.getCollection(DAOConstants.PRODUCT_BIDDING_COLL_NAME);
        if (productBiddingColls.findOne(productId.toString(), ID_FIELD) != null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(
                LocalMessages.illegal_remove_product_item_operation, productId), productId);
        }

        DBCollection productHotelColls = productDb.getCollection(DAOConstants.PRODUCT_TRAFFIC_COLL_NAME);
        DBObject productHotelsBs = productHotelColls.findOne(productId.toString());
        if (productHotelsBs != null) {
            JsonArrayBuilder hotelsBuilder = Json.createArrayBuilder();
            boolean removing = false;
            JsonObject productHotels = Json.createReader(new StringReader(productHotelsBs.toString())).readObject();
            JsonArray hotelsJs = productHotels.getJsonArray(Introspection.JSONKeys.ITEMS);
            for (int i = 0; i < hotelsJs.size(); i++) {
                JsonObject hotelJs = hotelsJs.getJsonObject(i);
                if (trafficItemId.toString().equals(hotelJs.getString(Introspection.JSONKeys.UUID, null))) {
                    removing = true;
                } else {
                    hotelsBuilder.add(hotelJs);
                }
            }

            if (removing) {
                try {
                    WriteResult wr = storeProductItems(productId, TrafficItem.SUB_TYPE, hotelsBuilder.build());
                    LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
                } catch (MongoException e) {
                    throw new DatabaseAccessException(e);
                }
                return trafficItemId;
            } else {
                return null;
            }
        }

        return null;
    }

    public UUID removeResortItem(UUID productId, UUID resortItemId) throws InvalidTravelProductException,
        DatabaseAccessException {
        if (productId == null || resortItemId == null) {
            throw new NullPointerException();
        }

        DBCollection productColls = productDb.getCollection(DAOConstants.PRODUCT_COLL_NAME);
        if (productColls.findOne(productId.toString(), ID_FIELD) == null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.missing_travel_product,
                productId), productId);
        }

        DBCollection productBiddingColls = productDb.getCollection(DAOConstants.PRODUCT_BIDDING_COLL_NAME);
        if (productBiddingColls.findOne(productId.toString(), ID_FIELD) != null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(
                LocalMessages.illegal_remove_product_item_operation, productId), productId);
        }

        DBCollection productResortColls = productDb.getCollection(DAOConstants.PRODUCT_RESORT_COLL_NAME);
        DBObject productResortsBs = productResortColls.findOne(productId.toString());
        if (productResortsBs != null) {
            JsonArrayBuilder resortsBuilder = Json.createArrayBuilder();
            boolean removing = false;
            JsonObject productResorts = Json.createReader(new StringReader(productResortsBs.toString())).readObject();
            JsonArray resortsJs = productResorts.getJsonArray(Introspection.JSONKeys.ITEMS);
            for (int i = 0; i < resortsJs.size(); i++) {
                JsonObject resortJs = resortsJs.getJsonObject(i);
                if (resortItemId.toString().equals(resortJs.getString(Introspection.JSONKeys.UUID, null))) {
                    removing = true;
                } else {
                    resortsBuilder.add(resortJs);
                }
            }

            if (removing) {
                try {
                    WriteResult wr = storeProductItems(productId, ResortItem.SUB_TYPE, resortsBuilder.build());
                    LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
                } catch (MongoException e) {
                    throw new DatabaseAccessException(e);
                }
                return resortItemId;
            } else {
                return null;
            }
        }

        return null;
    }

    public UUID removeTrivItem(UUID productId, UUID trivItemId) throws InvalidTravelProductException,
        DatabaseAccessException {
        if (productId == null || trivItemId == null) {
            throw new NullPointerException();
        }

        DBCollection productColls = productDb.getCollection(DAOConstants.PRODUCT_COLL_NAME);
        if (productColls.findOne(productId.toString(), ID_FIELD) == null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.missing_travel_product,
                productId), productId);
        }

        DBCollection productTrivColls = productDb.getCollection(DAOConstants.PRODUCT_TRIV_COLL_NAME);
        DBObject productTrivsBs = productTrivColls.findOne(productId.toString());
        if (productTrivsBs != null) {
            JsonArrayBuilder trivsBuilder = Json.createArrayBuilder();
            boolean removing = false;
            JsonObject productTrivs = Json.createReader(new StringReader(productTrivsBs.toString())).readObject();
            JsonArray trivsJs = productTrivs.getJsonArray(Introspection.JSONKeys.ITEMS);
            for (int i = 0; i < trivsJs.size(); i++) {
                JsonObject trivJs = trivsJs.getJsonObject(i);
                if (trivItemId.toString().equals(trivJs.getString(Introspection.JSONKeys.UUID, null))) {
                    removing = true;
                } else {
                    trivsBuilder.add(trivJs);
                }
            }

            if (removing) {
                try {
                    WriteResult wr = storeProductItems(productId, TrivItem.SUB_TYPE, trivsBuilder.build());
                    LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
                } catch (MongoException e) {
                    throw new DatabaseAccessException(e);
                }
                return trivItemId;
            } else {
                return null;
            }
        }

        return null;
    }

    public Bidding unsetBidding(UUID productId) throws InvalidTravelProductException, DatabaseAccessException {
        if (productId == null) {
            throw new NullPointerException();
        }

        DBCollection productColls = productDb.getCollection(DAOConstants.PRODUCT_COLL_NAME);
        if (productColls.findOne(productId.toString(), ID_FIELD) == null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.missing_travel_product,
                productId), productId);
        }

        DBCollection productBiddingColls = productDb.getCollection(DAOConstants.PRODUCT_BIDDING_COLL_NAME);
        DBObject biddingBs = productBiddingColls.findOne(productId.toString());
        if (biddingBs == null) {
            return null;
        } else {
            WriteResult wr = productBiddingColls.remove(biddingBs);
            LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_remove_record, wr.toString()));
            JsonObject bidding = Json.createReader(new StringReader(biddingBs.toString())).readObject();
            return new Bidding().fromJSON(bidding);
        }
    }

    public TravelProduct updateProductStatus(Account account, UUID productId, ProductStatus oldStatus,
        ProductStatus newStatus) throws InvalidTravelProductException, DatabaseAccessException {
        if (account == null || productId == null || newStatus == null) {
            throw new NullPointerException();
        }

        DBCollection productColls = productDb.getCollection(DAOConstants.PRODUCT_COLL_NAME);
        DBObject travelProduct = productColls.findOne(productId.toString());
        if (travelProduct == null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.missing_travel_product,
                productId), productId);
        }
        if (oldStatus != null) {
            Integer statusEnum = (Integer) travelProduct.get(Introspection.JSONKeys.STATUS);
            ProductStatus currentStatus = ProductStatus.valueOf(statusEnum);
            if (!oldStatus.equals(currentStatus)) {
                throw new InvalidTravelProductException(LocalMessages.getMessage(
                    LocalMessages.miss_travel_product_status, productId, oldStatus.enumValue(),
                    currentStatus.enumValue()));
            }
        }

        if (!account.getUuid().equals(travelProduct.get(Introspection.JSONKeys.OWNER))) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(
                LocalMessages.illegal_submit_product_operation, productId, account.getUuid()), productId);
        }

        try {
            travelProduct.put(Introspection.JSONKeys.STATUS, newStatus.enumValue());
            BasicDBObject query1 = new BasicDBObject(DAOConstants.mongo_database_pk, productId.toString());
            BasicDBObject query2 = new BasicDBObject(Introspection.JSONKeys.OWNER, account.getUuid());
            BasicDBObject productQuery = new BasicDBObject("$and", new BasicDBObject[] { query1, query2 });
            WriteResult wr = productColls.update(productQuery, travelProduct, false, false, WriteConcern.MAJORITY);
            LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
        } catch (MongoException e) {
            throw new DatabaseAccessException(e);
        }

        JsonObject product = Json.createReader(new StringReader(travelProduct.toString())).readObject();
        return JsonObjectHelper.toProduct(product, true);
    }

    private WriteResult storeProduct(JsonObject product, String accountId) {
        String productId = product.getString(Introspection.JSONKeys.UUID);
        DBCollection productColls = productDb.getCollection(DAOConstants.PRODUCT_COLL_NAME);
        DBObject proposalBs = (DBObject) JSON.parse(product.toString());
        proposalBs.put(DAOConstants.mongo_database_pk, productId);
        proposalBs.put(Introspection.JSONKeys.OWNER, accountId);
        return productColls.insert(proposalBs, WriteConcern.MAJORITY);
    }

    private WriteResult storeProductBidding(UUID productId, JsonObject bidding) {
        DBCollection biddingColls = productDb.getCollection(DAOConstants.PRODUCT_BIDDING_COLL_NAME);
        DBObject biddingBs = (DBObject) JSON.parse(bidding.toString());
        biddingBs.put(DAOConstants.mongo_database_pk, productId.toString());
        return biddingColls.insert(biddingBs, WriteConcern.MAJORITY);
    }

    private WriteResult storeProductItems(UUID productId, String itemType, JsonArray items) {
        if (HotelItem.SUB_TYPE.equals(itemType)) {
            JsonObjectBuilder hotelsJs = Json.createObjectBuilder();
            hotelsJs.add(Introspection.JSONKeys.ITEMS, items);

            DBObject hotelsBs = (DBObject) JSON.parse(hotelsJs.build().toString());
            hotelsBs.put(DAOConstants.mongo_database_pk, productId.toString());

            DBCollection productHotelColls = productDb.getCollection(DAOConstants.PRODUCT_HOTEL_COLL_NAME);
            DBObject productQuery = QueryBuilder.start(DAOConstants.mongo_database_pk).is(productId.toString()).get();
            return productHotelColls.update(productQuery, hotelsBs, true, false, WriteConcern.MAJORITY);
        } else if (TrafficItem.SUB_TYPE.equals(itemType)) {
            JsonObjectBuilder trafficsJs = Json.createObjectBuilder();
            trafficsJs.add(Introspection.JSONKeys.ITEMS, items);

            DBObject trafficBs = (DBObject) JSON.parse(trafficsJs.build().toString());
            trafficBs.put(DAOConstants.mongo_database_pk, productId.toString());

            DBCollection productTrafficColls = productDb.getCollection(DAOConstants.PRODUCT_TRAFFIC_COLL_NAME);
            DBObject productQuery = QueryBuilder.start(DAOConstants.mongo_database_pk).is(productId.toString()).get();
            return productTrafficColls.update(productQuery, trafficBs, true, false, WriteConcern.MAJORITY);
        } else if (ResortItem.SUB_TYPE.equals(itemType)) {
            JsonObjectBuilder resortsJs = Json.createObjectBuilder();
            resortsJs.add(Introspection.JSONKeys.ITEMS, items);

            DBObject resortBs = (DBObject) JSON.parse(resortsJs.build().toString());
            resortBs.put(DAOConstants.mongo_database_pk, productId.toString());

            DBCollection productResortColls = productDb.getCollection(DAOConstants.PRODUCT_RESORT_COLL_NAME);
            DBObject productQuery = QueryBuilder.start(DAOConstants.mongo_database_pk).is(productId.toString()).get();
            return productResortColls.update(productQuery, resortBs, true, false, WriteConcern.MAJORITY);
        } else {
            JsonObjectBuilder trivsJs = Json.createObjectBuilder();
            trivsJs.add(Introspection.JSONKeys.ITEMS, items);

            DBObject trivBs = (DBObject) JSON.parse(trivsJs.build().toString());
            trivBs.put(DAOConstants.mongo_database_pk, productId.toString());

            DBCollection productTrivColls = productDb.getCollection(DAOConstants.PRODUCT_TRIV_COLL_NAME);
            DBObject productQuery = QueryBuilder.start(DAOConstants.mongo_database_pk).is(productId.toString()).get();
            return productTrivColls.update(productQuery, trivBs, true, false, WriteConcern.MAJORITY);
        }
    }

    public UUID publishProduct(TravelProduct product, TravelProposal proposal) throws InvalidTravelProductException,
        DatabaseAccessException {
        if (product == null || proposal == null) {
            throw new NullPointerException();
        }

        if (!product.getProposalUUID().equals(proposal.getUUID())) {
            throw new IllegalArgumentException();
        }

        product = product.getCore();

        JsonArrayBuilder departuresRelBuilder = Json.createArrayBuilder();
        {
            Set<String> depaIds = new HashSet<String>();
            TravelLocation departure = (TravelLocation) product.adapt(Introspection.JSONKeys.DEPARTURE,
                TravelLocation.class);
            departuresRelBuilder.add(departure.getName());
            departuresRelBuilder.add(departure.getChineseName());
            departuresRelBuilder.add(departure.getPinyinName());
            Object[] departureIds = departure.getRelatedLocations();
            for (int i = 0; i < departureIds.length; i++) {
                if (departureIds[i] instanceof Continent) {
                    Continent continent = (Continent) departureIds[i];
                    departuresRelBuilder.add(continent.getName());
                    departuresRelBuilder.add(continent.getChineseName());
                    departuresRelBuilder.add(continent.getPinyinName());
                } else {
                    depaIds.add(((UUID) departureIds[i]).toString());
                }
            }

            List<StringTriple> locationNames = travelBasicDao.getLocationIndexTermsByLocatoinIds(
                new ArrayList<String>(depaIds));
            List<StringTriple> regionNames = travelBasicDao.getRegionIndexTermsByRegionalLocatoinIds(
                new ArrayList<String>(depaIds));
            locationNames.addAll(regionNames);
            for (int i = 0; i < locationNames.size(); i++) {
                StringTriple names = locationNames.get(i);
                departuresRelBuilder.add(names.getPrimary()).add(names.getSecondary()).add(names.getTertius());
            }
        }

        JsonArrayBuilder destinationsRelBuilder = Json.createArrayBuilder();
        {
            Set<String> destIds = new HashSet<String>();
            Iterator<TravelRequirement> reqs = proposal.getTravelRequirements().iterator();
            while (reqs.hasNext()) {
                TravelRequirement travelRequirement = (TravelRequirement) reqs.next();
                if (travelRequirement.isItinerary()) {
                    TravelLocation destination = ((ItineraryRequirement) travelRequirement).getDestination();
                    destinationsRelBuilder.add(destination.getName());
                    destinationsRelBuilder.add(destination.getChineseName());
                    destinationsRelBuilder.add(destination.getPinyinName());
                    Object[] destinationIds = destination.getRelatedLocations();
                    for (int i = 0; i < destinationIds.length; i++) {
                        if (destinationIds[i] instanceof Continent) {
                            Continent continent = (Continent) destinationIds[i];
                            destinationsRelBuilder.add(continent.getName());
                            destinationsRelBuilder.add(continent.getChineseName());
                            destinationsRelBuilder.add(continent.getPinyinName());
                        } else {
                            destIds.add(((UUID) destinationIds[i]).toString());
                        }
                    }
                }
            }
            List<StringTriple> locationNames = travelBasicDao.getLocationIndexTermsByLocatoinIds(
                new ArrayList<String>(destIds));
            List<StringTriple> regionNames = travelBasicDao.getRegionIndexTermsByRegionalLocatoinIds(
                new ArrayList<String>(destIds));
            locationNames.addAll(regionNames);
            for (int i = 0; i < locationNames.size(); i++) {
                StringTriple names = locationNames.get(i);
                destinationsRelBuilder.add(names.getPrimary()).add(names.getSecondary()).add(names.getTertius());
            }
        }

        Account proposalOwner = travelRequirementDao.getTravelProposalOwner(proposal.getUUID());
        if (proposalOwner == null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.miss_travel_proposal_owner,
                proposal.getUUID()), proposal.getUUID());
        }

        Account productOwner = this.getTravelProductOwner(product.getProductUUID());
        if (productOwner == null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.miss_travel_product_owner,
                product.getProductUUID()), product.getProductUUID());
        }

        JsonObject productJs = product.toJSON();
        JsonObject proposalJs = proposal.toJSON();
        JsonObjectBuilder aggregatedProductBuilder = JsonObjectUtil.merge(productJs, Introspection.JSONKeys.REF_ENTITY,
            proposalJs);
        aggregatedProductBuilder.add(Introspection.JSONKeys.DEPARTURE_REL, departuresRelBuilder);
        aggregatedProductBuilder.add(Introspection.JSONKeys.DESTINATION_REL, destinationsRelBuilder);
        aggregatedProductBuilder.add(Introspection.JSONKeys.PRODUCT_OWNER, productOwner.toJSON());
        aggregatedProductBuilder.add(Introspection.JSONKeys.PROPOSAL_OWNER, proposalOwner.toJSON());
        String aggregatedProductJs = aggregatedProductBuilder.build().toString();

        String productId = product.getProductUUID().toString();
        IndexRequestBuilder requestBuilder = esClient.prepareIndex(DAOConstants.elasticsearch_product_index,
            DAOConstants.elasticsearch_product_type, productId);
        IndexResponse response = requestBuilder.setSource(aggregatedProductJs).execute().actionGet();

        LOG.info(LocalMessages.getMessage(response.isCreated() ? LocalMessages.product_index_created
            : LocalMessages.product_index_updated, productId, response.getIndex(), response.getType(),
            response.getId(), response.getVersion(), response.getHeaders(), response.isCreated()));
        return UuidUtil.fromUuidStr(response.getId());
    }

    public UUID unpublishProduct(UUID productId) throws InvalidTravelProductException, DatabaseAccessException {
        if (productId == null) {
            throw new NullPointerException();
        }

        String productUuid = productId.toString();
        DeleteRequestBuilder requestBuilder = esClient.prepareDelete(DAOConstants.elasticsearch_product_index,
            DAOConstants.elasticsearch_product_type, productUuid);
        DeleteResponse response = requestBuilder.execute().actionGet();
        if (response.isFound()) {
            LOG.info(LocalMessages.getMessage(LocalMessages.product_index_deleted, productUuid, response.getIndex(),
                response.getType(), response.getId(), response.getVersion(), response.getHeaders()));
            return UuidUtil.fromUuidStr(response.getId());
        } else {
            LOG.info(LocalMessages.getMessage(LocalMessages.product_index_not_found, productUuid, response.getIndex(),
                response.getType(), response.getId(), response.getVersion(), response.getHeaders()));
            return null;
        }
    }

    public JsonObject searchProduct(QueryTemplate template, Map<String, Object> templageParams)
        throws DatabaseAccessException {
        if (template == null || templageParams == null) {
            throw new NullPointerException();
        }

        SearchResponse response = esClient.prepareSearch(DAOConstants.elasticsearch_product_index)
            .setTypes(DAOConstants.elasticsearch_product_type)
            .setTemplateName(template.nameValue())
            .setTemplateType(ScriptType.INDEXED)
            .setTemplateParams(templageParams)
            .get();

        SearchHits hits = response.getHits();
        LOG.info(LocalMessages.getMessage(LocalMessages.product_index_searched, template.nameValue(),
            templageParams.toString(), response.isTimedOut(), response.isTerminatedEarly(), response.getTookInMillis(),
            response.getTotalShards(), response.getSuccessfulShards(), response.getFailedShards()));

        JsonObjectBuilder resultBuilder = Json.createObjectBuilder();
        resultBuilder.add(Introspection.JSONKeys.TOTAL_HITS_NUMBER, hits.getTotalHits());
        JsonArrayBuilder resultArrayBuilder = Json.createArrayBuilder();
        Iterator<SearchHit> hitsIter = hits.iterator();
        while (hitsIter.hasNext()) {
            SearchHit searchHit = (SearchHit) hitsIter.next();

            LOG.info(LocalMessages.getMessage(LocalMessages.product_search_hit, searchHit.id(), searchHit.getIndex(),
                searchHit.getType(), searchHit.getId(), searchHit.getVersion(), searchHit.getScore()));

            JsonObject hitSource = Json.createReader(new StringReader(searchHit.getSourceAsString())).readObject();
            resultArrayBuilder.add(hitSource);
        }
        resultBuilder.add(Introspection.JSONKeys.HITS, resultArrayBuilder);

        return resultBuilder.build();
    }

    public Account getTravelProductOwner(UUID productId) throws DatabaseAccessException {
        if (productId == null) {
            throw new NullPointerException();
        }

        DBCollection productColls = productDb.getCollection(DAOConstants.PRODUCT_COLL_NAME);
        DBObject productBs = productColls.findOne(productId.toString());
        if (productBs == null) {
            return null;
        } else {
            JsonObject product = Json.createReader(new StringReader(productBs.toString())).readObject();
            String productOwnerId = product.getString(Introspection.JSONKeys.OWNER, null);
            if (Constants.DEFAULT_USER_ACCOUNT.getUuid().equals(productOwnerId)) {
                return Constants.DEFAULT_USER_ACCOUNT;
            } else if (Constants.DEFAULT_AGENCY_ACCOUNT.getUuid().equals(productOwnerId)) {
                return Constants.DEFAULT_AGENCY_ACCOUNT;
            } else if (Constants.ADMIN_ACCOUNT.getUuid().equals(productOwnerId)) {
                return Constants.ADMIN_ACCOUNT;
            } else {
                try {
                    return accountDao.retrieveAccount(UuidUtil.fromUuidStr(productOwnerId));
                } catch (InvalidAccountException e) {
                    LOG.error(e.getMessage(), e);
                    return null;
                }
            }
        }
    }
}
