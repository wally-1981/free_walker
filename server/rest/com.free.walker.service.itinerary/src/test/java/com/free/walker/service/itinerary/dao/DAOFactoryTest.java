package com.free.walker.service.itinerary.dao;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.free.walker.service.itinerary.dao.memo.InMemoryTravelBasicDAOImpl;
import com.free.walker.service.itinerary.dao.memo.InMemoryTravelRequirementDAOImpl;
import com.free.walker.service.itinerary.dao.mysql.MySQLTravelBasicDAOImpl;
import com.free.walker.service.itinerary.dao.mysql.MySQLTravelRequirementDAOImpl;

public class DAOFactoryTest {
    @Test
    public void testRequirementDAOFactoryByInMemory() {
        TravelRequirementDAO travelRequirementDAO = DAOFactory
            .getTravelRequirementDAO(InMemoryTravelRequirementDAOImpl.class.getName());
        assertTrue(travelRequirementDAO instanceof InMemoryTravelRequirementDAOImpl);
        assertTrue(travelRequirementDAO.pingPersistence());
    }

    @Test
    public void testRequirementDAOFactoryByMySQL() {
        TravelRequirementDAO travelRequirementDAO = DAOFactory
            .getTravelRequirementDAO(MySQLTravelRequirementDAOImpl.class.getName());
        assertTrue(travelRequirementDAO instanceof MySQLTravelRequirementDAOImpl);
        assertTrue(travelRequirementDAO.pingPersistence());
    }

    @Test
    public void testBasicDAOFactoryByInMemory() {
        TravelBasicDAO travelBasicDAO = DAOFactory
            .getTravelBasicDAO(InMemoryTravelBasicDAOImpl.class.getName());
        assertTrue(travelBasicDAO instanceof InMemoryTravelBasicDAOImpl);
        assertTrue(travelBasicDAO.pingPersistence());
    }

    @Test
    public void testBasicDAOFactoryByMySQL() {
        TravelBasicDAO travelBasicDAO = DAOFactory
            .getTravelBasicDAO(MySQLTravelBasicDAOImpl.class.getName());
        assertTrue(travelBasicDAO instanceof MySQLTravelBasicDAOImpl);
        assertTrue(travelBasicDAO.pingPersistence());
    }
}
