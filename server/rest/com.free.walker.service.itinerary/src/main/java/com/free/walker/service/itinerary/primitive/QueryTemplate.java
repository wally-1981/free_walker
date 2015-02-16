package com.free.walker.service.itinerary.primitive;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.free.walker.service.itinerary.Enumable;

public class QueryTemplate implements Enumable {
    private static final Map<String, QueryTemplate> TEMPLATES_1 = new HashMap<String, QueryTemplate>();
    private static final Map<Integer, QueryTemplate> TEMPLATES_2 = new HashMap<Integer, QueryTemplate>();

    public static final QueryTemplate TEST_TEMPLACE = new QueryTemplate(1, "test_template");
    public static final QueryTemplate PRODUCT_DEPARTURE = new QueryTemplate(2, "product_departure");
    public static final QueryTemplate PRODUCT_DESTINATION = new QueryTemplate(3, "product_destination");
    public static final QueryTemplate PRODUCT_OWNER = new QueryTemplate(4, "product_owner");
    public static final QueryTemplate PROPOSAL_OWNER = new QueryTemplate(5, "proposal_owner");

    public static boolean isValid(String templateName) {
        return TEMPLATES_1.containsKey(templateName) && TEMPLATES_1.get(templateName) != null;
    }

    public static boolean isValid(int templateId) {
        return TEMPLATES_2.containsKey(templateId) && TEMPLATES_2.get(templateId) != null;
    }

    public static QueryTemplate getQueryTemplate(String templateName) {
        return TEMPLATES_1.get(templateName);
    }

    public static QueryTemplate getQueryTemplate(int templateId) {
        return TEMPLATES_2.get(templateId);
    }

    private int enumValue = 0;
    private String templateName;

    protected QueryTemplate(int enumValue, String templateName) {
        if (enumValue <= 0) {
            throw new IllegalArgumentException();
        }

        if (templateName == null) {
            throw new NullPointerException();
        }

        if (StringUtils.isEmpty(templateName)) {
            throw new IllegalArgumentException();
        }

        this.enumValue = enumValue;
        this.templateName = templateName;

        TEMPLATES_1.put(templateName, this);
        TEMPLATES_2.put(enumValue, this);
    }

    public int enumValue() {
        return enumValue;
    }

    public String nameValue() {
        return templateName;
    }
}
