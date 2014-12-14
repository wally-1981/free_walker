package com.free.walker.service.itinerary.basic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.json.Json;
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
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.util.UuidUtil;

public class Country implements Serializable, Loadable {
    private static Logger LOG = LoggerFactory.getLogger(Country.class);
    private static Map<UUID, Country> countries = new HashMap<UUID, Country>();
    private static TravelBasicDAO travelBasicDAO;

    static {
        travelBasicDAO = DAOFactory.getTravelBasicDAO();
    }

    private UUID uuid;
    private String name;
    private String chineseName;
    private String pinyinName;

    public Country() {
        ;
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Introspection.JSONKeys.UUID, uuid.toString());
        resBuilder.add(Introspection.JSONKeys.NAME, name);
        resBuilder.add(Introspection.JSONKeys.CHINESE_NAME, chineseName);
        resBuilder.add(Introspection.JSONKeys.PINYIN_NAME, pinyinName);
        return resBuilder.build();
    }

    public Country fromJSON(JsonObject jsObject) throws JsonException {
        String id = jsObject.getString(Introspection.JSONKeys.UUID, null);

        if (id == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.UUID, id));
        }

        Country country = countries.get(UuidUtil.fromUuidStr(id));
        if (country != null) {
            return country;
        } else {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.UUID, id));
        }
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.NULL;
    }

    public boolean load() {
        if (!countries.isEmpty()) {
            LOG.debug(LocalMessages.getMessage(LocalMessages.load_country_success, 0));
            return true;
        }

        try {
            List<Country> countries = travelBasicDAO.getAllCountries();

            if (countries == null || countries.size() == 0) {
                LOG.error(LocalMessages.getMessage(LocalMessages.load_country_failed));
                return false;
            }

            for (int i = 0; i < countries.size(); i++) {
                Country country = countries.get(i);
                Country.countries.put(country.uuid, country);
            }

            LOG.info(LocalMessages.getMessage(LocalMessages.load_country_success, countries.size()));
            return true;
        } catch (DatabaseAccessException e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.load_country_failed), e);
            countries.clear();
            return false;
        } catch (Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.load_country_failed), e);
            countries.clear();
            return false;
        }
    }

    public String getUuid() {
        return uuid.toString();
    }

    public void setUuid(String uuid) {
        try {
            if (UuidUtil.isCmpUuidStr(uuid)) {
                this.uuid = UuidUtil.fromCmpUuidStr(uuid);
            } else {
                this.uuid = UuidUtil.fromUuidStr(uuid);
            }
        } catch (InvalidTravelReqirementException e) {
            throw new IllegalStateException(e);
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
}
