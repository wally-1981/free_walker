package com.free.walker.service.itinerary.dao.db;

import org.junit.Before;

import com.free.walker.service.itinerary.dao.AbstractAccountDAOImplTest;
import com.free.walker.service.itinerary.dao.DAOFactory;

public class MyMongoSQLAccountDAOImplTest extends AbstractAccountDAOImplTest {
    @Before
    public void before() {
        accountDAO = DAOFactory.getAccountDAO(MyMongoSQLAccountDAOImpl.class.getName());

        super.before();
    }
}
