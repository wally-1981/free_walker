package com.free.walker.service.itinerary.dao;

import java.util.UUID;

import com.free.walker.service.itinerary.basic.Account;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidAccountException;

public interface AccountDAO extends HealthyDAO {
    public Account registerAccount(Account account) throws InvalidAccountException, DatabaseAccessException;

    public Account modifyAccount(Account account) throws InvalidAccountException, DatabaseAccessException;

    public Account retrieveAccount(String login) throws InvalidAccountException, DatabaseAccessException;

    public Account retrieveAccount(UUID uuid) throws InvalidAccountException, DatabaseAccessException;

    public Account lockAccount(String login) throws InvalidAccountException, DatabaseAccessException;

    public Account deactivateAccount(String login) throws InvalidAccountException, DatabaseAccessException;

    public Account activateAccount(String login) throws InvalidAccountException, DatabaseAccessException;

    public Account revokeAccount(String login) throws InvalidAccountException, DatabaseAccessException;

    public void changePassword(String login, String currentPwd, String newPwd) throws InvalidAccountException,
        DatabaseAccessException;
}
