package com.free.walker.service.itinerary.dao.memo;

import org.junit.After;
import org.junit.Before;

import com.free.walker.service.itinerary.dao.AbstractTravelProductDAOImplTest;
import com.free.walker.service.itinerary.dao.DAOFactory;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.infra.PlatformInitializer;

public class InMemoryTravelProductDAOImplTest extends AbstractTravelProductDAOImplTest {
    private InMemoryTravelProductDAOImpl memoImpl;

    @Before
    public void before() throws InvalidTravelReqirementException, DatabaseAccessException {
        PlatformInitializer.init();

        travelProductDAO = DAOFactory.getTravelProductDAO(InMemoryTravelProductDAOImpl.class.getName());
        travelRequirementDAO = DAOFactory.getTravelRequirementDAO(InMemoryTravelRequirementDAOImpl.class.getName());
        memoImpl = ((InMemoryTravelProductDAOImpl) travelProductDAO);

        super.before();
    }

    @After
    public void after() {
        super.after();

        memoImpl.travelProducts.clear();
        memoImpl.travelProductItems.clear();
        memoImpl.travelProductBiddings.clear();
    }
}
