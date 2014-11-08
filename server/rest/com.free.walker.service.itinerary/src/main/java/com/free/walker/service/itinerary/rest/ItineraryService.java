package com.free.walker.service.itinerary.rest;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
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

import com.free.walker.service.itinerary.Enumable;
import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.City;
import com.free.walker.service.itinerary.basic.Flight;
import com.free.walker.service.itinerary.basic.Hotel;
import com.free.walker.service.itinerary.basic.Introspection;
import com.free.walker.service.itinerary.basic.Resort;
import com.free.walker.service.itinerary.basic.Train;
import com.free.walker.service.itinerary.basic.TravelLocation;
import com.free.walker.service.itinerary.dao.DAOFactory;
import com.free.walker.service.itinerary.dao.InMemoryTravelRequirementDAOImpl;
import com.free.walker.service.itinerary.dao.TravelRequirementDAO;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.req.HotelRequirement;
import com.free.walker.service.itinerary.req.ItineraryRequirement;
import com.free.walker.service.itinerary.req.ResortRequirement;
import com.free.walker.service.itinerary.req.TrafficRequirement;
import com.free.walker.service.itinerary.req.TravelProposal;
import com.free.walker.service.itinerary.req.TravelRequirement;
import com.free.walker.service.itinerary.util.JsonObjectHelper;
import com.free.walker.service.itinerary.util.UuidUtil;

@Path("/service/itinerary/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
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

        {
            JsonObjectBuilder keyDataBuilder = Json.createObjectBuilder();
            Field[] fields = Introspection.JSONKeys.class.getFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    try {
                        keyDataBuilder.add(field.getName(), (String) field.get(Introspection.JSONKeys.class));
                    } catch (JsonException e) {
                        LOG.error(LocalMessages.introspection_failure, e);
                        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                    } catch (Exception e) {
                        LOG.error(LocalMessages.introspection_failure, e);
                        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                    }
                }
            }  
            resBuilder.add("key_data", keyDataBuilder);
        }

        {
            JsonObjectBuilder valueDataBuilder = Json.createObjectBuilder();
            Field[] fields = Introspection.JSONValues.class.getFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    try {
                        if (field.get(Introspection.JSONValues.class) instanceof Enumable) {
                            valueDataBuilder.add(field.getName(),
                                ((Enumable) field.get(Introspection.JSONValues.class)).enumValue());
                        } else {
                            valueDataBuilder.add(field.getName(), (String) field.get(Introspection.JSONKeys.class));
                        }
                    } catch (JsonException e) {
                        LOG.error(LocalMessages.introspection_failure, e);
                        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                    } catch (Exception e) {
                        LOG.error(LocalMessages.introspection_failure, e);
                        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                    }
                }
            }  
            resBuilder.add("value_data", valueDataBuilder);
        }

        {            
            JsonArrayBuilder sampleDataBuilder = Json.createArrayBuilder();

            TravelLocation dept = new TravelLocation(City.BEIJING);
            TravelLocation dest = new TravelLocation(City.BOSTON);
            ItineraryRequirement itineraryRequirement = new ItineraryRequirement(dept, dest);
            TravelProposal proposal = new TravelProposal(itineraryRequirement);
            sampleDataBuilder.add(proposal.toJSON());

            TravelRequirement hotelRequirementA = new HotelRequirement(6);
            TravelRequirement hotelRequirementB = new HotelRequirement(6, Introspection.JSONValues.HOTEL_STD_5);
            TravelRequirement hotelRequirementC = new HotelRequirement(6, new Hotel());
            sampleDataBuilder.add(hotelRequirementA.toJSON());
            sampleDataBuilder.add(hotelRequirementB.toJSON());
            sampleDataBuilder.add(hotelRequirementC.toJSON());
            
            TravelRequirement resortRequirementA = new ResortRequirement(Introspection.JSONValues.RANGE_12_18);
            TravelRequirement resortRequirementB = new ResortRequirement(Introspection.JSONValues.RANGE_12_18,
                Introspection.JSONValues.RESORT_STD_2A);
            TravelRequirement resortRequirementC = new ResortRequirement(Introspection.JSONValues.RANGE_12_18,
                new Resort());
            sampleDataBuilder.add(resortRequirementA.toJSON());
            sampleDataBuilder.add(resortRequirementB.toJSON());
            sampleDataBuilder.add(resortRequirementC.toJSON());
            
            TravelRequirement trafficRequirementA = new TrafficRequirement(Introspection.JSONValues.TRAFFIC_TOOL_TRAIN);
            TravelRequirement trafficRequirementB = new TrafficRequirement(
                Introspection.JSONValues.TRAFFIC_TOOL_FLIGHT, Introspection.JSONValues.RANGE_18_23);
            TravelRequirement trafficRequirementC = new TrafficRequirement(
                Introspection.JSONValues.TRAFFIC_TOOL_FLIGHT, Arrays.asList(Introspection.JSONValues.RANGE_00_06,
                    Introspection.JSONValues.RANGE_18_23));
            TravelRequirement trafficRequirementD = new TrafficRequirement(new Flight("CA1981"));
            TravelRequirement trafficRequirementE = new TrafficRequirement(new Train("Z38"));            
            sampleDataBuilder.add(trafficRequirementA.toJSON());
            sampleDataBuilder.add(trafficRequirementB.toJSON());
            sampleDataBuilder.add(trafficRequirementC.toJSON());
            sampleDataBuilder.add(trafficRequirementD.toJSON());
            sampleDataBuilder.add(trafficRequirementE.toJSON());

            resBuilder.add("sample_data", sampleDataBuilder);
        }

        return Response.ok(resBuilder.build()).build();
    }

    @GET
    @Path("/proposals/")
    public Response searchProposals(
        @QueryParam("pageNum") String pageNum,
        @QueryParam("pageSize") String pageSize,
        @QueryParam("searchTerm") String searchTerm) {
        return Response.ok().build();
    }

    @GET
    @Path("/proposals/{proposalId}/")
    public Response getProposal(@PathParam("proposalId") String proposalId) {
        TravelRequirement proposal;
        try {
            UUID propId = UuidUtil.fromUuidStr(proposalId);
            proposal = travelRequirementDAO.getRequirement(propId);
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
        if (Introspection.JSONValues.PROPOSAL.equals(requirementType)) {
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
                itinerary = travelRequirementDAO.getRequirement(reqId);
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
            requirement = travelRequirementDAO.getRequirement(reqId);
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

    @DELETE
    @Path("/requirements/{requirementId}/")
    public Response deleteRequirement(@PathParam("requirementId") String requirementId) {
        try {
            travelRequirementDAO.removeRequirement(UuidUtil.fromUuidStr(requirementId));
            return Response.ok().build();
        } catch (InvalidTravelReqirementException e) {
            return Response.ok(e.toJSON()).build();
        }
    }

    @PUT
    @Path("/requirements/")
    public Response updateRequirement(JsonObject travelRequirement) {
        try {
            UUID requirementId = UuidUtil.fromUuidStr(travelRequirement.getString(Introspection.JSONKeys.UUID));
            TravelRequirement requirement = JsonObjectHelper.toRequirement(travelRequirement);
            travelRequirementDAO.updateRequirement(requirementId, requirement);
            return Response.ok().build();
        } catch (InvalidTravelReqirementException e) {
            return Response.ok(e.toJSON()).build();
        }
    }

    @POST
    @Path("/proposals/")
    public Response addProposal(JsonObject travelProposal) {
        try {
            TravelRequirement proposal = JsonObjectHelper.toRequirement(travelProposal);
            String proposalId = travelRequirementDAO.createProposal((TravelProposal) proposal).toString();
            JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, proposalId).build();
            return Response.ok(res).build();
        } catch (InvalidTravelReqirementException e) {
            return Response.ok(e.toJSON()).build();
        }
    }

    @POST
    @Path("/itineraries/{proposalId}")
    public Response addItinerary(@PathParam("proposalId") String proposalId, JsonObject itineraryRequirement) {
        try {
            TravelRequirement itinerary = JsonObjectHelper.toRequirement(itineraryRequirement);
            String itineraryId = travelRequirementDAO.addRequirement(UuidUtil.fromUuidStr(proposalId), itinerary)
                .toString();
            JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, itineraryId).build();
            return Response.ok(res).build();
        } catch (InvalidTravelReqirementException e) {
            return Response.ok(e.toJSON()).build();
        }
    }

    @POST
    @Path("/requirements/{proposalId}")
    public Response addRequirement(@PathParam("proposalId") String proposalId, JsonObject travelRequirement) {
        try {
            TravelRequirement requirement = JsonObjectHelper.toRequirement(travelRequirement);
            String requirementId = travelRequirementDAO.addRequirement(UuidUtil.fromUuidStr(proposalId), requirement)
                .toString();
            JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, requirementId).build();
            return Response.ok(res).build();
        } catch (InvalidTravelReqirementException e) {
            return Response.ok(e.toJSON()).build();
        }
    }

    @POST
    @Path("/requirements/{proposalId}/{itineraryId}")
    public Response addRequirement(@PathParam("proposalId") String proposalId,
        @PathParam("itineraryId") String itineraryId, JsonObject travelRequirement) {
        try {
            TravelRequirement requirement = JsonObjectHelper.toRequirement(travelRequirement);
            String requirementId = travelRequirementDAO.addRequirement(UuidUtil.fromUuidStr(proposalId), UuidUtil.fromUuidStr(itineraryId),
                requirement).toString();
            JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, requirementId).build();
            return Response.ok(res).build();
        } catch (InvalidTravelReqirementException e) {
            return Response.ok(e.toJSON()).build();
        }
    }
}
