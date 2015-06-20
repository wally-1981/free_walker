package com.free.walker.service.itinerary.dao;

import java.util.Map;

import javax.json.JsonObject;

import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.DependencyException;
import com.free.walker.service.itinerary.primitive.QueryTemplate;
import com.free.walker.service.itinerary.res.ResourceProvider;
import com.ibm.icu.util.Calendar;

public interface TravelResourceDAO extends HealthyDAO {
    /**
     * Search the product resources.
     * 
     * @param queryTemplate
     * @param templageParams
     * @return
     * @throws DatabaseAccessException
     */
    public JsonObject searchResources(QueryTemplate queryTemplate, Map<String, String> templageParams)
        throws DatabaseAccessException;

    /**
     * Synchronize the resources of the specified resource provider id.
     * Typically, it will be called periodically and automatically in the system
     * back end and maintain the resources data up to date.
     * 
     * @param providerId
     * @param exhausted
     * @param since
     * @return
     * @throws DependencyException
     */
    public boolean synchrinizeResources(String providerId, boolean exhausted, Calendar since)
        throws DependencyException;

    /**
     * Get the resource provider.
     * 
     * @param providerId
     * @return
     */
    public ResourceProvider getResourceProvider(String providerId);
}
