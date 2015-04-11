package com.free.walker.service.itinerary.handler;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.http.client.utils.URIBuilder;
import org.eclipse.jetty.http.HttpSchemes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.SecurityPolicy;
import com.free.walker.service.itinerary.util.UriUtil;

public class SecurityPolicyInterceptor extends AbstractPhaseInterceptor<Message> {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityPolicyInterceptor.class);

    public SecurityPolicyInterceptor() {
        super(Phase.USER_LOGICAL);
    }

    public void handleMessage(Message message) throws Fault {
        HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
        HttpServletResponse response = (HttpServletResponse) message.get(AbstractHTTPDestination.HTTP_RESPONSE);
        Method serviceMethod = (Method) message.get(Constants.SERVICE_METHOD_KEY);
        URI serviceUri;
        try {
            serviceUri = new URIBuilder((String) message.get(Message.REQUEST_URL)).setCustomQuery(
                request.getQueryString()).build();
        } catch (URISyntaxException e) {
            Fault fault = new Fault(e);
            fault.setStatusCode(Status.BAD_REQUEST.getStatusCode());
            message.getInterceptorChain().abort();
            throw fault;
        }

        SecurityPolicy secPolicy = serviceMethod.getAnnotation(SecurityPolicy.class);
        if (secPolicy != null && secPolicy.level().ordinal() < SecurityPolicy.SecurityLevel.MEDIUM.ordinal()) {
            if (HttpSchemes.HTTP.equalsIgnoreCase(serviceUri.getScheme())) {
                String secureServiceUri;
                try {
                    secureServiceUri = UriUtil.ensureSecureUri(serviceUri).toString();
                } catch (URISyntaxException e) {
                    Fault fault = new Fault(e);
                    fault.setStatusCode(Status.BAD_REQUEST.getStatusCode());
                    message.getInterceptorChain().abort();
                    throw fault;
                }

                response.setHeader(HttpHeaders.LOCATION, secureServiceUri);
                
                Fault fault = new Fault(new IllegalAccessException(LocalMessages.getMessage(
                    LocalMessages.non_secure_request_redirected, serviceUri, secureServiceUri)));
                fault.setStatusCode(Status.TEMPORARY_REDIRECT.getStatusCode());
                
                message.getInterceptorChain().abort();
                LOG.debug(LocalMessages.getMessage(LocalMessages.non_secure_request_redirected, serviceUri,
                    secureServiceUri));
                
                throw fault;
            }
        }
    }
}
