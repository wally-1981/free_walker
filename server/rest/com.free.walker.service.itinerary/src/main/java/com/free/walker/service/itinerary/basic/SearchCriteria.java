package com.free.walker.service.itinerary.basic;

import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.apache.cxf.common.util.StringUtils;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.Serializable;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.primitive.QueryTemplate;
import com.free.walker.service.itinerary.primitive.SortType;

public class SearchCriteria implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(SearchCriteria.class);

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE_NUMBER = 0;

    private String searchTerm;
    private QueryTemplate template;
    private int pageNum;
    private int pageSize;
    private String sortKey;
    private SortOrder sortOrder;
    private SortType sortType;

    protected static boolean validate(JsonObject jsObject) {
        boolean valid = true;

        String term = jsObject.getString(Introspection.JSONKeys.TERM, null);
        valid &= !StringUtils.isEmpty(term);
        if (!valid) {
            LOG.warn(LocalMessages.getMessage(LocalMessages.invalid_term_invalid, term));
            return valid;
        }

        String templateName = jsObject.getString(Introspection.JSONKeys.TEMPLATE, "");
        int templateId = jsObject.getInt(Introspection.JSONKeys.TEMPLATE, 0);
        valid &= (QueryTemplate.isValid(templateId) || QueryTemplate.isValid(templateName));
        if (!valid) {
            LOG.warn(LocalMessages.getMessage(LocalMessages.invalid_template_invalid, templateName, templateId));
            return valid;
        }

        String sortType = jsObject.getString(Introspection.JSONKeys.SORT_TYPE, "");
        int sortTypeId = jsObject.getInt(Introspection.JSONKeys.SORT_TYPE, 0);
        valid &= (SortType.isValid(sortTypeId) || SortType.isValid(sortType));
        if (!valid) {
            LOG.warn(LocalMessages.getMessage(LocalMessages.invalid_sort_type_invalid, sortType, sortTypeId));
            return valid;
        }

        return valid;
    }

    public JsonObject toJSON() {
        throw new UnsupportedOperationException();
    }

    public SearchCriteria fromJSON(JsonObject jsObject) throws JsonException {
        if (!SearchCriteria.validate(jsObject)) {
            return null;
        }

        searchTerm = jsObject.getString(Introspection.JSONKeys.TERM);

        QueryTemplate t1 = QueryTemplate.getQueryTemplate(jsObject.getString(Introspection.JSONKeys.TEMPLATE, null));
        QueryTemplate t2 = QueryTemplate.getQueryTemplate(jsObject.getInt(Introspection.JSONKeys.TEMPLATE, 0));
        template = t1 == null ? t2 : t1;

        pageSize = jsObject.getInt(Introspection.JSONKeys.PAGE_SIZE, -1);
        if (pageSize <= 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        pageNum = jsObject.getInt(Introspection.JSONKeys.PAGE_NUM, -1);
        if (pageNum < 0) {
            pageNum = DEFAULT_PAGE_NUMBER;
        }

        sortKey = jsObject.getString(Introspection.JSONKeys.SORT_KEY, null);
        if (StringUtils.isEmpty(sortKey)) {
            sortKey = "_score";
        }

        int sOInt = jsObject.getInt(Introspection.JSONKeys.SORT_ORDER, SortOrder.DESC.ordinal());
        String sOStr = jsObject.getString(Introspection.JSONKeys.SORT_ORDER, SortOrder.DESC.toString());
        if (SortOrder.ASC.ordinal() == sOInt || SortOrder.ASC.toString().equals(sOStr)) {
            sortOrder = SortOrder.ASC;
        } else {
            sortOrder = SortOrder.DESC;
        }

        SortType sT1 = SortType.getSortType(jsObject.getString(Introspection.JSONKeys.SORT_TYPE, null));
        SortType sT2 = SortType.getSortType(jsObject.getInt(Introspection.JSONKeys.SORT_TYPE, 0));
        sortType = sT1 == null  ? sT2 : sT1;

        return this;
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.NULL;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public QueryTemplate getTemplate() {
        return template;
    }

    public int getPageNum() {
        return pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public String getSortKey() {
        return sortKey;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public SortType getSortType() {
        return sortType;
    }
}
