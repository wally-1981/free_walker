package com.free.walker.service.itinerary.task;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.MRRoutine;
import com.free.walker.service.itinerary.basic.Agency;
import com.free.walker.service.itinerary.dao.TravelBasicDAO;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.util.SystemConfigUtil;

public abstract class AgencyElectionBaseRoutine implements MRRoutine {
    private static final Logger LOG = LoggerFactory.getLogger(AgencyElectionBaseRoutine.class);

    private static int AGENCY_ELECTION_BY_FEEDBACK_SIZE;
    private static int AGENCY_ELECTION_BY_EXPERIENCE_SIZE;
    private static int AGENCY_ELECTION_BY_RANDOMIZATION_SIZE;
    private static int AGENCY_ELECTION_MAX_SIZE;

    static {
        String setting = null;
        try {
            Properties applicationConfig = SystemConfigUtil.getApplicationConfig();
            setting = applicationConfig.getProperty(Constants.agency_election_by_feedback_size);
            AGENCY_ELECTION_BY_FEEDBACK_SIZE = Integer.valueOf(setting);
            setting = applicationConfig.getProperty(Constants.agency_election_by_experience_size);
            AGENCY_ELECTION_BY_EXPERIENCE_SIZE = Integer.valueOf(setting);
            setting = applicationConfig.getProperty(Constants.agency_election_by_randomization_size);
            AGENCY_ELECTION_BY_RANDOMIZATION_SIZE = Integer.valueOf(setting);
            setting = applicationConfig.getProperty(Constants.agency_election_max_size);
            AGENCY_ELECTION_MAX_SIZE = Integer.valueOf(setting);
        } catch (FileNotFoundException e) {
            LOG.warn(LocalMessages.getMessage(LocalMessages.read_configuration_file_failed), e);
        } catch (IOException e) {
            LOG.warn(LocalMessages.getMessage(LocalMessages.read_configuration_file_failed), e);
        } catch (RuntimeException e) {
            AGENCY_ELECTION_BY_FEEDBACK_SIZE = 1;
            AGENCY_ELECTION_BY_EXPERIENCE_SIZE = 1;
            AGENCY_ELECTION_BY_RANDOMIZATION_SIZE = 1;
            AGENCY_ELECTION_MAX_SIZE = 3;
            LOG.warn(LocalMessages.getMessage(LocalMessages.eval_configuration_value_failed, setting,
                LocalMessages.eval_configuration_value_failed), e);
        }
    }

    private List<Object> result = new ArrayList<Object>();
    protected String proposalId;
    protected TravelBasicDAO travelBasicDAO;

    protected AgencyElectionBaseRoutine(String proposalId, TravelBasicDAO travelBasicDAO) {
        this.proposalId = proposalId;
        this.travelBasicDAO = travelBasicDAO;
    }

    public MRRoutine reduce() {
        try {
            List<Agency> candidates = travelBasicDAO.getRespondedAgencyCandidates4Proposal(proposalId);
            if (candidates.size() <= AGENCY_ELECTION_MAX_SIZE) {
                result.addAll(candidates);
                return this;
            }

            List<Agency> topFeedback = reduceByFeedback(candidates, AGENCY_ELECTION_BY_FEEDBACK_SIZE);
            List<Agency> topExperience = reduceByExperience(candidates, AGENCY_ELECTION_BY_EXPERIENCE_SIZE);
            List<Agency> randomSelection = reduceByRandomization(candidates, AGENCY_ELECTION_BY_RANDOMIZATION_SIZE);

            int cadidateSize = Math.min(AGENCY_ELECTION_BY_FEEDBACK_SIZE + AGENCY_ELECTION_BY_EXPERIENCE_SIZE
                + AGENCY_ELECTION_BY_RANDOMIZATION_SIZE, AGENCY_ELECTION_MAX_SIZE);
            for (int i = 1; i <= cadidateSize; i++) {
                if (i % 3 == 0) {
                    if (topExperience.isEmpty()) {
                        if (randomSelection.isEmpty()) {
                            result.add(topFeedback.remove(0));
                        } else {
                            result.add(randomSelection.remove(0));
                        }
                    } else {
                        result.add(topExperience.remove(0));
                    }
                } else if (i % 2 == 0) {
                    if (topFeedback.isEmpty()) {
                        if (topExperience.isEmpty()) {
                            result.add(randomSelection.remove(0));
                        } else {
                            result.add(topExperience.remove(0));
                        }
                    } else {
                        result.add(topFeedback.remove(0));
                    }
                } else {
                    if (randomSelection.isEmpty()) {
                        if (topFeedback.isEmpty()) {
                            result.add(topExperience.remove(0));
                        } else {
                            result.add(topFeedback.remove(0));
                        }
                    } else {
                        result.add(randomSelection.remove(0));
                    }
                }
            }
        } catch (DatabaseAccessException e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.agency_election_reduce_failed, proposalId), e);
            throw new IllegalStateException(e);
        }

        return this;
    }

    public List<Object> collect() {
        return result;
    }

    protected void notify(List<Agency> agencies) {
        // TODO: Integrate with Messaging System
        return;
    }

    public abstract List<Agency> reduceByFeedback(List<Agency> agencies, int resultMaxSize);

    public abstract List<Agency> reduceByExperience(List<Agency> agencies, int resultMaxSize);

    public abstract List<Agency> reduceByRandomization(List<Agency> agencies, int resultMaxSize);
}
