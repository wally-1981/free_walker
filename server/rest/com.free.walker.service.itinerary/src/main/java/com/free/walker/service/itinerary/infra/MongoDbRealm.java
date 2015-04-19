package com.free.walker.service.itinerary.infra;

import java.util.Arrays;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.free.walker.service.itinerary.basic.Account;
import com.free.walker.service.itinerary.dao.AccountDAO;
import com.free.walker.service.itinerary.dao.DAOFactory;
import com.free.walker.service.itinerary.dao.db.MyMongoSQLAccountDAOImpl;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidAccountException;
import com.free.walker.service.itinerary.primitive.AccountType;
import com.free.walker.service.itinerary.primitive.AccountType.Role;

public class MongoDbRealm extends AuthorizingRealm {
    private static AccountDAO accountDAO = DAOFactory.getAccountDAO(MyMongoSQLAccountDAOImpl.class.getName());

    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Object primaryPrincipal = principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        if (primaryPrincipal instanceof Account) {
            Role[] roles = ((Account) primaryPrincipal).getRoles();
            for (int i = 0; roles != null && i < roles.length && roles[i] != null; i++) {
                authorizationInfo.addRole(roles[i].name().toLowerCase());
                Permission[] permissions = AccountType.Role.getDefaultPermissions(roles[i]);
                authorizationInfo.addObjectPermissions(Arrays.asList(permissions));
            }
            return authorizationInfo;
        } else {
            return null;
        }
    }

    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
        if (token instanceof UsernamePasswordToken) {
            String login = ((UsernamePasswordToken) token).getPrincipal().toString();
            if (login != null && !login.trim().isEmpty()) {
                try {
                    Account account = accountDAO.retrieveAccount(login);
                    if (account == null) {
                        return null;
                    } else {
                        return new SimpleAuthenticationInfo(account, account.getPassword(), getName());
                    }
                } catch (InvalidAccountException | DatabaseAccessException e) {
                    throw new AuthenticationException(e);
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
