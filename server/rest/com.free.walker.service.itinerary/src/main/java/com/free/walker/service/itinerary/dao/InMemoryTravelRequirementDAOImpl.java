package com.free.walker.service.itinerary.dao;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.req.TravelProposal;
import com.free.walker.service.itinerary.req.TravelRequirement;

public class InMemoryTravelRequirementDAOImpl implements TravelRequirementDAO {
    protected Map<UUID, List<TravelRequirement>> travelProposals;
    protected Map<UUID, TravelRequirement> travelRequirements;

    private static class SingletonHolder {
        private static final TravelRequirementDAO INSTANCE = new InMemoryTravelRequirementDAOImpl();
    }

    public static TravelRequirementDAO getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private InMemoryTravelRequirementDAOImpl() {
        travelProposals = new HashMap<UUID, List<TravelRequirement>>();
        travelRequirements = new HashMap<UUID, TravelRequirement>();
    }

    public boolean pingPersistence() {
        return true;
    }

    public UUID createTravelProposal(TravelProposal travelProposal) {
        if (travelProposal == null) {
            throw new NullPointerException();
        }

        List<TravelRequirement> requirements = new LinkedList<TravelRequirement>();
        travelProposals.put(travelProposal.getUUID(), requirements);
        travelRequirements.put(travelProposal.getUUID(), travelProposal);

        TravelRequirement travelRequirement = travelProposal.getTravelRequirements().get(0);
        requirements.add(travelRequirement);
        travelRequirements.put(travelRequirement.getUUID(), travelRequirement);

        return travelProposal.getUUID();
    }

    public UUID addTravelRequirement(UUID travelProposalId, TravelRequirement travelRequirement)
        throws InvalidTravelReqirementException {
        if (travelProposalId == null || travelRequirement == null) {
            throw new NullPointerException();
        }

        List<TravelRequirement> proposalRequirements = travelProposals.get(travelProposalId);

        if (proposalRequirements == null) {
            throw new InvalidTravelReqirementException(travelProposalId, LocalMessages.getMessage(
                LocalMessages.missing_travel_requirement, travelProposalId));
        }

        if (travelRequirements.containsKey(travelRequirement.getUUID())) {
            throw new InvalidTravelReqirementException(travelRequirement.getUUID(), LocalMessages.getMessage(
                LocalMessages.existed_travel_requirement, travelRequirement.getUUID()));
        }

        proposalRequirements.add(travelRequirement);
        travelRequirements.put(travelRequirement.getUUID(), travelRequirement);

        return travelRequirement.getUUID();
    }

    public UUID addTravelRequirement(UUID travelProposalId, UUID itineraryRequirementId,
        TravelRequirement travelRequirement) throws InvalidTravelReqirementException {
        if (travelProposalId == null || itineraryRequirementId == null || travelRequirement == null) {
            throw new NullPointerException();
        }

        if (travelRequirement.isItinerary()) {
            throw new InvalidTravelReqirementException(travelRequirement.getUUID(), LocalMessages.getMessage(
                LocalMessages.illegal_add_travel_requirement_operation, itineraryRequirementId,
                travelRequirement.getUUID()));
        }

        List<TravelRequirement> proposalRequirements = travelProposals.get(travelProposalId);

        if (proposalRequirements == null) {
            throw new InvalidTravelReqirementException(travelProposalId, LocalMessages.getMessage(
                LocalMessages.missing_travel_requirement, travelProposalId));
        }

        if (travelRequirements.containsKey(travelRequirement.getUUID())) {
            throw new InvalidTravelReqirementException(travelRequirement.getUUID(), LocalMessages.getMessage(
                LocalMessages.existed_travel_requirement, travelRequirement.getUUID()));
        }

        for (int i = 0; i < proposalRequirements.size(); i++) {
            boolean isValidRequirementId = proposalRequirements.get(i).getUUID().equals(itineraryRequirementId);
            boolean isItineraryRequirement = proposalRequirements.get(i).isItinerary();
            if (isValidRequirementId && isItineraryRequirement) {
                proposalRequirements.add(i + 1, travelRequirement);
                travelRequirements.put(travelRequirement.getUUID(), travelRequirement);
                break;
            }

            if (i + 1 == proposalRequirements.size()) {
                throw new InvalidTravelReqirementException(itineraryRequirementId, LocalMessages.getMessage(
                    LocalMessages.missing_itinerary_requirement, itineraryRequirementId));
            }
        }
        return travelRequirement.getUUID();
    }

    public List<TravelRequirement> getRequirements(UUID travelProposalId) throws InvalidTravelReqirementException {
        if (travelProposalId == null) {
            throw new NullPointerException();
        }

        List<TravelRequirement> proposalRequirements = travelProposals.get(travelProposalId);

        if (proposalRequirements == null) {
            throw new InvalidTravelReqirementException(travelProposalId, LocalMessages.getMessage(
                LocalMessages.missing_travel_requirement, travelProposalId));
        }

        proposalRequirements.retainAll(travelRequirements.values());

        List<TravelRequirement> result = new LinkedList<TravelRequirement>(proposalRequirements);

        return result;
    }

    public List<TravelRequirement> getItineraryRequirements(UUID travelProposalId)
        throws InvalidTravelReqirementException {
        if (travelProposalId == null) {
            throw new NullPointerException();
        }

        List<TravelRequirement> proposalRequirements = travelProposals.get(travelProposalId);

        if (proposalRequirements == null) {
            throw new InvalidTravelReqirementException(travelProposalId, LocalMessages.getMessage(
                LocalMessages.missing_travel_requirement, travelProposalId));
        }

        proposalRequirements.retainAll(travelRequirements.values());

        List<TravelRequirement> result = new LinkedList<TravelRequirement>(proposalRequirements);

        Iterator<TravelRequirement> iter = result.iterator();
        while (iter.hasNext()) {
            TravelRequirement requirement = iter.next();
            if (!requirement.isItinerary()) {
                iter.remove();
            }
        }

        return result;
    }

    public TravelRequirement getPrevItineraryRequirement(UUID travelProposalId, UUID travelRequirementId)
        throws InvalidTravelReqirementException {
        if (travelProposalId == null || travelRequirementId == null) {
            throw new NullPointerException();
        }

        List<TravelRequirement> proposalRequirements = travelProposals.get(travelProposalId);

        if (proposalRequirements == null) {
            throw new InvalidTravelReqirementException(travelProposalId, LocalMessages.getMessage(
                LocalMessages.missing_travel_requirement, travelProposalId));
        }

        proposalRequirements.retainAll(travelRequirements.values());

        if (!travelRequirements.containsKey(travelRequirementId)) {
            throw new InvalidTravelReqirementException(travelRequirementId, LocalMessages.getMessage(
                LocalMessages.missing_travel_requirement, travelRequirementId));
        }

        for (int i = 0; i < proposalRequirements.size(); i++) {
            if (proposalRequirements.get(i).getUUID().equals(travelRequirementId)) {
                for (int j = i - 1; j >= 0; j--) {
                    if (proposalRequirements.get(j).isItinerary()) {
                        return proposalRequirements.get(j);
                    }
                }
            }
        }

        throw new InvalidTravelReqirementException(travelRequirementId, LocalMessages.getMessage(
            LocalMessages.not_found_itinerary_requirement, "previous", travelProposalId, travelRequirementId));
    }

    public TravelRequirement getNextItineraryRequirement(UUID travelProposalId, UUID travelRequirementId)
        throws InvalidTravelReqirementException {
        if (travelProposalId == null || travelRequirementId == null) {
            throw new NullPointerException();
        }

        List<TravelRequirement> proposalRequirements = travelProposals.get(travelProposalId);

        if (proposalRequirements == null) {
            throw new InvalidTravelReqirementException(travelProposalId, LocalMessages.getMessage(
                LocalMessages.missing_travel_requirement, travelProposalId));
        }

        proposalRequirements.retainAll(travelRequirements.values());

        if (!travelRequirements.containsKey(travelRequirementId)) {
            throw new InvalidTravelReqirementException(travelRequirementId, LocalMessages.getMessage(
                LocalMessages.existed_travel_requirement, travelRequirementId));
        }

        for (int i = 0; i < proposalRequirements.size(); i++) {
            if (proposalRequirements.get(i).getUUID().equals(travelRequirementId)) {
                for (int j = i + 1; j < proposalRequirements.size(); j++) {
                    if (proposalRequirements.get(j).isItinerary()) {
                        return proposalRequirements.get(j);
                    }
                }
            }
        }

        throw new InvalidTravelReqirementException(travelRequirementId, LocalMessages.getMessage(
            LocalMessages.not_found_itinerary_requirement, "next", travelProposalId, travelRequirementId));
    }

    public TravelRequirement getTravelRequirement(UUID travelRequirementId) throws InvalidTravelReqirementException {
        if (travelRequirementId == null) {
            throw new NullPointerException();
        }

        TravelRequirement travelRequirement = travelRequirements.get(travelRequirementId);

        if (travelRequirement == null) {
            throw new InvalidTravelReqirementException(travelRequirementId, LocalMessages.getMessage(
                LocalMessages.missing_travel_requirement, travelRequirementId));
        }

        return travelRequirement;
    }

    public UUID updateTravelRequirement(UUID travelRequirementId, TravelRequirement travelRequirement)
        throws InvalidTravelReqirementException {
        if (travelRequirementId == null || travelRequirement == null) {
            throw new NullPointerException();
        }

        if (travelRequirementId.equals(travelRequirement.getUUID())) {
            throw new IllegalArgumentException();
        }

        if (travelRequirement.isItinerary() || travelRequirement instanceof TravelProposal) {
            throw new InvalidTravelReqirementException(travelRequirement.getUUID(), LocalMessages.getMessage(
                LocalMessages.illegal_update_travel_requirement_operation, travelRequirement.getUUID()));
        }

        TravelRequirement updatingTravelRequirement = travelRequirements.get(travelRequirementId);

        if (updatingTravelRequirement == null) {
            throw new InvalidTravelReqirementException(travelRequirementId, LocalMessages.getMessage(
                LocalMessages.missing_travel_requirement, travelRequirementId));
        }

        if (updatingTravelRequirement instanceof TravelProposal) {
            throw new InvalidTravelReqirementException(travelRequirementId, LocalMessages.getMessage(
                LocalMessages.illegal_update_travel_requirement_operation, travelRequirementId));
        }

        if (!updatingTravelRequirement.getClass().equals(travelRequirement.getClass())) {
            throw new IllegalArgumentException();
        }

        travelRequirements.remove(travelRequirementId);
        travelRequirements.put(travelRequirement.getUUID(), travelRequirement);
        Iterator<List<TravelRequirement>> iter = travelProposals.values().iterator();
        while(iter.hasNext()) {
            List<TravelRequirement> travelRequirements = (List<TravelRequirement>) iter.next();
            for (int i = 0; i < travelRequirements.size(); i++) {
                if (travelRequirementId.equals(travelRequirements.get(i))) {
                    travelRequirements.remove(i);
                    break;
                }
            }
            travelRequirements.add(travelRequirement);
        }

        return travelRequirement.getUUID();
    }

    public UUID removeTravelRequirement(UUID travelRequirementId) throws InvalidTravelReqirementException {
        if (travelRequirementId == null) {
            throw new NullPointerException();
        }

        if (travelRequirements.remove(travelRequirementId) == null) {
            throw new InvalidTravelReqirementException(travelRequirementId, LocalMessages.getMessage(
                LocalMessages.missing_travel_requirement, travelRequirementId));
        }

        return travelRequirementId;
    }
}
