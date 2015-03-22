package com.free.walker.service.itinerary.dao.db;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyMongoSQLSessionDAOImpl extends AbstractSessionDAO {
    private static final Logger LOG = LoggerFactory.getLogger(MyMongoSQLSessionDAOImpl.class);
    private Map<Serializable, Session> sessions = new HashMap<Serializable, Session>();

    @Override
    public void update(Session session) throws UnknownSessionException {
        LOG.debug("Session Update: " + session.getId().toString());
        sessions.put(session.getId(), session);
    }

    @Override
    public void delete(Session session) {
        LOG.debug("Session Delete: " + session.getId().toString());
        sessions.remove(session.getId());
    }

    @Override
    public Collection<Session> getActiveSessions() {
        LOG.debug("Active Session Count: " + sessions.size());
        return sessions.values();
    }

    @Override
    protected Serializable doCreate(Session session) {
        assignSessionId(session, generateSessionId(session));
        LOG.debug("Session Create: " + session.getId().toString());
        sessions.put(session.getId(), session);
        return session.getId();
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        LOG.debug("Session Retrieve: " +sessionId.toString());
        return sessions.get(sessionId);
    }

}
