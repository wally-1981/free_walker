package com.free.walker.service.itinerary.task;

import com.free.walker.service.itinerary.MRRoutine;
import com.free.walker.service.itinerary.basic.TravelLocation;
import com.free.walker.service.itinerary.dao.TravelBasicDAO;
import com.free.walker.service.itinerary.dao.TravelRequirementDAO;

public class AgencyElectionReduceRoutine extends AgencyElectionRoutine {
    public AgencyElectionReduceRoutine(String proposalId, TravelBasicDAO travelBasicDAO,
        TravelRequirementDAO travelRequirementDAO, TravelLocation departure) {
        super(proposalId, travelBasicDAO, travelRequirementDAO, departure);
    }

    public AgencyElectionReduceRoutine(String proposalId, TravelBasicDAO travelBasicDAO,
        TravelRequirementDAO travelRequirementDAO, TravelLocation departure, TravelLocation destination) {
        super(proposalId, travelBasicDAO, travelRequirementDAO, departure, destination);
    }

    public MRRoutine map() {
        return this;
    }

    public boolean immediate() {
        return true;
    }
}
