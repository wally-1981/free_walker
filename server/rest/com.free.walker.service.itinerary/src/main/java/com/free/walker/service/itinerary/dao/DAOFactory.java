package com.free.walker.service.itinerary.dao;

import com.free.walker.service.itinerary.dao.memo.InMemoryTravelBasicDAOImpl;
import com.free.walker.service.itinerary.dao.memo.InMemoryTravelRequirementDAOImpl;
import com.free.walker.service.itinerary.dao.mysql.MySQLTravelBasicDAOImpl;
import com.free.walker.service.itinerary.dao.mysql.MyMongoSQLTravelRequirementDAOImpl;

public class DAOFactory {
    public static TravelRequirementDAO getTravelRequirementDAO(String className) {
        if (InMemoryTravelRequirementDAOImpl.class.getName().equals(className)) {
            return InMemoryTravelRequirementDAOImpl.getInstance();
        } else if (MyMongoSQLTravelRequirementDAOImpl.class.getName().equals(className)) {
            return MyMongoSQLTravelRequirementDAOImpl.getInstance();
        } else {
            return null;
        }
    }

    public static TravelBasicDAO getTravelBasicDAO(String className) {
        if (InMemoryTravelBasicDAOImpl.class.getName().equals(className)) {
            return InMemoryTravelBasicDAOImpl.getInstance();
        } else if (MySQLTravelBasicDAOImpl.class.getName().equals(className)) {
            return MySQLTravelBasicDAOImpl.getInstance();
        } else {
            return null;
        }
    }
}
