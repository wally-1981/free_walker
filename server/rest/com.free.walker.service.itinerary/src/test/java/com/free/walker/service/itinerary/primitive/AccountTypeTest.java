package com.free.walker.service.itinerary.primitive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AccountTypeTest {
    @Test
    public void testIsTouristAccount() {
        assertFalse(AccountType.isTouristAccount(AccountType.ADMIN.ordinal()));
        assertTrue(AccountType.isTouristAccount(AccountType.MASTER.ordinal()));
        assertFalse(AccountType.isTouristAccount(AccountType.AGENCY.ordinal()));
        assertTrue(AccountType.isTouristAccount(AccountType.WeChat.ordinal()));
        assertTrue(AccountType.isTouristAccount(AccountType.QQ.ordinal()));
        assertTrue(AccountType.isTouristAccount(AccountType.AliPay.ordinal()));
        assertTrue(AccountType.isTouristAccount(AccountType.WeiBo.ordinal()));
        assertFalse(AccountType.isTouristAccount(Integer.MAX_VALUE));
    }

    @Test
    public void testValueOf() {
        assertEquals(AccountType.ADMIN, AccountType.valueOf(AccountType.ADMIN.ordinal()));
        assertEquals(AccountType.MASTER, AccountType.valueOf(AccountType.MASTER.ordinal()));
        assertEquals(AccountType.AGENCY, AccountType.valueOf(AccountType.AGENCY.ordinal()));
        assertEquals(AccountType.WeChat, AccountType.valueOf(AccountType.WeChat.ordinal()));
        assertEquals(AccountType.QQ, AccountType.valueOf(AccountType.QQ.ordinal()));
        assertEquals(AccountType.AliPay, AccountType.valueOf(AccountType.AliPay.ordinal()));
        assertEquals(AccountType.WeiBo, AccountType.valueOf(AccountType.WeiBo.ordinal()));
        assertNull(AccountType.valueOf(Integer.MAX_VALUE));
        assertNull(AccountType.valueOf(Integer.MIN_VALUE));
    }
}
