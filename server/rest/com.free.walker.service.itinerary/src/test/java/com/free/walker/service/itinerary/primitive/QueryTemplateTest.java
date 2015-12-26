package com.free.walker.service.itinerary.primitive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class QueryTemplateTest {
    @Test
    public void testIsValid4String() {
        assertFalse(QueryTemplate.isValid("missing_template"));
        assertTrue(QueryTemplate.isValid("product"));
        assertTrue(QueryTemplate.isValid("product_departure"));
        assertTrue(QueryTemplate.isValid("product_destination"));
        assertTrue(QueryTemplate.isValid("proposal_owner"));
        assertTrue(QueryTemplate.isValid("product_owner"));
        assertTrue(QueryTemplate.isValid("resource"));
    }

    @Test
    public void testIsValid4Int() {
        assertFalse(QueryTemplate.isValid(-1));
        assertFalse(QueryTemplate.isValid(0));
        assertTrue(QueryTemplate.isValid(1));
        assertTrue(QueryTemplate.isValid(2));
        assertTrue(QueryTemplate.isValid(3));
        assertTrue(QueryTemplate.isValid(4));
        assertTrue(QueryTemplate.isValid(5));
        assertTrue(QueryTemplate.isValid(6));
        assertFalse(QueryTemplate.isValid(7));
    }

    @Test
    public void testGetEnum() {
        assertEquals(1, QueryTemplate.PRODUCT_TEMPLATE.enumValue());
        assertEquals(2, QueryTemplate.PRODUCT_DEPARTURE.enumValue());
        assertEquals(3, QueryTemplate.PRODUCT_DESTINATION.enumValue());
        assertEquals(4, QueryTemplate.PRODUCT_OWNER.enumValue());
        assertEquals(5, QueryTemplate.PROPOSAL_OWNER.enumValue());
        assertEquals(6, QueryTemplate.RESOURCE.enumValue());
    }

    @Test
    public void testGetName() {
        assertEquals("product", QueryTemplate.PRODUCT_TEMPLATE.nameValue());
        assertEquals("product_departure", QueryTemplate.PRODUCT_DEPARTURE.nameValue());
        assertEquals("product_destination", QueryTemplate.PRODUCT_DESTINATION.nameValue());
        assertEquals("product_owner", QueryTemplate.PRODUCT_OWNER.nameValue());
        assertEquals("proposal_owner", QueryTemplate.PROPOSAL_OWNER.nameValue());
        assertEquals("resource", QueryTemplate.RESOURCE.nameValue());
    }
}
