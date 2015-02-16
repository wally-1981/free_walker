package com.free.walker.service.itinerary.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import org.elasticsearch.search.sort.SortOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.primitive.QueryTemplate;
import com.free.walker.service.itinerary.primitive.SortType;

public class SearchCriteriaTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testValidateWithInvalidTerm() {
        JsonObjectBuilder jsBuilder1 = Json.createObjectBuilder();
        assertFalse(SearchCriteria.validate(jsBuilder1.build()));

        JsonObjectBuilder jsBuilder2 = Json.createObjectBuilder();
        jsBuilder2.add(Introspection.JSONKeys.TERM, "");
        assertFalse(SearchCriteria.validate(jsBuilder2.build()));

        JsonObjectBuilder jsBuilder3 = Json.createObjectBuilder();
        jsBuilder3.add(Introspection.JSONKeys.TERM, " ");
        assertFalse(SearchCriteria.validate(jsBuilder3.build()));

        JsonObjectBuilder jsBuilder4 = Json.createObjectBuilder();
        jsBuilder4.add(Introspection.JSONKeys.TERM, "\t");
        assertFalse(SearchCriteria.validate(jsBuilder4.build()));
    }

    @Test
    public void testValidateWithInvalidTemplate() {
        JsonObjectBuilder jsBuilder1 = Json.createObjectBuilder();
        jsBuilder1.add(Introspection.JSONKeys.TERM, "find me");
        assertFalse(SearchCriteria.validate(jsBuilder1.build()));

        JsonObjectBuilder jsBuilder2 = Json.createObjectBuilder();
        jsBuilder2.add(Introspection.JSONKeys.TERM, "find me");
        jsBuilder2.add(Introspection.JSONKeys.TEMPLATE, "");
        assertFalse(SearchCriteria.validate(jsBuilder2.build()));

        JsonObjectBuilder jsBuilder3 = Json.createObjectBuilder();
        jsBuilder3.add(Introspection.JSONKeys.TERM, "find me");
        jsBuilder3.add(Introspection.JSONKeys.TEMPLATE, " ");
        assertFalse(SearchCriteria.validate(jsBuilder3.build()));

        JsonObjectBuilder jsBuilder4 = Json.createObjectBuilder();
        jsBuilder4.add(Introspection.JSONKeys.TERM, "find me");
        jsBuilder4.add(Introspection.JSONKeys.TEMPLATE, "\t");
        assertFalse(SearchCriteria.validate(jsBuilder4.build()));

        JsonObjectBuilder jsBuilder5 = Json.createObjectBuilder();
        jsBuilder5.add(Introspection.JSONKeys.TERM, "find me");
        jsBuilder5.add(Introspection.JSONKeys.TEMPLATE, -1);
        assertFalse(SearchCriteria.validate(jsBuilder5.build()));

        JsonObjectBuilder jsBuilder6 = Json.createObjectBuilder();
        jsBuilder6.add(Introspection.JSONKeys.TERM, "find me");
        jsBuilder6.add(Introspection.JSONKeys.TEMPLATE, 0);
        assertFalse(SearchCriteria.validate(jsBuilder6.build()));

        JsonObjectBuilder jsBuilder7 = Json.createObjectBuilder();
        jsBuilder7.add(Introspection.JSONKeys.TERM, "find me");
        jsBuilder7.add(Introspection.JSONKeys.TEMPLATE, 6);
        assertFalse(SearchCriteria.validate(jsBuilder7.build()));

        JsonObjectBuilder jsBuilder8 = Json.createObjectBuilder();
        jsBuilder8.add(Introspection.JSONKeys.TERM, "find me");
        jsBuilder8.add(Introspection.JSONKeys.TEMPLATE, "missing_template");
        assertFalse(SearchCriteria.validate(jsBuilder8.build()));
    }

    @Test
    public void testValidateWithInvalidSortType() {
        JsonObjectBuilder jsBuilder2 = Json.createObjectBuilder();
        jsBuilder2.add(Introspection.JSONKeys.TERM, "find me");
        jsBuilder2.add(Introspection.JSONKeys.TEMPLATE, "test_template");
        jsBuilder2.add(Introspection.JSONKeys.SORT_TYPE, "");
        assertFalse(SearchCriteria.validate(jsBuilder2.build()));

        JsonObjectBuilder jsBuilder3 = Json.createObjectBuilder();
        jsBuilder3.add(Introspection.JSONKeys.TERM, "find me");
        jsBuilder3.add(Introspection.JSONKeys.TEMPLATE, "test_template");
        jsBuilder3.add(Introspection.JSONKeys.SORT_TYPE, " ");
        assertFalse(SearchCriteria.validate(jsBuilder3.build()));

        JsonObjectBuilder jsBuilder4 = Json.createObjectBuilder();
        jsBuilder4.add(Introspection.JSONKeys.TERM, "find me");
        jsBuilder4.add(Introspection.JSONKeys.TEMPLATE, "test_template");
        jsBuilder4.add(Introspection.JSONKeys.SORT_TYPE, "\t");
        assertFalse(SearchCriteria.validate(jsBuilder4.build()));

        JsonObjectBuilder jsBuilder5 = Json.createObjectBuilder();
        jsBuilder5.add(Introspection.JSONKeys.TERM, "find me");
        jsBuilder5.add(Introspection.JSONKeys.TEMPLATE, "test_template");
        jsBuilder5.add(Introspection.JSONKeys.SORT_TYPE, -1);
        assertFalse(SearchCriteria.validate(jsBuilder5.build()));

        JsonObjectBuilder jsBuilder6 = Json.createObjectBuilder();
        jsBuilder6.add(Introspection.JSONKeys.TERM, "find me");
        jsBuilder6.add(Introspection.JSONKeys.TEMPLATE, "test_template");
        jsBuilder6.add(Introspection.JSONKeys.SORT_TYPE, 0);
        assertFalse(SearchCriteria.validate(jsBuilder6.build()));

        JsonObjectBuilder jsBuilder7 = Json.createObjectBuilder();
        jsBuilder7.add(Introspection.JSONKeys.TERM, "find me");
        jsBuilder7.add(Introspection.JSONKeys.TEMPLATE, "test_template");
        jsBuilder7.add(Introspection.JSONKeys.SORT_TYPE, 5);
        assertFalse(SearchCriteria.validate(jsBuilder7.build()));

        JsonObjectBuilder jsBuilder8 = Json.createObjectBuilder();
        jsBuilder8.add(Introspection.JSONKeys.TERM, "find me");
        jsBuilder8.add(Introspection.JSONKeys.TEMPLATE, "test_template");
        jsBuilder8.add(Introspection.JSONKeys.SORT_TYPE, "Long");
        assertFalse(SearchCriteria.validate(jsBuilder8.build()));
    }

    @Test
    public void testValidate() {
        JsonObjectBuilder jsBuilder1 = Json.createObjectBuilder();
        jsBuilder1.add(Introspection.JSONKeys.TERM, "find me");
        jsBuilder1.add(Introspection.JSONKeys.TEMPLATE, "test_template");
        jsBuilder1.add(Introspection.JSONKeys.SORT_TYPE, "long");
        assertTrue(SearchCriteria.validate(jsBuilder1.build()));

        JsonObjectBuilder jsBuilder2 = Json.createObjectBuilder();
        jsBuilder2.add(Introspection.JSONKeys.TERM, "find me");
        jsBuilder2.add(Introspection.JSONKeys.TEMPLATE, 2);
        jsBuilder2.add(Introspection.JSONKeys.SORT_TYPE, 2);
        assertTrue(SearchCriteria.validate(jsBuilder2.build()));
    }

    @Test
    public void testFromJson() {
        {
            JsonObjectBuilder jsBuilder1 = Json.createObjectBuilder();
            jsBuilder1.add(Introspection.JSONKeys.TERM, "find me");
            jsBuilder1.add(Introspection.JSONKeys.TEMPLATE, "test_template");
            jsBuilder1.add(Introspection.JSONKeys.PAGE_SIZE, 60);
            jsBuilder1.add(Introspection.JSONKeys.PAGE_NUM, 2);
            jsBuilder1.add(Introspection.JSONKeys.SORT_KEY, "sort by me");
            jsBuilder1.add(Introspection.JSONKeys.SORT_ORDER, "asc");
            jsBuilder1.add(Introspection.JSONKeys.SORT_TYPE, "long");
            SearchCriteria criteria = new SearchCriteria().fromJSON(jsBuilder1.build());
            assertEquals("find me", criteria.getSearchTerm());
            assertEquals(QueryTemplate.TEST_TEMPLACE, criteria.getTemplate());
            assertEquals(60, criteria.getPageSize());
            assertEquals(2, criteria.getPageNum());
            assertEquals("sort by me", criteria.getSortKey());
            assertEquals(SortOrder.ASC, criteria.getSortOrder());
            assertEquals(SortType.LONG, criteria.getSortType());
        }

        {
            JsonObjectBuilder jsBuilder1 = Json.createObjectBuilder();
            jsBuilder1.add(Introspection.JSONKeys.TERM, "find me");
            jsBuilder1.add(Introspection.JSONKeys.TEMPLATE, 1);
            jsBuilder1.add(Introspection.JSONKeys.PAGE_SIZE, 60);
            jsBuilder1.add(Introspection.JSONKeys.PAGE_NUM, 2);
            jsBuilder1.add(Introspection.JSONKeys.SORT_KEY, "sort by me");
            jsBuilder1.add(Introspection.JSONKeys.SORT_ORDER, 1);
            jsBuilder1.add(Introspection.JSONKeys.SORT_TYPE, 2);
            SearchCriteria criteria = new SearchCriteria().fromJSON(jsBuilder1.build());
            assertEquals("find me", criteria.getSearchTerm());
            assertEquals(QueryTemplate.TEST_TEMPLACE, criteria.getTemplate());
            assertEquals(60, criteria.getPageSize());
            assertEquals(2, criteria.getPageNum());
            assertEquals("sort by me", criteria.getSortKey());
            assertEquals(SortOrder.DESC, criteria.getSortOrder());
            assertEquals(SortType.INT, criteria.getSortType());
        }
    }

    @Test
    public void testToJson() {
        JsonObjectBuilder jsBuilder1 = Json.createObjectBuilder();
        jsBuilder1.add(Introspection.JSONKeys.TERM, "find me");
        jsBuilder1.add(Introspection.JSONKeys.TEMPLATE, 1);
        jsBuilder1.add(Introspection.JSONKeys.PAGE_SIZE, 60);
        jsBuilder1.add(Introspection.JSONKeys.PAGE_NUM, 2);
        jsBuilder1.add(Introspection.JSONKeys.SORT_KEY, "sort by me");
        jsBuilder1.add(Introspection.JSONKeys.SORT_ORDER, 1);
        jsBuilder1.add(Introspection.JSONKeys.SORT_TYPE, 2);
        SearchCriteria criteria = new SearchCriteria().fromJSON(jsBuilder1.build());
        thrown.expect(UnsupportedOperationException.class);
        criteria.toJSON();
    }
}
