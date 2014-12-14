package com.free.walker.service.itinerary.dao;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.free.walker.service.itinerary.dao.db.MyMongoSQLTravelProductDAOImpl;
import com.free.walker.service.itinerary.dao.db.MyMongoSQLTravelRequirementDAOImpl;
import com.free.walker.service.itinerary.dao.db.MySQLTravelBasicDAOImpl;
import com.free.walker.service.itinerary.dao.memo.InMemoryTravelProductDAOImpl;
import com.free.walker.service.itinerary.dao.memo.InMemoryTravelRequirementDAOImpl;

public class DAOFactoryTest {
    @Test
    public void testBasicDAOFactoryByDb() {
        TravelBasicDAO travelBasicDAO = DAOFactory.getTravelBasicDAO();
        assertTrue(travelBasicDAO instanceof MySQLTravelBasicDAOImpl);
        assertTrue(travelBasicDAO.pingPersistence());
    }

    @Test
    public void testRequirementDAOFactoryByInMemory() {
        TravelRequirementDAO travelRequirementDAO = DAOFactory
            .getTravelRequirementDAO(InMemoryTravelRequirementDAOImpl.class.getName());
        assertTrue(travelRequirementDAO instanceof InMemoryTravelRequirementDAOImpl);
        assertTrue(travelRequirementDAO.pingPersistence());
    }

    @Test
    public void testRequirementDAOFactoryByDb() {
        TravelRequirementDAO travelRequirementDAO = DAOFactory
            .getTravelRequirementDAO(MyMongoSQLTravelRequirementDAOImpl.class.getName());
        assertTrue(travelRequirementDAO instanceof MyMongoSQLTravelRequirementDAOImpl);
        assertTrue(travelRequirementDAO.pingPersistence());
    }

    @Test
    public void testProductDAOFactoryByInMemory() {
        TravelProductDAO travelProductDAO = DAOFactory
            .getTravelProductDAO(InMemoryTravelProductDAOImpl.class.getName());
        assertTrue(travelProductDAO instanceof InMemoryTravelProductDAOImpl);
        assertTrue(travelProductDAO.pingPersistence());
    }

    @Test
    public void testProductDAOFactoryByDb() {
        TravelProductDAO travelProductDAO = DAOFactory.getTravelProductDAO(MyMongoSQLTravelProductDAOImpl.class
            .getName());
        assertTrue(travelProductDAO instanceof MyMongoSQLTravelProductDAOImpl);
        assertTrue(travelProductDAO.pingPersistence());
    }
}
