package com.free.walker.service.itinerary.handler;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.Account;

public class AccountRecognitionInterceptor extends AbstractPhaseInterceptor<Message> {
    public AccountRecognitionInterceptor() {
        super(Phase.USER_LOGICAL);
    }

    public void handleMessage(Message message) throws Fault {
        Object authorization = message.get(HttpHeaders.AUTHORIZATION);
        if (authorization == null) {
            message.get(Message.REQUEST_URI);
            Fault fault = new Fault(new IllegalAccessException(LocalMessages.getMessage(
                LocalMessages.unauthorized_account, null, message.get(Message.REQUEST_URL))));
            fault.setStatusCode(Status.UNAUTHORIZED.getStatusCode());
            message.getInterceptorChain().abort();
            throw fault;
        } else {
            String accountId = (String) authorization;
            if (Constants.DEFAULT_ACCOUNT.getUuid().equals(accountId)) {
                message.setContextualProperty(Account.class.getName(), Constants.DEFAULT_ACCOUNT);
            } else if (Constants.DEFAULT_AGENCY_ACCOUNT.getUuid().equals(accountId)) {
                message.setContextualProperty(Account.class.getName(), Constants.DEFAULT_AGENCY_ACCOUNT);
            } else if (Constants.ADMIN_ACCOUNT.getUuid().equals(accountId)) {
                message.setContextualProperty(Account.class.getName(), Constants.ADMIN_ACCOUNT);
            } else {
                Fault fault = new Fault(new IllegalAccessException(LocalMessages.getMessage(
                    LocalMessages.unauthorized_account, accountId, message.get(Message.REQUEST_URL))));
                fault.setStatusCode(Status.UNAUTHORIZED.getStatusCode());
                message.getInterceptorChain().abort();
                throw fault;
            }
        }
    }
}
