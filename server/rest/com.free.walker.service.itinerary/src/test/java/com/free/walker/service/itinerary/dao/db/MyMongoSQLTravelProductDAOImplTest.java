package com.free.walker.service.itinerary.dao.db;

import org.junit.After;
import org.junit.Before;

import com.free.walker.service.itinerary.dao.AbstractTravelProductDAOImplTest;
import com.free.walker.service.itinerary.dao.DAOFactory;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.infra.PlatformInitializer;

public class MyMongoSQLTravelProductDAOImplTest extends AbstractTravelProductDAOImplTest {
    @Before
    public void before() throws InvalidTravelReqirementException, DatabaseAccessException {
        PlatformInitializer.init();

        travelProductDAO = DAOFactory.getTravelProductDAO(MyMongoSQLTravelProductDAOImpl.class.getName());
        travelRequirementDAO = DAOFactory.getTravelRequirementDAO(MyMongoSQLTravelRequirementDAOImpl.class.getName());

        super.before();
    }

    @After
    public void after() {
        super.after();
    }
}
