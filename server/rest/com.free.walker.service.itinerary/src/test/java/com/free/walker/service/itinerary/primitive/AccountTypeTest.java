package com.free.walker.service.itinerary.primitive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.AllPermission;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AccountTypeTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testAccountTypeValueOf() {
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

    @Test
    public void testRoleValueOf() {
        assertEquals(AccountType.Role.ROOT, AccountType.Role.valueOf(AccountType.Role.ROOT.ordinal()));
        assertEquals(AccountType.Role.ADMIN, AccountType.Role.valueOf(AccountType.Role.ADMIN.ordinal()));
        assertEquals(AccountType.Role.AGENCY, AccountType.Role.valueOf(AccountType.Role.AGENCY.ordinal()));
        assertEquals(AccountType.Role.CUSTOMER, AccountType.Role.valueOf(AccountType.Role.CUSTOMER.ordinal()));
        assertNull(AccountType.Role.valueOf(Integer.MAX_VALUE));
        assertNull(AccountType.Role.valueOf(Integer.MIN_VALUE));
    }

    @Test
    public void testIsTouristAccount() {
        assertFalse(AccountType.isTouristAccount(AccountType.ADMIN));
        assertTrue(AccountType.isTouristAccount(AccountType.MASTER));
        assertFalse(AccountType.isTouristAccount(AccountType.AGENCY));
        assertTrue(AccountType.isTouristAccount(AccountType.WeChat));
        assertTrue(AccountType.isTouristAccount(AccountType.QQ));
        assertTrue(AccountType.isTouristAccount(AccountType.AliPay));
        assertTrue(AccountType.isTouristAccount(AccountType.WeiBo));
    }

    @Test
    public void testGetDefaultRole() {
        assertEquals(AccountType.Role.ADMIN, AccountType.getDefaultRole(AccountType.ADMIN));
        assertEquals(AccountType.Role.AGENCY, AccountType.getDefaultRole(AccountType.AGENCY));
        assertEquals(AccountType.Role.CUSTOMER, AccountType.getDefaultRole(AccountType.MASTER));
        assertEquals(AccountType.Role.CUSTOMER, AccountType.getDefaultRole(AccountType.WeChat));
        assertEquals(AccountType.Role.CUSTOMER, AccountType.getDefaultRole(AccountType.QQ));
        assertEquals(AccountType.Role.CUSTOMER, AccountType.getDefaultRole(AccountType.AliPay));
        assertEquals(AccountType.Role.CUSTOMER, AccountType.getDefaultRole(AccountType.WeiBo));
    }

    @Test
    public void testGetDefaultPermission() {
        assertEquals(1, AccountType.Role.getDefaultPermissions(AccountType.Role.ROOT).length);
        assertTrue(AccountType.Role.getDefaultPermissions(AccountType.Role.ROOT)[0] instanceof AllPermission);

        Assert.assertArrayEquals(AccountType.Permission.ADMIN_PERMISSIONS,
            AccountType.Role.getDefaultPermissions(AccountType.Role.ADMIN));
        Assert.assertArrayEquals(AccountType.Permission.AGENCY_PERMISSIONS,
            AccountType.Role.getDefaultPermissions(AccountType.Role.AGENCY));
        Assert.assertArrayEquals(AccountType.Permission.CUSTOMER_PERMISSIONS,
            AccountType.Role.getDefaultPermissions(AccountType.Role.CUSTOMER));
    }

    @Test
    public void testValueOfPermissions() {
        String[] permissionStrings = new String[] { "ManagePlatform", "GrabProposal" };
        List<Permission> permissions = AccountType.Permission.valueOfPermissions(permissionStrings);
        assertNotNull(permissions);
        assertEquals(2, permissions.size());
        assertEquals(AccountType.Permission.ManagePlatform, permissions.get(0));
        assertEquals(AccountType.Permission.GrabProposal, permissions.get(1));

        thrown.expect(IllegalArgumentException.class);
        AccountType.Permission.valueOfPermissions(new String[] { "ManageWrongPlatform" });
    }

    @Test
    public void testImplies() {
        assertTrue(AccountType.Permission.ManagePlatform.implies(AccountType.Permission.ManagePlatform));
    }
}
