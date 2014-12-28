package com.free.walker.service.itinerary.rest;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.ResourceProvider;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.dao.db.MyMongoSQLTravelProductDAOImpl;
import com.free.walker.service.itinerary.dao.db.MyMongoSQLTravelRequirementDAOImpl;
import com.free.walker.service.itinerary.dao.memo.InMemoryTravelProductDAOImpl;
import com.free.walker.service.itinerary.dao.memo.InMemoryTravelRequirementDAOImpl;
import com.free.walker.service.itinerary.handler.SimpleSecurityContextInInterceptor;
import com.free.walker.service.itinerary.infra.PlatformInitializer;
import com.free.walker.service.itinerary.util.SystemConfigUtil;
import com.ibm.icu.text.MessageFormat;

public class Server {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    private static final String MODE_SINGLE_DEVO = "Devo";
    private static final String MODE_SINGLE_PROD = "Prod";

    protected Server(String mode) throws Exception {
        PlatformInitializer.init();

        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();

        ItineraryService itinerarySvr = null;
        ProductService productSvr = null;
        if (mode.equals(MODE_SINGLE_DEVO)) {
            itinerarySvr = new ItineraryService(InMemoryTravelRequirementDAOImpl.class);
            productSvr = new ProductService(InMemoryTravelProductDAOImpl.class);
        } else if (mode.equals(MODE_SINGLE_PROD)) {
            itinerarySvr = new ItineraryService(MyMongoSQLTravelRequirementDAOImpl.class);
            productSvr = new ProductService(MyMongoSQLTravelProductDAOImpl.class);
        } else {
            throw new IllegalArgumentException();
        }

        List<Class<?>> classes = new ArrayList<Class<?>>();
        classes.add(ItineraryService.class);
        classes.add(ProductService.class);
        classes.add(PlatformService.class);
        List<ResourceProvider> providers = new ArrayList<ResourceProvider>();
        providers.add(new SingletonResourceProvider(itinerarySvr));
        providers.add(new SingletonResourceProvider(productSvr));
        providers.add(new SingletonResourceProvider(new PlatformService()));

        sf.setResourceClasses(classes);
        sf.setResourceProviders(providers);
        sf.setProvider(new JsrJsonpProvider());

        String localIp = InetAddress.getLocalHost().getHostAddress();
        LOG.info(LocalMessages.getMessage(LocalMessages.localhost_ip_founed, localIp));
        if (mode.equals(MODE_SINGLE_DEVO)) {
            sf.setAddress(MessageFormat.format("http://{0}:9010", localIp));
        } else if (mode.equals(MODE_SINGLE_PROD)) {
            sf.setAddress(MessageFormat.format("http://{0}:9000", localIp));
        } else {
            throw new IllegalArgumentException();
        }

        sf.getInInterceptors().add(new SimpleSecurityContextInInterceptor());

        sf.create();

        File logFile = new File(System.getProperty("user.dir"), SystemConfigUtil.getLogConfig().getProperty(
            "log4j.appender.application.File"));
        LOG.info(LocalMessages.getMessage(LocalMessages.log_file_path_determined, logFile.toString()));
        LOG.info(LocalMessages.getMessage(LocalMessages.server_started, ManagementFactory.getRuntimeMXBean().getName(),
            sf.getAddress()));

        synchronized (this) {
            this.wait();
        }
    }

    public static void main(String args[]) throws Exception {
        LOG.info("Sevrer starting...");
        new Server(args.length == 1 ? args[0].substring(1) : MODE_SINGLE_DEVO);

        LOG.info("Sevrer existing...");
        System.exit(0);
    }
}
