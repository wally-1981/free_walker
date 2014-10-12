package com.free.walker.service.itinerary.dao;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DAOFactoryTest {
    @Test
    public void testDAOFactoryByInMemory() {
        TravelRequirementDAO travelRequirementDAO = DAOFactory
            .getTravelRequirementDAO(InMemoryTravelRequirementDAOImpl.class.getName());
        assertTrue(travelRequirementDAO instanceof InMemoryTravelRequirementDAOImpl);
        assertTrue(travelRequirementDAO.pingPersistence());
    }

    @Test
    public void testDAOFactoryByMySQL() {
        TravelRequirementDAO travelRequirementDAO = DAOFactory
            .getTravelRequirementDAO(MySQLTravelRequirementDAOImpl.class.getName());
        assertTrue(travelRequirementDAO instanceof MySQLTravelRequirementDAOImpl);
        assertTrue(travelRequirementDAO.pingPersistence());
    }
}
