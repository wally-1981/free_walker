package com.free.walker.service.itinerary;

import javax.json.Json;

import org.apache.shiro.crypto.hash.Sha256Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.basic.Account;
import com.free.walker.service.itinerary.basic.City;
import com.free.walker.service.itinerary.primitive.AccountStatus;
import com.free.walker.service.itinerary.primitive.AccountType;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.util.UuidUtil;

public class Constants {
    public static City TAIBEI;
    public static City BARCELONA;
    public static City WUHAN;
    public static City GENEVA;

    public static final String NEW_PROPOSAL;

    public static final Account DEFAULT_USER_ACCOUNT;
    public static final Account DEFAULT_WECHAT_USER_ACCOUNT;
    public static final Account DEFAULT_AGENCY_ACCOUNT;
    public static final Account ADMIN_ACCOUNT;

    public static final String dot_sep = ",";

    public static final String agency_election_window_in_min = "agency_election_window_in_min";
    public static final String agency_election_by_feedback_size = "agency_election_by_feedback_size";
    public static final String agency_election_by_experience_size = "agency_election_by_experience_size";
    public static final String agency_election_by_randomization_size = "agency_election_by_randomization_size";
    public static final String agency_election_max_size = "agency_election_max_size";

    public static final String provider_ids = "provider_ids";
    public static final String providers_provider_name = "providers_provider_name";
    public static final String providers_provider_class = "providers_provider_class";
    public static final String providers_provider_url = "providers_provider_url";
    public static final String providers_provider_user = "providers_provider_user";
    public static final String providers_provider_sign = "providers_provider_sign";

    public static final String SERVICE_METHOD_KEY = "org.apache.cxf.resource.method";

    static {
        Sha256Hash hashedPwdBase64 = new Sha256Hash("passw0rd", null, 1024);

        ADMIN_ACCOUNT = new Account().fromJSON(
            Json.createObjectBuilder()
            .add(Introspection.JSONKeys.UUID, "8eeeeca9-a27c-46c1-a38f-30372d44fa70")
            .add(Introspection.JSONKeys.TYPE, AccountType.ADMIN.ordinal())
            .add(Introspection.JSONKeys.STATUS, AccountStatus.ACTIVE.ordinal())
            .add(Introspection.JSONKeys.LOGIN, "admin")
            .add(Introspection.JSONKeys.PASSWORD, hashedPwdBase64.toBase64())
            .add(Introspection.JSONKeys.MOBILE, "")
            .add(Introspection.JSONKeys.EMAIL, "")
            .add(Introspection.JSONKeys.NAME, "admin_user")
            .add(Introspection.JSONKeys.REF_LINK, "")
            .add(Introspection.JSONKeys.ROLE,
                Json.createArrayBuilder().add(AccountType.getDefaultRole(AccountType.ADMIN).ordinal()))
            .build());

        DEFAULT_USER_ACCOUNT = new Account().fromJSON(
            Json.createObjectBuilder()
            .add(Introspection.JSONKeys.UUID, "3b3e4dcf-e353-4418-adfb-3c9af7a54992")
            .add(Introspection.JSONKeys.TYPE, AccountType.MASTER.ordinal())
            .add(Introspection.JSONKeys.STATUS, AccountStatus.ACTIVE.ordinal())
            .add(Introspection.JSONKeys.LOGIN, "default_user")
            .add(Introspection.JSONKeys.PASSWORD, hashedPwdBase64.toBase64())
            .add(Introspection.JSONKeys.MOBILE, "")
            .add(Introspection.JSONKeys.EMAIL, "")
            .add(Introspection.JSONKeys.NAME, "default_user")
            .add(Introspection.JSONKeys.REF_LINK, "")
            .add(Introspection.JSONKeys.ROLE,
                Json.createArrayBuilder().add(AccountType.getDefaultRole(AccountType.MASTER).ordinal()))
            .build());

        DEFAULT_WECHAT_USER_ACCOUNT = new Account().fromJSON(
            Json.createObjectBuilder()
            .add(Introspection.JSONKeys.UUID, "18fd9d88-8e82-4903-9b2c-d94d02a8edb2")
            .add(Introspection.JSONKeys.TYPE, AccountType.WeChat.ordinal())
            .add(Introspection.JSONKeys.STATUS, AccountStatus.ACTIVE.ordinal())
            .add(Introspection.JSONKeys.LOGIN, "default_wechat_user")
            .add(Introspection.JSONKeys.PASSWORD, hashedPwdBase64.toBase64())
            .add(Introspection.JSONKeys.MOBILE, "")
            .add(Introspection.JSONKeys.EMAIL, "")
            .add(Introspection.JSONKeys.NAME, "default_wechat_user")
            .add(Introspection.JSONKeys.REF_LINK, "")
            .add(Introspection.JSONKeys.ROLE,
                Json.createArrayBuilder().add(AccountType.getDefaultRole(AccountType.WeChat).ordinal()))
            .build());

        DEFAULT_AGENCY_ACCOUNT = new Account().fromJSON(
            Json.createObjectBuilder()
            .add(Introspection.JSONKeys.UUID, "7d128f8f-5143-4971-96de-7d33ae7e9ba5")
            .add(Introspection.JSONKeys.TYPE, AccountType.AGENCY.ordinal())
            .add(Introspection.JSONKeys.STATUS, AccountStatus.ACTIVE.ordinal())
            .add(Introspection.JSONKeys.LOGIN, "default_agency")
            .add(Introspection.JSONKeys.PASSWORD, hashedPwdBase64.toBase64())
            .add(Introspection.JSONKeys.MOBILE, "")
            .add(Introspection.JSONKeys.EMAIL, "")
            .add(Introspection.JSONKeys.NAME, "default_agency")
            .add(Introspection.JSONKeys.REF_LINK, "")
            .add(Introspection.JSONKeys.ROLE,
                Json.createArrayBuilder().add(AccountType.getDefaultRole(AccountType.AGENCY).ordinal()))
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
