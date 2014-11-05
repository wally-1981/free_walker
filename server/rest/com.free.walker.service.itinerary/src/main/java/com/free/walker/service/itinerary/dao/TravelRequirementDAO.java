package com.free.walker.service.itinerary.dao;

import java.util.List;
import java.util.UUID;

import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.req.TravelProposal;
import com.free.walker.service.itinerary.req.TravelRequirement;

public interface TravelRequirementDAO {
    public boolean pingPersistence();

    public UUID createTravelProposal(TravelProposal travelProposal);

    public UUID addTravelRequirement(UUID travelProposalId, TravelRequirement travelRequirement)
        throws InvalidTravelReqirementException;

    public UUID addTravelRequirement(UUID travelProposalId, UUID itineraryRequirementId,
        TravelRequirement travelRequirement) throws InvalidTravelReqirementException;

    public List<TravelRequirement> getRequirements(UUID travelProposalId) throws InvalidTravelReqirementException;

    public List<TravelRequirement> getItineraryRequirements(UUID travelProposalId)
        throws InvalidTravelReqirementException;

    public List<TravelRequirement> getRequirements(UUID travelProposalId, UUID itineraryRequirementId)
        throws InvalidTravelReqirementException;

    public TravelRequirement getPrevItineraryRequirement(UUID travelProposalId, UUID travelRequirementId)
        throws InvalidTravelReqirementException;

    public TravelRequirement getNextItineraryRequirement(UUID travelProposalId, UUID travelRequirementId)
        throws InvalidTravelReqirementException;

    public TravelRequirement getTravelRequirement(UUID travelRequirementId) throws InvalidTravelReqirementException;

    public UUID updateTravelRequirement(UUID travelRequirementId, TravelRequirement travelRequirement)
        throws InvalidTravelReqirementException;

    public UUID removeTravelRequirement(UUID travelRequirementId) throws InvalidTravelReqirementException;
}
