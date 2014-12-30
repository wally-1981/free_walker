package com.free.walker.service.itinerary.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.MRRoutine;
import com.free.walker.service.itinerary.basic.Agency;
import com.free.walker.service.itinerary.basic.TravelLocation;
import com.free.walker.service.itinerary.dao.TravelBasicDAO;
import com.free.walker.service.itinerary.dao.TravelRequirementDAO;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.req.TravelProposal;
import com.free.walker.service.itinerary.util.UuidUtil;

public class AgencyElectionRoutine extends AgencyElectionBaseRoutine {
    private static final Logger LOG = LoggerFactory.getLogger(AgencyElectionRoutine.class);
    private static final int SUMMARY_MAX_LENGTH = 192;

    private TravelRequirementDAO travelRequirementDAO;
    private TravelLocation departure;
    private TravelLocation destination;

    private List<Agency> candidates = new ArrayList<Agency>();

    public AgencyElectionRoutine(String proposalId, TravelBasicDAO travelBasicDAO,
        TravelRequirementDAO travelRequirementDAO, TravelLocation source) {
        super(proposalId, travelBasicDAO);
        if (travelRequirementDAO == null || source == null) {
            throw new NullPointerException();
        }

        this.proposalId = proposalId;
        this.travelRequirementDAO = travelRequirementDAO;
        this.departure = source;
    }

    public AgencyElectionRoutine(String proposalId, TravelBasicDAO travelBasicDAO,
        TravelRequirementDAO travelRequirementDAO, TravelLocation source, TravelLocation destination) {
        this(proposalId, travelBasicDAO, travelRequirementDAO, source);

        if (destination == null) {
            throw new NullPointerException();
        }

        this.destination = destination;
    }

    public MRRoutine map() {
        try {
            if (destination == null || !travelBasicDAO.hasLocationByUuid(destination.getUuid())) {
                if (destination != null && destination.getUuid().matches("[1-7]")) {
                    candidates.addAll(travelBasicDAO.getAgencies4InternationalDestination(departure.getUuid(),
                        destination.getUuid()));
                } else {
                    candidates.addAll(travelBasicDAO.getAgencies4DangleDestination(departure.getUuid()));
                }
            } else {
                if (travelBasicDAO.isDomesticLocationByUuid(destination.getUuid())) {
                    candidates.addAll(travelBasicDAO.getAgencies4DomesticDestination(departure.getUuid(),
                        destination.getUuid()));
                } else {
                    candidates.addAll(travelBasicDAO.getAgencies4InternationalDestination(departure.getUuid(),
                        destination.getUuid()));
                }
            }
        } catch (DatabaseAccessException e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.agency_election_map_failed, departure.getName(),
                destination == null ? null : destination.getName()), e);
            throw new IllegalStateException(e);
        }

        if (candidates.isEmpty()) {
            LOG.info(LocalMessages.getMessage(LocalMessages.agency_election_no_candidate_found, proposalId));
            throw new CancellationException(LocalMessages.getMessage(LocalMessages.agency_election_no_candidate_found,
                proposalId));
        }

        String proposalSummary = null;
        try {
            TravelProposal travelProposal = (TravelProposal) travelRequirementDAO.getRequirement(
                UuidUtil.fromUuidStr(proposalId), Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL);
            if (travelProposal == null) {
                proposalSummary = null;
            } else {
                proposalSummary = travelProposal.getTitle();
            }

            if (proposalSummary == null || proposalSummary.isEmpty() ||
                proposalSummary.getBytes().length > SUMMARY_MAX_LENGTH) {
                LOG.error(LocalMessages.getMessage(LocalMessages.agency_election_invalid_summary),
                    proposalId, proposalSummary);
                throw new IllegalStateException(LocalMessages.getMessage(
                    LocalMessages.agency_election_invalid_summary, proposalSummary, proposalId));
            }

            travelBasicDAO.addAgencyCandidates4Proposal(proposalId, proposalSummary, candidates);
            LOG.info(LocalMessages.getMessage(LocalMessages.agency_election_persist_candidates_success,
                candidates.size(), proposalId));
        } catch (InvalidTravelReqirementException e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.agency_election_persist_candidates_failed,
                candidates.size(), proposalId, proposalSummary), e);
            throw new IllegalStateException(e);
        } catch (DatabaseAccessException e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.agency_election_persist_candidates_failed,
                candidates.size(), proposalId, proposalSummary), e);
            throw new IllegalStateException(e);
        }

        notify(candidates);

        return this;
    }

    public List<Agency> reduceByFeedback(List<Agency> agencies, int resultMaxSize) {
        return agencies.subList(0, agencies.isEmpty() ? 0 : Math.min(resultMaxSize, agencies.size() - 1));
    }

    public List<Agency> reduceByExperience(List<Agency> agencies, int resultMaxSize) {
        return agencies.subList(0, agencies.isEmpty() ? 0 : Math.min(resultMaxSize, agencies.size() - 1));
    }

    public List<Agency> reduceByRandomization(List<Agency> agencies, int resultMaxSize) {
        return agencies.subList(0, agencies.isEmpty() ? 0 : Math.min(resultMaxSize, agencies.size() - 1));
    }

    public boolean immediate() {
        return false;
    }
}
