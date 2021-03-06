package com.free.walker.service.itinerary.primitive;

import org.elasticsearch.search.sort.SortOrder;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.TravelLocation.LocationType;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.product.HotelItem;
import com.free.walker.service.itinerary.product.ResortItem;
import com.free.walker.service.itinerary.product.TrafficItem;
import com.free.walker.service.itinerary.product.TrivItem;
import com.free.walker.service.itinerary.req.DestinationRequirement;
import com.free.walker.service.itinerary.req.HotelRequirement;
import com.free.walker.service.itinerary.req.ResortRequirement;
import com.free.walker.service.itinerary.req.TrafficRequirement;

public class Introspection {
    public static class JSONKeys {
        /*
         * JSONObject Key
         */
        public static final String TYPE = "type";
        public static final String SUB_TYPE = "sub_type";
        public static final String ATTRIBUTES = "attributes";

        public static final String REQUIREMENTS = "requirements";
        public static final String ITEMS = "items";
        public static final String TAGS = "tags";
        public static final String AGENCIES = "agencies";
        public static final String CITIES = "cities";

        public static final String STATUS = "status";
        public static final String CITY = "city";
        public static final String PROVINCE = "province";
        public static final String COUNTRY = "country";
        public static final String REGION = "region";
        public static final String CONTINENT = "continent";
        public static final String HOTEL = "hotel";
        public static final String RESORT = "resort";
        public static final String TRAFFIC = "traffic";
        public static final String TRAIN = "train";
        public static final String FLIGHT = "flight";

        public static final String ABBR = "abbr";
        public static final String TERM = "term";
        public static final String TEMPLATE = "template";
        public static final String PAGE_NUM = "page_num";
        public static final String PAGE_SIZE = "page_size";
        public static final String SORT_KEY = "sort_key";
        public static final String SORT_ORDER = "sort_order";
        public static final String SORT_TYPE = "sort_type";
        public static final String HITS = "hits";
        public static final String TOTAL_HITS_NUMBER = "total_hits_number";

        public static final String ROLE = "role";
        public static final String PERMISSION = "permission";

        /*
         * JSONArray Keys
         */
        public static final String DATETIME_SELECTIONS = "datetime_selections";
        public static final String DATETIME_RANGE_SELECTIONS = "datetime_range_selections";

        /*
         * Primitive Keys
         */
        public static final String UUID = "uuid";
        public static final String ID = "id";
        public static final String VIA = "via";
        public static final String OWNER = "owner";
        public static final String REF_UUID = "ref_uuid";
        public static final String REF_LINK = "ref_link";
        public static final String REF_ENTITY = "ref_entity";
        public static final String LOGIN = "login";
        public static final String PASSWORD = "password";
        public static final String MOBILE = "mobile";
        public static final String EMAIL = "email";
        public static final String AUTHOR = "author";
        public static final String NAME = "name";
        public static final String TITLE = "title";
        public static final String BUDGET = "budget";
        public static final String UNIT = "unit";
        public static final String NOTE = "note";
        public static final String SUMMARY = "summary";
        public static final String LOCATION = "location";
        public static final String LOCATION_TYPE = "location_type";
        public static final String DATE = "date";
        public static final String CHINESE_NAME = "chinese_name";
        public static final String PINYIN_NAME = "pinyin_name";
        public static final String STAR = "star";
        public static final String HMD = "hmd";
        public static final String EXP = "exp";
        public static final String NIGHT = "night";
        public static final String DEADLINE_DATETIME = "deadline_datetime";
        public static final String ARRIVAL_DATETIME = "arrival_datetime";
        public static final String DEPARTURE_DATETIME = "departure_datetime";
        public static final String RETURN_DATETIME = "return_datetime";
        public static final String DEPARTURE = "departure";
        public static final String DEPARTURE_REL = "departure_rel";
        public static final String DESTINATION = "destination";
        public static final String DESTINATION_REL = "destination_rel";
        public static final String RETURN = "return";
        public static final String TIME_RANGE_START = "time_range_start";
        public static final String TIME_RANGE_OFFSET = "time_range_offset";
        public static final String TRAFFIC_TOOL_TYPE = "traffice_tool_type";
        public static final String TRAFFIC_TOOL_SEAT_CLASS = "traffice_tool_seat_class";
        public static final String GROUP_CAPACITY = "group_capacity";
        public static final String MIN = "min";
        public static final String MAX = "max";
        public static final String PRICE = "price";
        public static final String BIDDING = "bidding";
        public static final String FREQUENCY = "frequency";
        public static final String PRODUCT_OWNER = "product_owner";
        public static final String PROPOSAL_OWNER = "proposal_owner";
        public static final String SYNC_ADD_NUMBER = "sync_add_number";
        public static final String SYNC_UPDATE_NUMBER = "sync_update_number";
        public static final String SYNC_DELETE_NUMBER = "sync_delete_number";
        public static final String SYNC_ADD = "sync_add";
        public static final String SYNC_UPDATE = "sync_update";
        public static final String SYNC_DELETE = "sync_delete";
        public static final String SYNC_META = "sync_meta";
        public static final String SYNC_DATE_BEFORE = "sync_date_before";
        public static final String SYNC_DATE_AFTER = "sync_date_after";

        public static final String ERROR_CODE = "error_code";
        public static final String ERROR_TYPE = "error_type";
        public static final String ERROR_CNTX = "error_cntx";
        public static final String ERROR_DESC = "error_desc";
        public static final String ERROR_ACTN = "error_actn";

        public static final String SEND = "send";
        public static final String RECV = "recv";

        public static final String TIMEOUT = "timeout";
        public static final String SESSION_START = "session_start";
        public static final String SESSION_STOP = "session_stop";
        public static final String ACCESS = "access";

        public static final String ORIGINAL = "original";

        public static class Resounce {
            public static final String PROVIDER_ID = "provider_id";
            public static final String PROVIDER_NAME = "provider_name";
            public static final String LEVEL_1_CATEGORY = "level_1_category";
            public static final String LEVEL_2_CATEGORY = "level_2_category";
            public static final String CODE = "code";
            public static final String NAME = "name";
            public static final String LOCATION = "location";
            public static final String LATITUDE = "latitude";
            public static final String LONGITUDE = "longitude";
            public static final String DEST_NAME = "dest_name";
            public static final String DEPT_NAME = "dept_name";
            public static final String SUMMARY = "summary";
            public static final String DESCRIPTION = "description";
            public static final String PHOTOS = "photos";
            public static final String TAGS = "tags";
            public static final String DURATION_TYPE = "duration_type";
            public static final String DURATION_PRIMARY_PART = "duration_primary_part";
            public static final String DURATION_SECONDARY_PART = "duration_secondary_part";
            public static final String PICK_UP_REMARK = "pick_up_remark";
            public static final String DROP_OFF_REMARK = "drop_off_remark";
            public static final String HIGHLIGHTS = "highlights";
            public static final String NET_PRICE = "net_price";
            public static final String RETAIL_PRICE = "retail_price";
            public static final String CURRENCY = "currency";
            public static final String INCLUSION = "inclusion";
            public static final String EXCLUSION = "exclusion";
            public static final String ATTENTION = "attention";
            public static final String SPECIFICATIONS = "specifications";
            public static final String SPECIFICATION_NAME = "specification_name";
            public static final String CROWD_TYPE = "crowd_type";
            public static final String MIN_AGE = "min_age";
            public static final String MAX_AGE = "max_age";
            public static final String UNIT = "unit";

            public static final String SYNC_ACTION = "sync_action";
        }
    }

    public static class JSONValues {
        public static final String REQUIREMENT_TYPE_PROPOSAL = "proposal";
        public static final String REQUIREMENT_TYPE_ITINERARY = "itinerary";
        public static final String REQUIREMENT_TYPE_REQUIREMENT = "requirement";

        public static final HotelStar HOTEL_STAR_STD_5 = new HotelStar(500);
        public static final HotelStar HOTEL_STAR_STD_4 = new HotelStar(400);
        public static final HotelStar HOTEL_STAR_STD_3 = new HotelStar(300);
        public static final HotelStar HOTEL_STAR_STD_2 = new HotelStar(200);
        public static final HotelStar HOTEL_STAR_LST_5 = new HotelStar(50);
        public static final HotelStar HOTEL_STAR_LST_4 = new HotelStar(40);
        public static final HotelStar HOTEL_STAR_LST_3 = new HotelStar(30);

        public static final ResortStar RESORT_STAR_STD_5A = new ResortStar(500);
        public static final ResortStar RESORT_STAR_STD_4A = new ResortStar(400);
        public static final ResortStar RESORT_STAR_STD_3A = new ResortStar(300);
        public static final ResortStar RESORT_STAR_STD_2A = new ResortStar(200);

        public static final TrafficToolSeatClass TRAFFIC_TOOL_SEAT_CLASS_1ST = new TrafficToolSeatClass(100);
        public static final TrafficToolSeatClass TRAFFIC_TOOL_SEAT_CLASS_2ND = new TrafficToolSeatClass(200);
        public static final TrafficToolSeatClass TRAFFIC_TOOL_SEAT_CLASS_3RD = new TrafficToolSeatClass(300);

        public static final TrafficToolType TRAFFIC_TOOL_TYPE_FLIGHT = new TrafficToolType(1);
        public static final TrafficToolType TRAFFIC_TOOL_TYPE_TRAIN = new TrafficToolType(2);

        public static final TravelTimeRange TIME_RANGE_00_06 = new TravelTimeRange(0, 6);
        public static final TravelTimeRange TIME_RANGE_06_12 = new TravelTimeRange(6, 6);
        public static final TravelTimeRange TIME_RANGE_12_18 = new TravelTimeRange(12, 6);
        public static final TravelTimeRange TIME_RANGE_18_24 = new TravelTimeRange(18, 6);

        public static final ContinentID CONTINENT_ID_ASIA = new ContinentID(1);
        public static final ContinentID CONTINENT_ID_EUROPE = new ContinentID(2);
        public static final ContinentID CONTINENT_ID_NORTH_AMERICA = new ContinentID(3);
        public static final ContinentID CONTINENT_ID_SOUTH_AMERICA = new ContinentID(4);
        public static final ContinentID CONTINENT_ID_OCEANIA = new ContinentID(5);
        public static final ContinentID CONTINENT_ID_AFRICA = new ContinentID(6);
        public static final ContinentID CONTINENT_ID_ANTARCTICA = new ContinentID(7);

        public static final ProductStatus DRAFT_PRODUCT = ProductStatus.DRAFT_PRODUCT;
        public static final ProductStatus PRIVATE_PRODUCT = ProductStatus.PRIVATE_PRODUCT;
        public static final ProductStatus PUBLIC_PRODUCT = ProductStatus.PUBLIC_PRODUCT;
        public static final ProductStatus ACTIVE_PRODUCT = ProductStatus.ACTIVE_PRODUCT;
        public static final ProductStatus ING_PRODUCT = ProductStatus.ING_PRODUCT;
        public static final ProductStatus ARCHIVED_PRODUCT = ProductStatus.ARCHIVED_PRODUCT;

        public static final String CITY = LocationType.CITY.name();
        public static final String PROVINCE = LocationType.PROVINCE.name();
        public static final String COUNTRY = LocationType.COUNTRY.name();
        public static final String REGION = LocationType.REGION.name();
        public static final String CONTINENT = LocationType.CONTINENT.name();

        public static final String SUB_TYPE_HOTEL = HotelRequirement.SUB_TYPE;
        public static final String SUB_TYPE_TRAFFIC = TrafficRequirement.SUB_TYPE;
        public static final String SUB_TYPE_RESORT = ResortRequirement.SUB_TYPE;
        public static final String SUB_TYPE_DESTINATION = DestinationRequirement.SUB_TYPE;

        public static final String SUB_TYPE_HOTEL_ITEM = HotelItem.SUB_TYPE;
        public static final String SUB_TYPE_TRAFFIC_ITEM = TrafficItem.SUB_TYPE;
        public static final String SUB_TYPE_RESORT_ITEM = ResortItem.SUB_TYPE;
        public static final String SUB_TYPE_TRIV_ITEM = TrivItem.SUB_TYPE;

        public static final int ACCOUNT_TYPE_AGENCY = AccountType.AGENCY.ordinal();
        public static final int ACCOUNT_TYPE_MASTER = AccountType.MASTER.ordinal();
        public static final int ACCOUNT_TYPE_WECHAT = AccountType.WeChat.ordinal();
        public static final int ACCOUNT_TYPE_QQ = AccountType.QQ.ordinal();
        public static final int ACCOUNT_TYPE_ALIPAY = AccountType.AliPay.ordinal();
        public static final int ACCOUNT_TYPE_WEIBO = AccountType.WeiBo.ordinal();

        public static final int ACCOUNT_STATUS_ACTIVE = AccountStatus.ACTIVE.ordinal();
        public static final int ACCOUNT_STATUS_IN_ACTIVE = AccountStatus.IN_ACTIVE.ordinal();
        public static final int ACCOUNT_STATUS_LOCKED = AccountStatus.LOCKED.ordinal();
        public static final int ACCOUNT_STATUS_REVOKED = AccountStatus.REVOKED.ordinal();

        public static final int PRODUCT_TEMPLATE_AS_INT = QueryTemplate.PRODUCT_TEMPLATE.enumValue();
        public static final int PRODUCT_DEPARTURE_TEMPLATE_AS_INT = QueryTemplate.PRODUCT_DEPARTURE.enumValue();
        public static final int PRODUCT_DESTINATION_TEMPLATE_AS_INT = QueryTemplate.PRODUCT_DESTINATION.enumValue();
        public static final int PRODUCT_OWNER_TEMPLATE_AS_INT = QueryTemplate.PRODUCT_OWNER.enumValue();
        public static final int PROPOSAL_OWNER_TEMPLATE_AS_INT = QueryTemplate.PROPOSAL_OWNER.enumValue();
        public static final int RESOURCE_TEMPLATE_AS_INT = QueryTemplate.RESOURCE.enumValue();
        public static final String PRODUCT_TEMPLATE_AS_STR = QueryTemplate.PRODUCT_TEMPLATE.nameValue();
        public static final String PRODUCT_DEPARTURE_TEMPLATE_AS_STR = QueryTemplate.PRODUCT_DEPARTURE.nameValue();
        public static final String PRODUCT_DESTINATION_TEMPLATE_AS_STR = QueryTemplate.PRODUCT_DESTINATION.nameValue();
        public static final String PRODUCT_OWNER_TEMPLATE_AS_STR = QueryTemplate.PRODUCT_OWNER.nameValue();
        public static final String PROPOSAL_OWNER_TEMPLATE_AS_STR = QueryTemplate.PROPOSAL_OWNER.nameValue();
        public static final String RESOURCE_TEMPLATE_AS_STR = QueryTemplate.RESOURCE.nameValue();

        public static final int SORT_LONG_TYPE_AS_INT = SortType.LONG.enumValue();
        public static final int SORT_INT_TYPE_AS_INT = SortType.INT.enumValue();
        public static final int SORT_FLOAT_TYPE_AS_INT = SortType.FLOAT.enumValue();
        public static final int SORT_DOUBLE_TYPE_AS_INT = SortType.DOUBLE.enumValue();
        public static final String SORT_LONG_TYPE_AS_STR = SortType.LONG.nameValue();
        public static final String SORT_INT_TYPE_AS_STR = SortType.INT.nameValue();
        public static final String SORT_FLOAT_TYPE_AS_STR = SortType.FLOAT.nameValue();
        public static final String SORT_DOUBLE_TYPE_AS_STR = SortType.DOUBLE.nameValue();

        public static final int SORT_ASC_ORDER_AS_INT = SortOrder.ASC.ordinal();
        public static final int SORT_DESC_ORDER_AS_INT = SortOrder.DESC.ordinal();
        public static final String SORT_ASC_ORDER_AS_STR = SortOrder.ASC.toString();
        public static final String SORT_DESC_ORDER_AS_STR = SortOrder.DESC.toString();
    }

    public static class DefaultAccounts {
        public static final String ADMIN_ACCOUNT = Constants.ADMIN_ACCOUNT.getLogin();
        public static final String DEFAULT_MASTER_ACCOUNT = Constants.DEFAULT_USER_ACCOUNT.getLogin();
        public static final String DEFAULT_WECHAT_ACCOUNT = Constants.DEFAULT_WECHAT_USER_ACCOUNT.getLogin();
        public static final String DEFAULT_AGENCY_ACCOUNT = Constants.DEFAULT_AGENCY_ACCOUNT.getLogin();
        public static final String DEFAULT_MASTER_ACCOUNT_UUID = Constants.DEFAULT_USER_ACCOUNT.getUuid();
    }

    public static class TestValues {
        public static final String DEFAULT_PROPOSAL_TITLE = Constants.NEW_PROPOSAL;
    }

    public static class JsonValueHelper {
        public static HotelStar getHotelStar(int star) throws InvalidTravelReqirementException {
            if (star == JSONValues.HOTEL_STAR_STD_5.enumValue()) {
                return JSONValues.HOTEL_STAR_STD_5;
            } else if (star == JSONValues.HOTEL_STAR_STD_4.enumValue()) {
                return JSONValues.HOTEL_STAR_STD_4;
            } else if (star == JSONValues.HOTEL_STAR_STD_3.enumValue()) {
                return JSONValues.HOTEL_STAR_STD_3;
            } else if (star == JSONValues.HOTEL_STAR_STD_2.enumValue()) {
                return JSONValues.HOTEL_STAR_STD_2;
            } else if (star == JSONValues.HOTEL_STAR_LST_5.enumValue()) {
                return JSONValues.HOTEL_STAR_LST_5;
            } else if (star == JSONValues.HOTEL_STAR_LST_4.enumValue()) {
                return JSONValues.HOTEL_STAR_LST_4;
            } else if (star == JSONValues.HOTEL_STAR_LST_3.enumValue()) {
                return JSONValues.HOTEL_STAR_LST_3;
            } else {
                throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                    LocalMessages.invalid_parameter_with_value, JSONKeys.STAR, star));
            }
        }

        public static ResortStar getResortStar(int star) throws InvalidTravelReqirementException {
            if (star == JSONValues.RESORT_STAR_STD_5A.enumValue()) {
                return JSONValues.RESORT_STAR_STD_5A;
            } else if (star == JSONValues.RESORT_STAR_STD_4A.enumValue()) {
                return JSONValues.RESORT_STAR_STD_4A;
            } else if (star == JSONValues.RESORT_STAR_STD_3A.enumValue()) {
                return JSONValues.RESORT_STAR_STD_3A;
            } else if (star == JSONValues.RESORT_STAR_STD_2A.enumValue()) {
                return JSONValues.RESORT_STAR_STD_2A;
            } else {
                throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                    LocalMessages.invalid_parameter_with_value, JSONKeys.STAR, star));
            }
        }

        public static TrafficToolSeatClass getTrafficSeatClass(int seatClass) throws InvalidTravelReqirementException {
            if (seatClass == JSONValues.TRAFFIC_TOOL_SEAT_CLASS_1ST.enumValue()) {
                return JSONValues.TRAFFIC_TOOL_SEAT_CLASS_1ST;
            } else if (seatClass == JSONValues.TRAFFIC_TOOL_SEAT_CLASS_2ND.enumValue()) {
                return JSONValues.TRAFFIC_TOOL_SEAT_CLASS_2ND;
            } else if (seatClass == JSONValues.TRAFFIC_TOOL_SEAT_CLASS_3RD.enumValue()) {
                return JSONValues.TRAFFIC_TOOL_SEAT_CLASS_3RD;
            } else {
                throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                    LocalMessages.invalid_parameter_with_value, JSONKeys.TRAFFIC_TOOL_SEAT_CLASS, seatClass));
            }
        }

        public static TrafficToolType getTrafficToolType(int toolType) throws InvalidTravelReqirementException {
            if (toolType == JSONValues.TRAFFIC_TOOL_TYPE_FLIGHT.enumValue()) {
                return JSONValues.TRAFFIC_TOOL_TYPE_FLIGHT;
            } else if (toolType == JSONValues.TRAFFIC_TOOL_TYPE_TRAIN.enumValue()) {
                return JSONValues.TRAFFIC_TOOL_TYPE_TRAIN;
            } else {
                throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                    LocalMessages.invalid_parameter_with_value, JSONKeys.TRAFFIC_TOOL_TYPE, toolType));
            }
        }

        public static TravelTimeRange getTravelTimeRange(int start, int duration)
            throws InvalidTravelReqirementException {
            if (start == JSONValues.TIME_RANGE_00_06.realValue()
                && duration == JSONValues.TIME_RANGE_00_06.imaginaryValue()) {
                return JSONValues.TIME_RANGE_00_06;
            } else if (start == JSONValues.TIME_RANGE_06_12.realValue()
                && duration == JSONValues.TIME_RANGE_06_12.imaginaryValue()) {
                return JSONValues.TIME_RANGE_06_12;
            } else if (start == JSONValues.TIME_RANGE_12_18.realValue()
                && duration == JSONValues.TIME_RANGE_12_18.imaginaryValue()) {
                return JSONValues.TIME_RANGE_12_18;
            } else if (start == JSONValues.TIME_RANGE_18_24.realValue()
                && duration == JSONValues.TIME_RANGE_18_24.imaginaryValue()) {
                return JSONValues.TIME_RANGE_18_24;
            } else {
                throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                    LocalMessages.invalid_parameter_with_value, JSONKeys.TIME_RANGE_START + ":"
                        + JSONKeys.TIME_RANGE_OFFSET, start + ":" + duration));
            }
        }

        public static ContinentID getContinentID(int continentID) throws InvalidTravelReqirementException {
            if (continentID == JSONValues.CONTINENT_ID_ASIA.enumValue()) {
                return JSONValues.CONTINENT_ID_ASIA;
            } else if (continentID == JSONValues.CONTINENT_ID_EUROPE.enumValue()) {
                return JSONValues.CONTINENT_ID_EUROPE;
            } else if (continentID == JSONValues.CONTINENT_ID_NORTH_AMERICA.enumValue()) {
                return JSONValues.CONTINENT_ID_NORTH_AMERICA;
            } else if (continentID == JSONValues.CONTINENT_ID_SOUTH_AMERICA.enumValue()) {
                return JSONValues.CONTINENT_ID_SOUTH_AMERICA;
            } else if (continentID == JSONValues.CONTINENT_ID_OCEANIA.enumValue()) {
                return JSONValues.CONTINENT_ID_OCEANIA;
            } else if (continentID == JSONValues.CONTINENT_ID_AFRICA.enumValue()) {
                return JSONValues.CONTINENT_ID_AFRICA;
            } else if (continentID == JSONValues.CONTINENT_ID_ANTARCTICA.enumValue()) {
                return JSONValues.CONTINENT_ID_ANTARCTICA;
            } else {
                throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                    LocalMessages.invalid_parameter_with_value, JSONKeys.UUID, continentID));
            }
        }
    }
}
