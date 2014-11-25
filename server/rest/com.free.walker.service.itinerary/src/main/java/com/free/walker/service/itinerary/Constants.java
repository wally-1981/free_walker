package com.free.walker.service.itinerary;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.basic.City;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.util.UuidUtil;

public class Constants {
    public static City TAIBEI;
    public static City BARCELONA;
    public static City WUHAN;
    public static City GENEVA;

    // TODO: Replace this with the data from user account system.
    public static final UUID MockAdminUser;

    static {
        Logger LOG = LoggerFactory.getLogger(City.class);
        try {
            TAIBEI = new City(UuidUtil.fromUuidStr("02515d41-f141-4175-9a11-9e68b9cfe687"));
            BARCELONA = new City(UuidUtil.fromUuidStr("84844276-3036-47dd-90e0-f095cfa98da5"));
            WUHAN = new City(UuidUtil.fromUuidStr("79fd8642-a11d-4811-887d-ec4268097a82"));
            GENEVA = new City(UuidUtil.fromUuidStr("46e46912-f856-49ce-b9f8-cac99fe9211e"));
        } catch (InvalidTravelReqirementException e) {
            LOG.error(e.getMessage(), e);
        }

        MockAdminUser = UUID.fromString("8eeeeca9-a27c-46c1-a38f-30372d44fa70");
    }
}
