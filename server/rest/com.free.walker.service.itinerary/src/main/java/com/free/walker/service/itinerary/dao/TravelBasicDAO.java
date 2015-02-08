package com.free.walker.service.itinerary.dao;

import java.util.List;
import java.util.Map;

import com.free.walker.service.itinerary.basic.Agency;
import com.free.walker.service.itinerary.basic.City;
import com.free.walker.service.itinerary.basic.Country;
import com.free.walker.service.itinerary.basic.Province;
import com.free.walker.service.itinerary.basic.Region;
import com.free.walker.service.itinerary.basic.StringTriple;
import com.free.walker.service.itinerary.basic.Tag;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;

public interface TravelBasicDAO extends HealthyDAO {
    public List<Region> getAllRegions() throws DatabaseAccessException;

    public List<Country> getAllCountries() throws DatabaseAccessException;

    public List<Province> getAllProvinces() throws DatabaseAccessException;

    public List<City> getAllCities() throws DatabaseAccessException;

    public List<StringTriple> getLocationIndexTermsByLocatoinIds(List<String> locationIds)
        throws DatabaseAccessException;

    public List<StringTriple> getRegionIndexTermsByRegionalLocatoinIds(List<String> locationIds)
        throws DatabaseAccessException;

    public boolean hasLocationByTerm(String term) throws DatabaseAccessException;

    public boolean hasLocationByUuid(String uuid) throws DatabaseAccessException;

    public boolean isDomesticLocationByTerm(String term) throws DatabaseAccessException;

    public boolean isDomesticLocationByUuid(String uuid) throws DatabaseAccessException;

    public List<Agency> getAgencies4DomesticDestination(String sourceLocationUuid, String destinationLocationUuid)
        throws DatabaseAccessException;

    public List<Agency> getAgencies4InternationalDestination(String sourceLocationUuid, String destinationLocationUuid)
        throws DatabaseAccessException;

    public List<Agency> getAgencies4DangleDestination(String sourceLocationUuid) throws DatabaseAccessException;

    public List<Tag> getHottestTags(int topN) throws DatabaseAccessException;

    public void associateLocation(String primary, String secondary) throws DatabaseAccessException;

    public int countAssociatedLocation(String primary) throws DatabaseAccessException;

    public void deassociateLocation(String primary, String secondary) throws DatabaseAccessException;

    public void associatePortLocation(String primary, String secondary) throws DatabaseAccessException;

    public int countAssociatedPortLocation(String primary) throws DatabaseAccessException;

    public void deassociatePortLocation(String primary, String secondary) throws DatabaseAccessException;

    public String addAgency(Agency agency) throws DatabaseAccessException;

    public List<String> addAgencies(List<Agency> agencies, Map<String, Map<String, List<String>>> locations)
        throws DatabaseAccessException;

    public Agency getAgency(String agencyId) throws DatabaseAccessException;

    public void relAgencyLocation(String agencyId, List<String> sendLocationIds, List<String> recvLocationIds)
        throws DatabaseAccessException;

    public int countRelAgencyLocation4Send(String agencyId) throws DatabaseAccessException;

    public int countRelAgencyLocation4Recv(String agencyId) throws DatabaseAccessException;

    public void unrelAgencyLocation(String agencyId, List<String> sendLocationIds, List<String> recvLocationIds)
        throws DatabaseAccessException;

    public List<String> getAgencyLocations(String agencyId, int sendRecv) throws DatabaseAccessException;

    public String removeAgency(String uuid) throws DatabaseAccessException;

    public void addAgencyCandidates4Proposal(String proposalId, String proposalSummary, List<Agency> candidates)
        throws DatabaseAccessException;

    public Map<String, String> getProposals4AgencyCandidate(String agencyId, int electionWindow)
        throws DatabaseAccessException;

    public void markAgencyCandidateAsResponded(String proposalId, String agencyId) throws DatabaseAccessException;

    public boolean canRotateElection4Proposal(String proposalId, int electionRotateWindow)
        throws DatabaseAccessException;

    public List<Agency> getRespondedAgencyCandidates4Proposal(String proposalId) throws DatabaseAccessException;

    public void markAgencyCandidatesAsElected(String proposalId, List<String> agencyIds) throws DatabaseAccessException;

    public List<Agency> getAgencyCandidates4Proposal(String proposalId) throws DatabaseAccessException;

    public List<Agency> getElectedAgencyCandidates4Proposal(String proposalId) throws DatabaseAccessException;

    public List<Agency> getNotElectedAgencyCandidates4Proposal(String proposalId) throws DatabaseAccessException;
}
