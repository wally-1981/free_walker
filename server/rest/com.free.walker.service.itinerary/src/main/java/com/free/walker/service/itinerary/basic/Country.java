package com.free.walker.service.itinerary.basic;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.free.walker.service.itinerary.Serializable;
import com.ibm.icu.util.ULocale;

public class Country implements Serializable {
    public static final Country CHINA = new Country(ULocale.CHINA);
    public static final Country US = new Country(ULocale.US);
    public static final Country UK = new Country(ULocale.UK);
    public static final Country CANADA = new Country(ULocale.CANADA);

    private ULocale locale;

    private Country(ULocale locale) {
        if (locale == null) {
            throw new NullPointerException();
        }

        this.locale = locale;
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Introspection.JSONKeys.NAME, locale.getCountry());
        return resBuilder.build();
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.OBJECT;
    }
}
