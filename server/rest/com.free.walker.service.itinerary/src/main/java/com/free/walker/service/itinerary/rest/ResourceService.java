package com.free.walker.service.itinerary.rest;

import java.util.Date;

import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;

import com.free.walker.service.itinerary.dao.DAOFactory;
import com.free.walker.service.itinerary.dao.TravelBasicDAO;
import com.free.walker.service.itinerary.dao.TravelResourceDAO;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.DependencyException;
import com.free.walker.service.itinerary.res.ResourceProvider;
import com.ibm.icu.util.Calendar;

/**
 * <b>ResourceService</b> provides resource management, such as hotel, flight
 * resort, etc for both public resources import from 3rd party resource channels
 * as well as private resources imported by an agency and available only for
 * the owner agency.<br>
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
     * Search travel resources by simple search term. The result will be returned
     * with pagination support.<br>
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
        return null;
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
    @RequiresRoles("Admin")
    @RequiresPermissions("ManagePlatform")
    public Response syncResources(@PathParam("providerId") String providerId) {
        Calendar since = Calendar.getInstance();
        try {
            Date latestSyncDate = travelBasicDAO.getLatestResourceSyncDate(providerId);
            if (latestSyncDate != null) {
                since.setTimeInMillis(latestSyncDate.getTime());
            } else {
                ResourceProvider rp = travelResourceDAO.getResourceProvider(providerId);
                if (rp == null) {
                    return Response.status(Status.SERVICE_UNAVAILABLE).build();
                } else {
                    since.setTimeInMillis(rp.getProviderSince().getTimeInMillis());
                }
            }
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }

        try {
            if (travelResourceDAO.synchrinizeResources(providerId, true, Calendar.getInstance())) {
                return Response.status(Status.ACCEPTED).build();
            } else {
                return Response.status(Status.SERVICE_UNAVAILABLE).build();
            }
        } catch (DependencyException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }
}
