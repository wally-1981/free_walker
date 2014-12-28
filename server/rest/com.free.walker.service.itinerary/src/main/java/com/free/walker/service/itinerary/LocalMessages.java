package com.free.walker.service.itinerary;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.ibm.icu.text.MessageFormat;

public class LocalMessages {
    private static final String BUNDLE_NAME = "com.free.walker.service.itinerary.local_messages";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    public static String getMessage(String key) {
        if (RESOURCE_BUNDLE == null) {
            throw new IllegalStateException();
        }

        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    public static String getMessage(String key, String arg) {
        return getMessage(key, arg, null);
    }

    public static String getMessage(String key, String arg1, String arg2) {
        return getMessage(key, arg1, arg2, null);
    }

    public static String getMessage(String key, String arg1, String arg2, String arg3) {
        return getMessage(key, arg1, arg2, arg3, null);
    }

    public static String getMessage(String key, Object... arguments) {
        if (RESOURCE_BUNDLE == null) {
            throw new IllegalStateException();
        }

        try {
            String message = RESOURCE_BUNDLE.getString(key);
            return MessageFormat.format(message, arguments);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    private LocalMessages() {
        ;
    }

    public static final String server_started = "server_started";
    public static final String localhost_ip_founed = "localhost_ip_founed";
    public static final String fallback_config_path = "fallback_config_path";
    public static final String log_file_path_determined = "log_file_path_determined";
    public static final String read_configuration_file_failed = "read_configuration_file_failed";
    public static final String eval_configuration_value_failed = "eval_configuration_value_failed";

    public static final String illegal_uuid = "illegal_uuid";
    public static final String illegal_compact_uuid = "illegal_compact_uuid";
    public static final String illegal_hotel_nights = "illegal_hotel_nights";
    public static final String empty_itinerary_location = "empty_itinerary_location";
    public static final String illegal_itinerary_index = "illegal_itinerary_index";
    public static final String invalid_bidding_item_range = "invalid_bidding_item_range";
    public static final String conflict_bidding_item_range = "conflict_bidding_item_range";

    public static final String missing_travel_requirement = "missing_travel_requirement";
    public static final String existed_travel_requirement = "existed_travel_requirement";
    public static final String missing_travel_proposal = "missing_travel_proposal";
    public static final String existed_travel_proposal = "existed_travel_proposal";
    public static final String missing_travel_itinerary = "missing_travel_itinerary";
    public static final String existed_travel_itinerary = "existed_travel_itinerary";
    public static final String missing_travel_proposal_bidding = "missing_travel_proposal_bidding";
    public static final String missing_location = "missing_location";

    public static final String illegal_location_association = "illegal_location_association";
    public static final String illegal_port_association = "illegal_port_association";
    public static final String illegal_add_agency_operation = "illegal_add_agency_operation";
    public static final String illegal_add_proposal_as_requirement = "illegal_add_proposal_as_requirement";
    public static final String illegal_add_itinerary_as_requirement = "illegal_add_itinerary_as_requirement";
    public static final String illegal_update_travel_requirement_operation = "illegal_update_travel_requirement_operation";
    public static final String illegal_delete_travel_requirement_operation = "illegal_delete_travel_requirement_operation";
    public static final String illegal_remove_product_item_operation = "illegal_remove_product_item_operation";
    public static final String illegal_submit_proposal_operation = "illegal_submit_proposal_operation";

    public static final String requirement_not_found = "requirement_not_found";
    public static final String itinerary_not_found = "itinerary_not_found";
    public static final String proposal_not_found = "proposal_not_found";

    public static final String missing_travel_product = "missing_travel_product";
    public static final String existed_travel_product = "existed_travel_product";
    public static final String existed_product_bidding = "existed_product_bidding";

    public static final String introspection_failure = "introspection_failure";
    public static final String dao_init_failure = "dao_init_failure";
    public static final String dao_operation_failure = "dao_operation_failure";
    public static final String invalid_db_host_address = "invalid_db_host_address";

    public static final String invalid_parameter_with_value = "invalid_parameter_with_value";

    public static final String load_country_failed = "load_country_failed";
    public static final String load_province_failed = "load_province_failed";
    public static final String load_city_failed = "load_city_failed";
    public static final String load_tag_failed = "load_tag_failed";
    public static final String load_country_success = "load_country_success";
    public static final String load_province_success = "load_province_success";
    public static final String load_city_success = "load_city_success";
    public static final String load_tag_success = "load_tag_success";

    public static final String agency_election_start = "agency_election_start";
    public static final String agency_election_end = "agency_election_end";
    public static final String agency_election_success = "agency_election_success";
    public static final String agency_election_map_failed = "agency_election_map_failed";
    public static final String agency_election_reduce_failed = "agency_election_reduce_failed";
    public static final String agency_election_task_cancelled = "agency_election_task_cancelled";

    public static final String submit_proposal_failed = "submit_proposal_failed";
    public static final String submit_proposal_success = "submit_proposal_success";

    public static final String schedule_task_scheduled = "schedule_task_scheduled";
    public static final String schedule_task_scheduled_run_once = "schedule_task_scheduled_run_once";
    public static final String schedule_task_success = "schedule_task_success";
    public static final String schedule_task_failed = "schedule_task_failed";

    public static final String test_message = "test_message";
    public static final String test_message_with_1_arg = "test_message_with_1_arg";
    public static final String test_message_with_2_args = "test_message_with_2_args";
    public static final String test_message_with_3_args = "test_message_with_3_args";
    public static final String test_message_with_multiple_args = "test_message_with_multiple_args";
}
