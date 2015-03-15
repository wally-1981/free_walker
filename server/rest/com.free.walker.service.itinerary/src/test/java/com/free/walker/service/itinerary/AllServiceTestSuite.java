package com.free.walker.service.itinerary;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    AllDevoServiceTestSuite.class,
    AllDevoSecureServiceTestSuite.class,
    AllProdServiceTestSuite.class,
    AllProdSecureServiceTestSuite.class,
})
public class AllServiceTestSuite {}
