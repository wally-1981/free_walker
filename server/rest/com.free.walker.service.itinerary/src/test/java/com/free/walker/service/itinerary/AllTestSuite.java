package com.free.walker.service.itinerary;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.free.walker.service.itinerary.dao.DAOFactoryTest;
import com.free.walker.service.itinerary.dao.InMemoryTravelRequirementDAOImplTest;
import com.free.walker.service.itinerary.primitive.IntrospectionTest;
import com.free.walker.service.itinerary.product.TravelProductTest;
import com.free.walker.service.itinerary.req.HotelRequirementTest;
import com.free.walker.service.itinerary.req.ItineraryRequirementTest;
import com.free.walker.service.itinerary.req.ResortRequirementTest;
import com.free.walker.service.itinerary.req.TrafficRequirementTest;
import com.free.walker.service.itinerary.req.TrafficToolSeatRequirementTest;
import com.free.walker.service.itinerary.req.TravelProposalTest;
import com.free.walker.service.itinerary.req.TravelRequirementTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    LocalMessagesTest.class,
    IntrospectionTest.class,

    DAOFactoryTest.class,
    InMemoryTravelRequirementDAOImplTest.class,

    HotelRequirementTest.class,
    ItineraryRequirementTest.class,
    ResortRequirementTest.class,
    TrafficRequirementTest.class,
    TrafficToolSeatRequirementTest.class,

    TravelProposalTest.class,

    TravelProductTest.class,
    TravelRequirementTest.class,
})
public class AllTestSuite {}
