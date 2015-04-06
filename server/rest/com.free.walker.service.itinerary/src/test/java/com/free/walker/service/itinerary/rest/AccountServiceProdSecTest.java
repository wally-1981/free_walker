package com.free.walker.service.itinerary.rest;

import org.junit.Before;

public class AccountServiceProdSecTest extends AbstractAccountServiceTest {
    @Before
    public void before() {
        accountServiceUrlStr = getProdSecureServiceUrl(AccountService.class);
        super.before();
    }
}
