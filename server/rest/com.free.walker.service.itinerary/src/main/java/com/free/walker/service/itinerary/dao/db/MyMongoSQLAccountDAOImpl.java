package com.free.walker.service.itinerary.dao.db;

import java.io.IOException;
import java.io.StringReader;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.Account;
import com.free.walker.service.itinerary.dao.AccountDAO;
import com.free.walker.service.itinerary.dao.DAOConstants;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidAccountException;
import com.free.walker.service.itinerary.primitive.AccountStatus;
import com.free.walker.service.itinerary.primitive.AccountType;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.util.MongoDbClientBuilder;
import com.free.walker.service.itinerary.util.SystemConfigUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

public class MyMongoSQLAccountDAOImpl implements AccountDAO {
    private static final Logger LOG = LoggerFactory.getLogger(MyMongoSQLAccountDAOImpl.class);

    private MongoClient mdbClient;
    private String accountMongoDbUrl;

    private DB accountDb;

    private static class SingletonHolder {
        private static final AccountDAO INSTANCE = new MyMongoSQLAccountDAOImpl();
    }

    public static AccountDAO getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private MyMongoSQLAccountDAOImpl() {
        try {
            Properties config = SystemConfigUtil.getApplicationConfig();

            mdbClient = new MongoDbClientBuilder().build(config);
            accountMongoDbUrl = StringUtils.join(DAOConstants.account_mongo_database,
                config.getProperty(DAOConstants.mongo_database_url));
            accountDb = mdbClient.getDB(DAOConstants.account_mongo_database);
        } catch (UnknownHostException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            if (!pingPersistence()){
                if (mdbClient != null) {
                    mdbClient.close();
                    mdbClient = null;
                }
                throw new IllegalStateException();
            }
        }
    }

    public boolean pingPersistence() {
        boolean result = false;

        DBObject ping = new BasicDBObject("ping", "1");
        try {
            CommandResult cr = accountDb.command(ping);
            if (!cr.ok()) {
                return false;
            } else {
                result = true;
            }
        } catch (RuntimeException e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_init_failure, accountMongoDbUrl,
                MongoClient.class.getName()), e);
            return false;
        }

        return result;
    }

    public Account registerAccount(Account account) throws InvalidAccountException, DatabaseAccessException {
        if (account == null) {
            throw new NullPointerException();
        }

        DBCollection accountColls = accountDb.getCollection(DAOConstants.ACCOUNT_COLL_NAME);

        if (account.getLogin() == null || account.getLogin().trim().isEmpty()) {
            throw new InvalidAccountException(LocalMessages.getMessage(LocalMessages.missing_account_priciple),
                account.getLogin());
        }

        if (account.getPassword() == null || account.getPassword().trim().isEmpty()) {
            throw new InvalidAccountException(LocalMessages.getMessage(LocalMessages.missing_account_credential),
                account.getLogin());
        }

        if (accountColls.findOne(new BasicDBObject(Introspection.JSONKeys.LOGIN, account.getLogin())) != null) {
            throw new InvalidAccountException(LocalMessages.getMessage(LocalMessages.existed_account_login,
                account.getLogin()), account.getLogin());
        }

        if (account.getEmail() != null && !account.getEmail().trim().isEmpty()
            && accountColls.findOne(new BasicDBObject(Introspection.JSONKeys.EMAIL, account.getEmail())) != null) {
            throw new InvalidAccountException(LocalMessages.getMessage(LocalMessages.existed_account_email,
                account.getEmail()), account.getEmail());
        }

        if (account.getMobile() != null && !account.getMobile().trim().isEmpty()
            && accountColls.findOne(new BasicDBObject(Introspection.JSONKeys.MOBILE, account.getMobile())) != null) {
            throw new InvalidAccountException(LocalMessages.getMessage(LocalMessages.existed_account_mobile,
                account.getMobile()), account.getMobile());
        }

        DBObject creatingAccount = (DBObject) JSON.parse(account.toJSON().toString());
        Sha256Hash hashedPwdBase64 = new Sha256Hash(account.getPassword(), null, 1024);
        creatingAccount.put(Introspection.JSONKeys.PASSWORD, hashedPwdBase64.toBase64());

        try {
            WriteResult wr = storeAccount(creatingAccount);
            LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_create_record, wr.toString()));
        } catch (MongoException e) {
            throw new DatabaseAccessException(e);
        }

        JsonObject createdAccount = Json.createReader(new StringReader(creatingAccount.toString())).readObject();
        return new Account().fromJSON(createdAccount);
    }

    public Account modifyAccount(Account account) throws InvalidAccountException, DatabaseAccessException {
        if (account == null) {
            throw new NullPointerException();
        }

        DBCollection accountColls = accountDb.getCollection(DAOConstants.ACCOUNT_COLL_NAME);
        BasicDBObject byLogin = new BasicDBObject(Introspection.JSONKeys.LOGIN, account.getLogin());
        DBObject updatingAccount = accountColls.findOne(byLogin);
        if (updatingAccount == null) {
            throw new InvalidAccountException(LocalMessages.getMessage(LocalMessages.not_existed_account,
                account.getLogin()), account.getLogin());
        }

        if (!updatingAccount.get(Introspection.JSONKeys.UUID).equals(account.getUuid())) {
            throw new InvalidAccountException(LocalMessages.getMessage(LocalMessages.account_uuid_immutable,
                account.getLogin(), updatingAccount.get(Introspection.JSONKeys.UUID)), account.getLogin());
        }

        Integer typeEnum = (Integer) updatingAccount.get(Introspection.JSONKeys.TYPE);
        AccountType accountType = AccountType.valueOf(typeEnum);
        if (!accountType.equals(account.getAccountType())) {
            throw new InvalidAccountException(LocalMessages.getMessage(LocalMessages.account_type_immutable,
                account.getLogin(), accountType.name()), account.getLogin());
        }

        Integer statusEnum = (Integer) updatingAccount.get(Introspection.JSONKeys.STATUS);
        AccountStatus accountStatus = AccountStatus.valueOf(statusEnum);
        if (!account.getAccountStatus().equals(accountStatus)) {
            throw new InvalidAccountException(LocalMessages.getMessage(LocalMessages.account_status_immutable,
                account.getLogin(), accountStatus.name()), account.getLogin());
        }

        if (account.getEmail() != null && !account.getEmail().trim().isEmpty()
            && accountColls.findOne(new BasicDBObject(Introspection.JSONKeys.EMAIL, account.getEmail())) != null) {
            throw new InvalidAccountException(LocalMessages.getMessage(LocalMessages.existed_account_email,
                account.getEmail()), account.getEmail());
        }

        if (account.getMobile() != null && !account.getMobile().trim().isEmpty()
            && accountColls.findOne(new BasicDBObject(Introspection.JSONKeys.MOBILE, account.getMobile())) != null) {
            throw new InvalidAccountException(LocalMessages.getMessage(LocalMessages.existed_account_mobile,
                account.getMobile()), account.getMobile());
        }

        if (account.getEmail() != null) updatingAccount.put(Introspection.JSONKeys.EMAIL, account.getEmail());
        if (account.getMobile() != null) updatingAccount.put(Introspection.JSONKeys.MOBILE, account.getMobile());
        if (account.getName() != null) updatingAccount.put(Introspection.JSONKeys.NAME, account.getName());
        if (account.getImageUri() != null) updatingAccount.put(Introspection.JSONKeys.REF_LINK, account.getImageUri());

        try {
            WriteResult wr = updateAccount(updatingAccount);
            LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
        } catch (MongoException e) {
            throw new DatabaseAccessException(e);
        }

        JsonObject updatedAccount = Json.createReader(new StringReader(updatingAccount.toString())).readObject();
        return new Account().fromJSON(updatedAccount);
    }


    public Account retrieveAccount(String login) throws InvalidAccountException, DatabaseAccessException {
        if (login == null) {
            throw new NullPointerException();
        }

        if (login.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }

        DBCollection accountColls = accountDb.getCollection(DAOConstants.ACCOUNT_COLL_NAME);
        BasicDBObject byLogin = new BasicDBObject(Introspection.JSONKeys.LOGIN, login);
        DBObject accountBs = accountColls.findOne(byLogin);

        if (accountBs == null) {
            return null;
        } else {
            JsonObject account = Json.createReader(new StringReader(accountBs.toString())).readObject();
            return new Account().fromJSON(account);
        }
    }

    public Account retrieveAccount(UUID uuid) throws InvalidAccountException, DatabaseAccessException {
        if (uuid == null) {
            throw new NullPointerException();
        }

        DBCollection accountColls = accountDb.getCollection(DAOConstants.ACCOUNT_COLL_NAME);
        BasicDBObject byUuid = new BasicDBObject(Introspection.JSONKeys.UUID, uuid.toString());
        DBObject accountBs = accountColls.findOne(byUuid);

        if (accountBs == null) {
            return null;
        } else {
            JsonObject account = Json.createReader(new StringReader(accountBs.toString())).readObject();
            return new Account().fromJSON(account);
        }
    }

    public Account lockAccount(String login) throws InvalidAccountException, DatabaseAccessException {
        return switchStatus(login, null, AccountStatus.LOCKED);
    }

    public Account deactivateAccount(String login) throws InvalidAccountException, DatabaseAccessException {
        return switchStatus(login, null, AccountStatus.IN_ACTIVE);
    }

    public Account activateAccount(String login) throws InvalidAccountException, DatabaseAccessException {
        return switchStatus(login, null, AccountStatus.ACTIVE);
    }

    public Account revokeAccount(String login) throws InvalidAccountException, DatabaseAccessException {
        return switchStatus(login, null, AccountStatus.REVOKED);
    }

    public void changePassword(String login, String currentPwd, String newPwd) throws InvalidAccountException,
        DatabaseAccessException {
        if (login == null || currentPwd == null || newPwd == null) {
            throw new NullPointerException();
        }

        if (login.trim().isEmpty() || currentPwd.trim().isEmpty() || newPwd.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }

        DBCollection accountColls = accountDb.getCollection(DAOConstants.ACCOUNT_COLL_NAME);
        DBObject accountBs = accountColls.findOne(new BasicDBObject(Introspection.JSONKeys.LOGIN, login));

        if (accountBs == null) {
            throw new InvalidAccountException(LocalMessages.getMessage(LocalMessages.not_existed_account, login), login);
        } else {
            try {
                String hashedCurrentPasswordBase64 = new Sha256Hash(currentPwd, null, 1024).toBase64();
                String hashedNewPasswordBase64 = new Sha256Hash(newPwd, null, 1024).toBase64();
                accountBs.put(Introspection.JSONKeys.PASSWORD, hashedNewPasswordBase64);
                WriteResult wr = updateAccountPwd(accountBs, hashedCurrentPasswordBase64);
                LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
            } catch (MongoException e) {
                throw new DatabaseAccessException(e);
            }
        }

        return;
    }

    private Account switchStatus(String login, AccountStatus src, AccountStatus dest) throws DatabaseAccessException,
        InvalidAccountException {
        if (login == null || dest == null) {
            throw new NullPointerException();
        }

        if (login.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }

        DBCollection accountColls = accountDb.getCollection(DAOConstants.ACCOUNT_COLL_NAME);
        DBObject accountBs = accountColls.findOne(new BasicDBObject(Introspection.JSONKeys.LOGIN, login));

        if (accountBs == null) {
            throw new InvalidAccountException(LocalMessages.getMessage(LocalMessages.not_existed_account, login), login);
        } else {
            Integer statusEnum = (Integer) accountBs.get(Introspection.JSONKeys.STATUS);
            AccountStatus accountStatus = AccountStatus.valueOf(statusEnum);

            if (dest.equals(accountStatus)) {
                JsonObject account = Json.createReader(new StringReader(accountBs.toString())).readObject();
                return new Account().fromJSON(account);
            }

            if (src != null && !accountStatus.equals(src)) {
                throw new InvalidAccountException(LocalMessages.getMessage(LocalMessages.missmatched_account_status,
                    login, accountStatus.ordinal(), src.ordinal()), login);
            }

            accountBs.put(Introspection.JSONKeys.STATUS, dest.ordinal());

            try {
                WriteResult wr = updateAccountStatus(accountBs, src);
                LOG.debug(LocalMessages.getMessage(LocalMessages.mongodb_update_record, wr.toString()));
            } catch (MongoException e) {
                throw new DatabaseAccessException(e);
            }

            JsonObject account = Json.createReader(new StringReader(accountBs.toString())).readObject();
            return new Account().fromJSON(account);
        }
    }

    private WriteResult storeAccount(DBObject accountBs) {
        String login = (String) accountBs.get(Introspection.JSONKeys.LOGIN);
        DBCollection accountColls = accountDb.getCollection(DAOConstants.ACCOUNT_COLL_NAME);
        accountBs.put(DAOConstants.mongo_database_pk, login);
        return accountColls.insert(accountBs, WriteConcern.MAJORITY);
    }

    private WriteResult updateAccount(DBObject accountBs) {
        String login = (String) accountBs.get(Introspection.JSONKeys.LOGIN);
        DBCollection accountColls = accountDb.getCollection(DAOConstants.ACCOUNT_COLL_NAME);
        BasicDBObject accountQuery = new BasicDBObject(DAOConstants.mongo_database_pk, login);
        return accountColls.update(accountQuery, accountBs, false, false, WriteConcern.MAJORITY);
    }

    private WriteResult updateAccountStatus(DBObject accountBs, AccountStatus currentStatus) {
        String login = (String) accountBs.get(Introspection.JSONKeys.LOGIN);
        DBCollection accountColls = accountDb.getCollection(DAOConstants.ACCOUNT_COLL_NAME);
        if (currentStatus == null) {
            BasicDBObject accountQuery = new BasicDBObject(DAOConstants.mongo_database_pk, login);
            return accountColls.update(accountQuery, accountBs, false, false, WriteConcern.MAJORITY);
        } else {
            BasicDBObject query1 = new BasicDBObject(DAOConstants.mongo_database_pk, login);
            BasicDBObject query2 = new BasicDBObject(Introspection.JSONKeys.STATUS, currentStatus.ordinal());
            BasicDBObject accountQuery = new BasicDBObject("$and", new BasicDBObject[] { query1, query2 });
            return accountColls.update(accountQuery, accountBs, false, false, WriteConcern.MAJORITY);
        }
    }

    private WriteResult updateAccountPwd(DBObject accountBs, String currentPwd) {
        String login = (String) accountBs.get(Introspection.JSONKeys.LOGIN);
        DBCollection accountColls = accountDb.getCollection(DAOConstants.ACCOUNT_COLL_NAME);
        BasicDBObject query1 = new BasicDBObject(DAOConstants.mongo_database_pk, login);
        BasicDBObject query2 = new BasicDBObject(Introspection.JSONKeys.PASSWORD, currentPwd);
        BasicDBObject accountQuery = new BasicDBObject("$and", new BasicDBObject[] { query1, query2 });
        return accountColls.update(accountQuery, accountBs, false, false, WriteConcern.MAJORITY);
    }
}
