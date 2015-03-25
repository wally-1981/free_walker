package com.free.walker.service.itinerary.dao.memo;

import org.junit.Before;

import com.free.walker.service.itinerary.dao.AbstractSessionDAOImplTest;

public class InMemorySessionDAOImplTest extends AbstractSessionDAOImplTest {
    @Before
    public void before() {
        sessionDAO = new InMemorySessionDAOImpl();

        super.before();
    }
}
