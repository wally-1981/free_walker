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

    protected List<Agency> mappedAgencyIds = new ArrayList<Agency>();
    private List<Object> result = new ArrayList<Object>();

    public MRRoutine reduce() {
        List<Agency> topFeedback = reduceByFeedback(mappedAgencyIds, AGENCY_ELECTION_BY_FEEDBACK_SIZE);
        List<Agency> topExperience = reduceByExperience(mappedAgencyIds, AGENCY_ELECTION_BY_EXPERIENCE_SIZE);
        List<Agency> randomSelection = reduceByRandomization(mappedAgencyIds, AGENCY_ELECTION_BY_RANDOMIZATION_SIZE);

        result.addAll(topFeedback);
        result.addAll(topExperience);
        result.addAll(randomSelection);
        if (result.size() > AGENCY_ELECTION_MAX_SIZE) {
            result = result.subList(0, AGENCY_ELECTION_MAX_SIZE - 1);
        }

        return this;
    }

    public List<Object> collect() {
        return result;
    }

    public abstract List<Agency> reduceByFeedback(List<Agency> agencies, int resultMaxSize);

    public abstract List<Agency> reduceByExperience(List<Agency> agencies, int resultMaxSize);

    public abstract List<Agency> reduceByRandomization(List<Agency> agencies, int resultMaxSize);
}
