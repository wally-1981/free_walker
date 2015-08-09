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
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.subject.WebSubjectContext;
import org.apache.shiro.web.subject.support.DefaultWebSubjectContext;
import org.eclipse.jetty.http.HttpSchemes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.rest.ServiceConfigurationProvider;
import com.free.walker.service.itinerary.util.UriUtil;

public class AccountAuthenticationInterceptor extends AbstractPhaseInterceptor<Message> {
    private static final Logger LOG = LoggerFactory.getLogger(AccountAuthenticationInterceptor.class);

    public AccountAuthenticationInterceptor() {
        super(Phase.USER_LOGICAL);
    }

    public void handleMessage(Message message) throws Fault {
        HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
        HttpServletResponse response = (HttpServletResponse) message.get(AbstractHTTPDestination.HTTP_RESPONSE);

        WebSubjectContext subjectCntx = new DefaultWebSubjectContext();
        subjectCntx.setServletRequest(request);
        subjectCntx.setServletResponse(response);
        Subject currentUser = SecurityUtils.getSecurityManager().createSubject(subjectCntx);

        if (currentUser.isAuthenticated()) {
            message.setContent(Subject.class, currentUser);
            LOG.debug(LocalMessages.getMessage(LocalMessages.account_authenticated_previously,
                currentUser.getPrincipal(), currentUser.getSession().getId()));
        } else {
            if (isPublicService(message)) {
                message.setContent(Subject.class, currentUser);
                return;
            }

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

            if (HttpSchemes.HTTP.equalsIgnoreCase(serviceUri.getScheme())) {
                String secureServiceUri;
                try {
                    secureServiceUri = UriUtil.ensureSecureUri(serviceUri,
                        ServiceConfigurationProvider.ENABLE_ENFORCED_SECURITY).toString();
                } catch (URISyntaxException e) {
                    Fault fault = new Fault(e);
                    fault.setStatusCode(Status.BAD_REQUEST.getStatusCode());
                    message.getInterceptorChain().abort();
                    throw fault;
                }

                response.setHeader(HttpHeaders.LOCATION, secureServiceUri);

                Fault fault = new Fault(new IllegalAccessException(LocalMessages.getMessage(
                    LocalMessages.authentication_request_redirected, serviceUri, secureServiceUri)));
                fault.setStatusCode(Status.TEMPORARY_REDIRECT.getStatusCode());

                message.getInterceptorChain().abort();
                LOG.debug(LocalMessages.getMessage(LocalMessages.authentication_request_redirected, serviceUri,
                    secureServiceUri));

                throw fault;
            }

            String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorization == null) {
                Fault fault = challenge(request, response,
                    new IllegalAccessException(LocalMessages.getMessage(LocalMessages.bad_subject_and_credential)));
                message.getInterceptorChain().abort();
                throw fault;
            }

            String[] decodedAuthorization = authorization.split(" ");
            String[] subjectAndCredential = null;
            if (decodedAuthorization.length != 2 || !"basic".equalsIgnoreCase(decodedAuthorization[0])) {
                Fault fault = challenge(request, response,
                    new IllegalAccessException(LocalMessages.getMessage(LocalMessages.bad_subject_and_credential)));
                message.getInterceptorChain().abort();
                throw fault;
            } else {
                subjectAndCredential = Base64.decodeToString(decodedAuthorization[1]).split(":");
                if (subjectAndCredential == null || subjectAndCredential.length != 2) {
                    Fault fault = challenge(request, response,
                        new IllegalAccessException(LocalMessages.getMessage(LocalMessages.bad_subject_and_credential)));
                    message.getInterceptorChain().abort();
                    throw fault;
                }
            }

            try {
                String subject = subjectAndCredential[0];
                String credential = subjectAndCredential[1];
                UsernamePasswordToken token = new UsernamePasswordToken(subject, credential);
                currentUser.login(token);
                message.setContent(Subject.class, currentUser);
                LOG.debug(LocalMessages.getMessage(LocalMessages.account_authenticated_just_now,
                    currentUser.getPrincipal(), currentUser.getSession().getId()));
            } catch (UnknownAccountException | IncorrectCredentialsException | LockedAccountException e) {
                Fault fault = challenge(request, response, e);
                message.getInterceptorChain().abort();
                throw fault;
            } catch (AuthenticationException ae) {
                Fault fault = challenge(request, response, ae);
                message.getInterceptorChain().abort();
                throw fault;
            }
        }
    }

    private Fault challenge(HttpServletRequest request, HttpServletResponse response, Exception e) {
        Fault fault = new Fault(e);
        String basicAuthChanllengeHeader = new StringBuilder(HttpServletRequest.BASIC_AUTH).append(" realm=\"")
            .append(request.getPathInfo()).append("\"").toString();
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, basicAuthChanllengeHeader);
        fault.setStatusCode(Status.UNAUTHORIZED.getStatusCode());
        return fault;
    }

    private boolean isPublicService(Message message) {
        Method serviceMethod = (Method) message.get(Constants.SERVICE_METHOD_KEY);
        RequiresPermissions permissions = serviceMethod.getAnnotation(RequiresPermissions.class);
        RequiresRoles roles = serviceMethod.getAnnotation(RequiresRoles.class);
        if (permissions == null || permissions.value().length == 0) {
            if (roles == null || roles.value().length == 0) {
                return true;
            }
        }
        return false;
    }
}
