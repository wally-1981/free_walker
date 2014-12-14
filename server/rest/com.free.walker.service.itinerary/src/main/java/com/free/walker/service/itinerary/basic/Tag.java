package com.free.walker.service.itinerary.basic;

import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.Loadable;
import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.Reloadable;
import com.free.walker.service.itinerary.Serializable;
import com.free.walker.service.itinerary.dao.DAOFactory;
import com.free.walker.service.itinerary.dao.TravelBasicDAO;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.primitive.Introspection;

public class Tag implements Serializable, Loadable, Reloadable {
    private static final int TOP_N = 10;

    private static Logger LOG = LoggerFactory.getLogger(Tag.class);
    private static List<Tag> tags = new ArrayList<Tag>();
    private static TravelBasicDAO travelBasicDAO;

    static {
        travelBasicDAO = DAOFactory.getTravelBasicDAO();
    }

    private String name;
    private long frequency;

    public Tag() {
        ;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public long getFrequency() {
        return frequency;
    }
    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    public boolean load() {
        if (!tags.isEmpty()) {
            LOG.debug(LocalMessages.getMessage(LocalMessages.load_tag_success, 0));
            return true;
        }

        return reload();
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.NULL;
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Introspection.JSONKeys.NAME, name);
        resBuilder.add(Introspection.JSONKeys.FREQUENCY, frequency);
        return resBuilder.build();
    }

    public Object fromJSON(JsonObject jsObject) throws JsonException {
        String name = jsObject.getString(Introspection.JSONKeys.NAME, null);

        if (name == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.NAME, name));
        }

        JsonNumber frequency = jsObject.getJsonNumber(Introspection.JSONKeys.FREQUENCY);

        if (frequency == null || frequency.longValue() < 0) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.FREQUENCY, frequency.longValue()));
        } else {
            this.frequency = frequency.longValue();
        }

        return this;
    }

    public boolean reload() {
        try {
            List<Tag> tags = travelBasicDAO.getHottestTags(TOP_N);

            if (tags == null || tags.size() == 0) {
                LOG.error(LocalMessages.getMessage(LocalMessages.load_tag_failed));
                return false;
            }

            for (int i = 0; i < tags.size(); i++) {
                Tag tag = tags.get(i);
                Tag.tags.add(tag);
            }
            LOG.info(LocalMessages.getMessage(LocalMessages.load_tag_success, tags.size()));
            return true;
        } catch (DatabaseAccessException e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.load_tag_failed), e);
            tags.clear();
            return false;
        } catch (Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.load_tag_failed), e);
            tags.clear();
            return false;
        }
    }
}
