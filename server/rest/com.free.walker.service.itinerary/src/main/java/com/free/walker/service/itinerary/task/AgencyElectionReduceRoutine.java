package com.free.walker.service.itinerary.task;

import java.util.concurrent.CancellationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.MRRoutine;
import com.free.walker.service.itinerary.basic.TravelLocation;
import com.free.walker.service.itinerary.dao.TravelBasicDAO;
import com.free.walker.service.itinerary.dao.TravelRequirementDAO;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;

public class AgencyElectionReduceRoutine extends AgencyElectionRoutine {
    private static final Logger LOG = LoggerFactory.getLogger(AgencyElectionReduceRoutine.class);
    private static final int ELECTION_ROTATION_WINDOW = 6;

    public AgencyElectionReduceRoutine(String proposalId, TravelBasicDAO travelBasicDAO,
        TravelRequirementDAO travelRequirementDAO, TravelLocation departure) {
        super(proposalId, travelBasicDAO, travelRequirementDAO, departure);
    }

    public AgencyElectionReduceRoutine(String proposalId, TravelBasicDAO travelBasicDAO,
        TravelRequirementDAO travelRequirementDAO, TravelLocation departure, TravelLocation destination) {
        super(proposalId, travelBasicDAO, travelRequirementDAO, departure, destination);
    }

    public MRRoutine map() {
        try {
            if (!travelBasicDAO.canRotateElection4Proposal(proposalId, ELECTION_ROTATION_WINDOW)) {
                LOG.info(LocalMessages.getMessage(LocalMessages.agency_election_miss_rotation_window, proposalId,
                    ELECTION_ROTATION_WINDOW));
                throw new CancellationException(LocalMessages.getMessage(
                    LocalMessages.agency_election_miss_rotation_window, proposalId, ELECTION_ROTATION_WINDOW));
            }
        } catch (DatabaseAccessException e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.agency_election_can_rotate_failed, proposalId), e);
            throw new IllegalStateException(e);
        }
        return this;
    }

    public boolean immediate() {
        return true;
    }
}
