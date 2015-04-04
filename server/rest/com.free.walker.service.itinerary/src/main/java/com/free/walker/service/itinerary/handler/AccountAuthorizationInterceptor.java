package com.free.walker.service.itinerary.handler;

import java.lang.reflect.Method;
import java.util.Arrays;

import javax.ws.rs.core.Response.Status;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.LocalMessages;

public class AccountAuthorizationInterceptor extends AbstractPhaseInterceptor<Message> {
    private static final Logger LOG = LoggerFactory.getLogger(AccountAuthorizationInterceptor.class);

    public AccountAuthorizationInterceptor() {
        super(Phase.USER_LOGICAL);
    }

    public void handleMessage(Message message) throws Fault {
        Subject currentUser = message.getContent(Subject.class);
        Method serviceMethod = (Method) message.get(Constants.SERVICE_METHOD_KEY);
        String serviceUri = (String) message.get(Message.REQUEST_URI);

        boolean pAuthrozied = false;
        RequiresPermissions permissions = serviceMethod.getAnnotation(RequiresPermissions.class);
        if (permissions == null || permissions.value().length == 0) {
            LOG.debug(LocalMessages.getMessage(LocalMessages.account_authorization_permissions_check_bypass,
                serviceUri, currentUser.getPrincipal()));
            pAuthrozied = true;
        } else {
            LOG.debug(LocalMessages.getMessage(LocalMessages.account_authorization_permissions_check,
                permissions.value(), permissions.logical(), currentUser.getPrincipal()));
            boolean[] resultArray = currentUser.isPermitted(permissions.value());
            if (Logical.AND.ordinal() == permissions.logical().ordinal()) {
                pAuthrozied = true;
                for (boolean result : resultArray) {
                    pAuthrozied = pAuthrozied && result;
                }
            } else {
                pAuthrozied = false;
                for (boolean result : resultArray) {
                    pAuthrozied = pAuthrozied || result;
                }
            }
        }

        boolean rAuthrozied = false;
        RequiresRoles roles = serviceMethod.getAnnotation(RequiresRoles.class);
        if (roles == null || roles.value().length == 0) {
            LOG.debug(LocalMessages.getMessage(LocalMessages.account_authorization_roles_check_bypass,
                serviceUri, currentUser.getPrincipal()));
            rAuthrozied = true;
        } else {
            LOG.debug(LocalMessages.getMessage(LocalMessages.account_authorization_roles_check,
                roles.value(), roles.logical(), currentUser.getPrincipal()));
            boolean[] resultArray = currentUser.hasRoles(Arrays.asList(roles.value()));
            if (Logical.AND.ordinal() == roles.logical().ordinal()) {
                rAuthrozied = true;
                for (boolean result : resultArray) {
                    rAuthrozied = rAuthrozied && result;
                }
            } else {
                rAuthrozied = false;
                for (boolean result : resultArray) {
                    rAuthrozied = rAuthrozied || result;
                }
            }
        }

        if (!pAuthrozied || !rAuthrozied) {
            Fault fault = new Fault(new IllegalAccessException(LocalMessages.getMessage(
                LocalMessages.account_not_authorized, currentUser.getPrincipal(), serviceUri)));
            fault.setStatusCode(Status.FORBIDDEN.getStatusCode());
            message.getInterceptorChain().abort();
            throw fault;
        }
    }
}
