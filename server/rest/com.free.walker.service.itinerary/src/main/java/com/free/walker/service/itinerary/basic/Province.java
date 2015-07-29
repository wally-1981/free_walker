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
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.util.UuidUtil;

public class Province  implements Serializable, Loadable {
    private static final Logger LOG = LoggerFactory.getLogger(Province.class);

    private static Map<UUID, Province> provinces = new HashMap<UUID, Province>();
    private TravelBasicDAO travelBasicDAO;

    private UUID uuid;
    private String name;
    private String chineseName;
    private String pinyinName;

    public Province() {
        travelBasicDAO = DAOFactory.getTravelBasicDAO();
    }

    public Province(UUID uuid) {
        this();

        if (!this.load()) throw new IllegalStateException();

        Province province = provinces.get(uuid);

        if (province != null) {
            this.uuid = province.uuid;
            this.name = province.name;
            this.chineseName = province.chineseName;
            this.pinyinName = province.pinyinName;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Introspection.JSONKeys.UUID, uuid.toString());
        resBuilder.add(Introspection.JSONKeys.NAME, name);
        resBuilder.add(Introspection.JSONKeys.CHINESE_NAME, chineseName);
        resBuilder.add(Introspection.JSONKeys.PINYIN_NAME, pinyinName);
        return resBuilder.build();
    }

    public Province fromJSON(JsonObject jsObject) throws JsonException {
        String id = jsObject.getString(Introspection.JSONKeys.UUID, null);

        if (id == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.UUID, id));
        }

        Province province = provinces.get(UuidUtil.fromUuidStr(id));
        if (province != null) {
            return province;
        } else {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.UUID, id));
        }
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.NULL;
    }

    public boolean load() {
        if (!provinces.isEmpty()) {
            LOG.debug(LocalMessages.getMessage(LocalMessages.load_province_success, 0));
            return true;
        }

        try {
            List<Province> provinces = travelBasicDAO.getAllProvinces();

            if (provinces == null || provinces.size() == 0) {
                LOG.error(LocalMessages.getMessage(LocalMessages.load_province_failed));
                return false;
            }

            for (int i = 0; i < provinces.size(); i++) {
                Province province = provinces.get(i);
                Province.provinces.put(province.uuid, province);
            }
            LOG.info(LocalMessages.getMessage(LocalMessages.load_province_success, provinces.size()));
            return true;
        } catch (DatabaseAccessException e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.load_province_failed), e);
            provinces.clear();
            return false;
        } catch (Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.load_province_failed), e);
            provinces.clear();
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
}
