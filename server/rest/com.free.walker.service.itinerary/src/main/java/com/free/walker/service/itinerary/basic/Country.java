package com.free.walker.service.itinerary.basic;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.Serializable;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.ibm.icu.util.ULocale;

public class Country implements Serializable {
    private ULocale locale;

    public Country() {
        ;
    }

    public Country(ULocale locale) {
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

    public Country fromJSON(JsonObject jsObject) throws JsonException {
        String countryName = jsObject.getString(Introspection.JSONKeys.NAME);

        if (countryName == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.NAME, countryName));
        }

        ULocale[] locales = ULocale.getAvailableLocales();

        for (int i = 0; i < locales.length; i++) {
            if (locales[i].getCountry().equalsIgnoreCase(countryName)) {
                locale = locales[i];
                return this;
            }
        }

        throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
            Introspection.JSONKeys.NAME, countryName));
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.OBJECT;
    }
}
