package com.free.walker.service.itinerary.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.dao.TravelRequirementDAO;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.util.UuidUtil;

public class AgencyElectionTask extends TimerTask {
    private static final int SCHEDULE_DELAY = 1 * 1000;

    private static Logger LOG = LoggerFactory.getLogger(AgencyElectionTask.class);
    private static Timer timer = new Timer();

    private String proposalId;
    private TravelRequirementDAO travelRequirementDAO;

    private AgencyElectionTask(TravelRequirementDAO travelRequirementDAO, String proposalId) {
        this.proposalId = proposalId;
        this.travelRequirementDAO = travelRequirementDAO;
    }

    public void run() {
        List<String> agencies = new ArrayList<String>();
        LOG.info(LocalMessages.getMessage(LocalMessages.agency_election_start, proposalId));

        // TODO: Agency Election Algorithm 

        LOG.info(LocalMessages.getMessage(LocalMessages.agency_election_end, proposalId));

        if (Boolean.TRUE.booleanValue()) {
            String[] agencyIds = agencies.toArray(new String[agencies.size()]);
            LOG.info(LocalMessages.getMessage(LocalMessages.agency_election_success, proposalId, agencyIds.toString()));
            try {
                travelRequirementDAO.joinProposalBid(UuidUtil.fromUuidStr(proposalId), UUID.randomUUID());
            } catch (InvalidTravelReqirementException e) {
                LOG.error(LocalMessages.getMessage(LocalMessages.submit_proposal_failed, agencyIds.toString()), e);
            } catch (DatabaseAccessException e) {
                LOG.error(LocalMessages.getMessage(LocalMessages.submit_proposal_failed, agencyIds.toString()), e);
            }
            LOG.info(LocalMessages.getMessage(LocalMessages.submit_proposal_success, proposalId, agencyIds.toString()));
        } else {
            LOG.info(LocalMessages.getMessage(LocalMessages.agency_election_failed, proposalId));
        }
    }

    public static void schedule(TravelRequirementDAO travelRequirementDAO, String proposalId) {
        if (travelRequirementDAO == null || proposalId == null) {
            throw new NullPointerException();
        }

        timer.schedule(new AgencyElectionTask(travelRequirementDAO, proposalId), SCHEDULE_DELAY / 1000);
        LOG.info(LocalMessages.getMessage(LocalMessages.schedule_task_scheduled_run_once,
            AgencyElectionTask.class.getSimpleName(), SCHEDULE_DELAY / 1000));
    }
}
