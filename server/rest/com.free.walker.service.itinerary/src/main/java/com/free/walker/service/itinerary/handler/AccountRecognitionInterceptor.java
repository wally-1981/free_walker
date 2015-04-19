package com.free.walker.service.itinerary.handler;

import javax.ws.rs.core.Response.Status;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.shiro.subject.Subject;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.Account;
import com.free.walker.service.itinerary.primitive.Introspection;

public class AccountRecognitionInterceptor extends AbstractPhaseInterceptor<Message> {
    public AccountRecognitionInterceptor() {
        super(Phase.USER_LOGICAL);
    }

    public void handleMessage(Message message) throws Fault {
        Subject currentUser = message.getContent(Subject.class);
        Object subjectPrincipal = null;
        if (currentUser != null && currentUser.isAuthenticated()) {
            subjectPrincipal = currentUser.getPrincipal();
            if (Introspection.DefaultAccounts.ADMIN_ACCOUNT.equals(subjectPrincipal)) {
                message.setContent(Account.class, Constants.ADMIN_ACCOUNT);
                return;
            } else if (Introspection.DefaultAccounts.DEFAULT_MASTER_ACCOUNT.equals(subjectPrincipal)) {
                message.setContent(Account.class, Constants.DEFAULT_USER_ACCOUNT);
                return;
            } else if (Introspection.DefaultAccounts.DEFAULT_WECHAT_ACCOUNT.equals(subjectPrincipal)) {
                message.setContent(Account.class, Constants.DEFAULT_WECHAT_USER_ACCOUNT);
                return;
            } else if (Introspection.DefaultAccounts.DEFAULT_AGENCY_ACCOUNT.equals(subjectPrincipal)) {
                message.setContent(Account.class, Constants.DEFAULT_AGENCY_ACCOUNT);
                return;
            } else {
                Object account = currentUser.getPrincipal();
                if (account instanceof Account) {
                    message.setContent(Account.class, account);
                    return;
                }
            }

            Fault fault = new Fault(new IllegalAccessException(LocalMessages.getMessage(
                LocalMessages.account_unknown, subjectPrincipal)));
            fault.setStatusCode(Status.UNAUTHORIZED.getStatusCode());
            message.getInterceptorChain().abort();
            throw fault;
        }
    }
}
