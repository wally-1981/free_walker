package com.free.walker.service.itinerary.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import javax.json.JsonObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.DependencyException;
import com.free.walker.service.itinerary.infra.PlatformInitializer;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.primitive.QueryTemplate;
import com.free.walker.service.itinerary.res.LixingResourceProvider;
import com.free.walker.service.itinerary.res.ResourceProvider;
import com.ibm.icu.util.Calendar;

public class TravelResourceDAOImplTest {
    protected TravelResourceDAO travelResourceDAO;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() throws DatabaseAccessException {
        PlatformInitializer.init();

        travelResourceDAO = DAOFactory.getTravelResourceDAO();
    }

    @Test
    public void testGetResourcesProvider() throws DependencyException {
        Calendar since = Calendar.getInstance();
        since.set(Calendar.YEAR, 2014);
        since.set(Calendar.MONTH, Calendar.JANUARY);
        since.set(Calendar.DAY_OF_MONTH, 1);
        since.set(Calendar.HOUR_OF_DAY, 0);
        since.set(Calendar.MINUTE, 0);
        since.set(Calendar.SECOND, 0);
        since.set(Calendar.MILLISECOND, 0);

        ResourceProvider rp = travelResourceDAO.getResourceProvider("0000001");
        assertNotNull(rp);
        assertEquals(LixingResourceProvider.class, rp.getClass());
        assertEquals("0000001", rp.getProviderId());
        assertEquals("lixing.test", rp.getProviderName());
        assertEquals(since.getTimeInMillis(), rp.getProviderSince().getTimeInMillis());
    }

    @Test
    public void testGetResourcesProviderWithWrongProdiverId() throws DependencyException {
        ResourceProvider rp = travelResourceDAO.getResourceProvider("43523453425");
        assertNull(rp);
    }

    @Test
    public void testSynchrinizeResources() throws DependencyException {
        Calendar since = Calendar.getInstance();
        since.set(Calendar.YEAR, 2014);
        since.set(Calendar.MONTH, Calendar.JANUARY);
        since.set(Calendar.DAY_OF_MONTH, 1);
        since.set(Calendar.HOUR_OF_DAY, 0);
        since.set(Calendar.MINUTE, 0);
        since.set(Calendar.SECOND, 0);
        since.set(Calendar.MILLISECOND, 0);
        JsonObject syncResult = travelResourceDAO.synchrinizeResource("0000001", false, since, true);
        assertNotNull(syncResult);
        assertNotNull(syncResult.getInt(Introspection.JSONKeys.SYNC_ADD_NUMBER));
        assertNotNull(syncResult.getInt(Introspection.JSONKeys.SYNC_UPDATE_NUMBER));
        assertNotNull(syncResult.getInt(Introspection.JSONKeys.SYNC_DELETE_NUMBER));
        int add = syncResult.getInt(Introspection.JSONKeys.SYNC_ADD_NUMBER);
        int update = syncResult.getInt(Introspection.JSONKeys.SYNC_UPDATE_NUMBER);
        int delete = syncResult.getInt(Introspection.JSONKeys.SYNC_DELETE_NUMBER);
        assertEquals(100, add + update + delete);
    }

    @Test
    public void testSynchrinizeResourcesExhausted() throws DependencyException {
        Calendar since = Calendar.getInstance();
        since.set(Calendar.YEAR, 2014);
        since.set(Calendar.MONTH, Calendar.JANUARY);
        since.set(Calendar.DAY_OF_MONTH, 1);
        since.set(Calendar.HOUR_OF_DAY, 0);
        since.set(Calendar.MINUTE, 0);
        since.set(Calendar.SECOND, 0);
        since.set(Calendar.MILLISECOND, 0);
        JsonObject syncResult = travelResourceDAO.synchrinizeResource("0000001", true, since, false);
        assertNotNull(syncResult);
        assertNotNull(syncResult.getInt(Introspection.JSONKeys.SYNC_ADD_NUMBER));
        assertNotNull(syncResult.getInt(Introspection.JSONKeys.SYNC_UPDATE_NUMBER));
        assertNotNull(syncResult.getInt(Introspection.JSONKeys.SYNC_DELETE_NUMBER));
        int add = syncResult.getInt(Introspection.JSONKeys.SYNC_ADD_NUMBER);
        int update = syncResult.getInt(Introspection.JSONKeys.SYNC_UPDATE_NUMBER);
        int delete = syncResult.getInt(Introspection.JSONKeys.SYNC_DELETE_NUMBER);
        assertEquals(900, add + update + delete);
    }

    @Test
    public void testSynchrinizeResourcesWithMissingProvider() throws DependencyException {
        Calendar since = Calendar.getInstance();
        since.set(Calendar.YEAR, 2014);
        since.set(Calendar.MONTH, Calendar.JANUARY);
        since.set(Calendar.DAY_OF_MONTH, 1);
        since.set(Calendar.HOUR_OF_DAY, 0);
        since.set(Calendar.MINUTE, 0);
        since.set(Calendar.SECOND, 0);
        since.set(Calendar.MILLISECOND, 0);

        thrown.expect(DependencyException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.not_found_resource_provider, "0000000"));
        travelResourceDAO.synchrinizeResource("0000000", true, since, true);
    }

    @Test
    public void testSearchResourceWithNullTemplateName() throws DatabaseAccessException {
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("test_template_key", "test_template_value");
        thrown.expect(NullPointerException.class);
        travelResourceDAO.searchResource(null, templateParams);
    }

    @Test
    public void testSearchResourceWithNullTemplateParams() throws DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelResourceDAO.searchResource(QueryTemplate.TEST_TEMPLATE, null);
    }

    @Test
    public void testSearchResource() throws DatabaseAccessException, InterruptedException {
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put(DAOConstants.elasticsearch_from, String.valueOf(0 * 150));
        templateParams.put(DAOConstants.elasticsearch_size, String.valueOf(150));
        JsonObject resources = travelResourceDAO.searchResource(QueryTemplate.RESOURCE, templateParams);
        assertNotNull(resources);
        assertTrue(resources.containsKey(Introspection.JSONKeys.TOTAL_HITS_NUMBER));
        assertTrue(resources.containsKey(Introspection.JSONKeys.HITS));
        assertTrue(resources.getJsonArray(Introspection.JSONKeys.HITS).size() == 150);
    }

    @After
    public void after() throws DatabaseAccessException {
        ;
    }
}
