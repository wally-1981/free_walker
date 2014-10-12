package com.free.walker.service.itinerary.dao;


public class DAOFactory {
    public static TravelRequirementDAO getTravelRequirementDAO(String className) {
        if (InMemoryTravelRequirementDAOImpl.class.getName().equals(className)) {
            return InMemoryTravelRequirementDAOImpl.getInstance();
        } else if (MySQLTravelRequirementDAOImpl.class.getName().equals(className)) {
            return MySQLTravelRequirementDAOImpl.getInstance();
        } else {
            return null;
        }
    }
}
