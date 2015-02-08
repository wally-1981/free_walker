package com.free.walker.service.itinerary.basic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.Loadable;
import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.Serializable;
import com.free.walker.service.itinerary.dao.DAOFactory;
import com.free.walker.service.itinerary.dao.TravelBasicDAO;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.util.UuidUtil;

public class City implements Serializable, Loadable {
    private static final Logger LOG = LoggerFactory.getLogger(City.class);

    private static Map<UUID, City> cities = new HashMap<UUID, City>();
    private TravelBasicDAO travelBasicDAO;

    private UUID uuid;
    private String name;
    private String chineseName;
    private String pinyinName;
    private UUID provinceUuid;
    private UUID countryUuid;
    private int continentId;

    public City() {
        travelBasicDAO = DAOFactory.getTravelBasicDAO();
    }

    public City(UUID uuid) {
        this();

        if (!this.load()) throw new IllegalStateException();

        City city = cities.get(uuid);

        if (city != null) {
            this.uuid = city.uuid;
            this.name = city.name;
            this.chineseName = city.chineseName;
            this.pinyinName = city.pinyinName;
            this.provinceUuid = city.provinceUuid;
            this.countryUuid = city.countryUuid;
            this.continentId = city.continentId;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        JsonArrayBuilder idsBuilder = Json.createArrayBuilder();
        idsBuilder.add(uuid.toString());
        idsBuilder.add(provinceUuid.toString());
        idsBuilder.add(countryUuid.toString());
        idsBuilder.add(String.valueOf(continentId));
        resBuilder.add(Introspection.JSONKeys.UUID, idsBuilder);
        resBuilder.add(Introspection.JSONKeys.NAME, name);
        resBuilder.add(Introspection.JSONKeys.CHINESE_NAME, chineseName);
        resBuilder.add(Introspection.JSONKeys.PINYIN_NAME, pinyinName);
        return resBuilder.build();
    }

    public City fromJSON(JsonObject jsObject) throws JsonException {
        String id = jsObject.getString(Introspection.JSONKeys.UUID, null);
        if (id == null) {
            JsonArray uuidArray = jsObject.getJsonArray(Introspection.JSONKeys.UUID);
            if (uuidArray != null && uuidArray.size() >= 1) {
                id = uuidArray.getString(0, null);
            }
        }

        if (id == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.UUID, id));
        }

        City city = cities.get(UuidUtil.fromUuidStr(id));
        if (city != null) {
            return city;
        } else {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.UUID, id));
        }
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.NULL;
    }

    public boolean load() {
        if (!cities.isEmpty()) {
            LOG.debug(LocalMessages.getMessage(LocalMessages.load_city_success, 0));
            return true;
        }

        try {
            List<City> cities = travelBasicDAO.getAllCities();

            if (cities == null || cities.size() == 0) {
                LOG.error(LocalMessages.getMessage(LocalMessages.load_country_failed));
                return false;
            }

            for (int i = 0; i < cities.size(); i++) {
                City city = cities.get(i);
                City.cities.put(city.uuid, city);
            }

            LOG.info(LocalMessages.getMessage(LocalMessages.load_city_success, cities.size()));
            return true;
        } catch (DatabaseAccessException e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.load_city_failed), e);
            cities.clear();
            return false;
        } catch (Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.load_city_failed), e);
            cities.clear();
            return false;
        }
    }

    public String getUuid() {
        return uuid.toString();
    }

    public void setUuid(String uuid) {
        if (UuidUtil.isCmpUuidStr(uuid)) {
            this.uuid = UuidUtil.fromCmpUuidStr(uuid);
        } else {
            this.uuid = UuidUtil.fromUuidStr(uuid);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getPinyinName() {
        return pinyinName;
    }

    public void setPinyinName(String pinyinName) {
        this.pinyinName = pinyinName;
    }

    public String getProvinceUuid() {
        return provinceUuid.toString();
    }

    public void setProvinceUuid(String provinceUuid) {
        if (UuidUtil.isCmpUuidStr(provinceUuid)) {
            this.provinceUuid = UuidUtil.fromCmpUuidStr(provinceUuid);
        } else {
            this.provinceUuid = UuidUtil.fromUuidStr(provinceUuid);
        }
    }

    public String getCountryUuid() {
        return countryUuid.toString();
    }

    public void setCountryUuid(String countryUuid) {
        if (UuidUtil.isCmpUuidStr(countryUuid)) {
            this.countryUuid = UuidUtil.fromCmpUuidStr(countryUuid);
        } else {
            this.countryUuid = UuidUtil.fromUuidStr(countryUuid);
        }
    }

    public int getContinentId() {
        return continentId;
    }

    public void setContinentId(int continentId) {
        this.continentId = continentId;
    }

    public Object[] getRelatedLocations() {
        return new Object[] { provinceUuid, countryUuid, Continent.getContinent(continentId) };
    }
}
