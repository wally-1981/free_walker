package com.free.walker.service.itinerary.res;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.json.JsonArray;

import com.free.walker.service.itinerary.exp.DependencyException;
import com.ibm.icu.util.Calendar;

public interface ResourceProvider {
    public ResourceProvider setup(ResourceProviderContext context);

    public boolean ping() throws DependencyException;

    public Vector<JsonArray> sync(boolean exhausted, Calendar since) throws DependencyException;

    public boolean sanitize();

    public String getProviderId();

    public String getProviderName();

    public Calendar getProviderSince();

    public class ResourceProviderContext {
        private Map<String, Object> context = new HashMap<String, Object>();

        public Object getContext(String key) {
            return context.get(key);
        }

        public String getContextAsString(String key, String defaultValue) {
            if (context.get(key) instanceof String) {
                return (String) context.get(key);
            } else {
                return defaultValue;
            }
        }

        public void setContext(String key, Object value) {
            context.put(key, value);
        }
    }
}
