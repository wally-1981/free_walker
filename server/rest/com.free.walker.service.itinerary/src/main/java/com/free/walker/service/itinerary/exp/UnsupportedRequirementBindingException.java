package com.free.walker.service.itinerary.exp;

import com.free.walker.service.itinerary.req.TravelRequirement;

public class UnsupportedRequirementBindingException extends IllegalAccessException {
    private static final long serialVersionUID = -8411238185715632548L;

    public UnsupportedRequirementBindingException(TravelRequirement bindee, TravelRequirement binder) {

    }
}
