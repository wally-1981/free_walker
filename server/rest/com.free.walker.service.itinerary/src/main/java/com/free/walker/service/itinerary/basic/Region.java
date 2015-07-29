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

public class Region implements Serializable, Loadable {
    private static final Logger LOG = LoggerFactory.getLogger(Region.class);

    private static Map<UUID, Region> regions = new HashMap<UUID, Region>();
    private TravelBasicDAO travelBasicDAO;

    private UUID uuid;
    private String name;
    private String chineseName;
    private String pinyinName;

    public Region() {
        travelBasicDAO = DAOFactory.getTravelBasicDAO();
    }

    public Region(UUID uuid) {
        this();

        if (!this.load()) throw new IllegalStateException();

        Region region = regions.get(uuid);

        if (region != null) {
            this.uuid = region.uuid;
            this.name = region.name;
            this.chineseName = region.chineseName;
            this.pinyinName = region.pinyinName;
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

    public Region fromJSON(JsonObject jsObject) throws JsonException {
        String id = jsObject.getString(Introspection.JSONKeys.UUID, null);

        if (id == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.UUID, id));
        }

        Region resgion = regions.get(UuidUtil.fromUuidStr(id));
        if (resgion != null) {
            return resgion;
        } else {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.UUID, id));
        }
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.NULL;
    }

    public boolean load() {
        if (!regions.isEmpty()) {
            LOG.debug(LocalMessages.getMessage(LocalMessages.load_region_success, 0));
            return true;
        }

        try {
            List<Region> countries = travelBasicDAO.getAllRegions();

            if (countries == null || countries.size() == 0) {
                LOG.error(LocalMessages.getMessage(LocalMessages.load_region_failed));
                return false;
            }

            for (int i = 0; i < countries.size(); i++) {
                Region region = countries.get(i);
                Region.regions.put(region.uuid, region);
            }

            LOG.info(LocalMessages.getMessage(LocalMessages.load_region_success, countries.size()));
            return true;
        } catch (DatabaseAccessException e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.load_region_failed), e);
            regions.clear();
            return false;
        } catch (Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.load_region_failed), e);
            regions.clear();
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
