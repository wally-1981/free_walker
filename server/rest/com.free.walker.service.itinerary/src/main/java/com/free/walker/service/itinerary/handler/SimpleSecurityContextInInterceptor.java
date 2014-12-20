package com.free.walker.service.itinerary.handler;

import java.util.Collections;
import java.util.Map;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.basic.Account;

public class SimpleSecurityContextInInterceptor extends AbstractPhaseInterceptor<Message> {

    private Map<String, Interceptor<Message>> authenticationHandlers = Collections.emptyMap();

    public SimpleSecurityContextInInterceptor() {
        super(Phase.UNMARSHAL);
    }

    public void handleMessage(Message message) throws Fault {
        message.setContextualProperty(Account.class.getName(), Constants.DEFAULT_ACCOUNT);
    }

    public void addAuthenticationHandler(String scheme, Interceptor<Message> handler) {
        if (scheme == null || handler == null) {
            throw new NullPointerException();
        }

        authenticationHandlers.put(scheme, handler);
    }
}
