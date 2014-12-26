package com.free.walker.service.itinerary.basic;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.Serializable;
import com.free.walker.service.itinerary.primitive.Introspection;

public class TravelLocation implements Serializable {
    private City city;
    private Province province;
    private Country country;
    private Continent continent;

    public TravelLocation() {
        ;
    }

    public TravelLocation(City city) {
        this.city = city;
    }

    public TravelLocation(Province province) {
        this.province = province;
    }

    public TravelLocation(Country country) {
        this.country = country;
    }

    public TravelLocation(Continent continent) {
        this.continent = continent;
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder res = Json.createObjectBuilder();

        if (city != null) {
            res.add(Introspection.JSONKeys.CITY, city.toJSON());
        } else if (province != null) {
            res.add(Introspection.JSONKeys.PROVINCE, province.toJSON());
        } else if (country != null) {
            res.add(Introspection.JSONKeys.COUNTRY, country.toJSON());
        } else if (continent != null) {
            res.add(Introspection.JSONKeys.CONTINENT, continent.toJSON());
        } else {
            ;
        }

        return res.build();
    }

    public Object fromJSON(JsonObject jsObject) throws JsonException {
        JsonObject cityObj = jsObject.getJsonObject(Introspection.JSONKeys.CITY);
        JsonObject provinceObj = jsObject.getJsonObject(Introspection.JSONKeys.PROVINCE);
        JsonObject countryObj = jsObject.getJsonObject(Introspection.JSONKeys.COUNTRY);
        JsonObject continentObj = jsObject.getJsonObject(Introspection.JSONKeys.CONTINENT);

        if (cityObj != null) {            
            city = new City().fromJSON(cityObj);
        } else if (provinceObj != null) {
            province = new Province().fromJSON(provinceObj);
        } else if (countryObj != null) {
            country = new Country().fromJSON(countryObj);
        } else if (continentObj != null) {
            continent = new Continent().fromJSON(continentObj);
        } else {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.CITY, cityObj));
        }

        return this;
    }

    public String getUuid() {
        if (city != null) {
            return city.getUuid();
        } else if (province != null) {
            return province.getUuid();
        } else if (country != null) {
            return country.getUuid();
        } else if (continent != null) {
            return continent.getUuid();
        } else {
            return "";
        }
    }

    public String getName() {
        if (city != null) {
            return city.getName();
        } else if (province != null) {
            return province.getName();
        } else if (country != null) {
            return country.getName();
        } else if (continent != null) {
            return continent.getName();
        } else {
            return "";
        }
    }

    public String getChineseName() {
        if (city != null) {
            return city.getChineseName();
        } else if (province != null) {
            return province.getChineseName();
        } else if (country != null) {
            return country.getChineseName();
        } else if (continent != null) {
            return continent.getChineseName();
        } else {
            return "";
        }
    }

    public String getPinyinName() {
        if (city != null) {
            return city.getPinyinName();
        } else if (province != null) {
            return province.getPinyinName();
        } else if (country != null) {
            return country.getPinyinName();
        } else if (continent != null) {
            return continent.getPinyinName();
        } else {
            return "";
        }
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.NULL;
    }
}
