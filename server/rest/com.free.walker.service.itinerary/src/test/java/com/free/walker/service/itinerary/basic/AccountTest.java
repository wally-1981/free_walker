package com.free.walker.service.itinerary.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.UUID;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.exp.InvalidAccountException;
import com.free.walker.service.itinerary.primitive.AccountStatus;
import com.free.walker.service.itinerary.primitive.AccountType;
import com.free.walker.service.itinerary.primitive.Introspection;

public class AccountTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private JsonObjectBuilder accountABuilder;
    private JsonObjectBuilder accountBBuilder;

    @Before
    public void before() {
        accountABuilder = Json.createObjectBuilder();
        accountABuilder.add(Introspection.JSONKeys.TYPE, AccountType.AliPay.ordinal());
        accountABuilder.add(Introspection.JSONKeys.STATUS, AccountStatus.LOCKED.ordinal());
        accountABuilder.add(Introspection.JSONKeys.LOGIN, "test_account");
        accountABuilder.add(Introspection.JSONKeys.PASSWORD, "hashedpassword");
        accountABuilder.add(Introspection.JSONKeys.EMAIL, "");
        accountABuilder.add(Introspection.JSONKeys.NAME, "nick_name");
        accountABuilder.add(Introspection.JSONKeys.REF_LINK, "http://www.china.gov");

        accountBBuilder = Json.createObjectBuilder();
        accountBBuilder.add(Introspection.JSONKeys.TYPE, AccountType.AliPay.ordinal());
        accountBBuilder.add(Introspection.JSONKeys.STATUS, AccountStatus.LOCKED.ordinal());
        accountBBuilder.add(Introspection.JSONKeys.LOGIN, "test_account");
        accountBBuilder.add(Introspection.JSONKeys.PASSWORD, "");
        accountBBuilder.add(Introspection.JSONKeys.EMAIL, "12345678@mobile.com");
        accountBBuilder.add(Introspection.JSONKeys.NAME, "nick_name");
        accountBBuilder.add(Introspection.JSONKeys.REF_LINK, "http://www.china.gov");
    }

    @Test
    public void testFromToJSON() throws InvalidAccountException {
        String uuid = UUID.randomUUID().toString();
        accountABuilder.add(Introspection.JSONKeys.UUID, uuid);
        Account account = new Account().fromJSON(accountABuilder.build());

        JsonObject accountObj = account.toJSON();
        assertEquals(uuid, accountObj.getString(Introspection.JSONKeys.UUID));
        assertEquals(AccountType.AliPay.ordinal(), accountObj.getInt(Introspection.JSONKeys.TYPE));
        assertEquals(AccountStatus.LOCKED.ordinal(), accountObj.getInt(Introspection.JSONKeys.STATUS));
        assertEquals("test_account", accountObj.getString(Introspection.JSONKeys.LOGIN));
        assertEquals("hashedpassword", accountObj.getString(Introspection.JSONKeys.PASSWORD));
        assertNull(accountObj.getString(Introspection.JSONKeys.MOBILE, null));
        assertEquals("", accountObj.getString(Introspection.JSONKeys.EMAIL));
        assertEquals("nick_name", accountObj.getString(Introspection.JSONKeys.NAME));
        assertEquals("http://www.china.gov", accountObj.getString(Introspection.JSONKeys.REF_LINK));
    }

    @Test
    public void testNewFromToJSON() throws InvalidAccountException {
        Account account = new Account().newFromJSON(accountABuilder.build());

        JsonObject accountObj = account.toJSON();
        assertNotNull(accountObj.getString(Introspection.JSONKeys.UUID));
        assertEquals(AccountType.AliPay.ordinal(), accountObj.getInt(Introspection.JSONKeys.TYPE));
        assertEquals(AccountStatus.LOCKED.ordinal(), accountObj.getInt(Introspection.JSONKeys.STATUS));
        assertEquals("test_account", accountObj.getString(Introspection.JSONKeys.LOGIN));
        assertEquals("hashedpassword", accountObj.getString(Introspection.JSONKeys.PASSWORD));
        assertNull(accountObj.getString(Introspection.JSONKeys.MOBILE, null));
        assertEquals("", accountObj.getString(Introspection.JSONKeys.EMAIL));
        assertEquals("nick_name", accountObj.getString(Introspection.JSONKeys.NAME));
        assertEquals("http://www.china.gov", accountObj.getString(Introspection.JSONKeys.REF_LINK));
    }

    @Test
    public void testFromToJSONWithInvalidLogin() throws InvalidAccountException {
        String uuid = UUID.randomUUID().toString();
        accountBBuilder.add(Introspection.JSONKeys.UUID, uuid);
        thrown.expect(JsonException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
            Introspection.JSONKeys.PASSWORD, ""));
        new Account().fromJSON(accountBBuilder.build());
    }
}
