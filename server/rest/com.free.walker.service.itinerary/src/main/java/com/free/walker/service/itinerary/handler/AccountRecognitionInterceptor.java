package com.free.walker.service.itinerary.handler;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.Account;

public class AccountRecognitionInterceptor extends AbstractPhaseInterceptor<Message> {
    public AccountRecognitionInterceptor() {
        super(Phase.USER_LOGICAL);
    }

    public void handleMessage(Message message) throws Fault {
        String accountId = null;
        HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
        if (request != null) {
           accountId = request.getHeader(HttpHeaders.AUTHORIZATION);
           if (accountId != null) {
               if (Constants.DEFAULT_ACCOUNT.getUuid().equals(accountId)) {
                   message.setContextualProperty(Account.class.getName(), Constants.DEFAULT_ACCOUNT);
                   return;
               } else if (Constants.DEFAULT_AGENCY_ACCOUNT.getUuid().equals(accountId)) {
                   message.setContextualProperty(Account.class.getName(), Constants.DEFAULT_AGENCY_ACCOUNT);
                   return;
               } else if (Constants.ADMIN_ACCOUNT.getUuid().equals(accountId)) {
                   message.setContextualProperty(Account.class.getName(), Constants.ADMIN_ACCOUNT);
                   return;
               } else {
                   ;
               }
           }
        }

        Fault fault = new Fault(new IllegalAccessException(LocalMessages.getMessage(LocalMessages.unauthorized_account,
            accountId, message.get(Message.REQUEST_URL))));
        fault.setStatusCode(Status.UNAUTHORIZED.getStatusCode());
        message.getInterceptorChain().abort();
        throw fault;
    }
}
