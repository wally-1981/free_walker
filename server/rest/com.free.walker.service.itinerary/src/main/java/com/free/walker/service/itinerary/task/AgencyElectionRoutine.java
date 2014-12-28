package com.free.walker.service.itinerary.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.MRRoutine;
import com.free.walker.service.itinerary.basic.Agency;
import com.free.walker.service.itinerary.basic.TravelLocation;
import com.free.walker.service.itinerary.dao.TravelBasicDAO;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;

public class AgencyElectionRoutine extends AgencyElectionBaseRoutine {
    private static final Logger LOG = LoggerFactory.getLogger(AgencyElectionRoutine.class);

    private TravelBasicDAO travelBasicDAO;
    private TravelLocation source;
    private TravelLocation destination;

    public AgencyElectionRoutine(TravelBasicDAO travelBasicDAO, TravelLocation source) {
        if (travelBasicDAO == null || source == null) {
            throw new NullPointerException();
        }

        this.travelBasicDAO = travelBasicDAO;
        this.source = source;
    }

    public AgencyElectionRoutine(TravelBasicDAO travelBasicDAO, TravelLocation source, TravelLocation destination) {
        this(travelBasicDAO, source);

        if (destination == null) {
            throw new NullPointerException();
        }

        this.destination = destination;
    }

    public MRRoutine map() {
        try {
            if (destination == null || !travelBasicDAO.hasLocationByUuid(destination.getUuid())) {
                if (destination != null && destination.getUuid().matches("[1-7]")) {
                    mappedAgencyIds.addAll(travelBasicDAO.getAgencies4InternationalDestination(source.getUuid(),
                        destination.getUuid()));
                } else {
                    mappedAgencyIds.addAll(travelBasicDAO.getAgencies4DangleDestination(source.getUuid()));
                }
            } else {
                if (travelBasicDAO.isDomesticLocationByUuid(destination.getUuid())) {
                    mappedAgencyIds.addAll(travelBasicDAO.getAgencies4DomesticDestination(source.getUuid(),
                        destination.getUuid()));
                } else {
                    mappedAgencyIds.addAll(travelBasicDAO.getAgencies4InternationalDestination(source.getUuid(),
                        destination.getUuid()));
                }
            }
        } catch (DatabaseAccessException e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.agency_election_map_failed, source.getName(),
                destination == null ? null : destination.getName()), e);
            throw new IllegalStateException(e);
        }

        notify(mappedAgencyIds);

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

    private void notify(List<Agency> agencies) {
        // TODO: Integrate with Messaging System
        return;
    }
}
