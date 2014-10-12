package com.free.walker.service.itinerary.req;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.TravelTimeRange;
import com.free.walker.service.itinerary.basic.Resort;
import com.free.walker.service.itinerary.resort.ResortStar;

public class ResortRequirement extends BaseTravelRequirement implements TravelRequirement {
    private TravelTimeRange arrivalTimeRange;
    private Resort resort;
    private ResortStar resortStar;

    public ResortRequirement(TravelTimeRange arrivalTimeRange) {
        super();

        if (arrivalTimeRange == null) {
            throw new NullPointerException();
        }

        this.arrivalTimeRange = arrivalTimeRange;
    }

    public ResortRequirement(TravelTimeRange arrivalTimeRange, ResortStar resortStar) {
        this(arrivalTimeRange);

        if (resortStar == null) {
            throw new NullPointerException();
        }

        this.resortStar = resortStar;
    }

    public ResortRequirement(TravelTimeRange arrivalTimeRange, Resort resort) {
        this(arrivalTimeRange);

        if (resort == null) {
            throw new NullPointerException();
        }

        this.resort = resort;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject res = super.toJSON();
        res.put(Constants.JSONKeys.TIME_RANGE_START, arrivalTimeRange.getStart());
        res.put(Constants.JSONKeys.TIME_RANGE_OFFSET, arrivalTimeRange.getOffset());

        if (resortStar != null) {
            res.put(Constants.JSONKeys.STAR, resortStar.enumValue());
        }

        if (resort != null) {
            res.put(Constants.JSONKeys.RESORT, resort.toJSON());
        }

        return res;
    }
}
