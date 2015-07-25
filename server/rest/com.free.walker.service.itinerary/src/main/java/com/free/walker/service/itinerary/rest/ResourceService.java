package com.free.walker.service.itinerary.rest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;

import com.free.walker.service.itinerary.basic.SearchCriteria;
import com.free.walker.service.itinerary.dao.DAOConstants;
import com.free.walker.service.itinerary.dao.DAOFactory;
import com.free.walker.service.itinerary.dao.TravelBasicDAO;
import com.free.walker.service.itinerary.dao.TravelResourceDAO;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.DependencyException;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.res.ResourceProvider;
import com.free.walker.service.itinerary.util.JsonObjectUtil;
import com.ibm.icu.util.Calendar;

/**
 * <b>ResourceService</b> provides resource management, such as hotel, flight
 * resort, etc for both public resources import from 3rd party resource channels
 * as well as private resources imported by an agency and available only for the
 * owner agency.<br>
 * <br>
 * This service supports consuming and producing data in below listed MIME
 * types:
 * <ul>
 * <li>application/json
 * </ul>
 */
@Path("/service/resource")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ResourceService {
    private TravelBasicDAO travelBasicDAO;
    private TravelResourceDAO travelResourceDAO;

    public ResourceService() {
        travelBasicDAO = DAOFactory.getTravelBasicDAO();
        travelResourceDAO = DAOFactory.getTravelResourceDAO();
    }

    /**
     * <b>PUT</b><br>
     * <br>
     * Search travel resources by simple search term. The result will be
     * returned with pagination support.<br>
     * <br>
     * <b>Sample Payload:</b><br>
     * <br>
     * <i>N/A</i><br>
     * <br>
     */
    @PUT
    @Path("/resources/")
    @RequiresPermissions("RetrieveResource")
    public Response searchResources(JsonObject searchCriteria) {
        SearchCriteria criteria = new SearchCriteria().fromJSON(searchCriteria);
        if (criteria == null) {
            return Response.status(Status.BAD_REQUEST).entity(searchCriteria).build();
        } else {
            Map<String, Object> templageParams = new HashMap<String, Object>();
            int from = criteria.getPageSize() * criteria.getPageNum();
            int size = criteria.getPageSize();
            templageParams.put(DAOConstants.elasticsearch_term, criteria.getSearchTerm());
            templageParams.put(DAOConstants.elasticsearch_from, String.valueOf(from));
            templageParams.put(DAOConstants.elasticsearch_size, String.valueOf(size));
            templageParams.put(DAOConstants.elasticsearch_sort_key, criteria.getSortKey());
            templageParams.put(DAOConstants.elasticsearch_sort_order, criteria.getSortOrder().toString());
            templageParams.put(DAOConstants.elasticsearch_sort_type, criteria.getSortType().nameValue());
            try {
                JsonObject results = travelResourceDAO.searchResource(criteria.getTemplate(), templageParams);
                return Response.status(Status.OK).entity(results.toString()).build();
            } catch (DatabaseAccessException e) {
                return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
            }
        }
    }

    /**
     * <b>PUT</b><br>
     * <br>
     * Synchronize travel resources of the given provider.<br>
     * <br>
     * <b>Sample Payload:</b><br>
     * <br>
     * <i>N/A</i><br>
     * <br>
     */
    @PUT
    @Path("/resources/{providerId}/")
    @RequiresRoles("admin")
    @RequiresPermissions("ManagePlatform")
    public Response syncResources(@PathParam("providerId") String providerId, @QueryParam("dryRun") boolean dryRun) {
        Calendar beforeSync = Calendar.getInstance(), afterSync = Calendar.getInstance();

        if (travelResourceDAO.getResourceProvider(providerId) == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        try {
            Date latestSyncDate = travelBasicDAO.getLatestResourceSyncDate(providerId);
            if (latestSyncDate != null) {
                beforeSync.setTimeInMillis(latestSyncDate.getTime());
            } else {
                ResourceProvider rp = travelResourceDAO.getResourceProvider(providerId);
                if (rp == null) {
                    return Response.status(Status.SERVICE_UNAVAILABLE).build();
                } else {
                    beforeSync.setTimeInMillis(rp.getProviderSince().getTimeInMillis());
                }
            }
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }

        try {
            JsonObject syncResult = travelResourceDAO.synchrinizeResource(providerId, true, beforeSync, dryRun);
            travelBasicDAO.setLatestResourceSyncDate(providerId, Calendar.getInstance().getTime());
            afterSync.setTimeInMillis(travelBasicDAO.getLatestResourceSyncDate(providerId).getTime());

            JsonObjectBuilder syncMeta = Json.createObjectBuilder();
            syncMeta.add(Introspection.JSONKeys.SYNC_DATE_BEFORE, beforeSync.getTimeInMillis());
            syncMeta.add(Introspection.JSONKeys.SYNC_DATE_AFTER, afterSync.getTimeInMillis());

            JsonObjectBuilder syncResultBuilder = JsonObjectUtil.merge(syncResult, Introspection.JSONKeys.SYNC_META,
                syncMeta.build());
            return Response.status(Status.OK).entity(syncResultBuilder.build()).build();
        } catch (DependencyException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }
}
