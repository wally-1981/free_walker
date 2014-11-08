package com.free.walker.service.itinerary.dao;

import java.util.List;
import java.util.UUID;

import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.req.TravelProposal;
import com.free.walker.service.itinerary.req.TravelRequirement;

public interface TravelRequirementDAO {
    public boolean pingPersistence();

    public UUID createProposal(TravelProposal travelProposal);

    public UUID addRequirement(UUID travelProposalId, TravelRequirement travelRequirement)
        throws InvalidTravelReqirementException;

    public UUID addRequirement(UUID travelProposalId, UUID itineraryRequirementId,
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

    public TravelRequirement getRequirement(UUID travelRequirementId) throws InvalidTravelReqirementException;

    public UUID updateRequirement(UUID travelRequirementId, TravelRequirement travelRequirement)
        throws InvalidTravelReqirementException;

    public UUID removeRequirement(UUID travelRequirementId) throws InvalidTravelReqirementException;
}
