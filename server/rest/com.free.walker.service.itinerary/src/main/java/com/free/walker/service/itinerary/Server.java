package com.free.walker.service.itinerary;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.ResourceProvider;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharingFilter;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.Factory;
import org.eclipse.jetty.http.HttpSchemes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.dao.db.MyMongoSQLAccountDAOImpl;
import com.free.walker.service.itinerary.dao.db.MyMongoSQLTravelProductDAOImpl;
import com.free.walker.service.itinerary.dao.db.MyMongoSQLTravelRequirementDAOImpl;
import com.free.walker.service.itinerary.dao.memo.InMemoryTravelProductDAOImpl;
import com.free.walker.service.itinerary.dao.memo.InMemoryTravelRequirementDAOImpl;
import com.free.walker.service.itinerary.handler.AccountAuthenticationInterceptor;
import com.free.walker.service.itinerary.handler.AccountAuthorizationInterceptor;
import com.free.walker.service.itinerary.handler.AccountRecognitionInterceptor;
import com.free.walker.service.itinerary.handler.SecurityPolicyInterceptor;
import com.free.walker.service.itinerary.infra.PlatformInitializer;
import com.free.walker.service.itinerary.rest.AccountService;
import com.free.walker.service.itinerary.rest.ItineraryService;
import com.free.walker.service.itinerary.rest.PlatformService;
import com.free.walker.service.itinerary.rest.ProductService;
import com.free.walker.service.itinerary.rest.ResourceService;
import com.free.walker.service.itinerary.rest.ServiceConfigurationProvider;
import com.free.walker.service.itinerary.util.SystemConfigUtil;
import com.ibm.icu.text.MessageFormat;

public class Server {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    private static final String MODE_SINGLE_DEVO = "Devo";
    private static final String MODE_SINGLE_PROD = "Prod";

    private static final String SHIRO_PROD_CONFIG = "classpath:shiro-prod.ini";
    private static final String SHIRO_DEVO_CONFIG = "classpath:shiro-devo.ini";

    static {
        SpringBusFactory factory = new SpringBusFactory();
        Bus bus = factory.createBus(SystemConfigUtil.getApplicationSpringConfig());
        BusFactory.setDefaultBus(bus);
    }

    protected Server(String mode, boolean isSecure) throws Exception {
        if (mode.equals(MODE_SINGLE_DEVO)) {
            Factory<SecurityManager> factory = new IniSecurityManagerFactory(SHIRO_DEVO_CONFIG);
            SecurityManager securityManager = factory.getInstance();
            SecurityUtils.setSecurityManager(securityManager);
        } else if (mode.equals(MODE_SINGLE_PROD)) {
            Factory<SecurityManager> factory = new IniSecurityManagerFactory(SHIRO_PROD_CONFIG);
            SecurityManager securityManager = factory.getInstance();
            SecurityUtils.setSecurityManager(securityManager);
        } else {
            throw new IllegalArgumentException();
        }

        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();

        AccountService accountSvr = null;
        ItineraryService itinerarySvr = null;
        ProductService productSvr = null;
        if (mode.equals(MODE_SINGLE_DEVO)) {
            itinerarySvr = new ItineraryService(InMemoryTravelRequirementDAOImpl.class);
            productSvr = new ProductService(InMemoryTravelProductDAOImpl.class, InMemoryTravelRequirementDAOImpl.class);
        } else if (mode.equals(MODE_SINGLE_PROD)) {
            accountSvr = new AccountService(MyMongoSQLAccountDAOImpl.class);
            itinerarySvr = new ItineraryService(MyMongoSQLTravelRequirementDAOImpl.class);
            productSvr = new ProductService(MyMongoSQLTravelProductDAOImpl.class, MyMongoSQLTravelRequirementDAOImpl.class);
        } else {
            throw new IllegalArgumentException();
        }

        List<Class<?>> classes = new ArrayList<Class<?>>();
        classes.add(ItineraryService.class);
        classes.add(ProductService.class);
        classes.add(ResourceService.class);
        classes.add(PlatformService.class);
        if (accountSvr != null) classes.add(AccountService.class);
        List<ResourceProvider> resourceProviders = new ArrayList<ResourceProvider>();
        if (accountSvr != null) resourceProviders.add(new SingletonResourceProvider(accountSvr));
        resourceProviders.add(new SingletonResourceProvider(itinerarySvr));
        resourceProviders.add(new SingletonResourceProvider(productSvr));
        resourceProviders.add(new SingletonResourceProvider(new ResourceService()));
        resourceProviders.add(new SingletonResourceProvider(new PlatformService()));

        CrossOriginResourceSharingFilter corsFilter = new CrossOriginResourceSharingFilter();
        corsFilter.setMaxAge(60);
        corsFilter.setAllowCredentials(true);
        corsFilter.setAllowOrigins(Arrays.asList(ServiceConfigurationProvider.ALLOWED_ORIGINS));

        sf.setResourceClasses(classes);
        sf.setResourceProviders(resourceProviders);
        sf.setProvider(new JsrJsonpProvider());
        sf.setProvider(corsFilter);

        String localIp = InetAddress.getLocalHost().getHostAddress();
        String protocol = isSecure ? HttpSchemes.HTTPS : HttpSchemes.HTTP;
        LOG.info(LocalMessages.getMessage(LocalMessages.localhost_ip_founed, localIp));
        if (mode.equals(MODE_SINGLE_DEVO)) {
            sf.setAddress(MessageFormat.format("{0}://{1}:{2}", protocol, localIp,
                isSecure ? ServiceConfigurationProvider.DEVO_SEC_PORT : ServiceConfigurationProvider.DEVO_PORT));
       } else if (mode.equals(MODE_SINGLE_PROD)) {
            sf.setAddress(MessageFormat.format("{0}://{1}:{2}", protocol, localIp,
                isSecure ? ServiceConfigurationProvider.PROD_SEC_PORT : ServiceConfigurationProvider.PROD_PORT));
        } else {
            throw new IllegalArgumentException();
        }

        sf.getInInterceptors().add(new SecurityPolicyInterceptor());
        sf.getInInterceptors().add(new AccountAuthenticationInterceptor());
        sf.getInInterceptors().add(new AccountAuthorizationInterceptor());
        sf.getInInterceptors().add(new AccountRecognitionInterceptor());

        sf.create();

        File logFile = new File(System.getProperty("user.dir"), SystemConfigUtil.getLogConfig().getProperty(
            "log4j.appender.application.File"));
        LOG.info(LocalMessages.getMessage(LocalMessages.log_file_path_determined, logFile.toString()));
        LOG.info(LocalMessages.getMessage(isSecure ? LocalMessages.secure_server_started : LocalMessages.server_started,
            ManagementFactory.getRuntimeMXBean().getName(), sf.getAddress()));
    }

    public static void main(String args[]) throws Exception {
        LOG.info("Sevrer starting...");
        PlatformInitializer.init();
        new Server(args.length == 1 ? args[0].substring(1) : MODE_SINGLE_DEVO, false);
        new Server(args.length == 1 ? args[0].substring(1) : MODE_SINGLE_DEVO, true);

        synchronized (Thread.currentThread()) { Thread.currentThread().wait(); }

        LOG.info("Sevrer existing...");
        System.exit(0);
    }
}
