package com.free.walker.service.itinerary.primitive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class QueryTemplateTest {
    @Test
    public void testIsValid4String() {
        assertFalse(QueryTemplate.isValid("missing_template"));
        assertTrue(QueryTemplate.isValid("test_template"));
        assertTrue(QueryTemplate.isValid("product_departure"));
        assertTrue(QueryTemplate.isValid("product_destination"));
    }

    @Test
    public void testIsValid4Int() {
        assertFalse(QueryTemplate.isValid(-1));
        assertFalse(QueryTemplate.isValid(0));
        assertTrue(QueryTemplate.isValid(1));
        assertTrue(QueryTemplate.isValid(2));
        assertTrue(QueryTemplate.isValid(3));
        assertFalse(QueryTemplate.isValid(4));
    }

    @Test
    public void testGetEnum() {
        assertEquals(1, QueryTemplate.TEST_TEMPLACE.enumValue());
        assertEquals(2, QueryTemplate.PRODUCT_DEPARTURE.enumValue());
        assertEquals(3, QueryTemplate.PRODUCT_DESTINATION.enumValue());
    }

    @Test
    public void testGetName() {
        assertEquals("test_template", QueryTemplate.TEST_TEMPLACE.nameValue());
        assertEquals("product_departure", QueryTemplate.PRODUCT_DEPARTURE.nameValue());
        assertEquals("product_destination", QueryTemplate.PRODUCT_DESTINATION.nameValue());
    }
}
