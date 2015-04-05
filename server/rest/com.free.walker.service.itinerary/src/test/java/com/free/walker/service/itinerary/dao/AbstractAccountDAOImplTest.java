package com.free.walker.service.itinerary.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObjectBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.free.walker.service.itinerary.basic.Account;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidAccountException;
import com.free.walker.service.itinerary.primitive.AccountStatus;
import com.free.walker.service.itinerary.primitive.AccountType;
import com.free.walker.service.itinerary.primitive.Introspection;

public abstract class AbstractAccountDAOImplTest {
    protected AccountDAO accountDAO;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private JsonObjectBuilder accountBuilder;

    @Before
    public void before() {
        accountBuilder = Json.createObjectBuilder();
        accountBuilder.add(Introspection.JSONKeys.TYPE, AccountType.AliPay.ordinal());
        accountBuilder.add(Introspection.JSONKeys.STATUS, AccountStatus.LOCKED.ordinal());
        accountBuilder.add(Introspection.JSONKeys.PASSWORD, "hashedpassword");
        accountBuilder.add(Introspection.JSONKeys.EMAIL, "");
        accountBuilder.add(Introspection.JSONKeys.NAME, "nick_name");
        accountBuilder.add(Introspection.JSONKeys.REF_LINK, "http://www.china.gov");
    }

    @Test
    public void testRegisterAccount() throws InvalidAccountException, DatabaseAccessException {
        String uuid = UUID.randomUUID().toString();
        accountBuilder.add(Introspection.JSONKeys.UUID, uuid);
        accountBuilder.add(Introspection.JSONKeys.LOGIN, UUID.randomUUID().toString());
        Account account = new Account().fromJSON(accountBuilder.build());

        Account createdAccount = accountDAO.registerAccount(account);
        assertNotNull(createdAccount);
        assertEquals(account.getUuid(), createdAccount.getUuid());
    }

    @Test
    public void testModifyAccount() throws InvalidAccountException, DatabaseAccessException {
        String uuid = UUID.randomUUID().toString();
        accountBuilder.add(Introspection.JSONKeys.UUID, uuid);
        accountBuilder.add(Introspection.JSONKeys.LOGIN, UUID.randomUUID().toString());
        Account createdAccount = accountDAO.registerAccount(new Account().fromJSON(accountBuilder.build()));

        JsonObjectBuilder updatedAccountBuilder = Json.createObjectBuilder();
        updatedAccountBuilder.add(Introspection.JSONKeys.UUID, createdAccount.getUuid());
        updatedAccountBuilder.add(Introspection.JSONKeys.TYPE, createdAccount.getAccountType().ordinal());
        updatedAccountBuilder.add(Introspection.JSONKeys.STATUS, createdAccount.getAccountStatus().ordinal());
        updatedAccountBuilder.add(Introspection.JSONKeys.LOGIN, createdAccount.getLogin());
        updatedAccountBuilder.add(Introspection.JSONKeys.PASSWORD, "hashedpassword");
        updatedAccountBuilder.add(Introspection.JSONKeys.EMAIL, createdAccount.getLogin() + "@china.gov");
        updatedAccountBuilder.add(Introspection.JSONKeys.NAME, createdAccount.getName() + "_2016");
        updatedAccountBuilder.add(Introspection.JSONKeys.REF_LINK, createdAccount.getImageUri());

        Account updatedAccount = accountDAO.modifyAccount(new Account().fromJSON(updatedAccountBuilder.build()));
        assertNotNull(updatedAccount);
        assertEquals(createdAccount.getUuid(), updatedAccount.getUuid());
        assertEquals(createdAccount.getAccountStatus(), updatedAccount.getAccountStatus());
        assertEquals(createdAccount.getAccountType(), updatedAccount.getAccountType());
        assertEquals(createdAccount.getLogin() + "@china.gov", updatedAccount.getEmail());
        assertEquals(createdAccount.getMobile(), updatedAccount.getMobile());
        assertEquals(createdAccount.getName() + "_2016", updatedAccount.getName());
        assertEquals(createdAccount.getPassword(), updatedAccount.getPassword());
    }

    @Test
    public void testRetrieveAccount() throws InvalidAccountException, DatabaseAccessException {
        String uuid = UUID.randomUUID().toString();
        accountBuilder.add(Introspection.JSONKeys.UUID, uuid);
        accountBuilder.add(Introspection.JSONKeys.LOGIN, UUID.randomUUID().toString());
        Account createdAccount = accountDAO.registerAccount(new Account().fromJSON(accountBuilder.build()));

        Account retrievedAccount = accountDAO.retrieveAccount(createdAccount.getLogin());
        assertNotNull(retrievedAccount);
        assertEquals(createdAccount.getUuid(), retrievedAccount.getUuid());
        assertEquals(createdAccount.getAccountStatus(), retrievedAccount.getAccountStatus());
        assertEquals(createdAccount.getAccountType(), retrievedAccount.getAccountType());
        assertEquals(createdAccount.getEmail(), retrievedAccount.getEmail());
        assertEquals(createdAccount.getMobile(), retrievedAccount.getMobile());
        assertEquals(createdAccount.getName(), retrievedAccount.getName());
        assertEquals(createdAccount.getPassword(), retrievedAccount.getPassword());
    }

    @Test
    public void testLockAccount() throws InvalidAccountException, JsonException, DatabaseAccessException {
        String uuid = UUID.randomUUID().toString();
        accountBuilder.add(Introspection.JSONKeys.UUID, uuid);
        accountBuilder.add(Introspection.JSONKeys.LOGIN, UUID.randomUUID().toString());
        Account createdAccount = accountDAO.registerAccount(new Account().fromJSON(accountBuilder.build()));

        Account lockedAccount = accountDAO.lockAccount(createdAccount.getLogin());
        assertNotNull(lockedAccount);
        assertEquals(createdAccount.getUuid(), lockedAccount.getUuid());
        assertEquals(AccountStatus.LOCKED, lockedAccount.getAccountStatus());
        assertEquals(createdAccount.getAccountType(), lockedAccount.getAccountType());
        assertEquals(createdAccount.getEmail(), lockedAccount.getEmail());
        assertEquals(createdAccount.getMobile(), lockedAccount.getMobile());
        assertEquals(createdAccount.getName(), lockedAccount.getName());
        assertEquals(createdAccount.getPassword(), lockedAccount.getPassword());
    }

    @Test
    public void testActivateAccount() throws InvalidAccountException, JsonException, DatabaseAccessException {
        String uuid = UUID.randomUUID().toString();
        accountBuilder.add(Introspection.JSONKeys.UUID, uuid);
        accountBuilder.add(Introspection.JSONKeys.LOGIN, UUID.randomUUID().toString());
        Account createdAccount = accountDAO.registerAccount(new Account().fromJSON(accountBuilder.build()));

        Account activeAccount = accountDAO.activateAccount(createdAccount.getLogin());
        assertNotNull(activeAccount);
        assertEquals(createdAccount.getUuid(), activeAccount.getUuid());
        assertEquals(AccountStatus.ACTIVE, activeAccount.getAccountStatus());
        assertEquals(createdAccount.getAccountType(), activeAccount.getAccountType());
        assertEquals(createdAccount.getEmail(), activeAccount.getEmail());
        assertEquals(createdAccount.getMobile(), activeAccount.getMobile());
        assertEquals(createdAccount.getName(), activeAccount.getName());
        assertEquals(createdAccount.getPassword(), activeAccount.getPassword());
    }

    @Test
    public void testDeactivateAccount() throws InvalidAccountException, JsonException, DatabaseAccessException {
        String uuid = UUID.randomUUID().toString();
        accountBuilder.add(Introspection.JSONKeys.UUID, uuid);
        accountBuilder.add(Introspection.JSONKeys.LOGIN, UUID.randomUUID().toString());
        Account createdAccount = accountDAO.registerAccount(new Account().fromJSON(accountBuilder.build()));

        Account deactiveAccount = accountDAO.deactivateAccount(createdAccount.getLogin());
        assertNotNull(deactiveAccount);
        assertEquals(createdAccount.getUuid(), deactiveAccount.getUuid());
        assertEquals(AccountStatus.IN_ACTIVE, deactiveAccount.getAccountStatus());
        assertEquals(createdAccount.getAccountType(), deactiveAccount.getAccountType());
        assertEquals(createdAccount.getEmail(), deactiveAccount.getEmail());
        assertEquals(createdAccount.getMobile(), deactiveAccount.getMobile());
        assertEquals(createdAccount.getName(), deactiveAccount.getName());
        assertEquals(createdAccount.getPassword(), deactiveAccount.getPassword());
    }

    @Test
    public void testRevokeAccount() throws InvalidAccountException, JsonException, DatabaseAccessException {
        String uuid = UUID.randomUUID().toString();
        accountBuilder.add(Introspection.JSONKeys.UUID, uuid);
        accountBuilder.add(Introspection.JSONKeys.LOGIN, UUID.randomUUID().toString());
        Account createdAccount = accountDAO.registerAccount(new Account().fromJSON(accountBuilder.build()));

        Account revokedAccount = accountDAO.revokeAccount(createdAccount.getLogin());
        assertNotNull(revokedAccount);
        assertEquals(createdAccount.getUuid(), revokedAccount.getUuid());
        assertEquals(AccountStatus.REVOKED, revokedAccount.getAccountStatus());
        assertEquals(createdAccount.getAccountType(), revokedAccount.getAccountType());
        assertEquals(createdAccount.getEmail(), revokedAccount.getEmail());
        assertEquals(createdAccount.getMobile(), revokedAccount.getMobile());
        assertEquals(createdAccount.getName(), revokedAccount.getName());
        assertEquals(createdAccount.getPassword(), revokedAccount.getPassword());
    }

    @Test
    public void testResetPassword() throws InvalidAccountException, JsonException, DatabaseAccessException {
        String uuid = UUID.randomUUID().toString();
        accountBuilder.add(Introspection.JSONKeys.UUID, uuid);
        accountBuilder.add(Introspection.JSONKeys.LOGIN, UUID.randomUUID().toString());
        Account createdAccount = accountDAO.registerAccount(new Account().fromJSON(accountBuilder.build()));

        accountDAO.changePassword(createdAccount.getLogin(), "hashedpassword", "123456");
        Account retrievedAccount = accountDAO.retrieveAccount(createdAccount.getLogin());
        assertNotNull(retrievedAccount);
        assertEquals(createdAccount.getUuid(), retrievedAccount.getUuid());
        assertEquals(createdAccount.getAccountStatus(), retrievedAccount.getAccountStatus());
        assertEquals(createdAccount.getAccountType(), retrievedAccount.getAccountType());
        assertEquals(createdAccount.getEmail(), retrievedAccount.getEmail());
        assertEquals(createdAccount.getMobile(), retrievedAccount.getMobile());
        assertEquals(createdAccount.getName(), retrievedAccount.getName());
        assertNotEquals(createdAccount.getPassword(), retrievedAccount.getPassword());
    }

    @After
    public void after() {
        ;
    }
}
