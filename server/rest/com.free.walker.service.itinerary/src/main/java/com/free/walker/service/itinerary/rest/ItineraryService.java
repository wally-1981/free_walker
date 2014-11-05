package com.free.walker.service.itinerary.rest;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.dao.DAOFactory;
import com.free.walker.service.itinerary.dao.InMemoryTravelRequirementDAOImpl;
import com.free.walker.service.itinerary.dao.TravelRequirementDAO;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.req.ItineraryRequirement;
import com.free.walker.service.itinerary.req.TravelProposal;
import com.free.walker.service.itinerary.req.TravelRequirement;
import com.free.walker.service.itinerary.util.UuidUtil;

@Path("/service/itinerary/")
@Produces(MediaType.APPLICATION_JSON)
public class ItineraryService {
    private static Logger LOG = LoggerFactory.getLogger(ItineraryService.class);

    private TravelRequirementDAO travelRequirementDAO;

    public ItineraryService() {
        travelRequirementDAO = DAOFactory.getTravelRequirementDAO(InMemoryTravelRequirementDAOImpl.class.getName());
    }

    @GET
    @Path("/introspection")
    public Response getIntrospection() {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        Field[] fields = Constants.JSONKeys.class.getFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                try {
                    resBuilder.add(field.getName(), (String) field.get(Constants.JSONKeys.class));
                } catch (JsonException e) {
                    LOG.error(LocalMessages.introspection_failure, e);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                } catch (Exception e) {
                    LOG.error(LocalMessages.introspection_failure, e);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                }
            }
        }
        return Response.ok(resBuilder.build()).build();
    }

    @GET
    @Path("/proposals/{proposalId}/")
    public Response getProposal(@PathParam("proposalId") String proposalId) {
        TravelRequirement proposal;
        try {
            UUID propId = UuidUtil.fromUuidStr(proposalId);
            proposal = travelRequirementDAO.getTravelRequirement(propId);
            if (proposal.isProposal()) {
                return Response.ok(proposal.toJSON()).build();
            } else {
                return Response.status(Status.NOT_FOUND).build();
            }
        } catch (InvalidTravelReqirementException e) {
            return Response.ok(e.toJSON()).build();
        }
    }

    @GET
    @Path("/itineraries/{requirementId}/")
    public Response getItinerary(@PathParam("requirementId") String requirementId,
        @QueryParam("requirementType") String requirementType) {
        if (Constants.JSONKeys.PROPOSAL.equals(requirementType)) {
            List<TravelRequirement> itineraries;
            try {
                UUID reqId = UuidUtil.fromUuidStr(requirementId);
                itineraries = travelRequirementDAO.getItineraryRequirements(reqId);
                JsonArrayBuilder resBuilder = Json.createArrayBuilder();
                for (int i = 0; i < itineraries.size(); i++) {
                    resBuilder.add(itineraries.get(i).toJSON());
                }
                return Response.ok(resBuilder.build()).build();
            } catch (InvalidTravelReqirementException e) {
                return Response.ok(e.toJSON()).build();
            }
        } else {
            TravelRequirement itinerary;
            try {
                UUID reqId = UuidUtil.fromUuidStr(requirementId);
                itinerary = travelRequirementDAO.getTravelRequirement(reqId);
                if (itinerary.isItinerary()) {
                    return Response.ok(itinerary.toJSON()).build();
                } else {
                    return Response.status(Status.NOT_FOUND).build();
                }
            } catch (InvalidTravelReqirementException e) {
                return Response.ok(e.toJSON()).build();
            }
        }
    }

    @GET
    @Path("/requirements/{requirementId}/")
    public Response getRequirement(@PathParam("requirementId") String requirementId) {
        TravelRequirement requirement;
        try {
            UUID reqId = UuidUtil.fromUuidStr(requirementId);
            requirement = travelRequirementDAO.getTravelRequirement(reqId);
            if (!requirement.isProposal() && !requirement.isItinerary()) {
                return Response.ok(requirement.toJSON()).build();
            } else {
                return Response.status(Status.NOT_FOUND).build();
            }
        } catch (InvalidTravelReqirementException e) {
            return Response.ok(e.toJSON()).build();
        }
    }

    @GET
    @Path("/requirements/{proposalId}/{itineraryId}/")
    public Response getRequirements(@PathParam("proposalId") String proposalId,
        @PathParam("itineraryId") String itineraryId) {
        List<TravelRequirement> requirements;
        try {
            UUID propId = UuidUtil.fromUuidStr(proposalId);
            UUID itinId = UuidUtil.fromUuidStr(itineraryId);
            requirements = travelRequirementDAO.getRequirements(propId, itinId);
            JsonArrayBuilder resBuilder = Json.createArrayBuilder();
            for (int i = 0; i < requirements.size(); i++) {
                resBuilder.add(requirements.get(i).toJSON());
            }
            return Response.ok(resBuilder.build()).build();
        } catch (InvalidTravelReqirementException e) {
            return Response.ok(e.toJSON()).build();
        }
    }

    @POST
    @Path("/proposals/")
    public Response addProposal(TravelProposal travelProposal) {
        return Response.ok().build();
    }

    @POST
    @Path("/itineraries/{proposalId}")
    public Response addItinerary(@PathParam("proposalId") String proposalId, ItineraryRequirement itineraryRequirement) {
        return Response.ok().build();
    }

    @POST
    @Path("/requirements/{itineraryId}")
    public Response addRequirement(@PathParam("itineraryId") String itineraryId, TravelRequirement travelRequirement) {
        return Response.ok().build();
    }

    @PUT
    @Path("/requirements/")
    public Response updateRequirement(TravelRequirement travelRequirement) {
        return Response.notModified().build();
    }

    @DELETE
    @Path("/itineraries/{itineraryId}/")
    public Response deleteItinerary(@PathParam("itineraryId") String id) {
        return Response.notModified().build();
    }

    @DELETE
    @Path("/requirements/{requirementId}/")
    public Response deleteRequirement(@PathParam("requirementId") String id) {
        return Response.notModified().build();
    }
}
