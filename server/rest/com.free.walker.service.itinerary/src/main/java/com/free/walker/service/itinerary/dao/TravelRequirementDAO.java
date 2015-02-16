package com.free.walker.service.itinerary.dao;

import java.util.List;
import java.util.UUID;

import com.free.walker.service.itinerary.basic.Account;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.req.ItineraryRequirement;
import com.free.walker.service.itinerary.req.TravelProposal;
import com.free.walker.service.itinerary.req.TravelRequirement;
import com.ibm.icu.util.Calendar;

public interface TravelRequirementDAO extends HealthyDAO {
    /**
     * Create the specified proposal, and all attached initial itineraries will
     * be created either as ordered.
     * 
     * @param account
     * @param travelProposal
     * @return
     * @throws InvalidTravelReqirementException
     * @throws DatabaseAccessException
     */
    public UUID createProposal(Account account, TravelProposal travelProposal) throws InvalidTravelReqirementException,
        DatabaseAccessException;

    /**
     * Publish the specified proposal, so that travel agency could start join
     * the bidding.
     * 
     * @param travelProposalId
     * @param account
     * @return
     * @throws InvalidTravelReqirementException
     * @throws DatabaseAccessException
     */
    public UUID startProposalBid(UUID travelProposalId, Account account) throws InvalidTravelReqirementException,
        DatabaseAccessException;

    /**
     * Bid for the specified proposal by the specified agency.
     * 
     * @param travelProposalId
     * @param agencyId
     * @return
     * @throws InvalidTravelReqirementException
     * @throws DatabaseAccessException
     */
    public UUID joinProposalBid(UUID travelProposalId, UUID agencyId) throws InvalidTravelReqirementException,
        DatabaseAccessException;

    /**
     * Add the itinerary to the specified proposal and make it as the next of
     * the specified itinerary.
     * 
     * @param travelProposalId
     * @param itineraryRequirementId
     * @param itineraryRequirement
     * @return
     * @throws InvalidTravelReqirementException
     * @throws DatabaseAccessException
     */
    public UUID addItinerary(UUID travelProposalId, UUID itineraryRequirementId,
        ItineraryRequirement itineraryRequirement) throws InvalidTravelReqirementException, DatabaseAccessException;

    /**
     * Add the requirement to the specified proposal and make it as an ordinary
     * requirement to the specified itinerary.
     * 
     * @param travelProposalId
     * @param itineraryRequirementId
     * @param travelRequirement
     * @return
     * @throws InvalidTravelReqirementException
     * @throws DatabaseAccessException
     */
    public UUID addRequirement(UUID travelProposalId, UUID itineraryRequirementId, TravelRequirement travelRequirement)
        throws InvalidTravelReqirementException, DatabaseAccessException;

    /**
     * Add the requirement to the specified proposal. The requirement can be an
     * itinernary or an ordinary requirement. If the requirement is an
     * itinerary, it will be made as the last itinerary of the proposal. If the
     * requiremnt is an ordinary requirement, it will be made as an ordinary
     * requirement to the latest itinerary.
     * 
     * @param travelProposalId
     * @param travelRequirement
     * @return
     * @throws InvalidTravelReqirementException
     * @throws DatabaseAccessException
     */
    public UUID addRequirement(UUID travelProposalId, TravelRequirement travelRequirement)
        throws InvalidTravelReqirementException, DatabaseAccessException;

    /**
     * Retrieve all requirements of the specified proposal. It will include both
     * itineraries and ordinary requirements, and all itineraries are ordered.
     * The ordinary requirements of an itinerary will follow its itinerary until
     * the next itinerary or the end of the list.
     * 
     * @param travelProposalId
     * @return
     * @throws InvalidTravelReqirementException
     * @throws DatabaseAccessException
     */
    public List<TravelRequirement> getRequirements(UUID travelProposalId) throws InvalidTravelReqirementException,
        DatabaseAccessException;

    /**
     * Retrieve all itineraries of the specified proposal, and all itineraries
     * are ordered.
     * 
     * @param travelProposalId
     * @return
     * @throws InvalidTravelReqirementException
     * @throws DatabaseAccessException
     */
    public List<TravelRequirement> getItineraryRequirements(UUID travelProposalId)
        throws InvalidTravelReqirementException, DatabaseAccessException;

    /**
     * Retrieve all ordinary requirements of the specified proposal and
     * itinerary.
     * 
     * @param travelProposalId
     * @param itineraryRequirementId
     * @return
     * @throws InvalidTravelReqirementException
     * @throws DatabaseAccessException
     */
    public List<TravelRequirement> getRequirements(UUID travelProposalId, UUID itineraryRequirementId)
        throws InvalidTravelReqirementException, DatabaseAccessException;

    /**
     * Retrieve the previous itinerary of the specified proposal and
     * requirement. The requirement id must not point to a proposal. null will
     * be retunred if there is no more itinerary backwards;
     * 
     * @param travelProposalId
     * @param travelRequirementId
     * @return
     * @throws InvalidTravelReqirementException
     * @throws DatabaseAccessException
     */
    public TravelRequirement getPrevItineraryRequirement(UUID travelProposalId, UUID travelRequirementId)
        throws InvalidTravelReqirementException, DatabaseAccessException;

    /**
     * Retrieve the next itinerary of the specified proposal and requirement.
     * The requirement id must not point to a proposal. null will be retunred if
     * there is no more itinerary forwards.
     * 
     * @param travelProposalId
     * @param travelRequirementId
     * @return
     * @throws InvalidTravelReqirementException
     * @throws DatabaseAccessException
     */
    public TravelRequirement getNextItineraryRequirement(UUID travelProposalId, UUID travelRequirementId)
        throws InvalidTravelReqirementException, DatabaseAccessException;

    /**
     * Retrieve the specified requirement by identifier and type. The type could
     * be "proposal", "itinerary", "requirement" or null. null type means the
     * retrieval process will go through to check all the possible types. null
     * will be returned if not found the requirement.
     * 
     * @param travelRequirementId
     * @param requirementType
     * @return
     * @throws InvalidTravelReqirementException
     * @throws DatabaseAccessException
     */
    public TravelRequirement getRequirement(UUID travelRequirementId, String requirementType)
        throws InvalidTravelReqirementException, DatabaseAccessException;

    /**
     * Update the specified requirement by identifier with the specified
     * requirement. The proposal and itinerary are immutable, so they are not
     * allowed to be updated.
     * 
     * @param travelRequirement
     * @return
     * @throws InvalidTravelReqirementException
     * @throws DatabaseAccessException
     */
    public UUID updateRequirement(TravelRequirement travelRequirement) throws InvalidTravelReqirementException,
        DatabaseAccessException;

    /**
     * Remove the specified requirement by identifier. Removing an itinerary
     * will remove all of its ordinary requirements. A proposal can not be
     * removed and the last itinerary in the proposal can not be removed. null
     * will be returned if the removal does not found the removing requirement.
     * 
     * @param travelProposalId
     * @param travelRequirementId
     * @return
     * @throws InvalidTravelReqirementException
     * @throws DatabaseAccessException
     */
    public UUID removeRequirement(UUID travelProposalId, UUID travelRequirementId)
        throws InvalidTravelReqirementException, DatabaseAccessException;

    /**
     * Retrieve all proposals by travel agency.
     * 
     * @param agencyId
     * @param since
     * @param numberOfDay
     * @return
     */
    public List<TravelProposal> getTravelProposalsByAgency(UUID agencyId, Calendar since, int numberOfDay)
        throws InvalidTravelReqirementException, DatabaseAccessException;

    /**
     * Retrieve all proposals by author.
     * 
     * @param accountId
     * @param since
     * @param numberOfDay
     * @return
     */
    public List<TravelProposal> getTravelProposalsByAccount(UUID accountId, Calendar since, int numberOfDay)
        throws InvalidTravelReqirementException, DatabaseAccessException;

    /**
     * Retrieve the owner of the proposal specified by the proposal identifier.
     * 
     * @param travelProposalId
     * @return
     * @throws DatabaseAccessException
     */
    public Account getTravelProposalOwner(UUID travelProposalId) throws DatabaseAccessException;
}
