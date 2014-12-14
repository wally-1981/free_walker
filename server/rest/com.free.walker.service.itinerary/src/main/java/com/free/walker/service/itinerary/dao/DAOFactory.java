package com.free.walker.service.itinerary.dao;

import com.free.walker.service.itinerary.dao.db.MyMongoSQLTravelProductDAOImpl;
import com.free.walker.service.itinerary.dao.db.MyMongoSQLTravelRequirementDAOImpl;
import com.free.walker.service.itinerary.dao.db.MySQLTravelBasicDAOImpl;
import com.free.walker.service.itinerary.dao.memo.InMemoryTravelProductDAOImpl;
import com.free.walker.service.itinerary.dao.memo.InMemoryTravelRequirementDAOImpl;

public class DAOFactory {
    public static TravelBasicDAO getTravelBasicDAO() {
        return MySQLTravelBasicDAOImpl.getInstance();
    }

    public static TravelRequirementDAO getTravelRequirementDAO(String className) {
        if (InMemoryTravelRequirementDAOImpl.class.getName().equals(className)) {
            return InMemoryTravelRequirementDAOImpl.getInstance();
        } else if (MyMongoSQLTravelRequirementDAOImpl.class.getName().equals(className)) {
            return MyMongoSQLTravelRequirementDAOImpl.getInstance();
        } else {
            return null;
        }
    }

    public static TravelProductDAO getTravelProductDAO(String className) {
        if (InMemoryTravelProductDAOImpl.class.getName().equals(className)) {
            return InMemoryTravelProductDAOImpl.getInstance();
        } else if (MyMongoSQLTravelProductDAOImpl.class.getName().equals(className)) {
            return MyMongoSQLTravelProductDAOImpl.getInstance();
        } else {
            return null;
        }
    }
}
