package com.free.walker.service.itinerary.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;

public class UuidUtilTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testIsCmpUuidStr() {
        assertFalse(UuidUtil.isCmpUuidStr("02515d41-f141-4175-9a11-9e68b9cfe687"));
        assertTrue(UuidUtil.isCmpUuidStr("02515d41f14141759a119e68b9cfe687"));
        assertFalse(UuidUtil.isCmpUuidStr("02515d41f141-4175-9a11-9e68b9cfe687"));
        assertFalse(UuidUtil.isCmpUuidStr("02515d41-f1414175-9a11-9e68b9cfe687"));
        assertFalse(UuidUtil.isCmpUuidStr("02515d41-f141-41759a11-9e68b9cfe687"));
        assertFalse(UuidUtil.isCmpUuidStr("02515d41-f141-4175-9a119e68b9cfe687"));
    }

    @Test
    public void testFromUuidStr() throws InvalidTravelReqirementException {
        assertNotNull(UuidUtil.fromUuidStr("02515d41-f141-4175-9a11-9e68b9cfe687"));

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage("02515d41f14141759a119e68b9cfe687");
        assertNotNull(UuidUtil.fromUuidStr("02515d41f14141759a119e68b9cfe687"));

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage("02515d41-f141-41759a11-9e68b9cfe687");
        assertNotNull(UuidUtil.fromUuidStr("02515d41-f141-41759a11-9e68b9cfe687"));
    }

    @Test
    public void testFromCmpUuidStr() throws InvalidTravelReqirementException {
        thrown.expect(InvalidTravelReqirementException.class);
        assertNotNull(UuidUtil.fromCmpUuidStr("02515d41-f141-4175-9a11-9e68b9cfe687"));

        assertNotNull(UuidUtil.fromCmpUuidStr("02515d41f14141759a119e68b9cfe687"));

        thrown.expect(InvalidTravelReqirementException.class);
        assertNotNull(UuidUtil.fromUuidStr("02515d41-f141-41759a11-9e68b9cfe687"));
    }
}
