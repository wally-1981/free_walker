package com.free.walker.service.itinerary.util;

import java.util.Properties;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.res.LixingResourceProvider;
import com.free.walker.service.itinerary.res.ResourceProvider;

public class ResourceProviderBuilder {
    private static final String AT = "@";

    public static ResourceProvider build(String providerId, Properties config) {
        String name = config.getProperty(Constants.providers_provider_name + AT + providerId);
        String clazz = config.getProperty(Constants.providers_provider_class + AT + providerId);
        String syncUrl = config.getProperty(Constants.providers_provider_url + AT + providerId);
        String syncUser = config.getProperty(Constants.providers_provider_user + AT + providerId);
        String syncSign = config.getProperty(Constants.providers_provider_sign + AT + providerId);

        if (LixingResourceProvider.class.getName().equals(clazz)) {
            ResourceProvider rp = new LixingResourceProvider();
            ResourceProvider.ResourceProviderContext context = new ResourceProvider.ResourceProviderContext();
            context.setContext(LixingResourceProvider.ID, providerId);
            context.setContext(LixingResourceProvider.NAME, name);
            context.setContext(LixingResourceProvider.URL, syncUrl);
            context.setContext(LixingResourceProvider.USER, syncUser);
            context.setContext(LixingResourceProvider.SIGN, syncSign);
            return rp.setup(context);
        } else {
            throw new IllegalStateException(LocalMessages.getMessage(LocalMessages.not_found_resource_provider_impl,
                providerId, clazz));
        }
    }
}
