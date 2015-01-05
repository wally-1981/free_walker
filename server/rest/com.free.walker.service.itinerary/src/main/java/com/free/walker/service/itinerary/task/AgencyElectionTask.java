package com.free.walker.service.itinerary.task;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CancellationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.MRRoutine;
import com.free.walker.service.itinerary.dao.TravelBasicDAO;
import com.free.walker.service.itinerary.dao.TravelRequirementDAO;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.util.SystemConfigUtil;
import com.free.walker.service.itinerary.util.UuidUtil;

public class AgencyElectionTask extends TimerTask {
    private static final Logger LOG = LoggerFactory.getLogger(AgencyElectionTask.class);
    private static final int SCHEDULE_DELAY = 1 * 1000 * 60;
    private static final int DEFAULT_DELAY_MIN = 10;

    private static int delayMins = DEFAULT_DELAY_MIN;

    private MRRoutine agencyElectionRoutine;
    private String proposalId;

    private static TravelBasicDAO travelBasicDAO;
    private static TravelRequirementDAO travelRequirementDAO;

    private AgencyElectionTask(MRRoutine agencyElectionRoutine, String proposalId) {
        this.agencyElectionRoutine = agencyElectionRoutine;
        this.proposalId = proposalId;
    }

    public void run() {
        LOG.info(LocalMessages.getMessage(LocalMessages.agency_election_start, proposalId));
        String[] agencyIds = null;
        try {
            List<Object> agencies = agencyElectionRoutine.reduce().collect();
            agencyIds = agencies.toArray(new String[agencies.size()]);
            if (agencyIds.length == 0) {
                throw new IllegalStateException(LocalMessages.getMessage(LocalMessages.agency_election_error,
                    proposalId));
            } else {
                LOG.info(LocalMessages.getMessage(LocalMessages.agency_election_success, proposalId,
                    agencyIds.toString()));
            }
        } catch (IllegalStateException e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.agency_election_failed, proposalId), e);
            return;
        }
        LOG.info(LocalMessages.getMessage(LocalMessages.agency_election_end, proposalId));

        try {
            for (int i = 0; i < agencyIds.length; i++) {
                travelRequirementDAO.joinProposalBid(UuidUtil.fromUuidStr(proposalId),
                    UuidUtil.fromUuidStr(agencyIds[i]));
            }
            LOG.info(LocalMessages.getMessage(LocalMessages.proposal_submission_success, proposalId, agencyIds.toString()));
        } catch (InvalidTravelReqirementException e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.proposal_submission_failed, agencyIds.toString()), e);
            return;
        } catch (DatabaseAccessException e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.proposal_submission_failed, agencyIds.toString()), e);
            return;
        }

        try {
            travelBasicDAO.markAgencyCandidatesAsElected(proposalId, Arrays.asList(agencyIds));
        } catch (DatabaseAccessException e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.proposal_submission_mark_elected_agency_failed,
                    agencyIds.toString(), proposalId), e);
        }

        return;
    }

    public static void schedule(MRRoutine agencyElectionRoutine, String proposalId, int delayMins) {
        if (agencyElectionRoutine == null || proposalId == null) {
            throw new NullPointerException();
        }

        if (agencyElectionRoutine.immediate()) {
            AgencyElectionTask.delayMins = 0;
        } else {
            if (delayMins <= 0) {
                String setting = null;
                try {
                    Properties applicationConfig = SystemConfigUtil.getApplicationConfig();
                    setting = applicationConfig.getProperty(Constants.agency_election_window_in_min);
                    AgencyElectionTask.delayMins = Integer.valueOf(setting);
                } catch (FileNotFoundException e) {
                    LOG.warn(LocalMessages.getMessage(LocalMessages.read_configuration_file_failed), e);
                } catch (IOException e) {
                    LOG.warn(LocalMessages.getMessage(LocalMessages.read_configuration_file_failed), e);
                } catch (RuntimeException e) {
                    AgencyElectionTask.delayMins = DEFAULT_DELAY_MIN;
                    LOG.warn(LocalMessages.getMessage(LocalMessages.eval_configuration_value_failed, setting,
                        LocalMessages.eval_configuration_value_failed), e);
                }
            } else {
                AgencyElectionTask.delayMins = delayMins;
            }
        }

        try {
            new Timer().schedule(new AgencyElectionTask(agencyElectionRoutine.map(), proposalId),
                (SCHEDULE_DELAY * delayMins) / 1000);
            LOG.info(LocalMessages.getMessage(LocalMessages.schedule_task_scheduled_run_once,
                AgencyElectionTask.class.getSimpleName(), (SCHEDULE_DELAY * delayMins) / 1000));
        } catch (CancellationException e) {
            LOG.info(LocalMessages.getMessage(LocalMessages.agency_election_task_descheduled, proposalId));
            throw e;
        } catch (IllegalStateException e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.agency_election_task_cancelled, proposalId), e);
            throw e;
        }
    }

    public static int getElectionWindow() {
        return delayMins;
    }
}
