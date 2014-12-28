package com.free.walker.service.itinerary.task;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;
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

    private static Timer timer = new Timer();

    private MRRoutine agencyElectionRoutine;
    private String proposalId;

    public static TravelBasicDAO travelBasicDAO;
    public static TravelRequirementDAO travelRequirementDAO;

    private AgencyElectionTask(MRRoutine agencyElectionRoutine, String proposalId) {
        this.agencyElectionRoutine = agencyElectionRoutine;
        this.proposalId = proposalId;
    }

    public void run() {
        LOG.info(LocalMessages.getMessage(LocalMessages.agency_election_start, proposalId));
        List<Object> agencies = new ArrayList<Object>();
        try {
            agencies = agencyElectionRoutine.reduce().collect();
        } catch (IllegalStateException e) {
            agencies = null;
        }
        LOG.info(LocalMessages.getMessage(LocalMessages.agency_election_end, proposalId));

        if (agencies != null) {
            String[] agencyIds = agencies.toArray(new String[agencies.size()]);
            LOG.info(LocalMessages.getMessage(LocalMessages.agency_election_success, proposalId,
                StringUtils.join(agencyIds, ',')));

            try {
                for (int i = 0; i < agencyIds.length; i++) {
                    travelRequirementDAO.joinProposalBid(UuidUtil.fromUuidStr(proposalId),
                        UuidUtil.fromUuidStr(agencyIds[i]));
                }
                LOG.info(LocalMessages.getMessage(LocalMessages.submit_proposal_success, proposalId,
                    StringUtils.join(agencyIds, ',')));
            } catch (InvalidTravelReqirementException e) {
                LOG.error(LocalMessages.getMessage(LocalMessages.submit_proposal_failed,
                    StringUtils.join(agencyIds, ',')), e);
            } catch (DatabaseAccessException e) {
                LOG.error(LocalMessages.getMessage(LocalMessages.submit_proposal_failed,
                    StringUtils.join(agencyIds, ',')), e);
            }
        }
    }

    public static void schedule(MRRoutine agencyElectionRoutine, String proposalId) {
        if (agencyElectionRoutine == null || proposalId == null) {
            throw new NullPointerException();
        }

        int delayMins = DEFAULT_DELAY_MIN;
        String setting = null;
        try {
            Properties applicationConfig = SystemConfigUtil.getApplicationConfig();
            setting = applicationConfig.getProperty(Constants.agency_election_window_in_min);
            delayMins = Integer.valueOf(setting);
        } catch (FileNotFoundException e) {
            LOG.warn(LocalMessages.getMessage(LocalMessages.read_configuration_file_failed), e);
        } catch (IOException e) {
            LOG.warn(LocalMessages.getMessage(LocalMessages.read_configuration_file_failed), e);
        } catch (RuntimeException e) {
            delayMins = DEFAULT_DELAY_MIN;
            LOG.warn(LocalMessages.getMessage(LocalMessages.eval_configuration_value_failed, setting,
                LocalMessages.eval_configuration_value_failed), e);
        }

        try {
            timer.schedule(new AgencyElectionTask(agencyElectionRoutine.map(), proposalId),
                (SCHEDULE_DELAY * delayMins) / 1000);
            LOG.info(LocalMessages.getMessage(LocalMessages.schedule_task_scheduled_run_once,
                AgencyElectionTask.class.getSimpleName(), SCHEDULE_DELAY / 1000));
        } catch (IllegalStateException e) {
            LOG.info(LocalMessages.getMessage(LocalMessages.agency_election_task_cancelled));
        }
    }
}
