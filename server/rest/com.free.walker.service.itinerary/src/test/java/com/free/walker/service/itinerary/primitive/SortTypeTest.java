package com.free.walker.service.itinerary.primitive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SortTypeTest {
    @Test
    public void testIsValid4String() {
        assertFalse(QueryTemplate.isValid("wrong_type"));
        assertTrue(SortType.isValid("double"));
        assertTrue(SortType.isValid("long"));
        assertTrue(SortType.isValid("integer"));
        assertTrue(SortType.isValid("float"));
    }

    @Test
    public void testIsValid4Int() {
        assertFalse(SortType.isValid(-1));
        assertFalse(SortType.isValid(0));
        assertTrue(SortType.isValid(1));
        assertTrue(SortType.isValid(2));
        assertTrue(SortType.isValid(3));
        assertTrue(SortType.isValid(4));
        assertFalse(SortType.isValid(5));
    }

    @Test
    public void testGetEnum() {
        assertEquals(4, SortType.DOUBLE.enumValue());
        assertEquals(3, SortType.FLOAT.enumValue());
        assertEquals(2, SortType.INT.enumValue());
        assertEquals(1, SortType.LONG.enumValue());
    }

    @Test
    public void testGetName() {
        assertEquals("double", SortType.DOUBLE.nameValue());
        assertEquals("float", SortType.FLOAT.nameValue());
        assertEquals("integer", SortType.INT.nameValue());
        assertEquals("long", SortType.LONG.nameValue());
    }
}
