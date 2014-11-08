package com.free.walker.service.itinerary.basic;


public class Introspection {
    public static class JSONKeys {
        /*
         * JSONObject Key
         */
        public static final String TYPE = "type";
        public static final String SUB_TYPE = "sub_type";
        public static final String PROPOSAL = "proposal";
        public static final String ITINERARY = "itinerary";
        public static final String REQUIREMENT = "requirement";

        public static final String REQUIREMENTS = "requirements";

        public static final String CITY = "city";
        public static final String COUNTRY = "country";
        public static final String FLIGHT = "flight";
        public static final String HOTEL = "hotel";
        public static final String RESORT = "resort";
        public static final String TRAIN = "train";

        /*
         * JSONArray Keys
         */
        public static final String DATETIME_SELECTIONS = "datetime_selections";
        public static final String DATETIME_RANGE_SELECTIONS = "datetime_range_selections";

        /*
         * Primitive Keys
         */
        public static final String UUID = "uuid";
        public static final String NAME = "name";
        public static final String STAR = "star";
        public static final String NIGHT = "night";
        public static final String DESTINATION = "destination";
        public static final String DEPARTURE = "departure";
        public static final String TIME_RANGE_START = "time_range_start";
        public static final String TIME_RANGE_OFFSET = "time_range_offset";
        public static final String TRAFFIC_TOOL_TYPE = "traffice_tool_type";
        public static final String TRAFFIC_TOOL_SEAT_CLASS = "traffice_tool_seat_class";

        public static final String ERROR_CODE = "error_code";
        public static final String ERROR_CNTX = "error_cntx";
        public static final String ERROR_DESC = "error_desc";
        public static final String ERROR_ACTN = "error_actn";
    }

    public static class JSONValues {
        public static final HotelStar STD_5 = new HotelStar(50);
        public static final HotelStar STD_4 = new HotelStar(40);
        public static final HotelStar STD_3 = new HotelStar(30);
        public static final HotelStar STD_2 = new HotelStar(20);
        public static final HotelStar LST_5 = new HotelStar(5);
        public static final HotelStar LST_4 = new HotelStar(4);
        public static final HotelStar LST_3 = new HotelStar(3);

        public static final ResortStar STD_5A = new ResortStar(5);
        public static final ResortStar STD_4A = new ResortStar(4);
        public static final ResortStar STD_3A = new ResortStar(3);
        public static final ResortStar STD_2A = new ResortStar(2);

        public static final TrafficToolSeatClass CLASS_1ST = new TrafficToolSeatClass(30);
        public static final TrafficToolSeatClass CLASS_2ND = new TrafficToolSeatClass(20);
        public static final TrafficToolSeatClass CLASS_3RD = new TrafficToolSeatClass(10);

        public static final TrafficToolType FLIGHT = new TrafficToolType(60);
        public static final TrafficToolType TRAIN = new TrafficToolType(50);

        public static final TravelTimeRange RANGE_00_06 = new TravelTimeRange(0, 6);
        public static final TravelTimeRange RANGE_06_12 = new TravelTimeRange(6, 6);
        public static final TravelTimeRange RANGE_12_18 = new TravelTimeRange(12, 6);
        public static final TravelTimeRange RANGE_18_23 = new TravelTimeRange(18, 6);
    }
}
