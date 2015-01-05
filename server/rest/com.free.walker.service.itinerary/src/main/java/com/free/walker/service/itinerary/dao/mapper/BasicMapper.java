package com.free.walker.service.itinerary.dao.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.free.walker.service.itinerary.basic.Agency;
import com.free.walker.service.itinerary.basic.City;
import com.free.walker.service.itinerary.basic.Country;
import com.free.walker.service.itinerary.basic.Province;
import com.free.walker.service.itinerary.basic.Region;
import com.free.walker.service.itinerary.basic.StringPair;
import com.free.walker.service.itinerary.basic.Tag;

public interface BasicMapper {
    public int pingPersistence();

    public List<Region> getAllRegions();

    public List<Country> getAllCounties();

    public List<Province> getAllProvinces();

    public List<City> getAllCities();

    public Region getRegion(@Param("uuid") String uuid);

    public Country getCountry(@Param("uuid") String uuid);

    public Province getProvince(@Param("uuid") String uuid);

    public City getCity(@Param("uuid") String uuid);

    public Agency getAgency(@Param("uuid") String uuid);

    public String hasLocationByTerm(@Param("term") String term);

    public String hasLocationByUuid(@Param("uuid") String uuid);

    public int countLocationByTerm(@Param("term") String term);

    public int countLocationByUuid(@Param("uuid") String uuid);

    public int countDomesticLocationByTerm(@Param("term") String term);

    public int countDomesticLocationByUuid(@Param("uuid") String uuid);

    public List<Agency> getAgencies4DomesticDestination(@Param("departureLocationUuid") String departureLocationUuid,
        @Param("destinationLocationUuid") String destinationLocationUuid);

    public List<Agency> getAgencies4InternationalDestination(@Param("departureLocationUuid") String departureLocationUuid,
        @Param("destinationLocationUuid") String destinationLocationUuid);

    public List<Agency> getAgencies4DangleDestination(@Param("departureLocationUuid") String departureLocationUuid);

    public List<Tag> getHottestTags(@Param("topN") int topN);

    public void associateLocation(@Param("primary") String primary, @Param("secondary") String secondary);

    public int countAssociatedLocation(@Param("primary") String primary);

    public void deassociateLocation(@Param("primary") String primary, @Param("secondary") String secondary);

    public void associatePortLocation(@Param("primary") String primary, @Param("secondary") String secondary);

    public int countAssociatedPortLocation(@Param("primary") String primary);

    public void deassociatePortLocation(@Param("primary") String primary, @Param("secondary") String secondary);

    public void addAgency(Agency agency);

    public void addAgencies(Map<String, Object> agencies);

    public void relAgencyLocation(@Param("agencyId") String agencyId, @Param("locationId") String locationId,
        @Param("sendRecv") boolean isRecv);

    public int countRelAgencyLocation4Send(@Param("agencyId") String agencyId);

    public int countRelAgencyLocation4Recv(@Param("agencyId") String agencyId);

    public void unrelAgencyLocation(@Param("agencyId") String agencyId, @Param("locationId") String locationId,
        @Param("sendRecv") boolean isRecv);

    public List<String> getRelAgencyLocation4Send(@Param("agencyId") String agencyId);

    public List<String> getRelAgencyLocation4Recv(@Param("agencyId") String agencyId);

    public void deleteAgency(@Param("uuid") String uuid);

    public void addAgencyCandidates4Proposal(Map<String, Object> proposalCandidates);

    public List<Agency> getAgencyCandidates4Proposal(@Param("proposalId") String proposalId);

    public List<StringPair> getProposals4AgencyCandidate(@Param("agencyId") String agencyId,
        @Param("latestNMin") int latestNMin);

    public void markAgencyCandidateAsResponded(@Param("proposalId") String proposalId,
        @Param("agencyId") String agencyId);

    public List<Agency> getRespondedAgencyCandidates4Proposal(@Param("proposalId") String proposalId);

    public String canRotateElection4Proposal(@Param("proposalId") String proposalId,
        @Param("latestNHour") int latestNHour);

    public void markAgencyCandidatesAsElected(Map<String, Object> proposalAgencyIds);

    public List<Agency> getElectedAgencyCandidates4Proposal(@Param("proposalId") String proposalId);

    public List<Agency> getNotElectedAgencyCandidates4Proposal(@Param("proposalId") String proposalId);
}
