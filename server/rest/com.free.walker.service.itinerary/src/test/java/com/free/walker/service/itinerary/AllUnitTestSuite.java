package com.free.walker.service.itinerary;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.free.walker.service.itinerary.basic.AccountTest;
import com.free.walker.service.itinerary.basic.CityTest;
import com.free.walker.service.itinerary.basic.ContinentTest;
import com.free.walker.service.itinerary.basic.ConversationTest;
import com.free.walker.service.itinerary.basic.CountryTest;
import com.free.walker.service.itinerary.basic.ProvinceTest;
import com.free.walker.service.itinerary.basic.ResortTest;
import com.free.walker.service.itinerary.basic.SearchCriteriaTest;
import com.free.walker.service.itinerary.basic.TravelLocationTest;
import com.free.walker.service.itinerary.dao.DAOFactoryTest;
import com.free.walker.service.itinerary.dao.TravelBasicDAOImplTest;
import com.free.walker.service.itinerary.dao.db.MyMongoSQLAccountDAOImplTest;
import com.free.walker.service.itinerary.dao.db.MyMongoSQLSessionDAOImplTest;
import com.free.walker.service.itinerary.dao.db.MyMongoSQLTravelProductDAOImplTest;
import com.free.walker.service.itinerary.dao.db.MyMongoSQLTravelRequirementDAOImplTest;
import com.free.walker.service.itinerary.dao.memo.InMemorySessionDAOImplTest;
import com.free.walker.service.itinerary.dao.memo.InMemoryTravelProductDAOImplTest;
import com.free.walker.service.itinerary.dao.memo.InMemoryTravelRequirementDAOImplTest;
import com.free.walker.service.itinerary.primitive.AccountTypeTest;
import com.free.walker.service.itinerary.primitive.IntrospectionTest;
import com.free.walker.service.itinerary.primitive.QueryTemplateTest;
import com.free.walker.service.itinerary.primitive.SortTypeTest;
import com.free.walker.service.itinerary.product.BiddingTest;
import com.free.walker.service.itinerary.product.TravelProductTest;
import com.free.walker.service.itinerary.req.HotelRequirementTest;
import com.free.walker.service.itinerary.req.ItineraryRequirementTest;
import com.free.walker.service.itinerary.req.ResortRequirementTest;
import com.free.walker.service.itinerary.req.TrafficRequirementTest;
import com.free.walker.service.itinerary.req.TrafficToolSeatRequirementTest;
import com.free.walker.service.itinerary.req.TravelProposalTest;
import com.free.walker.service.itinerary.req.TravelRequirementTest;
import com.free.walker.service.itinerary.util.JsonObjectHelperTest;
import com.free.walker.service.itinerary.util.JsonObjectUtilTest;
import com.free.walker.service.itinerary.util.UriUtilTest;
import com.free.walker.service.itinerary.util.UuidUtilTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    LocalMessagesTest.class,
    IntrospectionTest.class,
    QueryTemplateTest.class,
    SortTypeTest.class,
    AccountTypeTest.class,

    JsonObjectHelperTest.class,
    UuidUtilTest.class,
    UriUtilTest.class,
    JsonObjectUtilTest.class,

    AccountTest.class,
    ContinentTest.class,
    CountryTest.class,
    ProvinceTest.class,
    CityTest.class,
    ResortTest.class,
    BiddingTest.class,
    TravelLocationTest.class,
    SearchCriteriaTest.class,
    ConversationTest.class,

    DAOFactoryTest.class,
    TravelBasicDAOImplTest.class,
    InMemorySessionDAOImplTest.class,
    MyMongoSQLSessionDAOImplTest.class,
    InMemoryTravelRequirementDAOImplTest.class,
    MyMongoSQLTravelRequirementDAOImplTest.class,
    InMemoryTravelProductDAOImplTest.class,
    MyMongoSQLTravelProductDAOImplTest.class,
    MyMongoSQLAccountDAOImplTest.class,

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
