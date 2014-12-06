package com.free.walker.service.itinerary.dao.db;

import java.io.IOException;
import java.io.StringReader;
import java.net.UnknownHostException;
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
import com.free.walker.service.itinerary.basic.Bidding;
import com.free.walker.service.itinerary.dao.DAOConstants;
import com.free.walker.service.itinerary.dao.TravelProductDAO;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidTravelProductException;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.product.HotelItem;
import com.free.walker.service.itinerary.product.ResortItem;
import com.free.walker.service.itinerary.product.TrafficItem;
import com.free.walker.service.itinerary.product.TravelProduct;
import com.free.walker.service.itinerary.product.TravelProductItem;
import com.free.walker.service.itinerary.util.MongoDbClientBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.QueryBuilder;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

public class MyMongoSQLTravelProductDAOImpl implements TravelProductDAO {
    private static Logger LOG = LoggerFactory.getLogger(MyMongoSQLTravelProductDAOImpl.class);

    private DB productDb;
    private String productMongoDbUrl;
    private String mongoDbDriver;

    private static class SingletonHolder {
        private static final TravelProductDAO INSTANCE = new MyMongoSQLTravelProductDAOImpl();
    }

    public static TravelProductDAO getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public MyMongoSQLTravelProductDAOImpl() {
        try {
            String resource = "com/free/walker/service/itinerary/dao/config.properties";
            Properties config = new Properties();
            config.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(resource));
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
        if (productColls.findOne(travelProduct.getUUID().toString()) != null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.existed_travel_product,
                travelProduct.getUUID()), travelProduct.getUUID());
        }

        DBCollection productHotelColls = productDb.getCollection(DAOConstants.PRODUCT_HOTEL_COLL_NAME);
        if (productHotelColls.findOne(travelProduct.getUUID().toString()) != null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.existed_travel_product,
                travelProduct.getUUID()), travelProduct.getUUID());
        }

        DBCollection productTrafficColls = productDb.getCollection(DAOConstants.PRODUCT_TRAFFIC_COLL_NAME);
        if (productTrafficColls.findOne(travelProduct.getUUID().toString()) != null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.existed_travel_product,
                travelProduct.getUUID()), travelProduct.getUUID());
        }

        DBCollection productResortColls = productDb.getCollection(DAOConstants.PRODUCT_RESORT_COLL_NAME);
        if (productResortColls.findOne(travelProduct.getUUID().toString()) != null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.existed_travel_product,
                travelProduct.getUUID()), travelProduct.getUUID());
        }

        JsonArrayBuilder hotelItemsJs = Json.createArrayBuilder();
        JsonArrayBuilder trafficItemsJs = Json.createArrayBuilder();
        JsonArrayBuilder resortItemsJs = Json.createArrayBuilder();
        for (int i = 0; i < travelProduct.getTravelProductItems().size(); i++) {
            TravelProductItem item = travelProduct.getTravelProductItems().get(i);
            if (item.isTriv()) {
                continue;
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
            LOG.info("UpsertedId:" + (String) wr.getUpsertedId() + ";N:" + wr.getN());
        } catch (MongoException e) {
            throw new InvalidTravelProductException(travelProduct.getUUID(), e);
        }

        try {
            WriteResult wr = storeProductItems(travelProduct.getUUID(), HotelItem.SUB_TYPE, hotelItemsJs.build());
            LOG.info("UpsertedId:" + (String) wr.getUpsertedId() + ";N:" + wr.getN());
        } catch (MongoException e) {
            throw new InvalidTravelProductException(travelProduct.getUUID(), e);
        }

        try {
            WriteResult wr = storeProductItems(travelProduct.getUUID(), TrafficItem.SUB_TYPE, trafficItemsJs.build());
            LOG.info("UpsertedId:" + (String) wr.getUpsertedId() + ";N:" + wr.getN());
        } catch (MongoException e) {
            throw new InvalidTravelProductException(travelProduct.getUUID(), e);
        }

        try {
            WriteResult wr = storeProductItems(travelProduct.getUUID(), ResortItem.SUB_TYPE, resortItemsJs.build());
            LOG.info("UpsertedId:" + (String) wr.getUpsertedId() + ";N:" + wr.getN());
        } catch (MongoException e) {
            throw new InvalidTravelProductException(travelProduct.getUUID(), e);
        }

        return travelProduct.getUUID();
    }

    public UUID addItem(TravelProductItem travelProductItem, String itemType) throws InvalidTravelProductException,
        DatabaseAccessException {
        if (travelProductItem == null) {
            throw new NullPointerException();
        }

        DBCollection productColls = productDb.getCollection(DAOConstants.PRODUCT_COLL_NAME);
        if (productColls.findOne(travelProductItem.getUUID().toString()) == null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.missing_travel_product,
                travelProductItem.getUUID()), travelProductItem.getUUID());
        }

        DBCollection productItemColls;
        if (HotelItem.SUB_TYPE.equals(itemType)) {
            productItemColls = productDb.getCollection(DAOConstants.PRODUCT_HOTEL_COLL_NAME);
        } else if (TrafficItem.SUB_TYPE.equals(itemType)) {
            productItemColls = productDb.getCollection(DAOConstants.PRODUCT_TRAFFIC_COLL_NAME);
        } else if (ResortItem.SUB_TYPE.equals(itemType)) {
            productItemColls = productDb.getCollection(DAOConstants.PRODUCT_RESORT_COLL_NAME);
        } else {
            productItemColls = productDb.getCollection(DAOConstants.PRODUCT_TRIV_COLL_NAME);
        }

        UUID productId = travelProductItem.getUUID();
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
            WriteResult wr = storeProductItems(productId, HotelItem.SUB_TYPE, itemsBuilder.build());
            LOG.info("UpsertedId:" + (String) wr.getUpsertedId() + ";N:" + wr.getN());
        } catch (MongoException e) {
            throw new InvalidTravelProductException(travelProductItem.getUUID(), e);
        }

        return productId;
    }

    public UUID setBidding(Bidding bidding) throws InvalidTravelProductException, DatabaseAccessException {
        if (bidding == null) {
            throw new NullPointerException();
        }

        DBCollection productColls = productDb.getCollection(DAOConstants.PRODUCT_COLL_NAME);
        if (productColls.findOne(bidding.getUUID().toString()) == null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.missing_travel_product,
                bidding.getUUID()), bidding.getUUID());
        }

        try {
            WriteResult wr = storeProductBidding(bidding.getUUID(), bidding.toJSON());
            LOG.info("UpsertedId:" + (String) wr.getUpsertedId() + ";N:" + wr.getN());
        } catch (MongoException e) {
            throw new InvalidTravelProductException(bidding.getUUID(), e);
        }

        return bidding.getUUID();
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

    public TravelProduct getProduct(UUID productId) throws InvalidTravelProductException, DatabaseAccessException {
        // TODO Auto-generated method stub
        return null;
    }

    public List<TravelProductItem> getHotelItems(UUID productId) throws InvalidTravelProductException,
        DatabaseAccessException {
        // TODO Auto-generated method stub
        return null;
    }

    public List<TravelProductItem> getTrafficItems(UUID productId) throws InvalidTravelProductException,
        DatabaseAccessException {
        // TODO Auto-generated method stub
        return null;
    }

    public List<TravelProductItem> getResortItems(UUID productId) throws InvalidTravelProductException,
        DatabaseAccessException {
        // TODO Auto-generated method stub
        return null;
    }

    public List<TravelProductItem> getTrivItems(UUID productId) throws InvalidTravelProductException,
        DatabaseAccessException {
        // TODO Auto-generated method stub
        return null;
    }

    public Bidding getBidding(UUID productId) throws InvalidTravelProductException, DatabaseAccessException {
        // TODO Auto-generated method stub
        return null;
    }

    public UUID removeHotelItem(UUID productId, UUID hotelItemId) throws InvalidTravelProductException,
        DatabaseAccessException {
        // TODO Auto-generated method stub
        return null;
    }

    public UUID removeTrafficItem(UUID productId, UUID trafficItemId) throws InvalidTravelProductException,
        DatabaseAccessException {
        // TODO Auto-generated method stub
        return null;
    }

    public UUID removeResortItem(UUID productId, UUID resortItemId) throws InvalidTravelProductException,
        DatabaseAccessException {
        // TODO Auto-generated method stub
        return null;
    }

    public UUID removeTrivItem(UUID productId, UUID trivItemId) throws InvalidTravelProductException,
        DatabaseAccessException {
        // TODO Auto-generated method stub
        return null;
    }
}
