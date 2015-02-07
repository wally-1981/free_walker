package com.free.walker.service.itinerary.primitive;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.free.walker.service.itinerary.Enumable;

public class SortType implements Enumable {
    private static final Map<String, SortType> TYPES_1 = new HashMap<String, SortType>();
    private static final Map<Integer, SortType> TYPES_2 = new HashMap<Integer, SortType>();

    public static final SortType LONG = new SortType(1, Long.class.getSimpleName().toLowerCase());
    public static final SortType INT = new SortType(2, Integer.class.getSimpleName().toLowerCase());
    public static final SortType FLOAT = new SortType(3, Float.class.getSimpleName().toLowerCase());
    public static final SortType DOUBLE = new SortType(4, Double.class.getSimpleName().toLowerCase());

    public static boolean isValid(String templateName) {
        return TYPES_1.containsKey(templateName) && TYPES_1.get(templateName) != null;
    }

    public static boolean isValid(int templateId) {
        return TYPES_2.containsKey(templateId) && TYPES_2.get(templateId) != null;
    }

    public static SortType getSortType(String templateName) {
        return TYPES_1.get(templateName);
    }

    public static SortType getSortType(int templateId) {
        return TYPES_2.get(templateId);
    }

    private int enumValue = 0;
    private String sortType;

    protected SortType(int enumValue, String templateName) {
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
        this.sortType = templateName;

        TYPES_1.put(templateName, this);
        TYPES_2.put(enumValue, this);
    }

    public int enumValue() {
        return enumValue;
    }

    public String nameValue() {
        return sortType;
    }
}
