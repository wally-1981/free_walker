package com.free.walker.service.itinerary;

import javax.json.Json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.basic.Account;
import com.free.walker.service.itinerary.basic.City;
import com.free.walker.service.itinerary.primitive.AccountType;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.util.UuidUtil;

public class Constants {
    public static City TAIBEI;
    public static City BARCELONA;
    public static City WUHAN;
    public static City GENEVA;

    public static final String NEW_PROPOSAL;

    public static final Account DEFAULT_ACCOUNT;
    public static final Account DEFAULT_AGENCY_ACCOUNT;
    public static final Account ADMIN_ACCOUNT;

    public static final String agency_election_window_in_min = "agency_election_window_in_min";
    public static final String agency_election_by_feedback_size = "agency_election_by_feedback_size";
    public static final String agency_election_by_experience_size = "agency_election_by_experience_size";
    public static final String agency_election_by_randomization_size = "agency_election_by_randomization_size";
    public static final String agency_election_max_size = "agency_election_max_size";

    static {
        ADMIN_ACCOUNT = new Account().fromJSON(
            Json.createObjectBuilder()
            .add(Introspection.JSONKeys.UUID, "8eeeeca9-a27c-46c1-a38f-30372d44fa70")
            .add(Introspection.JSONKeys.TYPE, AccountType.ADMIN.ordinal())
            .add(Introspection.JSONKeys.LOGIN, "admin")
            .add(Introspection.JSONKeys.MOBILE, "")
            .add(Introspection.JSONKeys.EMAIL, "")
            .add(Introspection.JSONKeys.NAME, "admin_user")
            .add(Introspection.JSONKeys.REF_LINK, "")
            .build());

        DEFAULT_ACCOUNT = new Account().fromJSON(
            Json.createObjectBuilder()
            .add(Introspection.JSONKeys.UUID, "3b3e4dcf-e353-4418-adfb-3c9af7a54992")
            .add(Introspection.JSONKeys.TYPE, AccountType.MASTER.ordinal())
            .add(Introspection.JSONKeys.LOGIN, "default")
            .add(Introspection.JSONKeys.MOBILE, "")
            .add(Introspection.JSONKeys.EMAIL, "")
            .add(Introspection.JSONKeys.NAME, "default_user")
            .add(Introspection.JSONKeys.REF_LINK, "")
            .build());

        DEFAULT_AGENCY_ACCOUNT = new Account().fromJSON(
            Json.createObjectBuilder()
            .add(Introspection.JSONKeys.UUID, "7d128f8f-5143-4971-96de-7d33ae7e9ba5")
            .add(Introspection.JSONKeys.TYPE, AccountType.AGENCY.ordinal())
            .add(Introspection.JSONKeys.LOGIN, "agency")
            .add(Introspection.JSONKeys.MOBILE, "")
            .add(Introspection.JSONKeys.EMAIL, "")
            .add(Introspection.JSONKeys.NAME, "agency_user")
            .add(Introspection.JSONKeys.REF_LINK, "")
            .build());

        NEW_PROPOSAL = "New Proposal";

        Logger LOG = LoggerFactory.getLogger(City.class);
        try {
            TAIBEI = new City(UuidUtil.fromUuidStr("02515d41-f141-4175-9a11-9e68b9cfe687"));
            BARCELONA = new City(UuidUtil.fromUuidStr("84844276-3036-47dd-90e0-f095cfa98da5"));
            WUHAN = new City(UuidUtil.fromUuidStr("79fd8642-a11d-4811-887d-ec4268097a82"));
            GENEVA = new City(UuidUtil.fromUuidStr("46e46912-f856-49ce-b9f8-cac99fe9211e"));
        } catch (IllegalArgumentException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
