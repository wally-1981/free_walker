package com.free.walker.service.itinerary.primitive;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.free.walker.service.itinerary.Enumable;

public class QueryTemplate implements Enumable {
    private static final Map<String, QueryTemplate> TEMPLATES_NAME_IDX = new HashMap<String, QueryTemplate>();
    private static final Map<Integer, QueryTemplate> TEMPLATES_ID_IDX = new HashMap<Integer, QueryTemplate>();

    public static final QueryTemplate PRODUCT_TEMPLATE = new QueryTemplate(1, "product");
    public static final QueryTemplate PRODUCT_DEPARTURE = new QueryTemplate(2, "product_departure");
    public static final QueryTemplate PRODUCT_DESTINATION = new QueryTemplate(3, "product_destination");
    public static final QueryTemplate PRODUCT_OWNER = new QueryTemplate(4, "product_owner");
    public static final QueryTemplate PROPOSAL_OWNER = new QueryTemplate(5, "proposal_owner");
    public static final QueryTemplate RESOURCE = new QueryTemplate(6, "resource");

    public static boolean isValid(String templateName) {
        return TEMPLATES_NAME_IDX.containsKey(templateName) && TEMPLATES_NAME_IDX.get(templateName) != null;
    }

    public static boolean isValid(int templateId) {
        return TEMPLATES_ID_IDX.containsKey(templateId) && TEMPLATES_ID_IDX.get(templateId) != null;
    }

    public static QueryTemplate getQueryTemplate(String templateName) {
        return TEMPLATES_NAME_IDX.get(templateName);
    }

    public static QueryTemplate getQueryTemplate(int templateId) {
        return TEMPLATES_ID_IDX.get(templateId);
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

        TEMPLATES_NAME_IDX.put(templateName, this);
        TEMPLATES_ID_IDX.put(enumValue, this);
    }

    public int enumValue() {
        return enumValue;
    }

    public String nameValue() {
        return templateName;
    }
}
