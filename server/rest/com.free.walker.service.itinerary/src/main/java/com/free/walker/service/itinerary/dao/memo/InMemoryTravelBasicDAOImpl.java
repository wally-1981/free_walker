package com.free.walker.service.itinerary.dao.memo;

import java.util.List;

import com.free.walker.service.itinerary.basic.City;
import com.free.walker.service.itinerary.basic.Country;
import com.free.walker.service.itinerary.basic.Province;
import com.free.walker.service.itinerary.dao.TravelBasicDAO;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;

public class InMemoryTravelBasicDAOImpl implements TravelBasicDAO {
    private static class SingletonHolder {
        private static final TravelBasicDAO INSTANCE = new InMemoryTravelBasicDAOImpl();
    }

    public static TravelBasicDAO getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private InMemoryTravelBasicDAOImpl() {
    }

    public boolean pingPersistence() {
        return true;
    }

    public List<Country> getAllCountries() throws DatabaseAccessException {
        throw new UnsupportedOperationException();
    }

    public List<Province> getAllProvinces() throws DatabaseAccessException {
        throw new UnsupportedOperationException();
    }

    public List<City> getAllCities() throws DatabaseAccessException {
        throw new UnsupportedOperationException();
    }

}
