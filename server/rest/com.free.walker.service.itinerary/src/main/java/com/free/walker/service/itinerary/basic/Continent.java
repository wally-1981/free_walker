package com.free.walker.service.itinerary.basic;

import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.Serializable;
import com.free.walker.service.itinerary.primitive.ContinentID;
import com.free.walker.service.itinerary.primitive.Introspection;

public class Continent implements Serializable {
    private static final Map<Integer, Continent> CONTINENTS = new HashMap<Integer, Continent>();

    private static final Continent ASIA = new Continent(Introspection.JSONValues.CONTINENT_ID_ASIA,
        "Asia", "亚洲", "yazhou");
    private static final Continent EUROPE = new Continent(Introspection.JSONValues.CONTINENT_ID_EUROPE,
        "Europe", "欧洲", "ouzhou");
    private static final Continent NORTH_AMERICA = new Continent(Introspection.JSONValues.CONTINENT_ID_NORTH_AMERICA,
        "North America", "北美洲", "beimeizhou");
    private static final Continent SOUTH_AMERICA = new Continent(Introspection.JSONValues.CONTINENT_ID_SOUTH_AMERICA,
        "South America", "南美洲", "nanmeizhou");
    private static final Continent OCEANIA = new Continent(Introspection.JSONValues.CONTINENT_ID_OCEANIA,
        "Oceania", "大洋州", "dayangzhou");
    private static final Continent AFRICA = new Continent(Introspection.JSONValues.CONTINENT_ID_AFRICA,
        "Africa", "非洲", "feizhou");
    private static final Continent ANTARCTICA = new Continent(Introspection.JSONValues.CONTINENT_ID_ANTARCTICA,
        "Antarctica", "南极洲", "nanjizhou");

    private ContinentID id;
    private String name;
    private String chineseName;
    private String pinyinName;

    public static Continent getContinent(int continentId) {
        return CONTINENTS.get(continentId);
    }

    protected Continent() {
        ;
    }

    private Continent(ContinentID id, String name, String chineseName, String pinyinName) {
        if (id == null || name == null || chineseName == null || pinyinName == null) {
            throw new NullPointerException();
        }

        this.id = id;
        this.name = name;
        this.chineseName = chineseName;
        this.pinyinName = pinyinName;

        CONTINENTS.put(id.enumValue(), this);
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Introspection.JSONKeys.UUID, id.enumValue());
        resBuilder.add(Introspection.JSONKeys.NAME, name);
        resBuilder.add(Introspection.JSONKeys.CHINESE_NAME, chineseName);
        resBuilder.add(Introspection.JSONKeys.PINYIN_NAME, pinyinName);
        return resBuilder.build();
    }

    public Continent fromJSON(JsonObject jsObject) throws JsonException {
        int id = jsObject.getInt(Introspection.JSONKeys.UUID, 0);

        if (id == Introspection.JSONValues.CONTINENT_ID_ASIA.enumValue()) {
            return ASIA;
        } else if (id == Introspection.JSONValues.CONTINENT_ID_EUROPE.enumValue()) {
            return EUROPE;
        } else if (id == Introspection.JSONValues.CONTINENT_ID_NORTH_AMERICA.enumValue()) {
            return NORTH_AMERICA;
        } else if (id == Introspection.JSONValues.CONTINENT_ID_SOUTH_AMERICA.enumValue()) {
            return SOUTH_AMERICA;
        } else if (id == Introspection.JSONValues.CONTINENT_ID_OCEANIA.enumValue()) {
            return OCEANIA;
        } else if (id == Introspection.JSONValues.CONTINENT_ID_AFRICA.enumValue()) {
            return AFRICA;
        } else if (id == Introspection.JSONValues.CONTINENT_ID_ANTARCTICA.enumValue()) {
            return ANTARCTICA;
        } else {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.UUID, id));
        }
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.NULL;
    }

    public String getUuid() {
        return String.valueOf(id.enumValue());
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
