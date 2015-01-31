package com.free.walker.service.itinerary.dao.db;

import java.io.IOException;
import java.io.StringReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import com.free.walker.service.itinerary.dao.TravelRequirementDAO;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.req.ItineraryRequirement;
import com.free.walker.service.itinerary.req.TravelProposal;
import com.free.walker.service.itinerary.req.TravelRequirement;
import com.free.walker.service.itinerary.util.JsonObjectHelper;
import com.free.walker.service.itinerary.util.MongoDbClientBuilder;
import com.free.walker.service.itinerary.util.SystemConfigUtil;
import com.ibm.icu.util.Calendar;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
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

public class MyMongoSQLTravelRequirementDAOImpl implements TravelRequirementDAO {
    private static final Logger LOG = LoggerFactory.getLogger(MyMongoSQLTravelRequirementDAOImpl.class);
    private static final DBObject ID_FIELD = new BasicDBObjectBuilder().add(DAOConstants.mongo_database_pk, true).get();

    private MongoClient mdbClient;
    private String itineraryMongoDbUrl;
    private DB itineraryDb;

    private static class SingletonHolder {
        private static final TravelRequirementDAO INSTANCE = new MyMongoSQLTravelRequirementDAOImpl();
    }

    public static TravelRequirementDAO getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private MyMongoSQLTravelRequirementDAOImpl() {
        try {
            Properties config = SystemConfigUtil.getApplicationConfig();
            mdbClient = new MongoDbClientBuilder().build(config);
            itineraryMongoDbUrl = config.getProperty(DAOConstants.mongo_database_url);
            itineraryDb = mdbClient.getDB(DAOConstants.itinerary_mongo_database);
        } catch (UnknownHostException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            if (!pingPersistence()){
                if (mdbClient == null) {
                    mdbClient.close();
                    mdbClient = null;
                }
                throw new IllegalStateException();
            }
        }
    }

    public boolean pingPersistence() {
        DBObject ping = new BasicDBObject("ping", "1");
        try {
            CommandResult cr = itineraryDb.command(ping);
            if (!cr.ok()) {
                return false;
            } else {
                return true;
            }
        } catch (RuntimeException e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_init_failure, itineraryMongoDbUrl,
                MongoClient.class.getName()), e);
            return false;
        }
    }

    public UUID createProposal(UUID anctId, TravelProposal travelProposal) throws InvalidTravelReqirementException,
        DatabaseAccessException {
        if (anctId == null || travelProposal == null) {
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

        DBCollection proposalColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        if (proposalColls.findOne(travelProposal.getUUID().toString(), ID_FIELD) != null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.existed_travel_requirement, travelProposal.getUUID()), travelProposal.getUUID());
        }

        DBCollection itineraryColls = itineraryDb.getCollection(DAOConstants.ITINERARY_COLL_NAME);
        String[] itineraryIds = new String[itinerariesJs.size()];
        JsonArrayBuilder itineraryIdsJs = Json.createArrayBuilder();
        for (int i = 0; i < itinerariesJs.size(); i++) {
            JsonObject requirment = itinerariesJs.get(i);
            String requirementId = requirment.getString(Introspection.JSONKeys.UUID);
            itineraryIds[i] = requirementId;
            itineraryIdsJs.add(requirementId);
        }

        DBObject itinerariesQuery = new BasicDBObjectBuilder().add(DAOConstants.mongo_database_pk,
            new BasicDBObject(DAOConstants.mongo_database_op_in, itineraryIds)).get();
        if (itineraryColls.findOne(itinerariesQuery, ID_FIELD) != null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.existed_travel_requirement, itineraryIds.toString()), travelProposal.getUUID());
        }

        /*
         * Transaction Start
         */
        try {
            WriteResult wr = storeProposal(proposalJs);
            LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_create_record, wr.toString()));
        } catch (MongoException e) {
            throw new InvalidTravelReqirementException(travelProposal.getUUID(), e);
        }

        try {
            BulkWriteResult bwr = storeItineraries(itinerariesJs);
            LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_bulk_create_record, bwr.toString()));
        } catch (MongoException e) {
            throw new InvalidTravelReqirementException(travelProposal.getUUID(), e);
        }

        try {
            WriteResult wr = storeProposalRequirements(travelProposal.getUUID(), itineraryIdsJs.build());
            LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
        } catch (MongoException e) {
            throw new InvalidTravelReqirementException(travelProposal.getUUID(), e);
        }
        /*
         * Transaction End
         */

        return travelProposal.getUUID();
    }

    public UUID startProposalBid(UUID travelProposalId, UUID accoutId) throws InvalidTravelReqirementException,
        DatabaseAccessException {
        if (travelProposalId == null || accoutId == null) {
            throw new NullPointerException();
        }

        DBCollection proposalColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        if (proposalColls.findOne(travelProposalId.toString(), ID_FIELD) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        DBCollection proposalAgencyColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_SUBMISSION_COLL_NAME);
        DBObject proposalAgency = proposalAgencyColls.findOne(travelProposalId.toString());
        if (proposalAgency != null) {
            if (!accoutId.toString().equals(proposalAgency.get(Introspection.JSONKeys.OWNER))) {
                throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                    LocalMessages.illegal_submit_proposal_operation, travelProposalId, accoutId), travelProposalId);
            } else {
                return travelProposalId;
            }
        } else {
            try {
                WriteResult wr = storeProposalBid(travelProposalId, accoutId, Json.createArrayBuilder().build());
                LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
            } catch (MongoException e) {
                throw new InvalidTravelReqirementException(travelProposalId, e);
            }
            return travelProposalId;
        }
    }

    public UUID joinProposalBid(UUID travelProposalId, UUID agencyId) throws InvalidTravelReqirementException,
        DatabaseAccessException {
        if (travelProposalId == null || agencyId == null) {
            throw new NullPointerException();
        }

        DBCollection proposalColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        if (proposalColls.findOne(travelProposalId.toString(), ID_FIELD) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        DBCollection proposalAgencyColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_SUBMISSION_COLL_NAME);
        DBObject proposalAgency = proposalAgencyColls.findOne(travelProposalId.toString());
        if (proposalAgency == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.missing_travel_proposal_bidding, travelProposalId), travelProposalId);
        } else {
            Object agenciesBs = proposalAgency.get(Introspection.JSONKeys.AGENCIES);
            Object ownerBs = proposalAgency.get(Introspection.JSONKeys.OWNER);
            JsonArrayBuilder agenciesJs = Json.createArrayBuilder();
            agenciesJs.add(agencyId.toString());
            JsonArray agencies = Json.createReader(new StringReader(agenciesBs.toString())).readArray();
            for (int i = 0; i < agencies.size(); i++) {
                if (agencyId.toString().equals(agencies.getString(i))) {
                    return agencyId;
                } else {
                    agenciesJs.add(agencies.getString(i));
                }
            }

            try {
                WriteResult wr = storeProposalBid(travelProposalId, UUID.fromString((String) ownerBs),
                    agenciesJs.build());
                LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
            } catch (MongoException e) {
                throw new InvalidTravelReqirementException(travelProposalId, e);
            }
        }

        return agencyId;
    }

    public UUID addItinerary(UUID travelProposalId, UUID itineraryRequirementId,
        ItineraryRequirement itineraryRequirement) throws InvalidTravelReqirementException {
        DBCollection proposalColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        if (proposalColls.findOne(travelProposalId.toString(), ID_FIELD) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        DBCollection itineraryColls = itineraryDb.getCollection(DAOConstants.ITINERARY_COLL_NAME);
        if (itineraryColls.findOne(itineraryRequirementId.toString(), ID_FIELD) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_itinerary,
                itineraryRequirementId), itineraryRequirementId);
        }

        if (itineraryColls.findOne(itineraryRequirement.getUUID().toString(), ID_FIELD) != null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.existed_travel_requirement, itineraryRequirement.getUUID()),
                itineraryRequirement.getUUID());
        }

        DBCollection proposalRequirementColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_REQUIREMENT_COLL_NAME);
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

            if (foundItinerary && isTypeOf(requirementId, ItineraryRequirement.class)) {
                requirementsBuilder.add(itineraryRequirement.getUUID().toString());
            }

            requirementsBuilder.add(requirementId);

            foundItinerary = requirementId.equals(itineraryRequirementId.toString());

            if (i + 1 == requirements.size() && !foundItinerary) {
                throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                    LocalMessages.missing_travel_itinerary, itineraryRequirementId), itineraryRequirementId);
            }
        }

        /*
         * Transaction Start
         */
        try {
            WriteResult wr = storeItinerary(itineraryRequirement.toJSON());
            LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_create_record, wr.toString()));
        } catch (MongoException e) {
            throw new InvalidTravelReqirementException(itineraryRequirement.getUUID(), e);
        }

        try {
            WriteResult wr = storeProposalRequirements(travelProposalId, requirementsBuilder.build());
            LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
        } catch (MongoException e) {
            throw new InvalidTravelReqirementException(travelProposalId, e);
        }
        /*
         * Transaction End
         */

        return itineraryRequirement.getUUID();
    }

    public UUID addRequirement(UUID travelProposalId, UUID itineraryRequirementId, TravelRequirement travelRequirement)
        throws InvalidTravelReqirementException {
        DBCollection proposalColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        if (proposalColls.findOne(travelProposalId.toString(), ID_FIELD) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        DBCollection itineraryColls = itineraryDb.getCollection(DAOConstants.ITINERARY_COLL_NAME);
        if (itineraryColls.findOne(itineraryRequirementId.toString(), ID_FIELD) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_itinerary,
                itineraryRequirementId), itineraryRequirementId);
        }

        DBCollection requirementColls = itineraryDb.getCollection(DAOConstants.REQUIREMENT_COLL_NAME);
        if (requirementColls.findOne(travelRequirement.getUUID().toString(), ID_FIELD) != null) {
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

        DBCollection proposalRequirementColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_REQUIREMENT_COLL_NAME);
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

        /*
         * Transaction Start
         */
        try {
            WriteResult wr = storeRequirement(travelRequirement.toJSON());
            LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
        } catch (MongoException e) {
            throw new InvalidTravelReqirementException(travelRequirement.getUUID(), e);
        }

        try {
            WriteResult wr = storeProposalRequirements(travelProposalId, requirementsBuilder.build());
            LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
        } catch (MongoException e) {
            throw new InvalidTravelReqirementException(travelProposalId, e);
        }
        /*
         * Transaction End
         */

        return travelRequirement.getUUID();
    }

    public UUID addRequirement(UUID travelProposalId, TravelRequirement travelRequirement)
        throws InvalidTravelReqirementException {
        if (travelProposalId == null || travelRequirement == null) {
            throw new NullPointerException();
        }

        DBCollection proposalColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        if (proposalColls.findOne(travelProposalId.toString(), ID_FIELD) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        DBCollection requirementColls = itineraryDb.getCollection(DAOConstants.REQUIREMENT_COLL_NAME);
        DBCollection itineraryColls = itineraryDb.getCollection(DAOConstants.ITINERARY_COLL_NAME);
        if (requirementColls.findOne(travelRequirement.getUUID().toString(), ID_FIELD) != null
            || itineraryColls.findOne(travelRequirement.getUUID().toString(), ID_FIELD) != null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.existed_travel_requirement, travelRequirement.getUUID()), travelRequirement.getUUID());
        }

        if (travelRequirement.isProposal()) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.illegal_add_proposal_as_requirement, travelRequirement.getUUID(), travelProposalId),
                travelRequirement.getUUID());
        }

        DBCollection proposalRequirementColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_REQUIREMENT_COLL_NAME);
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

        /*
         * Transaction Start
         */
        try {
            if (travelRequirement.isItinerary()) {
                WriteResult wr = storeItinerary(travelRequirement.toJSON());
                LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_create_record, wr.toString()));
            } else {
                WriteResult wr = storeRequirement(travelRequirement.toJSON());
                LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
            }
        } catch (MongoException e) {
            throw new InvalidTravelReqirementException(travelRequirement.getUUID(), e);
        }

        try {
            WriteResult wr = storeProposalRequirements(travelProposalId, requirementsBuilder.build());
            LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
        } catch (MongoException e) {
            throw new InvalidTravelReqirementException(travelProposalId, e);
        }
        /*
         * Transaction End
         */

        return travelRequirement.getUUID();
    }

    public List<TravelRequirement> getRequirements(UUID travelProposalId) throws InvalidTravelReqirementException {
        if (travelProposalId == null) {
            throw new NullPointerException();
        }

        DBCollection proposalColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        if (proposalColls.findOne(travelProposalId.toString(), ID_FIELD) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        DBCollection proposalRequirementColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_REQUIREMENT_COLL_NAME);
        DBObject proposalRequirements = proposalRequirementColls.findOne(travelProposalId.toString());
        if (proposalRequirements == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        Map<String, TravelRequirement> requirementsMap = new HashMap<String, TravelRequirement>();
        Object requirementsBs = proposalRequirements.get(Introspection.JSONKeys.REQUIREMENTS);
        JsonArray requirements = Json.createReader(new StringReader(requirementsBs.toString())).readArray();
        List<String> requirementIds = new ArrayList<String>();
        for (int i = 0; i < requirements.size(); i++) {
            String requirementId = requirements.getString(i);
            requirementIds.add(requirementId);
        }

        DBCollection itineraryColls = itineraryDb.getCollection(DAOConstants.ITINERARY_COLL_NAME);
        DBObject itinerariesQuery = new BasicDBObjectBuilder().add(DAOConstants.mongo_database_pk,
            new BasicDBObject(DAOConstants.mongo_database_op_in, requirementIds)).get();
        DBCursor itineraryCursor = itineraryColls.find(itinerariesQuery);
        try {
            Iterator<DBObject> itinerariesBsIter = itineraryCursor.iterator();
            while (itinerariesBsIter.hasNext()) {
                DBObject itineraryBs = itinerariesBsIter.next();
                JsonObject itinerary = Json.createReader(new StringReader(itineraryBs.toString())).readObject();
                requirementsMap.put(itinerary.getString(DAOConstants.mongo_database_pk),
                    new ItineraryRequirement().fromJSON(itinerary));
            }
        } finally {
            itineraryCursor.close();
        }

        DBCollection requirementColls = itineraryDb.getCollection(DAOConstants.REQUIREMENT_COLL_NAME);
        DBObject requirementsQuery = new BasicDBObjectBuilder().add(DAOConstants.mongo_database_pk,
            new BasicDBObject(DAOConstants.mongo_database_op_in, requirementIds)).get();
        DBCursor requirementCursor = requirementColls.find(requirementsQuery);
        try {
            Iterator<DBObject> requirementsBsIter = requirementCursor.iterator();
            while (requirementsBsIter.hasNext()) {
                DBObject requirementBs = requirementsBsIter.next();
                JsonObject requirement = Json.createReader(new StringReader(requirementBs.toString())).readObject();
                requirementsMap.put(requirement.getString(DAOConstants.mongo_database_pk),
                    JsonObjectHelper.toRequirement(requirement, true));
            }
        } finally {
            requirementCursor.close();
        }

        List<TravelRequirement> requirementsList = new ArrayList<TravelRequirement>();
        for (int i = 0; i < requirementIds.size(); i++) {
            TravelRequirement travelRequirement = requirementsMap.get(requirementIds.get(i));
            if (travelRequirement != null) requirementsList.add(travelRequirement);
        }

        return requirementsList;
    }

    public List<TravelRequirement> getItineraryRequirements(UUID travelProposalId)
        throws InvalidTravelReqirementException {
        if (travelProposalId == null) {
            throw new NullPointerException();
        }

        DBCollection proposalColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        if (proposalColls.findOne(travelProposalId.toString(), ID_FIELD) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        DBCollection proposalRequirementColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_REQUIREMENT_COLL_NAME);
        DBObject proposalRequirements = proposalRequirementColls.findOne(travelProposalId.toString());
        if (proposalRequirements == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        Object requirementsBs = proposalRequirements.get(Introspection.JSONKeys.REQUIREMENTS);
        JsonArray requirements = Json.createReader(new StringReader(requirementsBs.toString())).readArray();
        List<String> requirementIds = new ArrayList<String>();
        for (int i = 0; i < requirements.size(); i++) {
            String requirementId = requirements.getString(i);
            requirementIds.add(requirementId);
        }

        Map<String, TravelRequirement> itinerariesMap = new HashMap<String, TravelRequirement>();
        DBCollection itineraryColls = itineraryDb.getCollection(DAOConstants.ITINERARY_COLL_NAME);
        DBObject itinerariesQuery = new BasicDBObjectBuilder().add(DAOConstants.mongo_database_pk,
            new BasicDBObject(DAOConstants.mongo_database_op_in, requirementIds)).get();
        DBCursor itineraryCursor = itineraryColls.find(itinerariesQuery);
        try {
            Iterator<DBObject> itinerariesBsIter = itineraryCursor.iterator();
            while (itinerariesBsIter.hasNext()) {
                DBObject itineraryBs = itinerariesBsIter.next();
                JsonObject itinerary = Json.createReader(new StringReader(itineraryBs.toString())).readObject();
                itinerariesMap.put(itinerary.getString(DAOConstants.mongo_database_pk),
                    new ItineraryRequirement().fromJSON(itinerary));
            }
        } finally {
            itineraryCursor.close();
        }

        List<TravelRequirement> itinerariesList = new ArrayList<TravelRequirement>();
        for (int i = 0; i < requirementIds.size(); i++) {
            TravelRequirement itineraryRequirement = itinerariesMap.get(requirementIds.get(i));
            if (itineraryRequirement != null) itinerariesList.add(itineraryRequirement);
        }

        return itinerariesList;
    }

    public List<TravelRequirement> getRequirements(UUID travelProposalId, UUID itineraryRequirementId)
        throws InvalidTravelReqirementException {
        if (travelProposalId == null || itineraryRequirementId == null) {
            throw new NullPointerException();
        }

        DBCollection proposalColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        if (proposalColls.findOne(travelProposalId.toString(), ID_FIELD) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        DBCollection itineraryColls = itineraryDb.getCollection(DAOConstants.ITINERARY_COLL_NAME);
        if (itineraryColls.findOne(itineraryRequirementId.toString(), ID_FIELD) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_itinerary,
                itineraryRequirementId), itineraryRequirementId);
        }

        DBCollection proposalRequirementColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_REQUIREMENT_COLL_NAME);
        DBObject proposalRequirements = proposalRequirementColls.findOne(travelProposalId.toString());
        if (proposalRequirements == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        Object requirementsBs = proposalRequirements.get(Introspection.JSONKeys.REQUIREMENTS);
        JsonArray requirements = Json.createReader(new StringReader(requirementsBs.toString())).readArray();
        List<String> requirementIds = new ArrayList<String>();
        for (int i = 0; i < requirements.size(); i++) {
            String requirementId = requirements.getString(i);
            requirementIds.add(requirementId);
        }

        Map<String, Boolean> itinerariesMap = new HashMap<String, Boolean>();
        DBObject itinerariesQuery = new BasicDBObjectBuilder().add(DAOConstants.mongo_database_pk,
            new BasicDBObject(DAOConstants.mongo_database_op_in, requirementIds)).get();
        DBCursor itinerariesCursor = itineraryColls.find(itinerariesQuery, ID_FIELD);
        try {
            Iterator<DBObject> requirementsBsIter = itinerariesCursor.iterator();
            while (requirementsBsIter.hasNext()) {
                DBObject requirementBs = requirementsBsIter.next();
                JsonObject requirement = Json.createReader(new StringReader(requirementBs.toString())).readObject();
                itinerariesMap.put(requirement.getString(DAOConstants.mongo_database_pk), true);
            }
        } finally {
            itinerariesCursor.close();
        }

        List<String> requirementIdsFiltered = new ArrayList<String>();
        for (int i = 0; i < requirementIds.size(); i++) {
            String requirementId = requirementIds.get(i);
            Boolean itineraryRequirement = itinerariesMap.get(requirementId);
            if (Boolean.TRUE.equals(itineraryRequirement) && requirementId.equals(itineraryRequirementId.toString())) {
                for (int j = i + 1; j < requirementIds.size(); j++) {
                    if (Boolean.TRUE.equals(itinerariesMap.get(requirementIds.get(j)))) {
                        break;
                    } else {
                        requirementIdsFiltered.add(requirementIds.get(j));
                        continue;
                    }
                }
                i = requirementIds.size();
            }
        }

        List<TravelRequirement> requirementsList = new LinkedList<TravelRequirement>();
        if (requirementIdsFiltered.isEmpty()) {
            return requirementsList;
        }

        DBCollection requirementColls = itineraryDb.getCollection(DAOConstants.REQUIREMENT_COLL_NAME);
        DBObject requirementsQuery = new BasicDBObjectBuilder().add(DAOConstants.mongo_database_pk,
            new BasicDBObject(DAOConstants.mongo_database_op_in, requirementIdsFiltered)).get();
        DBCursor requirementCursor = requirementColls.find(requirementsQuery);
        Map<String, TravelRequirement> requirementsMap = new HashMap<String, TravelRequirement>();
        try {
            Iterator<DBObject> requirementsBsIter = requirementCursor.iterator();
            while (requirementsBsIter.hasNext()) {
                DBObject requirementBs = requirementsBsIter.next();
                JsonObject requirement = Json.createReader(new StringReader(requirementBs.toString())).readObject();
                requirementsMap.put(requirement.getString(DAOConstants.mongo_database_pk),
                    JsonObjectHelper.toRequirement(requirement, true));
            }
        } finally {
            requirementCursor.close();
        }

        for (int i = 0; i < requirementIdsFiltered.size(); i++) {
            TravelRequirement travelRequirement = requirementsMap.get(requirementIdsFiltered.get(i));
            if (travelRequirement != null) requirementsList.add(travelRequirement);
        }

        return requirementsList;
    }

    public TravelRequirement getPrevItineraryRequirement(UUID travelProposalId, UUID travelRequirementId)
        throws InvalidTravelReqirementException {
        if (travelProposalId == null || travelRequirementId == null) {
            throw new NullPointerException();
        }

        DBCollection proposalColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        if (proposalColls.findOne(travelProposalId.toString(), ID_FIELD) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        DBCollection itineraryColls = itineraryDb.getCollection(DAOConstants.ITINERARY_COLL_NAME);
        DBCollection requirementColls = itineraryDb.getCollection(DAOConstants.REQUIREMENT_COLL_NAME);
        if (itineraryColls.findOne(travelRequirementId.toString(), ID_FIELD) == null
            && requirementColls.findOne(travelRequirementId.toString(), ID_FIELD) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.missing_travel_requirement, travelRequirementId), travelRequirementId);
        }

        DBCollection proposalRequirementColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_REQUIREMENT_COLL_NAME);
        DBObject proposalRequirements = proposalRequirementColls.findOne(travelProposalId.toString());
        if (proposalRequirements == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        Object requirementsBs = proposalRequirements.get(Introspection.JSONKeys.REQUIREMENTS);
        JsonArray requirements = Json.createReader(new StringReader(requirementsBs.toString())).readArray();
        List<String> requirementIds = new ArrayList<String>();
        for (int i = 0; i < requirements.size(); i++) {
            String requirementId = requirements.getString(i);
            requirementIds.add(requirementId);
        }

        Map<String, Boolean> itinerariesMap = new HashMap<String, Boolean>();
        DBObject itinerariesQuery = new BasicDBObjectBuilder().add(DAOConstants.mongo_database_pk,
            new BasicDBObject(DAOConstants.mongo_database_op_in, requirementIds)).get();
        DBCursor itineraryCursor = itineraryColls.find(itinerariesQuery, ID_FIELD);
        try {
            Iterator<DBObject> itinerariesBsIter = itineraryCursor.iterator();
            while (itinerariesBsIter.hasNext()) {
                DBObject itineraryBs = (DBObject) itinerariesBsIter.next();
                JsonObject itinerary = Json.createReader(new StringReader(itineraryBs.toString())).readObject();
                itinerariesMap.put(itinerary.getString(DAOConstants.mongo_database_pk), true);
            }
        } finally {
            itineraryCursor.close();
        }

        String prevItineraryId = null;
        for (int i = 0; i < requirementIds.size(); i++) {
            String requirementId = requirementIds.get(i);

            if (travelRequirementId.toString().equals(requirementId)) {
                DBObject itineraryBs = prevItineraryId == null ? null : itineraryColls.findOne(prevItineraryId);
                if (itineraryBs != null) {
                    JsonObject itinerary = Json.createReader(new StringReader(itineraryBs.toString())).readObject();
                    return JsonObjectHelper.toRequirement(itinerary, true);
                } else {
                    return null;
                }
            }

            Boolean itineraryRequirement = itinerariesMap.get(requirementId);
            if (Boolean.TRUE.equals(itineraryRequirement)) {
                prevItineraryId = requirementId;
            }
        }

        return null;
    }

    public TravelRequirement getNextItineraryRequirement(UUID travelProposalId, UUID travelRequirementId)
        throws InvalidTravelReqirementException {
        if (travelProposalId == null || travelRequirementId == null) {
            throw new NullPointerException();
        }

        DBCollection proposalColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        if (proposalColls.findOne(travelProposalId.toString(), ID_FIELD) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        DBCollection itineraryColls = itineraryDb.getCollection(DAOConstants.ITINERARY_COLL_NAME);
        DBCollection requirementColls = itineraryDb.getCollection(DAOConstants.REQUIREMENT_COLL_NAME);
        if (itineraryColls.findOne(travelRequirementId.toString(), ID_FIELD) == null
            && requirementColls.findOne(travelRequirementId.toString(), ID_FIELD) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.missing_travel_requirement, travelRequirementId), travelRequirementId);
        }

        DBCollection proposalRequirementColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_REQUIREMENT_COLL_NAME);
        DBObject proposalRequirements = proposalRequirementColls.findOne(travelProposalId.toString());
        if (proposalRequirements == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        Object requirementsBs = proposalRequirements.get(Introspection.JSONKeys.REQUIREMENTS);
        JsonArray requirements = Json.createReader(new StringReader(requirementsBs.toString())).readArray();
        List<String> requirementIds = new ArrayList<String>();
        for (int i = 0; i < requirements.size(); i++) {
            String requirementId = requirements.getString(i);
            requirementIds.add(requirementId);
        }

        Map<String, Boolean> itinerariesMap = new HashMap<String, Boolean>();
        DBObject itinerariesQuery = new BasicDBObjectBuilder().add(DAOConstants.mongo_database_pk,
            new BasicDBObject(DAOConstants.mongo_database_op_in, requirementIds)).get();
        DBCursor itineraryCursor = itineraryColls.find(itinerariesQuery, ID_FIELD);
        try {
            Iterator<DBObject> itinerariesBsIter = itineraryCursor.iterator();
            while (itinerariesBsIter.hasNext()) {
                DBObject itineraryBs = (DBObject) itinerariesBsIter.next();
                JsonObject itinerary = Json.createReader(new StringReader(itineraryBs.toString())).readObject();
                itinerariesMap.put(itinerary.getString(DAOConstants.mongo_database_pk), true);
            }
        } finally {
            itineraryCursor.close();
        }

        String nextItineraryId = null;
        for (int i = requirementIds.size() - 1; i >= 0; i--) {
            String requirementId = requirementIds.get(i);

            if (travelRequirementId.toString().equals(requirementId)) {
                DBObject itineraryBs = nextItineraryId == null ? null : itineraryColls.findOne(nextItineraryId);
                if (itineraryBs != null) {
                    JsonObject itinerary = Json.createReader(new StringReader(itineraryBs.toString())).readObject();
                    return JsonObjectHelper.toRequirement(itinerary, true);
                } else {
                    return null;
                }
            }

            Boolean itineraryRequirement = itinerariesMap.get(requirementId);
            if (Boolean.TRUE.equals(itineraryRequirement)) {
                nextItineraryId = requirementId;
            }
        }

        return null;
    }

    public TravelRequirement getRequirement(UUID travelRequirementId, String requirementType)
        throws InvalidTravelReqirementException {
        if (travelRequirementId == null) {
            throw new NullPointerException();
        }

        DBCollection proposalColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        DBCollection itineraryColls = itineraryDb.getCollection(DAOConstants.ITINERARY_COLL_NAME);
        DBCollection requirementColls = itineraryDb.getCollection(DAOConstants.REQUIREMENT_COLL_NAME);

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

    public UUID updateRequirement(TravelRequirement travelRequirement) throws InvalidTravelReqirementException {
        if (travelRequirement == null || travelRequirement.getUUID() == null) {
            throw new NullPointerException();
        }

        if (travelRequirement.isItinerary() || travelRequirement.isProposal()) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.illegal_update_travel_requirement_operation, travelRequirement.getUUID()),
                travelRequirement.getUUID());
        }

        DBCollection requirementColls = itineraryDb.getCollection(DAOConstants.REQUIREMENT_COLL_NAME);
        if (requirementColls.findOne(travelRequirement.getUUID().toString(), ID_FIELD) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.missing_travel_requirement, travelRequirement.getUUID().toString()),
                travelRequirement.getUUID());
        }

        String travelRequirementId = travelRequirement.getUUID().toString();
        if (isTypeOf(travelRequirementId, ItineraryRequirement.class)
            || isTypeOf(travelRequirementId, TravelProposal.class)) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.illegal_update_travel_requirement_operation, travelRequirementId),
                travelRequirement.getUUID());
        }

        try {
            WriteResult wr = storeRequirement(travelRequirement.toJSON());
            LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
        } catch (MongoException e) {
            throw new InvalidTravelReqirementException(travelRequirement.getUUID(), e);
        }

        return travelRequirement.getUUID();
    }

    public UUID removeRequirement(UUID travelProposalId, UUID travelRequirementId)
        throws InvalidTravelReqirementException {
        if (travelProposalId == null || travelRequirementId == null) {
            throw new NullPointerException();
        }

        DBCollection proposalColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        if (proposalColls.findOne(travelProposalId.toString(), ID_FIELD) == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        DBCollection proposalRequirementColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_REQUIREMENT_COLL_NAME);
        DBObject proposalRequirements = proposalRequirementColls.findOne(travelProposalId.toString());
        if (proposalRequirements == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        {
            Object requirementsBs = proposalRequirements.get(Introspection.JSONKeys.REQUIREMENTS);
            JsonArray requirements = Json.createReader(new StringReader(requirementsBs.toString())).readArray();
            List<String> requirementIds = new ArrayList<String>();
            for (int i = 0; i < requirements.size(); i++) {
                requirementIds.add(requirements.getString(i));
            }

            DBCollection itineraryColls = itineraryDb.getCollection(DAOConstants.ITINERARY_COLL_NAME);
            DBObject itinerariesQuery = new BasicDBObjectBuilder().add(DAOConstants.mongo_database_pk,
                new BasicDBObject(DAOConstants.mongo_database_op_in, requirementIds)).get();
            DBCursor itineraryCursor = itineraryColls.find(itinerariesQuery, ID_FIELD);
            try {
                if (itineraryCursor.count() == 1) {
                    throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                        LocalMessages.illegal_delete_travel_requirement_operation, travelProposalId), travelProposalId);
                }
            } finally {
                itineraryCursor.close();
            }
        }

        if (isTypeOf(travelRequirementId.toString(), TravelProposal.class)) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.illegal_delete_travel_requirement_operation, travelRequirementId), travelRequirementId);
        } else if (isTypeOf(travelRequirementId.toString(), ItineraryRequirement.class)) {
            DBCollection requirementColls = itineraryDb.getCollection(DAOConstants.REQUIREMENT_COLL_NAME);
            DBCollection itineraryColls = itineraryDb.getCollection(DAOConstants.ITINERARY_COLL_NAME);
            boolean removing = false;
            JsonArrayBuilder requirementsBuilder = Json.createArrayBuilder();
            Object requirementsBs = proposalRequirements.get(Introspection.JSONKeys.REQUIREMENTS);
            JsonArray requirements = Json.createReader(new StringReader(requirementsBs.toString())).readArray();
            List<String> requirementIds = new ArrayList<String>();
            for (int i = 0; i < requirements.size(); i++) {
                String requirementId = requirements.getString(i);
                if (requirementId.equals(travelRequirementId.toString())) {
                    for (int j = i + 1; j < requirements.size(); j++) {
                        requirementId = requirements.getString(j);
                        DBObject req = requirementColls.findOne(requirementId, ID_FIELD);
                        DBObject iti = itineraryColls.findOne(requirementId, ID_FIELD);
                        if (req != null && iti == null) {
                            requirementIds.add((String) req.get(DAOConstants.mongo_database_pk));
                        } else if (req == null && iti != null) {
                            i = j - 1;
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

            /*
             * Transactoin Start
             */
            if (removing) {
                try {
                    WriteResult wr = storeProposalRequirements(travelProposalId, requirementsBuilder.build());
                    LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
                } catch (MongoException e) {
                    throw new InvalidTravelReqirementException(travelProposalId, e);
                }
            }

            try {
                String[] ids = requirementIds.toArray(new String[requirementIds.size()]);
                BasicDBObjectBuilder requirementIdsBuilder = new BasicDBObjectBuilder().add(
                    DAOConstants.mongo_database_pk, new BasicDBObject(DAOConstants.mongo_database_op_in, ids));
                WriteResult wr = requirementColls.remove(requirementIdsBuilder.get());
                LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_remove_record, wr.toString()));
            } catch (MongoException e) {
                throw new InvalidTravelReqirementException(travelRequirementId, e);
            }

            try {
                WriteResult wr = itineraryColls.remove(new BasicDBObject(DAOConstants.mongo_database_pk,
                    travelRequirementId.toString()));
                LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_remove_record, wr.toString()));
            } catch (MongoException e) {
                throw new InvalidTravelReqirementException(travelRequirementId, e);
            }
            /*
             * Transactoin End
             */

            return travelRequirementId;
        } else if (isTypeOf(travelRequirementId.toString(), TravelRequirement.class)) {
            DBCollection requirementColls = itineraryDb.getCollection(DAOConstants.REQUIREMENT_COLL_NAME);
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

            /*
             * Transactoin Start
             */
            if (removing) {
                try {
                    WriteResult wr = storeProposalRequirements(travelProposalId, requirementsBuilder.build());
                    LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
                } catch (MongoException e) {
                    throw new InvalidTravelReqirementException(travelProposalId, e);
                }
            }

            try {
                WriteResult wr = requirementColls.remove(new BasicDBObject(DAOConstants.mongo_database_pk,
                    travelRequirementId.toString()));
                LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_remove_record, wr.toString()));
            } catch (MongoException e) {
                throw new InvalidTravelReqirementException(travelRequirementId, e);
            }
            /*
             * Transactoin End
             */

            return travelRequirementId;
        } else {
            return null;
        }
    }

    public List<TravelProposal> getTravelProposalsByAgency(UUID agencyId, Calendar since, int numberOfDay)
        throws InvalidTravelReqirementException {
        if (agencyId == null || since == null) {
            throw new NullPointerException();
        }

        Calendar until = Calendar.getInstance();
        if (numberOfDay != 0) {
            until.setTimeInMillis(since.getTimeInMillis());
            until.add(Calendar.DATE, numberOfDay);
        }

        List<TravelProposal> result = new ArrayList<TravelProposal>();
        DBCollection proposalSubmissionColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_SUBMISSION_COLL_NAME);
        BasicDBObject query1 = new BasicDBObject(Introspection.JSONKeys.DATE, new BasicDBObject("$gte",
            since.getTimeInMillis()).append("$lte", until.getTimeInMillis()));
        BasicDBObject query2 = new BasicDBObject(Introspection.JSONKeys.AGENCIES, new BasicDBObject("$in",
            new String[] { agencyId.toString() }));
        BasicDBObject query = new BasicDBObject("$and", new BasicDBObject[] { query1, query2 });
        DBCursor submissionsCr = proposalSubmissionColls.find(query);
        List<String> proposalIds = new ArrayList<String>();
        try {
            while (submissionsCr.hasNext()) {
                JsonObject proposalSubmission = Json.createReader(new StringReader(submissionsCr.next().toString()))
                    .readObject();
                String proposalId = proposalSubmission.getString(Introspection.JSONKeys.UUID, null);
                if (proposalId != null) proposalIds.add(proposalId);
            }
        } finally {
            submissionsCr.close();
        }

        DBCollection proposalColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        BasicDBObjectBuilder proposalIdsBuilder = new BasicDBObjectBuilder().add(DAOConstants.mongo_database_pk,
            new BasicDBObject(DAOConstants.mongo_database_op_in, proposalIds.toArray(new String[proposalIds.size()])));
        DBCursor proposalCr = proposalColls.find(proposalIdsBuilder.get());
        try {
            while (proposalCr.hasNext()) {
                JsonObject proposal = Json.createReader(new StringReader(proposalCr.next().toString())).readObject();
                result.add((TravelProposal) JsonObjectHelper.toRequirement(proposal, true));
            }
        } finally {
            proposalCr.close();
        }

        return result;
    }

    public List<TravelProposal> getTravelProposalsByAccount(UUID accountId, Calendar since, int numberOfDay)
        throws InvalidTravelReqirementException {
        if (accountId == null || since == null) {
            throw new NullPointerException();
        }

        Calendar until = Calendar.getInstance();
        if (numberOfDay != 0) {
            until.setTimeInMillis(since.getTimeInMillis());
            until.add(Calendar.DATE, numberOfDay);
        }

        List<TravelProposal> result = new ArrayList<TravelProposal>();
        DBCollection proposalSubmissionColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_SUBMISSION_COLL_NAME);
        BasicDBObject query1 = new BasicDBObject(Introspection.JSONKeys.DATE, new BasicDBObject("$gte",
            since.getTimeInMillis()).append("$lte", until.getTimeInMillis()));
        BasicDBObject query2 = new BasicDBObject(new BasicDBObject(Introspection.JSONKeys.OWNER, accountId.toString()));
        BasicDBObject query = new BasicDBObject("$and", new BasicDBObject[] { query1, query2 });
        DBCursor submissionsCr = proposalSubmissionColls.find(query);
        List<String> proposalIds = new ArrayList<String>();
        try {
            while (submissionsCr.hasNext()) {
                JsonObject proposalSubmission = Json.createReader(new StringReader(submissionsCr.next().toString()))
                    .readObject();
                String proposalId = proposalSubmission.getString(Introspection.JSONKeys.UUID, null);
                if (proposalId != null) proposalIds.add(proposalId);
            }
        } finally {
            submissionsCr.close();
        }

        DBCollection proposalColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        BasicDBObjectBuilder proposalIdsBuilder = new BasicDBObjectBuilder().add(DAOConstants.mongo_database_pk,
            new BasicDBObject(DAOConstants.mongo_database_op_in, proposalIds));
        DBCursor proposalCr = proposalColls.find(proposalIdsBuilder.get());
        try {
            while (proposalCr.hasNext()) {
                JsonObject proposal = Json.createReader(new StringReader(proposalCr.next().toString())).readObject();
                result.add((TravelProposal) JsonObjectHelper.toRequirement(proposal, true));
            }
        } finally {
            proposalCr.close();
        }

        return result;
    }

    private WriteResult storeProposal(JsonObject proposal) {
        String proposalId = proposal.getString(Introspection.JSONKeys.UUID);
        DBCollection proposalColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
        DBObject proposalBs = (DBObject) JSON.parse(proposal.toString());
        proposalBs.put(DAOConstants.mongo_database_pk, proposalId);
        return proposalColls.insert(proposalBs, WriteConcern.MAJORITY);
    }

    private WriteResult storeItinerary(JsonObject itinerary) {
        String itineraryId = itinerary.getString(Introspection.JSONKeys.UUID);
        DBCollection itineraryColls = itineraryDb.getCollection(DAOConstants.ITINERARY_COLL_NAME);
        DBObject itineraryBs = (DBObject) JSON.parse(itinerary.toString());
        itineraryBs.put(DAOConstants.mongo_database_pk, itineraryId);
        return itineraryColls.insert(itineraryBs, WriteConcern.MAJORITY);
    }

    private WriteResult storeRequirement(JsonObject requirement) {
        String requirementId = requirement.getString(Introspection.JSONKeys.UUID);
        DBObject requirementBs = (DBObject) JSON.parse(requirement.toString());
        requirementBs.put(DAOConstants.mongo_database_pk, requirementId);

        DBCollection requirementColls = itineraryDb.getCollection(DAOConstants.REQUIREMENT_COLL_NAME);
        DBObject requirementQuery = QueryBuilder.start(DAOConstants.mongo_database_pk).is(requirementId).get();
        return requirementColls.update(requirementQuery, requirementBs, true, false, WriteConcern.MAJORITY);
    }

    private BulkWriteResult storeItineraries(List<JsonObject> itineraries) {
        DBCollection itineraryColls = itineraryDb.getCollection(DAOConstants.ITINERARY_COLL_NAME);
        BulkWriteOperation op = itineraryColls.initializeOrderedBulkOperation();
        for (int i = 0; i < itineraries.size(); i++) {
            JsonObject itinerary = itineraries.get(i);
            if (itinerary != null) {
                String itineraryId = itinerary.getString(Introspection.JSONKeys.UUID);
                DBObject itineraryBs = (DBObject) JSON.parse(itinerary.toString());
                itineraryBs.put(DAOConstants.mongo_database_pk, itineraryId);
                op.insert(itineraryBs);
            }
        }
        return op.execute(WriteConcern.MAJORITY);
    }

    private WriteResult storeProposalRequirements(UUID proposalId, JsonArray requirements) {
        JsonObjectBuilder requirementsJs = Json.createObjectBuilder();
        requirementsJs.add(Introspection.JSONKeys.UUID, proposalId.toString());
        requirementsJs.add(Introspection.JSONKeys.REQUIREMENTS, requirements);

        DBObject requirementsBs = (DBObject) JSON.parse(requirementsJs.build().toString());
        requirementsBs.put(DAOConstants.mongo_database_pk, proposalId.toString());

        DBCollection proposalRequirementColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_REQUIREMENT_COLL_NAME);
        DBObject proposalQuery = QueryBuilder.start(DAOConstants.mongo_database_pk).is(proposalId.toString()).get();
        return proposalRequirementColls.update(proposalQuery, requirementsBs, true, false, WriteConcern.MAJORITY);
    }

    private WriteResult storeProposalBid(UUID proposalId, UUID accountId, JsonArray agencies) {
        JsonObjectBuilder bidJs = Json.createObjectBuilder();
        bidJs.add(Introspection.JSONKeys.UUID, proposalId.toString());
        bidJs.add(Introspection.JSONKeys.OWNER, accountId.toString());
        bidJs.add(Introspection.JSONKeys.AGENCIES, agencies);
        bidJs.add(Introspection.JSONKeys.DATE, Calendar.getInstance().getTimeInMillis());

        DBObject bidBs = (DBObject) JSON.parse(bidJs.build().toString());
        bidBs.put(DAOConstants.mongo_database_pk, proposalId.toString());

        DBCollection proposalBidColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_SUBMISSION_COLL_NAME);
        DBObject proposalQuery = QueryBuilder.start(DAOConstants.mongo_database_pk).is(proposalId.toString()).get();
        return proposalBidColls.update(proposalQuery, bidBs, true, false, WriteConcern.MAJORITY);
    }

    private boolean isTypeOf(String requirementId, Class<?> requirementClass) {
        if (TravelProposal.class.equals(requirementClass)) {
            DBCollection proposalColls = itineraryDb.getCollection(DAOConstants.PROPOSAL_COLL_NAME);
            return proposalColls.findOne(requirementId, ID_FIELD) != null;
        } else if (ItineraryRequirement.class.equals(requirementClass)) {
            DBCollection itineraryColls = itineraryDb.getCollection(DAOConstants.ITINERARY_COLL_NAME);
            return itineraryColls.findOne(requirementId, ID_FIELD) != null;
        } else if (TravelRequirement.class.equals(requirementClass)) {
            DBCollection requirementColls = itineraryDb.getCollection(DAOConstants.REQUIREMENT_COLL_NAME);
            return requirementColls.findOne(requirementId, ID_FIELD) != null;
        } else {
            return false;
        }
    }
}
