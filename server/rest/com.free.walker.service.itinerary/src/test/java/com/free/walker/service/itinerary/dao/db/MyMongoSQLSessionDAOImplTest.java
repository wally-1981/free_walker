package com.free.walker.service.itinerary.dao.db;

import org.junit.Before;

import com.free.walker.service.itinerary.dao.AbstractSessionDAOImplTest;

public class MyMongoSQLSessionDAOImplTest extends AbstractSessionDAOImplTest {
    @Before
    public void before() {
        sessionDAO = new MyMongoSQLSessionDAOImpl();

        super.before();
    }
}
