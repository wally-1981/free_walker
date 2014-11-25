package com.free.walker.service.itinerary.dao.mysql;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.dao.DAOConstants;
import com.free.walker.service.itinerary.dao.TravelRequirementDAO;
import com.free.walker.service.itinerary.dao.map.RequirementMapper;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.req.ItineraryRequirement;
import com.free.walker.service.itinerary.req.TravelProposal;
import com.free.walker.service.itinerary.req.TravelRequirement;
import com.free.walker.service.itinerary.util.JsonObjectHelper;
import com.free.walker.service.itinerary.util.MongoDbClientBuilder;
import com.free.walker.service.itinerary.util.UuidUtil;
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

/*
 * TODO: MongoDB does not have transaction support. Need to build transaction log for transaction recoverary automatically.
 */
public class MyMongoSQLTravelRequirementDAOImpl implements TravelRequirementDAO {
    private static Logger LOG = LoggerFactory.getLogger(MyMongoSQLTravelRequirementDAOImpl.class);

    private SqlSessionFactory sqlSessionFactory;
    private String mysqlDbUrl;
    private String mysqlDbDriver;

    private DB itineraryDB;
    private String mongoDbUrl;
    private String mongoDbDriver;

    private static class SingletonHolder {
        private static final TravelRequirementDAO INSTANCE = new MyMongoSQLTravelRequirementDAOImpl();
    }

    public static TravelRequirementDAO getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private MyMongoSQLTravelRequirementDAOImpl() {
        try {
            String resource = "com/free/walker/service/itinerary/dao/mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            mysqlDbUrl = sqlSessionFactory.getConfiguration().getVariables()
                .getProperty(DAOConstants.mysql_database_url);
            mysqlDbDriver = sqlSessionFactory.getConfiguration().getVariables()
                .getProperty(DAOConstants.mysql_database_driver);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        try {
            String resource = "com/free/walker/service/itinerary/dao/config.properties";
            Properties config = new Properties();
            config.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(resource));
            itineraryDB = new MongoDbClientBuilder().build(config);
            mongoDbUrl = config.getProperty(DAOConstants.mongo_database_url);
            mongoDbDriver = DB.class.getName();
        } catch (UnknownHostException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean pingPersistence() {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            RequirementMapper requirementMapper = session.getMapper(RequirementMapper.class);
            String version = requirementMapper.pingPersistence();
            if (version == null) {
                return false;
            }
        } catch (Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_init_failure, mysqlDbUrl, mysqlDbDriver), e);
            return false;
        } finally {
            session.close();
        }

        DBObject ping = new BasicDBObject("ping", "1");
        try {
            CommandResult cr = itineraryDB.command(ping);
            if (!cr.ok()) {
                return false;
            }
        } catch (MongoException e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_init_failure, mongoDbUrl, mongoDbDriver), e);
            return false;
        }

        return true;
    }

    public UUID createProposal(TravelProposal travelProposal) throws InvalidTravelReqirementException,
        DatabaseAccessException {
        if (travelProposal == null) {
            throw new NullPointerException();
        }

        List<JsonObject> itinerariesJs = new ArrayList<JsonObject>();
        for (int i = 0; i < travelProposal.getTravelRequirements().size(); i++) {
            TravelRequirement requirement = travelProposal.getTravelRequirements().get(i);
            if (requirement.isItinerary()) {
                itinerariesJs.add(requirement.toJSON());
            } else {
                continue;
            }
        }

        travelProposal.getTravelRequirements().clear();
        JsonObject proposalJs = travelProposal.toJSON();

        DBCollection proposalColls = itineraryDB.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        if (proposalColls.findOne(travelProposal.getUUID().toString()) != null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.existed_travel_requirement, travelProposal.getUUID()), travelProposal.getUUID());
        }

        DBCollection itineraryColls = itineraryDB.getCollection(DAOConstants.ITINERARY_COLL_NAME);
        for (int i = 0; i < itinerariesJs.size(); i++) {
            JsonObject requirment = itinerariesJs.get(i);
            String requirementId = requirment.getString(Introspection.JSONKeys.UUID);
            if (itineraryColls.findOne(requirementId) != null) {
                throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                    LocalMessages.existed_travel_requirement, requirementId), travelProposal.getUUID());
            }
        }

        try {
            WriteResult wr = storeProposal(proposalJs);
            LOG.info("UpsertedId:" + (String) wr.getUpsertedId() + ";N:" + wr.getN());
        } catch (MongoException e) {
            throw new InvalidTravelReqirementException(travelProposal.getUUID(), e);
        }

        JsonArrayBuilder itineraryIds = Json.createArrayBuilder();
        for (int i = 0; i < itinerariesJs.size(); i++) {
            try {
                itineraryIds.add(itinerariesJs.get(i).getString(Introspection.JSONKeys.UUID));
                WriteResult wr = storeItinerary(itinerariesJs.get(i));
                LOG.info("UpsertedId:" + (String) wr.getUpsertedId() + ";N:" + wr.getN());
            } catch (MongoException e) {
                throw new InvalidTravelReqirementException(UuidUtil.fromUuidStr(itinerariesJs.get(i).getString(
                    Introspection.JSONKeys.UUID)), e);
            }
        }

        try {
            WriteResult wr = storeProposalRequirements(travelProposal.getUUID(), itineraryIds.build());
            LOG.info("UpsertedId:" + (String) wr.getUpsertedId() + ";N:" + wr.getN());
        } catch (MongoException e) {
            throw new InvalidTravelReqirementException(travelProposal.getUUID(), e);
        }

        return travelProposal.getUUID();
    }

    public UUID addItinerary(UUID travelProposalId, UUID itineraryRequirementId,
        ItineraryRequirement itineraryRequirement) throws InvalidTravelReqirementException {
        DBCollection proposalColls = itineraryDB.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        if (proposalColls.findOne(travelProposalId.toString()) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        DBCollection itineraryColls = itineraryDB.getCollection(DAOConstants.ITINERARY_COLL_NAME);
        if (itineraryColls.findOne(itineraryRequirementId.toString()) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_itinerary,
                itineraryRequirementId), itineraryRequirementId);
        }

        if (itineraryColls.findOne(itineraryRequirement.getUUID().toString()) != null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.existed_travel_requirement, itineraryRequirement.getUUID()),
                itineraryRequirement.getUUID());
        }

        DBCollection proposalRequirementColls = itineraryDB.getCollection(DAOConstants.PROPOSAL_REQUIREMENT_COLL_NAME);
        DBObject proposalRequirements = proposalRequirementColls.findOne(travelProposalId.toString());
        if (proposalRequirements == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        boolean foundItinerary = false;
        JsonArrayBuilder requirementsBuilder = Json.createArrayBuilder();
        Object requirementsBs = proposalRequirements.get(Introspection.JSONKeys.REQUIREMENTS);
        JsonArray requirements = Json.createReader(new StringReader(requirementsBs.toString())).readArray();
        for (int i = 0; i < requirements.size(); i++) {
            String requirementId = requirements.getString(i);

            /*
             * TODO: [Optimization] get the boolean return only without get the
             * data.
             */
            if (foundItinerary && isTypeOf(requirementId, ItineraryRequirement.class) != null) {
                requirementsBuilder.add(itineraryRequirement.getUUID().toString());
            }

            requirementsBuilder.add(requirementId);

            foundItinerary = requirementId.equals(itineraryRequirementId.toString());

            if (i + 1 == requirements.size() && !foundItinerary) {
                throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                    LocalMessages.missing_travel_itinerary, itineraryRequirementId), itineraryRequirementId);
            }
        }

        try {
            WriteResult wr = storeItinerary(itineraryRequirement.toJSON());
            LOG.info("UpsertedId:" + (String) wr.getUpsertedId() + ";N:" + wr.getN());
        } catch (MongoException e) {
            throw new InvalidTravelReqirementException(itineraryRequirement.getUUID(), e);
        }

        try {
            WriteResult wr = storeProposalRequirements(travelProposalId, requirementsBuilder.build());
            LOG.info("UpsertedId:" + (String) wr.getUpsertedId() + ";N:" + wr.getN());
        } catch (MongoException e) {
            throw new InvalidTravelReqirementException(travelProposalId, e);
        }

        return itineraryRequirement.getUUID();
    }

    public UUID addRequirement(UUID travelProposalId, UUID itineraryRequirementId, TravelRequirement travelRequirement)
        throws InvalidTravelReqirementException {
        DBCollection proposalColls = itineraryDB.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        if (proposalColls.findOne(travelProposalId.toString()) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        DBCollection itineraryColls = itineraryDB.getCollection(DAOConstants.ITINERARY_COLL_NAME);
        if (itineraryColls.findOne(itineraryRequirementId.toString()) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_itinerary,
                itineraryRequirementId), itineraryRequirementId);
        }

        DBCollection requirementColls = itineraryDB.getCollection(DAOConstants.REQUIREMENT_COLL_NAME);
        if (requirementColls.findOne(travelRequirement.getUUID().toString()) != null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.existed_travel_requirement, travelRequirement.getUUID()), travelRequirement.getUUID());
        }

        if (travelRequirement.isProposal()) {
            throw new InvalidTravelReqirementException(
                LocalMessages.getMessage(LocalMessages.illegal_add_proposal_as_requirement,
                    travelRequirement.getUUID(), itineraryRequirementId), travelRequirement.getUUID());
        }

        if (travelRequirement.isItinerary()) {
            throw new InvalidTravelReqirementException(
                LocalMessages.getMessage(LocalMessages.illegal_add_itinerary_as_requirement,
                    travelRequirement.getUUID(), itineraryRequirementId), travelRequirement.getUUID());
        }

        DBCollection proposalRequirementColls = itineraryDB.getCollection(DAOConstants.PROPOSAL_REQUIREMENT_COLL_NAME);
        DBObject proposalRequirements = proposalRequirementColls.findOne(travelProposalId.toString());
        if (proposalRequirements == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        boolean foundItinerary = false;
        JsonArrayBuilder requirementsBuilder = Json.createArrayBuilder();
        Object requirementsBs = proposalRequirements.get(Introspection.JSONKeys.REQUIREMENTS);
        JsonArray requirements = Json.createReader(new StringReader(requirementsBs.toString())).readArray();
        for (int i = 0; i < requirements.size(); i++) {
            String requirementId = requirements.getString(i);

            requirementsBuilder.add(requirementId);

            if (requirementId.equals(itineraryRequirementId.toString())) {
                requirementsBuilder.add(travelRequirement.getUUID().toString());
                foundItinerary = true;
            }

            if (i + 1 == requirements.size() && !foundItinerary) {
                throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                    LocalMessages.missing_travel_itinerary, itineraryRequirementId), itineraryRequirementId);
            }
        }

        try {
            WriteResult wr = storeRequirement(travelRequirement.toJSON());
            LOG.info("UpsertedId:" + (String) wr.getUpsertedId() + ";N:" + wr.getN());
        } catch (MongoException e) {
            throw new InvalidTravelReqirementException(travelRequirement.getUUID(), e);
        }

        try {
            WriteResult wr = storeProposalRequirements(travelProposalId, requirementsBuilder.build());
            LOG.info("UpsertedId:" + (String) wr.getUpsertedId() + ";N:" + wr.getN());
        } catch (MongoException e) {
            throw new InvalidTravelReqirementException(travelProposalId, e);
        }

        return travelRequirement.getUUID();
    }

    public UUID addRequirement(UUID travelProposalId, TravelRequirement travelRequirement)
        throws InvalidTravelReqirementException {
        if (travelProposalId == null || travelRequirement == null) {
            throw new NullPointerException();
        }

        DBCollection proposalColls = itineraryDB.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        if (proposalColls.findOne(travelProposalId.toString()) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        DBCollection requirementColls = itineraryDB.getCollection(DAOConstants.REQUIREMENT_COLL_NAME);
        DBCollection itineraryColls = itineraryDB.getCollection(DAOConstants.ITINERARY_COLL_NAME);
        if (requirementColls.findOne(travelRequirement.getUUID().toString()) != null
            || itineraryColls.findOne(travelRequirement.getUUID().toString()) != null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.existed_travel_requirement, travelRequirement.getUUID()), travelRequirement.getUUID());
        }

        if (travelRequirement.isProposal()) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.illegal_add_proposal_as_requirement, travelRequirement.getUUID(), travelProposalId),
                travelRequirement.getUUID());
        }

        DBCollection proposalRequirementColls = itineraryDB.getCollection(DAOConstants.PROPOSAL_REQUIREMENT_COLL_NAME);
        DBObject proposalRequirements = proposalRequirementColls.findOne(travelProposalId.toString());
        if (proposalRequirements == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        JsonArrayBuilder requirementsBuilder = Json.createArrayBuilder();
        Object requirementsBs = proposalRequirements.get(Introspection.JSONKeys.REQUIREMENTS);
        JsonArray requirements = Json.createReader(new StringReader(requirementsBs.toString())).readArray();
        for (int i = 0; i < requirements.size(); i++) {
            String requirementId = requirements.getString(i);

            requirementsBuilder.add(requirementId);

            if (i + 1 == requirements.size()) {
                requirementsBuilder.add(travelRequirement.getUUID().toString());
            }
        }

        try {
            WriteResult wr;
            if (travelRequirement.isItinerary()) {
                wr = storeItinerary(travelRequirement.toJSON());
            } else {                
                wr = storeRequirement(travelRequirement.toJSON());
            }
            LOG.info("UpsertedId:" + (String) wr.getUpsertedId() + ";N:" + wr.getN());
        } catch (MongoException e) {
            throw new InvalidTravelReqirementException(travelRequirement.getUUID(), e);
        }

        try {
            WriteResult wr = storeProposalRequirements(travelProposalId, requirementsBuilder.build());
            LOG.info("UpsertedId:" + (String) wr.getUpsertedId() + ";N:" + wr.getN());
        } catch (MongoException e) {
            throw new InvalidTravelReqirementException(travelProposalId, e);
        }

        return travelRequirement.getUUID();
    }

    public List<TravelRequirement> getRequirements(UUID travelProposalId) throws InvalidTravelReqirementException {
        if (travelProposalId == null) {
            throw new NullPointerException();
        }

        DBCollection proposalColls = itineraryDB.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        if (proposalColls.findOne(travelProposalId.toString()) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        DBCollection proposalRequirementColls = itineraryDB.getCollection(DAOConstants.PROPOSAL_REQUIREMENT_COLL_NAME);
        DBObject proposalRequirements = proposalRequirementColls.findOne(travelProposalId.toString());
        if (proposalRequirements == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        /*
         * TODO: [Optimization] a batch call could be used here to avoid
         * multiple requests against backend DB.
         */
        List<TravelRequirement> requirementsList = new ArrayList<TravelRequirement>();
        Object requirementsBs = proposalRequirements.get(Introspection.JSONKeys.REQUIREMENTS);
        JsonArray requirements = Json.createReader(new StringReader(requirementsBs.toString())).readArray();
        for (int i = 0; i < requirements.size(); i++) {
            String requirementId = requirements.getString(i);
            TravelRequirement requirement = getRequirement(UuidUtil.fromUuidStr(requirementId),
                Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT);
            TravelRequirement itinerary = getRequirement(UuidUtil.fromUuidStr(requirementId),
                Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY);
            if (requirement != null && itinerary == null) {
                requirementsList.add(requirement);
            } else if (requirement == null && itinerary != null) {
                requirementsList.add(itinerary);
            } else if (requirement == null && itinerary == null) {
                continue;
            } else {
                throw new IllegalStateException();
            }
        }

        return requirementsList;
    }

    public List<TravelRequirement> getItineraryRequirements(UUID travelProposalId)
        throws InvalidTravelReqirementException {
        if (travelProposalId == null) {
            throw new NullPointerException();
        }

        DBCollection proposalColls = itineraryDB.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        if (proposalColls.findOne(travelProposalId.toString()) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        DBCollection proposalRequirementColls = itineraryDB.getCollection(DAOConstants.PROPOSAL_REQUIREMENT_COLL_NAME);
        DBObject proposalRequirements = proposalRequirementColls.findOne(travelProposalId.toString());
        if (proposalRequirements == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        /*
         * TODO: [Optimization] a batch call could be used here to avoid
         * multiple requests against backend DB.
         */
        List<TravelRequirement> itinerariesList = new ArrayList<TravelRequirement>();
        Object requirementsBs = proposalRequirements.get(Introspection.JSONKeys.REQUIREMENTS);
        JsonArray requirements = Json.createReader(new StringReader(requirementsBs.toString())).readArray();
        for (int i = 0; i < requirements.size(); i++) {
            String requirementId = requirements.getString(i);
            TravelRequirement itinerary = getRequirement(UuidUtil.fromUuidStr(requirementId),
                Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY);
            if (itinerary != null) {
                itinerariesList.add(itinerary);
            }
        }

        return itinerariesList;
    }

    public List<TravelRequirement> getRequirements(UUID travelProposalId, UUID itineraryRequirementId)
        throws InvalidTravelReqirementException {
        if (travelProposalId == null || itineraryRequirementId == null) {
            throw new NullPointerException();
        }

        DBCollection proposalColls = itineraryDB.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        if (proposalColls.findOne(travelProposalId.toString()) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        DBCollection itineraryColls = itineraryDB.getCollection(DAOConstants.ITINERARY_COLL_NAME);
        if (itineraryColls.findOne(itineraryRequirementId.toString()) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_itinerary,
                itineraryRequirementId), itineraryRequirementId);
        }

        DBCollection proposalRequirementColls = itineraryDB.getCollection(DAOConstants.PROPOSAL_REQUIREMENT_COLL_NAME);
        DBObject proposalRequirements = proposalRequirementColls.findOne(travelProposalId.toString());
        if (proposalRequirements == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        /*
         * TODO: [Optimization] a batch call could be used here to avoid
         * multiple requests against backend DB.
         */
        List<TravelRequirement> requirementsList = new LinkedList<TravelRequirement>();
        Object requirementsBs = proposalRequirements.get(Introspection.JSONKeys.REQUIREMENTS);
        JsonArray requirements = Json.createReader(new StringReader(requirementsBs.toString())).readArray();
        Iterator<JsonValue> iter = requirements.iterator();
        while (iter.hasNext()) {
            String requirementId = ((JsonString) iter.next()).getString();
            if (requirementId.equals(itineraryRequirementId.toString())) {
                while (iter.hasNext()) {
                    requirementId = ((JsonString) iter.next()).getString();
                    TravelRequirement requirement = getRequirement(UuidUtil.fromUuidStr(requirementId),
                        Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT);
                    TravelRequirement itinerary = getRequirement(UuidUtil.fromUuidStr(requirementId),
                        Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY);
                    if (requirement != null && itinerary == null) {
                        requirementsList.add(requirement);
                    } else if (requirement == null && itinerary != null) {
                        return requirementsList;
                    } else if (requirement == null && itinerary == null) {
                        continue;
                    } else {
                        throw new IllegalStateException();
                    }
                }
            }
        }

        return requirementsList;
    }

    public TravelRequirement getPrevItineraryRequirement(UUID travelProposalId, UUID travelRequirementId)
        throws InvalidTravelReqirementException {
        if (travelProposalId == null || travelRequirementId == null) {
            throw new NullPointerException();
        }

        DBCollection proposalColls = itineraryDB.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        if (proposalColls.findOne(travelProposalId.toString()) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        DBCollection itineraryColls = itineraryDB.getCollection(DAOConstants.ITINERARY_COLL_NAME);
        DBCollection requirementColls = itineraryDB.getCollection(DAOConstants.REQUIREMENT_COLL_NAME);
        if (itineraryColls.findOne(travelRequirementId.toString()) == null
            && requirementColls.findOne(travelRequirementId.toString()) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.missing_travel_requirement, travelRequirementId), travelRequirementId);
        }

        DBCollection proposalRequirementColls = itineraryDB.getCollection(DAOConstants.PROPOSAL_REQUIREMENT_COLL_NAME);
        DBObject proposalRequirements = proposalRequirementColls.findOne(travelProposalId.toString());
        if (proposalRequirements == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        /*
         * TODO: [Optimization] a batch call could be used here to avoid
         * multiple requests against backend DB.
         */
        Object requirementsBs = proposalRequirements.get(Introspection.JSONKeys.REQUIREMENTS);
        JsonArray requirements = Json.createReader(new StringReader(requirementsBs.toString())).readArray();
        for (int i = 0; i < requirements.size(); i++) {
            if (requirements.getString(i).equals(travelRequirementId.toString())) {
                for (int j = i - 1; j >= 0; j--) {
                    TravelRequirement requirement = getRequirement(UuidUtil.fromUuidStr(requirements.getString(j)),
                        Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT);
                    TravelRequirement itinerary = getRequirement(UuidUtil.fromUuidStr(requirements.getString(j)),
                        Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY);
                    if (requirement != null && itinerary == null) {
                        continue;
                    } else if (requirement == null && itinerary != null) {
                        return itinerary;
                    } else if (requirement == null && itinerary == null) {
                        continue;
                    } else {
                        throw new IllegalStateException();
                    }
                }
            }
        }

        return null;
    }

    public TravelRequirement getNextItineraryRequirement(UUID travelProposalId, UUID travelRequirementId)
        throws InvalidTravelReqirementException {
        if (travelProposalId == null || travelRequirementId == null) {
            throw new NullPointerException();
        }

        DBCollection proposalColls = itineraryDB.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        if (proposalColls.findOne(travelProposalId.toString()) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        DBCollection itineraryColls = itineraryDB.getCollection(DAOConstants.ITINERARY_COLL_NAME);
        DBCollection requirementColls = itineraryDB.getCollection(DAOConstants.REQUIREMENT_COLL_NAME);
        if (itineraryColls.findOne(travelRequirementId.toString()) == null
            && requirementColls.findOne(travelRequirementId.toString()) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.missing_travel_requirement, travelRequirementId), travelRequirementId);
        }

        DBCollection proposalRequirementColls = itineraryDB.getCollection(DAOConstants.PROPOSAL_REQUIREMENT_COLL_NAME);
        DBObject proposalRequirements = proposalRequirementColls.findOne(travelProposalId.toString());
        if (proposalRequirements == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        /*
         * TODO: [Optimization] a batch call could be used here to avoid
         * multiple requests against backend DB.
         */
        Object requirementsBs = proposalRequirements.get(Introspection.JSONKeys.REQUIREMENTS);
        JsonArray requirements = Json.createReader(new StringReader(requirementsBs.toString())).readArray();
        for (int i = 0; i < requirements.size(); i++) {
            if (requirements.getString(i).equals(travelRequirementId.toString())) {
                for (int j = i + 1; j < requirements.size(); j++) {
                    TravelRequirement requirement = getRequirement(UuidUtil.fromUuidStr(requirements.getString(j)),
                        Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT);
                    TravelRequirement itinerary = getRequirement(UuidUtil.fromUuidStr(requirements.getString(j)),
                        Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY);
                    if (requirement != null && itinerary == null) {
                        continue;
                    } else if (requirement == null && itinerary != null) {
                        return itinerary;
                    } else if (requirement == null && itinerary == null) {
                        continue;
                    } else {
                        throw new IllegalStateException();
                    }
                }
            }
        }

        return null;
    }

    public TravelRequirement getRequirement(UUID travelRequirementId, String requirementType)
        throws InvalidTravelReqirementException {
        if (travelRequirementId == null) {
            throw new NullPointerException();
        }

        DBCollection proposalColls = itineraryDB.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        DBCollection itineraryColls = itineraryDB.getCollection(DAOConstants.ITINERARY_COLL_NAME);
        DBCollection requirementColls = itineraryDB.getCollection(DAOConstants.REQUIREMENT_COLL_NAME);

        if (Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL.equals(requirementType)) {
            DBObject proposalBs = proposalColls.findOne(travelRequirementId.toString());
            if (proposalBs != null) {
                JsonObject proposal = Json.createReader(new StringReader(proposalBs.toString())).readObject();
                return JsonObjectHelper.toRequirement(proposal, true);
            } else {
                return null;
            }
        }

        if (Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY.equals(requirementType)) {
            DBObject itineraryBs = itineraryColls.findOne(travelRequirementId.toString());
            if (itineraryBs != null) {
                JsonObject itinerary = Json.createReader(new StringReader(itineraryBs.toString())).readObject();
                return JsonObjectHelper.toRequirement(itinerary, true);
            } else {
                return null;
            }
        }

        if (Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT.equals(requirementType)) {
            DBObject requirementBs = requirementColls.findOne(travelRequirementId.toString());
            if (requirementBs != null) {
                JsonObject requirement = Json.createReader(new StringReader(requirementBs.toString())).readObject();
                return JsonObjectHelper.toRequirement(requirement, true);
            } else {
                return null;
            }
        }

        DBObject proposalBs = proposalColls.findOne(travelRequirementId.toString());
        if (proposalBs != null) {
            JsonObject proposal = Json.createReader(new StringReader(proposalBs.toString())).readObject();
            TravelProposal travelProposal = new TravelProposal().fromJSON(proposal);
            return travelProposal;
        }

        DBObject itineraryBs = itineraryColls.findOne(travelRequirementId.toString());
        if (itineraryBs != null) {
            JsonObject itinerary = Json.createReader(new StringReader(itineraryBs.toString())).readObject();
            return new ItineraryRequirement().fromJSON(itinerary);
        }

        DBObject requirementBs = requirementColls.findOne(travelRequirementId.toString());
        if (requirementBs != null) {
            JsonObject requirement = Json.createReader(new StringReader(requirementBs.toString())).readObject();
            return JsonObjectHelper.toRequirement(requirement, true);
        }

        return null;
    }

    public UUID updateRequirement(TravelRequirement travelRequirement)
        throws InvalidTravelReqirementException {
        if (travelRequirement == null || travelRequirement.getUUID() == null) {
            throw new NullPointerException();
        }

        if (travelRequirement.isItinerary() || travelRequirement.isProposal()) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.illegal_update_travel_requirement_operation, travelRequirement.getUUID()),
                travelRequirement.getUUID());
        }

        DBObject updatingRequirementBs;
        DBCollection requirementColls = itineraryDB.getCollection(DAOConstants.REQUIREMENT_COLL_NAME);
        if ((updatingRequirementBs = requirementColls.findOne(travelRequirement.getUUID().toString())) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.missing_travel_requirement, travelRequirement.getUUID().toString()),
                travelRequirement.getUUID());
        }

        /*
         * TODO: [Optimization] get the boolean return only without get the
         * data.
         */
        if (isTypeOf(travelRequirement.getUUID().toString(), ItineraryRequirement.class) != null
            || isTypeOf(travelRequirement.getUUID().toString(), TravelProposal.class) != null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.illegal_update_travel_requirement_operation, travelRequirement.getUUID().toString()),
                travelRequirement.getUUID());
        }

        JsonObject updatingRequirementJs = Json.createReader(new StringReader(updatingRequirementBs.toString()))
            .readObject();
        TravelRequirement updatingTravelRequirement = JsonObjectHelper.toRequirement(updatingRequirementJs, true);

        if (!updatingTravelRequirement.getClass().equals(travelRequirement.getClass())) {
            throw new IllegalArgumentException();
        }

        storeRequirement(travelRequirement.toJSON());

        return travelRequirement.getUUID();
    }

    public UUID removeRequirement(UUID travelProposalId, UUID travelRequirementId)
        throws InvalidTravelReqirementException {
        if (travelProposalId == null || travelRequirementId == null) {
            throw new NullPointerException();
        }

        DBCollection proposalColls = itineraryDB.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        if (proposalColls.findOne(travelProposalId.toString()) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        DBCollection proposalRequirementColls = itineraryDB.getCollection(DAOConstants.PROPOSAL_REQUIREMENT_COLL_NAME);
        DBObject proposalRequirements = proposalRequirementColls.findOne(travelProposalId.toString());
        if (proposalRequirements == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        /*
         * TODO: [Optimization] avoid get all the itineraries by query the count
         * with conditions only.
         */
        List<TravelRequirement> itineraries = getItineraryRequirements(travelProposalId);
        if (itineraries.size() == 1) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.illegal_delete_travel_requirement_operation, travelProposalId), travelProposalId);
        }

        DBObject requirement = null;
        if ((requirement = isTypeOf(travelRequirementId.toString(), TravelProposal.class)) != null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.illegal_delete_travel_requirement_operation, travelRequirementId), travelRequirementId);
        } else if ((requirement = isTypeOf(travelRequirementId.toString(), ItineraryRequirement.class)) != null) {
            DBCollection requirementColls = itineraryDB.getCollection(DAOConstants.REQUIREMENT_COLL_NAME);
            boolean removing = false;
            JsonArrayBuilder requirementsBuilder = Json.createArrayBuilder();
            Object requirementsBs = proposalRequirements.get(Introspection.JSONKeys.REQUIREMENTS);
            JsonArray requirements = Json.createReader(new StringReader(requirementsBs.toString())).readArray();
            for (int i = 0; i < requirements.size(); i++) {
                String requirementId = requirements.getString(i);
                if (requirementId.equals(travelRequirementId.toString())) {
                    for (int j = i + 1; j < requirements.size(); j++) {
                        requirementId = requirements.getString(j);
                        DBObject req = isTypeOf(requirementId, TravelRequirement.class);
                        DBObject iti = isTypeOf(requirementId, ItineraryRequirement.class);
                        if (req != null && iti == null) {
                            WriteResult wr = requirementColls.remove(req);
                            LOG.info("UpsertedId:" + (String) wr.getUpsertedId() + ";N:" + wr.getN());
                        } else if (req == null && iti != null) {
                            break;
                        } else if (req == null && iti == null) {
                            continue;
                        } else {
                            throw new IllegalStateException();
                        }
                    }
                    removing = true;
                } else {
                    requirementsBuilder.add(requirementId);
                }
            }

            if (removing) {
                WriteResult wr = storeProposalRequirements(travelProposalId, requirementsBuilder.build());
                LOG.info("UpsertedId:" + (String) wr.getUpsertedId() + ";N:" + wr.getN());
            }

            DBCollection itineraryColls = itineraryDB.getCollection(DAOConstants.ITINERARY_COLL_NAME);
            WriteResult wr = itineraryColls.remove(requirement);
            LOG.info("UpsertedId:" + (String) wr.getUpsertedId() + ";N:" + wr.getN());

            return travelRequirementId;
        } else if ((requirement = isTypeOf(travelRequirementId.toString(), TravelRequirement.class)) != null) {
            boolean removing = false;
            JsonArrayBuilder requirementsBuilder = Json.createArrayBuilder();
            Object requirementsBs = proposalRequirements.get(Introspection.JSONKeys.REQUIREMENTS);
            JsonArray requirements = Json.createReader(new StringReader(requirementsBs.toString())).readArray();
            for (int i = 0; i < requirements.size(); i++) {
                String requirementId = requirements.getString(i);
                if (!requirementId.equals(travelRequirementId.toString())) {
                    requirementsBuilder.add(requirementId);
                } else {
                    removing = true;
                }
            }

            if (removing) {
                WriteResult wr = storeProposalRequirements(travelProposalId, requirementsBuilder.build());
                LOG.info("UpsertedId:" + (String) wr.getUpsertedId() + ";N:" + wr.getN());
            }

            DBCollection requirementColls = itineraryDB.getCollection(DAOConstants.REQUIREMENT_COLL_NAME);
            WriteResult wr = requirementColls.remove(requirement);
            LOG.info("UpsertedId:" + (String) wr.getUpsertedId() + ";N:" + wr.getN());

            return travelRequirementId;
        } else {
            return null;
        }
    }

    private WriteResult storeProposal(JsonObject proposal) {
        String proposalId = proposal.getString(Introspection.JSONKeys.UUID);
        DBCollection proposalColls = itineraryDB.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        DBObject proposalBs = (DBObject) JSON.parse(proposal.toString());
        proposalBs.put(DAOConstants.mongo_database_pk, proposalId);
        return proposalColls.insert(proposalBs, WriteConcern.MAJORITY);
    }

    private WriteResult storeItinerary(JsonObject itinerary) {
        String itineraryId = itinerary.getString(Introspection.JSONKeys.UUID);
        DBCollection itineraryColls = itineraryDB.getCollection(DAOConstants.ITINERARY_COLL_NAME);
        DBObject itineraryBs = (DBObject) JSON.parse(itinerary.toString());
        itineraryBs.put(DAOConstants.mongo_database_pk, itineraryId);
        return itineraryColls.insert(itineraryBs, WriteConcern.MAJORITY);
    }

    private WriteResult storeRequirement(JsonObject requirement) {
        String requirementId = requirement.getString(Introspection.JSONKeys.UUID);
        DBObject requirementBs = (DBObject) JSON.parse(requirement.toString());
        requirementBs.put(DAOConstants.mongo_database_pk, requirementId);

        DBCollection requirementColls = itineraryDB.getCollection(DAOConstants.REQUIREMENT_COLL_NAME);
        DBObject requirementQuery = QueryBuilder.start(DAOConstants.mongo_database_pk).is(requirementId).get();
        return requirementColls.update(requirementQuery, requirementBs, true, false, WriteConcern.MAJORITY);
    }

    private WriteResult storeProposalRequirements(UUID proposalId, JsonArray requirements) {
        JsonObjectBuilder requirementsJs = Json.createObjectBuilder();
        requirementsJs.add(Introspection.JSONKeys.UUID, proposalId.toString());
        requirementsJs.add(Introspection.JSONKeys.REQUIREMENTS, requirements);

        DBObject requirementsBs = (DBObject) JSON.parse(requirementsJs.build().toString());
        requirementsBs.put(DAOConstants.mongo_database_pk, proposalId.toString());

        DBCollection proposalRequirementColls = itineraryDB.getCollection(DAOConstants.PROPOSAL_REQUIREMENT_COLL_NAME);
        DBObject proposalQuery = QueryBuilder.start(DAOConstants.mongo_database_pk).is(proposalId.toString()).get();
        return proposalRequirementColls.update(proposalQuery, requirementsBs, true, false, WriteConcern.MAJORITY);
    }

    private DBObject isTypeOf(String requirementId, Class<?> requirementClass) {
        if (TravelProposal.class.equals(requirementClass)) {
            DBCollection proposalColls = itineraryDB.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
            return proposalColls.findOne(requirementId);
        } else if (ItineraryRequirement.class.equals(requirementClass)) {
            DBCollection itineraryColls = itineraryDB.getCollection(DAOConstants.ITINERARY_COLL_NAME);
            return itineraryColls.findOne(requirementId);
        } else if (TravelRequirement.class.equals(requirementClass)) {
            DBCollection requirementColls = itineraryDB.getCollection(DAOConstants.REQUIREMENT_COLL_NAME);
            return requirementColls.findOne(requirementId);
        } else {
            return null;
        }
    }
}
