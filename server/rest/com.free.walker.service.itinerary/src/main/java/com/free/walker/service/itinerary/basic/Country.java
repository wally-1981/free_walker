package com.free.walker.service.itinerary.basic;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.SerializableJSON;
import com.ibm.icu.util.ULocale;

public class Country implements SerializableJSON {
    public final static Country CHINA = new Country(ULocale.CHINA);
    public final static Country US = new Country(ULocale.US);
    public final static Country UK = new Country(ULocale.UK);
    public final static Country CANADA = new Country(ULocale.CANADA);

    private ULocale locale;

    private Country(ULocale locale) {
        if (locale == null) {
            throw new NullPointerException();
        }

        this.locale = locale;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject res = new JSONObject();
        res.put(Constants.JSONKeys.NAME, locale.getCountry());
        return res;
    }
}
