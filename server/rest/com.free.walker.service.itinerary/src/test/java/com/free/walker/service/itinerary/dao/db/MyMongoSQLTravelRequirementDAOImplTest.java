package com.free.walker.service.itinerary.dao.db;

import org.junit.After;
import org.junit.Before;

import com.free.walker.service.itinerary.dao.AbstractTravelRequirementDAOImplTest;
import com.free.walker.service.itinerary.dao.DAOFactory;
import com.free.walker.service.itinerary.dao.db.MyMongoSQLTravelRequirementDAOImpl;
import com.free.walker.service.itinerary.infra.PlatformInitializer;

public class MyMongoSQLTravelRequirementDAOImplTest extends AbstractTravelRequirementDAOImplTest {
    @Before
    public void before() {
        PlatformInitializer.init();

        travelRequirementDAO = DAOFactory.getTravelRequirementDAO(MyMongoSQLTravelRequirementDAOImpl.class.getName());

        super.before();
    }

    @After
    public void after() {
        super.after();
    }
}
