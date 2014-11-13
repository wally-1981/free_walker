package com.free.walker.service.itinerary.dao;

import java.util.List;

import com.free.walker.service.itinerary.basic.City;
import com.free.walker.service.itinerary.basic.Country;
import com.free.walker.service.itinerary.basic.Province;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;

public interface TravelBasicDAO extends HealthyDAO {
    public List<Country> getAllCountries() throws DatabaseAccessException;
    public List<Province> getAllProvinces() throws DatabaseAccessException;
    public List<City> getAllCities() throws DatabaseAccessException;
}
