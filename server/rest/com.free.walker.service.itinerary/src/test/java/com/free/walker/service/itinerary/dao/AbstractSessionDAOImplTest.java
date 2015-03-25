package com.free.walker.service.itinerary.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public abstract class AbstractSessionDAOImplTest {
    protected SessionDAO sessionDAO;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private SimpleSession simpleSession;

    @Before
    public void before() {
        Calendar sessionStart = Calendar.getInstance();
        sessionStart.add(Calendar.MINUTE, -1000000);

        Calendar sessionLastAccess = Calendar.getInstance();
        sessionLastAccess.add(Calendar.MINUTE, -31);

        simpleSession = new SimpleSession("88.77.66.55");
        simpleSession.setStartTimestamp(sessionStart.getTime());
        simpleSession.setTimeout(36000000);
        simpleSession.setLastAccessTime(sessionLastAccess.getTime());
        simpleSession.setAttribute("KA", "VA");
        simpleSession.setAttribute("KB", "VB");
    }

    @Test
    public void testCreateSession() {
        Serializable sessionId = sessionDAO.create(simpleSession);
        assertNotNull(sessionId);
    }

    @Test
    public void testReadSession() {
        Serializable sessionId = sessionDAO.create(simpleSession);
        Session session = sessionDAO.readSession(sessionId);
        assertEquals(simpleSession, session);
    }

    @Test
    public void testUpdateSession() {
        Serializable sessionId = sessionDAO.create(simpleSession);
        Session session = sessionDAO.readSession(sessionId);
        session.setTimeout(72000000);
        session.setAttribute("KA", "VAA");
        sessionDAO.update(session);
        Session updatedSession = sessionDAO.readSession(session.getId());
        assertEquals(session, updatedSession);
    }

    @Test
    public void testGetActiveSessions() {
        int originalSize = sessionDAO.getActiveSessions().size();
        sessionDAO.create(simpleSession);
        Collection<Session> activeSessions = sessionDAO.getActiveSessions();
        assertTrue(activeSessions.contains(simpleSession));
        assertEquals(originalSize + 1, activeSessions.size());
    }

    @Test
    public void testDeleteSession() {
        int originalSize = sessionDAO.getActiveSessions().size();
        Serializable sessionId = sessionDAO.create(simpleSession);
        sessionDAO.delete(sessionDAO.readSession(sessionId));
        Collection<Session> activeSessions = sessionDAO.getActiveSessions();
        assertEquals(originalSize, activeSessions.size());
    }

    @After
    public void after() {
        sessionDAO.delete(simpleSession);
    }
}
