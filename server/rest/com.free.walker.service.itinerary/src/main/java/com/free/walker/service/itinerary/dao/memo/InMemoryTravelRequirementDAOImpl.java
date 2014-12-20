package com.free.walker.service.itinerary.dao.memo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.dao.TravelRequirementDAO;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.req.ItineraryRequirement;
import com.free.walker.service.itinerary.req.TravelProposal;
import com.free.walker.service.itinerary.req.TravelRequirement;
import com.ibm.icu.util.Calendar;

public class InMemoryTravelRequirementDAOImpl implements TravelRequirementDAO {
    protected Map<UUID, List<TravelRequirement>> travelProposals;
    protected Map<UUID, TravelRequirement> travelRequirements;
    protected Map<UUID, List<UUID>> proposalAgencies;
    protected Map<UUID, UUID> proposalOwners;

    private static class SingletonHolder {
        private static final TravelRequirementDAO INSTANCE = new InMemoryTravelRequirementDAOImpl();
    }

    public static TravelRequirementDAO getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private InMemoryTravelRequirementDAOImpl() {
        travelProposals = new HashMap<UUID, List<TravelRequirement>>();
        travelRequirements = new HashMap<UUID, TravelRequirement>();
        proposalAgencies = new HashMap<UUID, List<UUID>>();
        proposalOwners = new HashMap<UUID, UUID>();
    }

    public boolean pingPersistence() {
        return true;
    }

    public UUID createProposal(UUID accountId, TravelProposal travelProposal) throws InvalidTravelReqirementException {
        if (travelProposal == null) {
            throw new NullPointerException();
        }

        if (travelRequirements.containsKey(travelProposal.getUUID())) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.existed_travel_requirement, travelProposal.getUUID()), travelProposal.getUUID());
        }

        List<TravelRequirement> itineraryRequirements = travelProposal.getTravelRequirements();
        for (int i = 0; i < itineraryRequirements.size(); i++) {
            TravelRequirement requirment = itineraryRequirements.get(i);
            if (travelRequirements.containsKey(requirment.getUUID())) {
                throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                    LocalMessages.existed_travel_requirement, requirment.getUUID()), travelProposal.getUUID());
            }
        }

        List<TravelRequirement> requirements = new LinkedList<TravelRequirement>();
        travelProposals.put(travelProposal.getUUID(), requirements);
        travelRequirements.put(travelProposal.getUUID(), travelProposal);

        for (int i = 0; i < itineraryRequirements.size(); i++) {
            if (itineraryRequirements.get(i).isItinerary()) {
                requirements.add(itineraryRequirements.get(i));
                travelRequirements.put(itineraryRequirements.get(i).getUUID(), itineraryRequirements.get(i));
            }
        }
        itineraryRequirements.clear();

        return travelProposal.getUUID();
    }

    public UUID startProposalBid(UUID travelProposalId, UUID accountId) throws InvalidTravelReqirementException,
        DatabaseAccessException {
        if (travelProposalId == null || accountId == null) {
            throw new NullPointerException();
        }

        TravelRequirement proposal = travelRequirements.get(travelProposalId);
        if (proposal == null || !proposal.isProposal()) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        if (proposalOwners.get(travelProposalId) == null) {
            proposalOwners.put(travelProposalId, accountId);
        } else {
            if (!accountId.equals(proposalOwners.get(travelProposalId))) {
                throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                    LocalMessages.illegal_submit_proposal_operation, travelProposalId, accountId), travelProposalId);
            }
        }

        if (proposalAgencies.containsKey(travelProposalId)) {
            return travelProposalId;
        } else {
            proposalAgencies.put(travelProposalId, new ArrayList<UUID>());
            return travelProposalId;
        }
    }

    public UUID joinProposalBid(UUID travelProposalId, UUID agencyId) throws InvalidTravelReqirementException,
        DatabaseAccessException {
        if (travelProposalId == null || agencyId == null) {
            throw new NullPointerException();
        }

        TravelRequirement proposal = travelRequirements.get(travelProposalId);
        if (proposal == null || !proposal.isProposal()) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        if (proposalAgencies.containsKey(travelProposalId)) {
            List<UUID> agencies = proposalAgencies.get(travelProposalId);
            agencies.add(agencyId);
            for (int i = 0; i < agencies.size() - 1; i++) {
                if (agencyId.equals(agencies.get(i))) {
                    agencies.remove(agencies.size() - 1);
                    return agencyId;
                }
            }
            return agencyId;
        } else {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.missing_travel_proposal_bidding, travelProposalId), travelProposalId);
        }
    }

    public UUID addItinerary(UUID travelProposalId, UUID itineraryRequirementId,
        ItineraryRequirement itineraryRequirement) throws InvalidTravelReqirementException {
        if (travelProposalId == null || itineraryRequirementId == null || itineraryRequirement == null) {
            throw new NullPointerException();
        }

        TravelRequirement proposal = travelRequirements.get(travelProposalId);
        if (proposal == null || !proposal.isProposal()) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        TravelRequirement itinerary = travelRequirements.get(itineraryRequirementId);
        if (itinerary == null || !itinerary.isItinerary()) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_itinerary,
                itineraryRequirementId), itineraryRequirementId);
        }

        if (travelRequirements.containsKey(itineraryRequirement.getUUID())) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.existed_travel_requirement, itineraryRequirement.getUUID()),
                itineraryRequirement.getUUID());
        }

        List<TravelRequirement> requirements = travelProposals.get(travelProposalId);
        if (requirements == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        for (int i = 0; i < requirements.size(); i++) {
            TravelRequirement requirement = requirements.get(i);
            boolean isValidRequirementId = requirement.getUUID().equals(itineraryRequirementId);
            boolean isItineraryRequirement = requirement.isItinerary();
            if (isValidRequirementId && isItineraryRequirement) {
                for (int j = 1; i + j < requirements.size(); j++) {
                    if (requirements.get(i + j).isItinerary()) {
                        requirements.add(i + j, itineraryRequirement);
                        break;
                    }

                    if (i + j + 1 == requirements.size()) {
                        requirements.add(i + j, itineraryRequirement);
                    }
                }
                travelRequirements.put(itineraryRequirement.getUUID(), itineraryRequirement);
                break;
            }

            if (i + 1 == requirements.size()) {
                throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                    LocalMessages.missing_travel_itinerary, itineraryRequirementId), itineraryRequirementId);
            }
        }

        return itineraryRequirement.getUUID();
    }

    public UUID addRequirement(UUID travelProposalId, UUID itineraryRequirementId, TravelRequirement travelRequirement)
        throws InvalidTravelReqirementException {
        if (travelProposalId == null || itineraryRequirementId == null || travelRequirement == null) {
            throw new NullPointerException();
        }

        TravelRequirement proposal = travelRequirements.get(travelProposalId);
        if (proposal == null || !proposal.isProposal()) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        TravelRequirement itinerary = travelRequirements.get(itineraryRequirementId);
        if (itinerary == null || !itinerary.isItinerary()) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_itinerary,
                itineraryRequirementId), itineraryRequirementId);
        }

        if (travelRequirements.containsKey(travelRequirement.getUUID())) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.existed_travel_requirement, travelRequirement.getUUID()), travelRequirement.getUUID());
        }

        if (travelRequirement.isProposal()) {
            throw new InvalidTravelReqirementException(
                LocalMessages.getMessage(LocalMessages.illegal_add_proposal_as_requirement,
                    travelRequirement.getUUID(), itineraryRequirementId), travelRequirement.getUUID());
        }

        if (travelRequirement.isItinerary()) {
            throw new InvalidTravelReqirementException(
                LocalMessages.getMessage(LocalMessages.illegal_add_itinerary_as_requirement,
                    travelRequirement.getUUID(), itineraryRequirementId), travelRequirement.getUUID());
        }

        List<TravelRequirement> requirements = travelProposals.get(travelProposalId);
        if (requirements == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        for (int i = 0; i < requirements.size(); i++) {
            TravelRequirement requirement = requirements.get(i);
            boolean isValidRequirementId = requirement.getUUID().equals(itineraryRequirementId);
            boolean isItineraryRequirement = requirement.isItinerary();
            if (isValidRequirementId && isItineraryRequirement) {
                requirements.add(i + 1, travelRequirement);
                travelRequirements.put(travelRequirement.getUUID(), travelRequirement);
                break;
            }

            if (i + 1 == requirements.size()) {
                throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                    LocalMessages.missing_travel_itinerary, itineraryRequirementId), itineraryRequirementId);
            }
        }

        return travelRequirement.getUUID();
    }

    public UUID addRequirement(UUID travelProposalId, TravelRequirement travelRequirement)
        throws InvalidTravelReqirementException {
        if (travelProposalId == null || travelRequirement == null) {
            throw new NullPointerException();
        }

        TravelRequirement proposal = travelRequirements.get(travelProposalId);
        if (proposal == null || !proposal.isProposal()) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        if (travelRequirements.containsKey(travelRequirement.getUUID())) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.existed_travel_requirement, travelRequirement.getUUID()), travelRequirement.getUUID());
        }

        if (travelRequirement.isProposal()) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.illegal_add_proposal_as_requirement, travelRequirement.getUUID(), travelProposalId),
                travelRequirement.getUUID());
        }

        List<TravelRequirement> requirements = travelProposals.get(travelProposalId);
        if (requirements == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        requirements.add(travelRequirement);
        travelRequirements.put(travelRequirement.getUUID(), travelRequirement);

        return travelRequirement.getUUID();
    }

    public List<TravelRequirement> getRequirements(UUID travelProposalId) throws InvalidTravelReqirementException {
        if (travelProposalId == null) {
            throw new NullPointerException();
        }

        TravelRequirement proposal = travelRequirements.get(travelProposalId);
        if (proposal == null || !proposal.isProposal()) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        List<TravelRequirement> requirements = travelProposals.get(travelProposalId);
        if (requirements == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        requirements.retainAll(travelRequirements.values());

        return new LinkedList<TravelRequirement>(requirements);
    }

    public List<TravelRequirement> getItineraryRequirements(UUID travelProposalId)
        throws InvalidTravelReqirementException {
        if (travelProposalId == null) {
            throw new NullPointerException();
        }

        TravelRequirement proposal = travelRequirements.get(travelProposalId);
        if (proposal == null || !proposal.isProposal()) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        List<TravelRequirement> requirements = travelProposals.get(travelProposalId);
        if (requirements == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        requirements.retainAll(travelRequirements.values());

        List<TravelRequirement> result = new LinkedList<TravelRequirement>(requirements);

        for (int i = 0; i < result.size(); i++) {
            TravelRequirement requirement = result.get(i);
            if (!requirement.isItinerary()) {
                result.remove(i);
                i--;
            }
        }

        return result;
    }

    public List<TravelRequirement> getRequirements(UUID travelProposalId, UUID itineraryRequirementId)
        throws InvalidTravelReqirementException {
        if (travelProposalId == null || itineraryRequirementId == null) {
            throw new NullPointerException();
        }

        TravelRequirement proposal = travelRequirements.get(travelProposalId);
        if (proposal == null || !proposal.isProposal()) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        TravelRequirement itinerary = travelRequirements.get(itineraryRequirementId);
        if (itinerary == null || !itinerary.isItinerary()) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_itinerary,
                itineraryRequirementId), itineraryRequirementId);
        }

        List<TravelRequirement> requirements = travelProposals.get(travelProposalId);
        if (requirements == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        requirements.retainAll(travelRequirements.values());

        List<TravelRequirement> result = new LinkedList<TravelRequirement>();

        Iterator<TravelRequirement> iter = requirements.iterator();
        while (iter.hasNext()) {
            TravelRequirement requirement = iter.next();
            if (requirement.getUUID().equals(itineraryRequirementId) && requirement.isItinerary()) {
                while (iter.hasNext()) {
                    requirement = iter.next();
                    if (requirement.isItinerary()) {
                        return result;
                    } else {
                        result.add(requirement);
                    }
                }
            }
        }

        return result;
    }

    public TravelRequirement getPrevItineraryRequirement(UUID travelProposalId, UUID travelRequirementId)
        throws InvalidTravelReqirementException {
        if (travelProposalId == null || travelRequirementId == null) {
            throw new NullPointerException();
        }

        TravelRequirement proposal = travelRequirements.get(travelProposalId);
        if (proposal == null || !proposal.isProposal()) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        TravelRequirement requirement = travelRequirements.get(travelRequirementId);
        if (requirement == null || requirement.isProposal()) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.missing_travel_requirement, travelRequirementId), travelRequirementId);
        }

        List<TravelRequirement> requirements = travelProposals.get(travelProposalId);
        if (requirements == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        requirements.retainAll(travelRequirements.values());

        for (int i = 0; i < requirements.size(); i++) {
            if (requirements.get(i).getUUID().equals(travelRequirementId)) {
                for (int j = i - 1; j >= 0; j--) {
                    if (requirements.get(j).isItinerary()) {
                        return requirements.get(j);
                    }
                }
            }
        }

        return null;
    }

    public TravelRequirement getNextItineraryRequirement(UUID travelProposalId, UUID travelRequirementId)
        throws InvalidTravelReqirementException {
        if (travelProposalId == null || travelRequirementId == null) {
            throw new NullPointerException();
        }

        TravelRequirement proposal = travelRequirements.get(travelProposalId);
        if (proposal == null || !proposal.isProposal()) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        TravelRequirement itinerary = travelRequirements.get(travelRequirementId);
        if (itinerary == null || itinerary.isProposal()) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.missing_travel_requirement, travelRequirementId), travelRequirementId);
        }

        List<TravelRequirement> requirements = travelProposals.get(travelProposalId);
        if (requirements == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        requirements.retainAll(travelRequirements.values());

        for (int i = 0; i < requirements.size(); i++) {
            if (requirements.get(i).getUUID().equals(travelRequirementId)) {
                for (int j = i + 1; j < requirements.size(); j++) {
                    if (requirements.get(j).isItinerary()) {
                        return requirements.get(j);
                    }
                }
            }
        }

        return null;
    }

    public TravelRequirement getRequirement(UUID travelRequirementId, String requirementType)
        throws InvalidTravelReqirementException {
        if (travelRequirementId == null) {
            throw new NullPointerException();
        }

        return travelRequirements.get(travelRequirementId);
    }

    public UUID updateRequirement(TravelRequirement travelRequirement) throws InvalidTravelReqirementException {
        if (travelRequirement == null || travelRequirement.getUUID() == null) {
            throw new NullPointerException();
        }

        if (travelRequirement.isItinerary() || travelRequirement.isProposal()) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.illegal_update_travel_requirement_operation, travelRequirement.getUUID()),
                travelRequirement.getUUID());
        }

        TravelRequirement updatingTravelRequirement = travelRequirements.get(travelRequirement.getUUID());
        if (updatingTravelRequirement == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.missing_travel_requirement, travelRequirement.getUUID().toString()),
                travelRequirement.getUUID());
        }

        if (updatingTravelRequirement.isItinerary() || updatingTravelRequirement.isProposal()) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.illegal_update_travel_requirement_operation, travelRequirement.getUUID().toString()),
                travelRequirement.getUUID());
        }

        if (!updatingTravelRequirement.getClass().equals(travelRequirement.getClass())) {
            throw new IllegalArgumentException();
        }

        travelRequirements.put(travelRequirement.getUUID(), travelRequirement);
        Iterator<List<TravelRequirement>> iter = travelProposals.values().iterator();
        while (iter.hasNext()) {
            List<TravelRequirement> requirements = (List<TravelRequirement>) iter.next();
            for (int i = 0; i < requirements.size(); i++) {
                if (travelRequirement.getUUID().equals(requirements.get(i).getUUID())) {
                    requirements.remove(i);
                    requirements.add(travelRequirement);
                    break;
                }
            }
        }

        return travelRequirement.getUUID();
    }

    public UUID removeRequirement(UUID travelProposalId, UUID travelRequirementId)
        throws InvalidTravelReqirementException {
        if (travelProposalId == null || travelRequirementId == null) {
            throw new NullPointerException();
        }

        TravelRequirement proposal = travelRequirements.get(travelProposalId);
        if (proposal == null || !proposal.isProposal()) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        List<TravelRequirement> requirements = travelProposals.get(travelProposalId);
        if (requirements == null) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(LocalMessages.missing_travel_proposal,
                travelProposalId), travelProposalId);
        }

        List<TravelRequirement> itineraries = getItineraryRequirements(travelProposalId);
        if (itineraries.size() == 1) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.illegal_delete_travel_requirement_operation, travelProposalId), travelProposalId);
        }

        TravelRequirement travelRequirement = travelRequirements.get(travelRequirementId);

        if (travelRequirement == null) {
            return null;
        } else if (travelRequirement.isProposal()) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.illegal_delete_travel_requirement_operation, travelRequirementId), travelRequirementId);
        } else {
            TravelRequirement requirement = travelRequirements.remove(travelRequirementId);
            if (requirement == null) {
                return null;
            } else {
                if (requirement.isItinerary()) {
                    for (int i = 0; i < requirements.size(); i++) {
                        if (requirement.getUUID().equals(requirements.get(i).getUUID())) {
                            for (int j = i + 1; j < requirements.size(); j++) {
                                if (requirements.get(j).isItinerary()) {
                                    break;
                                } else {
                                    travelRequirements.remove(requirements.get(j).getUUID());
                                }
                            }
                        }
                    }
                }
                return requirement.getUUID();
            }
        }
    }

    public List<TravelProposal> getTravelProposalsByAgency(UUID agencyId, Calendar since, int numberOfDay)
        throws InvalidTravelReqirementException {
        if (agencyId == null) {
            throw new NullPointerException();
        }

        List<TravelProposal> result = new ArrayList<TravelProposal>();

        Iterator<UUID> proposalIds = proposalAgencies.keySet().iterator();
        while (proposalIds.hasNext()) {
            UUID proposalId = proposalIds.next();
            List<UUID> agencies = proposalAgencies.get(proposalId);
            for (int i = 0; i < agencies.size(); i++) {
                if (agencyId.equals(agencies.get(i))) {
                    result.add((TravelProposal) travelRequirements.get(proposalId));
                }
            }
        }

        return result;
    }

    public List<TravelProposal> getTravelProposalsByAccount(UUID accountId, Calendar since, int numberOfDay)
        throws InvalidTravelReqirementException {
        if (accountId == null) {
            throw new NullPointerException();
        }

        List<TravelProposal> result = new ArrayList<TravelProposal>();

        Iterator<UUID> proposalIds = proposalOwners.keySet().iterator();
        while (proposalIds.hasNext()) {
            UUID proposalId = proposalIds.next();
            UUID proposalOwner = proposalOwners.get(proposalId);
            if (accountId.equals(proposalOwner)) {
                result.add((TravelProposal) travelRequirements.get(proposalId));
            }
        }

        return result;
    }
}
