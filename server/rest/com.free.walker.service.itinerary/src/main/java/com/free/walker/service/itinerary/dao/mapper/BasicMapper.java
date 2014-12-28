package com.free.walker.service.itinerary.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.free.walker.service.itinerary.basic.Agency;
import com.free.walker.service.itinerary.basic.City;
import com.free.walker.service.itinerary.basic.Country;
import com.free.walker.service.itinerary.basic.Province;
import com.free.walker.service.itinerary.basic.Tag;

public interface BasicMapper {
    public int pingPersistence();

    public List<Country> getAllCounties();

    public List<Province> getAllProvinces();

    public List<City> getAllCities();

    public Country getCountry(@Param("uuid") String uuid);

    public Province getProvince(@Param("uuid") String uuid);

    public City getCity(@Param("uuid") String uuid);

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

    public void associateLocatoin(@Param("primary") String primary, @Param("secondary") String secondary);

    public void deassociateLocatoin(@Param("primary") String primary, @Param("secondary") String secondary);

    public void associatePortLocatoin(@Param("primary") String primary, @Param("secondary") String secondary);

    public void deassociatePortLocatoin(@Param("primary") String primary, @Param("secondary") String secondary);

    public void addAgency(Agency agency);

    public void deleteAgency(@Param("uuid") String uuid);
}
