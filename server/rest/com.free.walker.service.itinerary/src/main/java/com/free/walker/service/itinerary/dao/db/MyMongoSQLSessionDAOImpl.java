package com.free.walker.service.itinerary.dao.db;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.Conversation;
import com.free.walker.service.itinerary.dao.DAOConstants;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.util.MongoDbClientBuilder;
import com.free.walker.service.itinerary.util.SystemConfigUtil;
import com.ibm.icu.util.Calendar;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.QueryBuilder;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

public class MyMongoSQLSessionDAOImpl extends AbstractSessionDAO {
    private static final Logger LOG = LoggerFactory.getLogger(MyMongoSQLSessionDAOImpl.class);
    private static final DBObject ID_FIELD = new BasicDBObjectBuilder().add(DAOConstants.mongo_database_pk, true).get();

    private MongoClient mdbClient;
    private String sessionMongoDbUrl;

    private DB sessionDb;

    public MyMongoSQLSessionDAOImpl() {
        try {
            Properties config = SystemConfigUtil.getApplicationConfig();

            mdbClient = new MongoDbClientBuilder().build(config);
            sessionMongoDbUrl = StringUtils.join(DAOConstants.session_mongo_database,
                config.getProperty(DAOConstants.mongo_database_url));
            sessionDb = mdbClient.getDB(DAOConstants.session_mongo_database);
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
                throw new IllegalStateException();
            }
        }
    }

    public boolean pingPersistence() {
        boolean result = false;

        DBObject ping = new BasicDBObject("ping", "1");
        try {
            CommandResult cr = sessionDb.command(ping);
            if (!cr.ok()) {
                return false;
            } else {
                result = true;
            }
        } catch (RuntimeException e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_init_failure, sessionMongoDbUrl,
                MongoClient.class.getName()), e);
            return false;
        }

        return result;
    }

    public void update(Session session) throws UnknownSessionException {
        if (session == null) {
            throw new NullPointerException();
        }

        String sessionId = session.getId().toString();
        DBCollection sessionColls = sessionDb.getCollection(DAOConstants.SESSION_COLL_NAME);
        DBObject sessionIdBs = sessionColls.findOne(sessionId, ID_FIELD);
        if (sessionIdBs == null) {
            throw new UnknownSessionException("There is no session with id [" + sessionId + "]");
        } else {
            DBObject sessionQuery = QueryBuilder.start(DAOConstants.mongo_database_pk).is(sessionId).get();
            DBObject sessionBs = (DBObject) JSON.parse(new Conversation(session).toJSON().toString());
            sessionBs.put(DAOConstants.mongo_database_pk, sessionId);
            WriteResult wr = sessionColls.update(sessionQuery, sessionBs, false, false, WriteConcern.MAJORITY);
            LOG.debug("Session Updated: " + wr.toString());
        }
    }

    public void delete(Session session) {
        if (session == null) {
            throw new NullPointerException();
        }

        DBCollection sessionColls = sessionDb.getCollection(DAOConstants.SESSION_COLL_NAME);
        WriteResult wr = sessionColls.remove(new BasicDBObject(DAOConstants.mongo_database_pk,
            session.getId().toString()));

        LOG.debug("Session Deleted: " + wr.toString());
    }

    public Collection<Session> getActiveSessions() {
        List<Session> sessions = new ArrayList<Session>();
        Calendar since = Calendar.getInstance();
        since.add(Calendar.MINUTE, -30);

        DBCollection sessionColls = sessionDb.getCollection(DAOConstants.SESSION_COLL_NAME);
        BasicDBObject query1 = new BasicDBObject(Introspection.JSONKeys.SESSION_STOP, new BasicDBObject("$exists",
            false));
        BasicDBObject query2 = new BasicDBObject(Introspection.JSONKeys.ACCESS, new BasicDBObject("$lt",
            since.getTimeInMillis()));
        BasicDBObject sessionQuery = new BasicDBObject("$and", new BasicDBObject[] { query1, query2 });
        DBCursor sessionCursor = sessionColls.find(sessionQuery);
        try {
            Iterator<DBObject> SessionsBsIter = sessionCursor.iterator();
            while (SessionsBsIter.hasNext()) {
                DBObject sessionBs = SessionsBsIter.next();
                JsonObject session = Json.createReader(new StringReader(sessionBs.toString())).readObject();
                sessions.add(new Conversation().fromJSON(session).getSession());
            }
        } finally {
            sessionCursor.close();
        }
        LOG.debug("Active Sessions Retrieved, Size: " + sessions.size());
        return sessions;
    }

    protected Serializable doCreate(Session session) {
        if (session == null) {
            throw new NullPointerException();
        }

        assignSessionId(session, generateSessionId(session));

        DBCollection sessionColls = sessionDb.getCollection(DAOConstants.SESSION_COLL_NAME);
        DBObject sessionId = sessionColls.findOne(session.getId().toString(), ID_FIELD);
        if (sessionId != null) {
            LOG.debug("Session Existed: " + sessionId.toString());
            return sessionId.get(Introspection.JSONKeys.UUID).toString();
        } else {
            assignSessionId(session, generateSessionId(session));
            DBObject sessionBs = (DBObject) JSON.parse(new Conversation(session).toJSON().toString());
            sessionBs.put(DAOConstants.mongo_database_pk, session.getId().toString());
            WriteResult wr = sessionColls.insert(sessionBs, WriteConcern.MAJORITY);
            LOG.debug("Session Created: " + wr.toString());
            return session.getId();
        }
    }

    protected Session doReadSession(Serializable sessionId) {
        if (sessionId == null) {
            throw new NullPointerException();
        }

        DBCollection sessionColls = sessionDb.getCollection(DAOConstants.SESSION_COLL_NAME);
        DBObject sessionBs = sessionColls.findOne(sessionId.toString());
        Session session = null;
        if (sessionBs != null) {
            JsonObject sessionJs = Json.createReader(new StringReader(sessionBs.toString())).readObject();
            session = new Conversation().fromJSON(sessionJs).getSession();
        }

        LOG.debug("Session Retrieved: " + session == null ? null : session.getId().toString());
        return session;
    }

}
