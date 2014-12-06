package com.free.walker.service.itinerary.dao.memo;

import org.junit.After;
import org.junit.Before;

import com.free.walker.service.itinerary.dao.AbstractTravelRequirementDAOImplTest;
import com.free.walker.service.itinerary.dao.DAOFactory;
import com.free.walker.service.itinerary.infra.PlatformInitializer;

public class InMemoryTravelRequirementDAOImplTest extends AbstractTravelRequirementDAOImplTest {
    private InMemoryTravelRequirementDAOImpl memoImpl;

    @Before
    public void before() {
        PlatformInitializer.init();

        travelRequirementDAO = DAOFactory.getTravelRequirementDAO(InMemoryTravelRequirementDAOImpl.class.getName());
        memoImpl = ((InMemoryTravelRequirementDAOImpl) travelRequirementDAO);

        super.before();
    }

    @After
    public void after() {
        super.after();

        memoImpl.travelProposals.clear();
        memoImpl.travelRequirements.clear();
    }
}
