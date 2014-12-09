package com.free.walker.service.itinerary.rest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.free.walker.service.itinerary.exp.InvalidTravelProductException;

public abstract class AbstractProductServiceTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() throws InvalidTravelProductException {
        ;
    }

    @Test
    public void testAll() throws InvalidTravelProductException {
        ;
    }

    protected abstract String getServiceUrl();
}
