package com.free.walker.service.itinerary.dao;

import java.util.List;

import com.free.walker.service.itinerary.basic.Agency;
import com.free.walker.service.itinerary.basic.City;
import com.free.walker.service.itinerary.basic.Country;
import com.free.walker.service.itinerary.basic.Province;
import com.free.walker.service.itinerary.basic.Tag;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;

public interface TravelBasicDAO extends HealthyDAO {
    public List<Country> getAllCountries() throws DatabaseAccessException;

    public List<Province> getAllProvinces() throws DatabaseAccessException;

    public List<City> getAllCities() throws DatabaseAccessException;

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

    public void associatePortLocation(String primary, String secondary) throws DatabaseAccessException;

    public void deassociateLocation(String primary, String secondary) throws DatabaseAccessException;

    public void deassociatePortLocation(String primary, String secondary) throws DatabaseAccessException;

    public String addAgency(Agency agency) throws DatabaseAccessException;

    public String removeAgency(String uuid) throws DatabaseAccessException;
}
