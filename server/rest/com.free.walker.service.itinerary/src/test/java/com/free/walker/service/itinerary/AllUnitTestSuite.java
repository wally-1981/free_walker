package com.free.walker.service.itinerary;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.free.walker.service.itinerary.basic.CityTest;
import com.free.walker.service.itinerary.basic.ContinentTest;
import com.free.walker.service.itinerary.basic.CountryTest;
import com.free.walker.service.itinerary.basic.ProvinceTest;
import com.free.walker.service.itinerary.basic.ResortTest;
import com.free.walker.service.itinerary.dao.DAOFactoryTest;
import com.free.walker.service.itinerary.dao.memo.InMemoryTravelRequirementDAOImplTest;
import com.free.walker.service.itinerary.dao.memo.MyMongoSQLTravelRequirementDAOImplTest;
import com.free.walker.service.itinerary.primitive.IntrospectionTest;
import com.free.walker.service.itinerary.product.TravelProductTest;
import com.free.walker.service.itinerary.req.HotelRequirementTest;
import com.free.walker.service.itinerary.req.ItineraryRequirementTest;
import com.free.walker.service.itinerary.req.ResortRequirementTest;
import com.free.walker.service.itinerary.req.TrafficRequirementTest;
import com.free.walker.service.itinerary.req.TrafficToolSeatRequirementTest;
import com.free.walker.service.itinerary.req.TravelProposalTest;
import com.free.walker.service.itinerary.req.TravelRequirementTest;
import com.free.walker.service.itinerary.util.JsonObjectHelperTest;
import com.free.walker.service.itinerary.util.UuidUtilTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    LocalMessagesTest.class,
    IntrospectionTest.class,

    JsonObjectHelperTest.class,
    UuidUtilTest.class,

    ContinentTest.class,
    CountryTest.class,
    ProvinceTest.class,
    CityTest.class,
    ResortTest.class,

    DAOFactoryTest.class,
    InMemoryTravelRequirementDAOImplTest.class,
    MyMongoSQLTravelRequirementDAOImplTest.class,

    HotelRequirementTest.class,
    ResortRequirementTest.class,
    TrafficRequirementTest.class,
    TrafficToolSeatRequirementTest.class,

    ItineraryRequirementTest.class,
    TravelProposalTest.class,

    TravelProductTest.class,
    TravelRequirementTest.class,
})
public class AllUnitTestSuite {}
