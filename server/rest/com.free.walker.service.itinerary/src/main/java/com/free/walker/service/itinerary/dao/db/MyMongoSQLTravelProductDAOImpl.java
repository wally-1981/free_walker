package com.free.walker.service.itinerary.dao.db;

import java.io.IOException;
import java.io.StringReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.dao.DAOConstants;
import com.free.walker.service.itinerary.dao.TravelProductDAO;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidTravelProductException;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.product.Bidding;
import com.free.walker.service.itinerary.product.HotelItem;
import com.free.walker.service.itinerary.product.ResortItem;
import com.free.walker.service.itinerary.product.SimpleTravelProduct;
import com.free.walker.service.itinerary.product.TrafficItem;
import com.free.walker.service.itinerary.product.TravelProduct;
import com.free.walker.service.itinerary.product.TravelProductItem;
import com.free.walker.service.itinerary.product.TrivItem;
import com.free.walker.service.itinerary.util.JsonObjectHelper;
import com.free.walker.service.itinerary.util.MongoDbClientBuilder;
import com.free.walker.service.itinerary.util.SystemConfigUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.QueryBuilder;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

public class MyMongoSQLTravelProductDAOImpl implements TravelProductDAO {
    private static final Logger LOG = LoggerFactory.getLogger(MyMongoSQLTravelProductDAOImpl.class);
    private static final DBObject ID_FIELD = new BasicDBObjectBuilder().add(DAOConstants.mongo_database_pk, true).get();

    private DB productDb;
    private String productMongoDbUrl;
    private String mongoDbDriver;

    private static class SingletonHolder {
        private static final TravelProductDAO INSTANCE = new MyMongoSQLTravelProductDAOImpl();
    }

    public static TravelProductDAO getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private MyMongoSQLTravelProductDAOImpl() {
        try {
            Properties config = SystemConfigUtil.getApplicationConfig();
            productDb = new MongoDbClientBuilder().build(DAOConstants.product_mongo_database, config);
            mongoDbDriver = DB.class.getName();
            productMongoDbUrl = config.getProperty(DAOConstants.mongo_database_url);
        } catch (UnknownHostException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean pingPersistence() {
        DBObject ping = new BasicDBObject("ping", "1");
        try {
            CommandResult cr = productDb.command(ping);
            if (!cr.ok()) {
                return false;
            }
        } catch (MongoException e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_init_failure, productMongoDbUrl, mongoDbDriver), e);
            return false;
        }

        return true;
    }

    public UUID createProduct(TravelProduct travelProduct) throws InvalidTravelProductException,
        DatabaseAccessException {
        if (travelProduct == null) {
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
            WriteResult wr = storeProduct(productJs);
            LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_create_record, wr.toString()));
        } catch (MongoException e) {
            throw new InvalidTravelProductException(travelProduct.getProductUUID(), e);
        }

        try {
            WriteResult wr = storeProductItems(travelProduct.getProductUUID(), HotelItem.SUB_TYPE, hotelItemsJs.build());
            LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
        } catch (MongoException e) {
            throw new InvalidTravelProductException(travelProduct.getProductUUID(), e);
        }

        try {
            WriteResult wr = storeProductItems(travelProduct.getProductUUID(), TrafficItem.SUB_TYPE, trafficItemsJs.build());
            LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
        } catch (MongoException e) {
            throw new InvalidTravelProductException(travelProduct.getProductUUID(), e);
        }

        try {
            WriteResult wr = storeProductItems(travelProduct.getProductUUID(), ResortItem.SUB_TYPE, resortItemsJs.build());
            LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
        } catch (MongoException e) {
            throw new InvalidTravelProductException(travelProduct.getProductUUID(), e);
        }

        try {
            WriteResult wr = storeProductItems(travelProduct.getProductUUID(), TrivItem.SUB_TYPE, trivItemsJs.build());
            LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
        } catch (MongoException e) {
            throw new InvalidTravelProductException(travelProduct.getProductUUID(), e);
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
            throw new InvalidTravelProductException(productId, e);
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
            throw new InvalidTravelProductException(productId, e);
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
                    throw new InvalidTravelProductException(productId, e);
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
                    throw new InvalidTravelProductException(productId, e);
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
                    throw new InvalidTravelProductException(productId, e);
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
                    throw new InvalidTravelProductException(productId, e);
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

    private WriteResult storeProduct(JsonObject product) {
        String productId = product.getString(Introspection.JSONKeys.UUID);
        DBCollection productColls = productDb.getCollection(DAOConstants.PRODUCT_COLL_NAME);
        DBObject proposalBs = (DBObject) JSON.parse(product.toString());
        proposalBs.put(DAOConstants.mongo_database_pk, productId);
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
}
