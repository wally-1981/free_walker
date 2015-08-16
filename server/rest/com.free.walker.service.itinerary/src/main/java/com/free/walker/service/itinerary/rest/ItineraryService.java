package com.free.walker.service.itinerary.rest;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CancellationException;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.MRRoutine;
import com.free.walker.service.itinerary.basic.Account;
import com.free.walker.service.itinerary.dao.DAOFactory;
import com.free.walker.service.itinerary.dao.TravelBasicDAO;
import com.free.walker.service.itinerary.dao.TravelRequirementDAO;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.req.ItineraryRequirement;
import com.free.walker.service.itinerary.req.TravelProposal;
import com.free.walker.service.itinerary.req.TravelRequirement;
import com.free.walker.service.itinerary.task.AgencyElectionReduceRoutine;
import com.free.walker.service.itinerary.task.AgencyElectionRoutine;
import com.free.walker.service.itinerary.task.AgencyElectionTask;
import com.free.walker.service.itinerary.util.JsonObjectHelper;
import com.free.walker.service.itinerary.util.UuidUtil;
import com.ibm.icu.util.Calendar;

/**
 * <b>ItineraryService</b> provides data access for travel proposal, itinerary
 * as well as requirement. A proposal can be added and composed by this service by
 * adding or inserting itinerary or requirement into the proposal and updating an
 * existing requirement.<br>
 * <br>
 * Besides data access, this service can serve proposal submission, and the
 * submitted proposals can be retrieved by an agency candidate or the proposal
 * owner for grabbing or checking my proposal list. The agencies grabbed the
 * proposal will be further elected by the system automatically in the back-end.
 * Finally, by this service, the elected agencies can retrieve their proposals
 * for product design and then join the bidding with other elected agencies.<br>
 * <br>
 * This service supports consuming and producing data in below listed MIME
 * types:
 * <ul>
 * <li>application/json
 * </ul>
 */
@Path("/service/itinerary/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItineraryService {
    private TravelBasicDAO travelBasicDAO;
    private TravelRequirementDAO travelRequirementDAO;

    public ItineraryService(Class<?> daoClass) {
        travelBasicDAO = DAOFactory.getTravelBasicDAO();
        travelRequirementDAO = DAOFactory.getTravelRequirementDAO(daoClass.getName());
    }

    /**
     * <b>GET</b><br>
     * <br>
     * Search proposals with simple search term. Given the pageNum and pageSize
     * for retrieving the search results in specific page.<br>
     */
    @GET
    @Path("/proposals/")
    @RequiresRoles("admin")
    @RequiresPermissions("SearchProposal")
    public Response searchProposals(@QueryParam("pageNum") String pageNum, @QueryParam("pageSize") String pageSize,
        @QueryParam("searchTerm") String searchTerm) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    /**
     * <b>GET</b><br>
     * <br>
     * Retrieve proposals submitted by the current requester within one week.<br>
     * <br>
     * The requester can specify <i>?pastDays=n</i> for retrieving old proposals
     * submitted by him/her. The max n can be set to 14 days; or the query will
     * fell back to 7 days.<br>
     */
    @GET
    @Context
    @Path("/proposals/my/")
    @RequiresRoles("customer")
    @RequiresPermissions("RetrieveProposal")
    public Response getProposals(@Context MessageContext msgCntx, @QueryParam("pastDays") int n) {
        Account acnt = (Account) msgCntx.getContent(Account.class);
        UUID acntId = UuidUtil.fromUuidStr(acnt.getUuid());

        Calendar daysAgo = Calendar.getInstance();
        daysAgo.add(Calendar.DATE, -(Math.abs(n) > 14 || Math.abs(n) == 0 ? 7 : Math.abs(n)));

        try {
            List<TravelProposal> proposals = travelRequirementDAO.getTravelProposalsByAccount(acntId, daysAgo, 7);

            if (proposals.isEmpty()) {
                JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, acntId.toString()).build();
                return Response.status(Status.NOT_FOUND).entity(res).build();
            }

            JsonArrayBuilder resBuilder = Json.createArrayBuilder();
            for (int i = 0; i < proposals.size(); i++) {
                resBuilder.add(proposals.get(i).toJSON());
            }
            return Response.ok(resBuilder.build()).build();
        } catch (InvalidTravelReqirementException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    /**
     * <b>GET</b><br>
     * <br>
     * Retrieve proposals for the elected agency within one week by the
     * specified agency identifier.<br>
     * <br>
     * There is no way to retrieve proposals by agency identifier if there is no
     * product was created by the agency within the week.<br>
     */
    @GET
    @Path("/proposals/agencies/{agencyId}/")
    @RequiresRoles("agency")
    @RequiresPermissions("RetrieveProposal")
    public Response getProposals(@PathParam("agencyId") String agencyId) {
        Calendar weekAgo = Calendar.getInstance();
        weekAgo.add(Calendar.DATE, -7);

        try {
            List<TravelProposal> proposals = travelRequirementDAO.getTravelProposalsByAgency(
                UuidUtil.fromUuidStr(agencyId), weekAgo, 7);

            if (proposals.isEmpty()) {
                JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, agencyId).build();
                return Response.status(Status.NOT_FOUND).entity(res).build();
            }

            JsonArrayBuilder resBuilder = Json.createArrayBuilder();
            for (int i = 0; i < proposals.size(); i++) {
                resBuilder.add(proposals.get(i).toJSON());
            }
            return Response.ok(resBuilder.build()).build();
        } catch (InvalidTravelReqirementException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    /**
     * <b>POST</b><br>
     * <br>
     * Submit the proposal by the given proposal identifier in terms of the
     * requester. The proposal will be submitted to server for agency election,
     * after which the proposal will be open for bidding for elected agencies
     * only, and only the agencies in the first group will be opened by the
     * proposal submission.<br>
     * <br>
     * <b>Sample Payload:</b><br>
     * <br>
     * <i>N/A</i><br>
     * <br>
     */
    @POST
    @Context
    @Path("/proposals/agencies/{proposalId}/")
    @RequiresRoles("customer")
    @RequiresPermissions("SubmitProposal")
    public Response submitProposal(@PathParam("proposalId") String proposalId, @Context MessageContext msgCntx,
        @QueryParam("delayMins") int delayMins) {
        try {
            Account acnt = (Account) msgCntx.getContent(Account.class);
            travelRequirementDAO.startProposalBid(UuidUtil.fromUuidStr(proposalId), acnt);
            List<TravelRequirement> itineraries = travelRequirementDAO.getItineraryRequirements(UuidUtil
                .fromUuidStr(proposalId));
            if (itineraries.size() == 1) {
                ItineraryRequirement itinerary = (ItineraryRequirement) itineraries.get(0);
                MRRoutine agencyElectionRoutine = new AgencyElectionRoutine(proposalId, travelBasicDAO,
                    travelRequirementDAO, itinerary.getDeparture(), itinerary.getDestination());
                AgencyElectionTask.schedule(agencyElectionRoutine, proposalId, delayMins, travelBasicDAO,
                    travelRequirementDAO);
            } else if (itineraries.size() > 1) {
                ItineraryRequirement itinerary = (ItineraryRequirement) itineraries.get(0);
                MRRoutine agencyElectionRoutine = new AgencyElectionRoutine(proposalId, travelBasicDAO,
                    travelRequirementDAO, itinerary.getDeparture());
                AgencyElectionTask.schedule(agencyElectionRoutine, proposalId, delayMins, travelBasicDAO,
                    travelRequirementDAO);
            } else {
                return Response.status(Status.CONFLICT).build();
            }
            return Response.status(Status.ACCEPTED).build();
        } catch (CancellationException e) {
            return Response.status(Status.NO_CONTENT).build();
        } catch (InvalidTravelReqirementException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        } catch (IllegalStateException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.getMessage()).build();
        }
    }

    /**
     * <b>POST</b><br>
     * <br>
     * Resubmit the proposal to elected agencies in the next group. The proposal
     * will be open to them for bidding.<br>
     * <br>
     * A proposal can not be resubmitted in short interval in hours, or the
     * resubmitted request will be ignored.<br>
     * <br>
     * <b>Sample Payload:</b><br>
     * <br>
     * <i>N/A</i><br>
     * <br>
     */
    @POST
    @Path("/proposals/agencies/{proposalId}/next/")
    @RequiresRoles("customer")
    @RequiresPermissions("SubmitProposal")
    public Response resubmitProposal(@PathParam("proposalId") String proposalId) {
        try {
            List<TravelRequirement> itineraries = travelRequirementDAO.getItineraryRequirements(UuidUtil
                .fromUuidStr(proposalId));
            if (itineraries.size() == 1) {
                ItineraryRequirement itinerary = (ItineraryRequirement) itineraries.get(0);
                MRRoutine agencyElectionRoutine = new AgencyElectionReduceRoutine(proposalId, travelBasicDAO,
                    travelRequirementDAO, itinerary.getDeparture(), itinerary.getDestination());
                AgencyElectionTask.schedule(agencyElectionRoutine, proposalId, -1, travelBasicDAO,
                    travelRequirementDAO);
            } else if (itineraries.size() > 1) {
                ItineraryRequirement itinerary = (ItineraryRequirement) itineraries.get(0);
                MRRoutine agencyElectionRoutine = new AgencyElectionReduceRoutine(proposalId, travelBasicDAO,
                    travelRequirementDAO, itinerary.getDeparture());
                AgencyElectionTask.schedule(agencyElectionRoutine, proposalId, -1, travelBasicDAO,
                    travelRequirementDAO);
            } else {
                return Response.status(Status.CONFLICT).build();
            }
            return Response.status(Status.ACCEPTED).build();
        } catch (CancellationException e) {
            return Response.status(Status.NO_CONTENT).build();
        } catch (InvalidTravelReqirementException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        } catch (IllegalStateException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.getMessage()).build();
        }
    }

    /**
     * <b>GET</b><br>
     * <br>
     * Retrieve selected proposals for the agency candidate specified by the
     * agency identifier.<br>
     * <br>
     * The returned proposals will contain proposal summary only without details
     * of the proposals. The agency can refer to the summary to determine if
     * joining the election or not by grabbing the proposal or ignore it.<br>
     * <br>
     * The proposal details will be visible to the finally elected agencies.<br>
     */
    @GET
    @Path("/proposals/agencies/selected/{agencyId}/")
    @RequiresRoles("agency")
    @RequiresPermissions("RetrieveProposal")
    public Response getSelectedProposals(@PathParam("agencyId") String agencyId) {
        try {
            Map<String, String> proposalsSummary = travelBasicDAO.getProposals4AgencyCandidate(agencyId,
                AgencyElectionTask.getElectionWindow());
            JsonArrayBuilder proposalIds = Json.createArrayBuilder();
            JsonArrayBuilder proposalSummaries = Json.createArrayBuilder();
            Iterator<String> proposalIdIter = proposalsSummary.keySet().iterator();
            while (proposalIdIter.hasNext()) {
                String proposalId = proposalIdIter.next();
                String proposalSummary = proposalsSummary.get(proposalId);
                proposalIds.add(UuidUtil.fromCmpUuidStr(proposalId).toString());
                proposalSummaries.add(proposalSummary);
            }
            JsonObject summaries = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, proposalIds)
                .add(Introspection.JSONKeys.SUMMARY, proposalSummaries).build();
            return Response.ok(summaries).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    /**
     * <b>PUT</b><br>
     * <br>
     * Grab the proposal, in terms of the agency specified by the agency
     * identifier, to confirm the intention of joinning the agency election of
     * the proposal specified by the proposal identifier.<br>
     * <br>
     * The agency election will be taken based on those agencies having
     * confirmation, all the agencies not confirmed will be ignored by the
     * election.<br>
     * <br>
     * <b>Sample Payload:</b><br>
     * <br>
     * <i>N/A</i><br>
     * <br>
     */
    @PUT
    @Path("/proposals/agencies/{proposalId}/{agencyId}/")
    @RequiresRoles("agency")
    @RequiresPermissions("GrabProposal")
    public Response grabProposal(@PathParam("proposalId") String proposalId, @PathParam("agencyId") String agencyId) {
        try {
            travelBasicDAO.markAgencyCandidateAsResponded(proposalId, agencyId);
            return Response.status(Status.OK).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    /**
     * <b>GET</b><br>
     * <br>
     * Retrieve the proposal by the given proposal identifier.<br>
     */
    @GET
    @Path("/proposals/{proposalId}/")
    @RequiresPermissions("RetrieveProposal")
    public Response getProposal(@PathParam("proposalId") String proposalId) {
        TravelRequirement proposal;
        try {
            UUID propId = UuidUtil.fromUuidStr(proposalId);
            proposal = travelRequirementDAO.getRequirement(propId, Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL);
            if (proposal == null) {
                JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, proposalId).build();
                return Response.status(Status.NOT_FOUND).entity(res).build();
            }

            if (proposal.isProposal()) {
                return Response.ok(proposal.toJSON()).build();
            } else {
                throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.proposal_not_found,
                    propId), propId);
            }
        } catch (InvalidTravelReqirementException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    /**
     * <b>GET</b><br>
     * <br>
     * Retrieve the itineraries by the given requirement identifier and the
     * given reqirement type in <i>?requirementType=itinerary</i> or
     * <i>?requirementType=proposal</i> for the identified itinerary or all
     * itineraries in the proposal.<br>
     */
    @GET
    @Path("/itineraries/{requirementId}/")
    @RequiresPermissions("RetrieveProposal")
    public Response getItinerary(@PathParam("requirementId") String requirementId,
        @QueryParam("requirementType") String requirementType) {
        if (Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL.equals(requirementType)) {
            List<TravelRequirement> itineraries;
            try {
                UUID reqId = UuidUtil.fromUuidStr(requirementId);
                itineraries = travelRequirementDAO.getItineraryRequirements(reqId);
                if (itineraries.isEmpty()) {
                    JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, requirementId).build();
                    return Response.status(Status.NOT_FOUND).entity(res).build();
                }

                JsonArrayBuilder resBuilder = Json.createArrayBuilder();
                for (int i = 0; i < itineraries.size(); i++) {
                    resBuilder.add(itineraries.get(i).toJSON());
                }
                return Response.ok(resBuilder.build()).build();
            } catch (InvalidTravelReqirementException e) {
                return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
            } catch (DatabaseAccessException e) {
                return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
            }
        } else if (Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY.equals(requirementType)) {
            TravelRequirement itinerary;
            try {
                UUID reqId = UuidUtil.fromUuidStr(requirementId);
                itinerary = travelRequirementDAO.getRequirement(reqId, requirementType);
                if (itinerary == null) {
                    JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, requirementId).build();
                    return Response.status(Status.NOT_FOUND).entity(res).build();
                }

                if (itinerary.isItinerary()) {
                    return Response.ok(itinerary.toJSON()).build();
                } else {
                    throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                        LocalMessages.itinerary_not_found, reqId), reqId);
                }
            } catch (InvalidTravelReqirementException e) {
                return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
            } catch (DatabaseAccessException e) {
                return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
            }
        } else {
            return Response.status(Status.BAD_REQUEST).entity(Json.createObjectBuilder().build()).build();
        }
    }

    /**
     * <b>GET</b><br>
     * <br>
     * Retrieve the requirement by the given requirement identifier.<br>
     */
    @GET
    @Path("/requirements/{requirementId}/")
    @RequiresPermissions("RetrieveProposal")
    public Response getRequirement(@PathParam("requirementId") String requirementId) {
        TravelRequirement requirement;
        try {
            UUID reqId = UuidUtil.fromUuidStr(requirementId);
            requirement = travelRequirementDAO.getRequirement(reqId,
                Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT);
            if (requirement == null) {
                return Response.status(Status.NOT_FOUND).build();
            }

            if (!requirement.isProposal() && !requirement.isItinerary()) {
                return Response.ok(requirement.toJSON()).build();
            } else {
                throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                    LocalMessages.requirement_not_found, reqId), reqId);
            }
        } catch (InvalidTravelReqirementException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    /**
     * <b>GET</b><br>
     * <br>
     * Retrieve all requirements in the itinerary and proposal by the given
     * itinerary and proposal identifiers.<br>
     */
    @GET
    @Path("/requirements/{proposalId}/{itineraryId}/")
    @RequiresPermissions("RetrieveProposal")
    public Response getRequirements(@PathParam("proposalId") String proposalId,
        @PathParam("itineraryId") String itineraryId) {
        try {
            UUID propId = UuidUtil.fromUuidStr(proposalId);
            UUID itinId = UuidUtil.fromUuidStr(itineraryId);
            List<TravelRequirement> requirements = travelRequirementDAO.getRequirements(propId, itinId);
            if (requirements.isEmpty()) {
                JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, itineraryId).build();
                return Response.status(Status.NOT_FOUND).entity(res).build();
            }

            JsonArrayBuilder resBuilder = Json.createArrayBuilder();
            for (int i = 0; i < requirements.size(); i++) {
                resBuilder.add(requirements.get(i).toJSON());
            }
            return Response.ok(resBuilder.build()).build();
        } catch (InvalidTravelReqirementException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    /**
     * <b>DELETE</b><br>
     * <br>
     * Remove the requirment by the given requirement and proposal identifers.<br>
     */
    @DELETE
    @Path("/requirements/{proposalId}/{requirementId}/")
    @RequiresPermissions("ModifyProposal")
    public Response deleteRequirement(@PathParam("proposalId") String proposalId,
        @PathParam("requirementId") String requirementId) {
        try {
            UUID deletedRequirementId = travelRequirementDAO.removeRequirement(UuidUtil.fromUuidStr(proposalId),
                UuidUtil.fromUuidStr(requirementId));
            if (deletedRequirementId == null) {
                JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, requirementId).build();
                return Response.status(Status.NOT_FOUND).entity(res).build();
            }

            JsonObject res = Json.createObjectBuilder()
                .add(Introspection.JSONKeys.UUID, deletedRequirementId.toString()).build();
            return Response.ok(res).build();
        } catch (InvalidTravelReqirementException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    /**
     * <b>DELETE</b><br>
     * <br>
     * Remove the itinerary by the given itinerary and proposal identifers. All
     * requirements in the removing itinerary will be also be removed.<br>
     */
    @DELETE
    @Path("/itineraries/{proposalId}/{itineraryId}/")
    @RequiresPermissions("ModifyProposal")
    public Response deleteItinerary(@PathParam("proposalId") String proposalId,
        @PathParam("itineraryId") String itineraryId) {
        try {
            UUID deletedItineraryId = travelRequirementDAO.removeRequirement(UuidUtil.fromUuidStr(proposalId),
                UuidUtil.fromUuidStr(itineraryId));
            if (deletedItineraryId == null) {
                JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, itineraryId).build();
                return Response.status(Status.NOT_FOUND).entity(res).build();
            }

            JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, deletedItineraryId.toString())
                .build();
            return Response.ok(res).build();
        } catch (InvalidTravelReqirementException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    /**
     * <b>PUT</b><br>
     * <br>
     * Update the requirement by the requirement in the put payload. A valid
     * requirement identifier should be contained in the payload.<br>
     * <br>
     * <b>Sample Payload:</b><br>
     * <br>
     * <i>http://&lt;host_name&gt;:&lt;port&gt;/service/platform/introspection?section=requirement_sample_data&part=2&range=12</i><br>
     * <br>
     */
    @PUT
    @Path("/requirements/")
    @RequiresPermissions("ModifyProposal")
    public Response updateRequirement(JsonObject travelRequirement) {
        try {
            TravelRequirement requirement = JsonObjectHelper.toRequirement(travelRequirement, true);
            UUID updatedRequirementId = travelRequirementDAO.updateRequirement(requirement);
            JsonObject res = Json.createObjectBuilder()
                .add(Introspection.JSONKeys.UUID, updatedRequirementId.toString()).build();
            return Response.ok(res).build();
        } catch (InvalidTravelReqirementException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    /**
     * <b>POST</b><br>
     * <br>
     * Add a proposal given in the post payload. The identier of the newly
     * created proposal will be returned.<br>
     * <br>
     * <b>Sample Payload:</b><br>
     * <br>
     * <i>http://&lt;host_name&gt;:&lt;port&gt;/service/platform/introspection?section=requirement_sample_data&part=0</i><br>
     * <br>
     */
    @POST
    @Context
    @Path("/proposals/")
    @RequiresPermissions("CreateProposal")
    public Response addProposal(JsonObject travelProposal, @Context MessageContext msgCntx) {
        try {
            Account acnt = (Account) msgCntx.getContent(Account.class);
            TravelRequirement proposal = JsonObjectHelper.toRequirement(travelProposal);
            String proposalId = travelRequirementDAO.createProposal(acnt, (TravelProposal) proposal).toString();
            JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, proposalId).build();
            return Response.ok(res).build();
        } catch (InvalidTravelReqirementException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    /**
     * <b>POST</b><br>
     * <br>
     * Append an itinerary given in the post payload into the proposal specified
     * by the proposal identifier. The identier of the newly created itinerary
     * will be returned.<br>
     * <br>
     * <b>Sample Payload:</b><br>
     * <br>
     * <i>http://&lt;host_name&gt;:&lt;port&gt;/service/platform/introspection?section=requirement_sample_data&part=1</i><br>
     * <br>
     */
    @POST
    @Path("/itineraries/{proposalId}/")
    @RequiresPermissions("ModifyProposal")
    public Response addItinerary(@PathParam("proposalId") String proposalId, JsonObject itineraryRequirement) {
        try {
            TravelRequirement itinerary = JsonObjectHelper.toRequirement(itineraryRequirement);
            String itineraryId = travelRequirementDAO.addRequirement(UuidUtil.fromUuidStr(proposalId), itinerary)
                .toString();
            JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, itineraryId).build();
            return Response.ok(res).build();
        } catch (InvalidTravelReqirementException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    /**
     * <b>POST</b><br>
     * <br>
     * Insert an itinerary given in the post payload into the proposal and after
     * the itinerary specified by the proposal and itinerary identifiers. The
     * identier of the newly created itinerary will be returned.<br>
     * <br>
     * <b>Sample Payload:</b><br>
     * <br>
     * <i>http://&lt;host_name&gt;:&lt;port&gt;/service/platform/introspection?section=requirement_sample_data&part=1</i><br>
     * <br>
     */
    @POST
    @Path("/itineraries/{proposalId}/{itineraryId}/")
    @RequiresPermissions("ModifyProposal")
    public Response insertItinerary(@PathParam("proposalId") String proposalId,
        @PathParam("itineraryId") String itineraryId, JsonObject itineraryRequirement) {
        try {
            TravelRequirement itinerary = JsonObjectHelper.toRequirement(itineraryRequirement);
            String addedItineraryId = travelRequirementDAO.addItinerary(UuidUtil.fromUuidStr(proposalId),
                UuidUtil.fromUuidStr(itineraryId), (ItineraryRequirement) itinerary).toString();
            JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, addedItineraryId).build();
            return Response.ok(res).build();
        } catch (InvalidTravelReqirementException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    /**
     * <b>POST</b><br>
     * <br>
     * Append a requirement given in the post payload into the last itinerary of
     * the proposal specified by the proposal identifier. The identier of the
     * newly created requirement will be returned.<br>
     * <br>
     * <b>Sample Payload:</b><br>
     * <br>
     * <i>http://&lt;host_name&gt;:&lt;port&gt;/service/platform/introspection?section=requirement_sample_data&part=2&range=12</i><br>
     * <br>
     */
    @POST
    @Path("/requirements/{proposalId}/")
    @RequiresPermissions("ModifyProposal")
    public Response addRequirement(@PathParam("proposalId") String proposalId, JsonObject travelRequirement) {
        try {
            TravelRequirement requirement = JsonObjectHelper.toRequirement(travelRequirement);
            String requirementId = travelRequirementDAO.addRequirement(UuidUtil.fromUuidStr(proposalId), requirement)
                .toString();
            JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, requirementId).build();
            return Response.ok(res).build();
        } catch (InvalidTravelReqirementException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    /**
     * <b>POST</b><br>
     * <br>
     * Insert a requirement given in the post payload into the itinerary in the
     * proposal specified by the itinerary and proposal identifiers. The
     * identier of the newly created requirement will be returned.<br>
     * <br>
     * <b>Sample Payload:</b><br>
     * <br>
     * <i>http://&lt;host_name&gt;:&lt;port&gt;/service/platform/introspection?section=requirement_sample_data&part=2&range=12</i><br>
     * <br>
     */
    @POST
    @Path("/requirements/{proposalId}/{itineraryId}/")
    @RequiresPermissions("ModifyProposal")
    public Response insertRequirement(@PathParam("proposalId") String proposalId,
        @PathParam("itineraryId") String itineraryId, JsonObject travelRequirement) {
        try {
            TravelRequirement requirement = JsonObjectHelper.toRequirement(travelRequirement);
            String addedRequirementId = travelRequirementDAO.addRequirement(UuidUtil.fromUuidStr(proposalId),
                UuidUtil.fromUuidStr(itineraryId), requirement).toString();
            JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, addedRequirementId).build();
            return Response.ok(res).build();
        } catch (InvalidTravelReqirementException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }
}
