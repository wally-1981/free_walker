package com.free.walker.service.itinerary.rest;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.ResourceProvider;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;

import com.free.walker.service.itinerary.dao.db.MyMongoSQLTravelProductDAOImpl;
import com.free.walker.service.itinerary.dao.db.MyMongoSQLTravelRequirementDAOImpl;
import com.free.walker.service.itinerary.dao.memo.InMemoryTravelProductDAOImpl;
import com.free.walker.service.itinerary.dao.memo.InMemoryTravelRequirementDAOImpl;
import com.free.walker.service.itinerary.handler.SimpleSecurityContextInInterceptor;
import com.free.walker.service.itinerary.infra.PlatformInitializer;

public class Server {
    private static final String MODE_SINGLE_DEVO = "Devo";
    private static final String MODE_SINGLE_PROD = "Prod";

    protected Server(String mode) throws Exception {
        PlatformInitializer.init(); 

        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();

        ItineraryService itinerarySvr = null;
        if (mode.equals(MODE_SINGLE_DEVO)) {
            itinerarySvr = new ItineraryService(InMemoryTravelRequirementDAOImpl.class);
        } else if (mode.equals(MODE_SINGLE_PROD)) {
            itinerarySvr = new ItineraryService(MyMongoSQLTravelRequirementDAOImpl.class);
        } else {
            throw new IllegalArgumentException();
        }

        ProductService productSvr = null;
        if (mode.equals(MODE_SINGLE_DEVO)) {
            productSvr = new ProductService(InMemoryTravelProductDAOImpl.class);
        } else if (mode.equals(MODE_SINGLE_PROD)) {
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

        if (mode.equals(MODE_SINGLE_DEVO)) {
            sf.setAddress("http://localhost:9010/");
        } else if (mode.equals(MODE_SINGLE_PROD)) {
            sf.setAddress("http://localhost:9000/");
        } else {
            throw new IllegalArgumentException();
        }

        sf.getInInterceptors().add(new SimpleSecurityContextInInterceptor());

        sf.create();
    }

    public static void main(String args[]) throws Exception {
        String mode = null;
        if (args.length == 1) {
            mode = args[0].substring(1);
        } else {
            mode = MODE_SINGLE_DEVO;
        }

        new Server(mode);
        System.out.println("Server ready...");

        Thread.sleep(5 * 6000 * 1000);
        System.out.println("Server exiting");
        System.exit(0);
    }
}
