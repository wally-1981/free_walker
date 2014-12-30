package com.free.walker.service.itinerary.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.free.walker.service.itinerary.basic.Agency;
import com.free.walker.service.itinerary.basic.City;
import com.free.walker.service.itinerary.basic.Country;
import com.free.walker.service.itinerary.basic.Province;
import com.free.walker.service.itinerary.basic.Tag;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.infra.PlatformInitializer;

public class TravelBasicDAOImplTest {
    protected TravelBasicDAO travelBasicDAO;

    private String a1;
    private String a2;
    private String a3;
    private String a4;
    private String a5;
    private String a6;
    private String a7;
    private String a8;
    private String a9;
    private String a10;

    private List<Agency> candidates;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() throws DatabaseAccessException {
        PlatformInitializer.init();

        candidates = new ArrayList<Agency>();

        travelBasicDAO = DAOFactory.getTravelBasicDAO();

        {
            Agency agency = new Agency();
            a1 = UUID.randomUUID().toString();
            agency.setUuid(a1);
            agency.setName("中青旅（宜昌->湖北）");
            agency.setDeparture("cda48bcd9ab64669994013897321a3fb");
            agency.setDestination("03161e050c2448378eb863bfcbe744f3");
            travelBasicDAO.addAgency(agency);
            candidates.add(agency);
        }

        {
            Agency agency = new Agency();
            a2 = UUID.randomUUID().toString();
            agency.setUuid(a2);
            agency.setName("国旅（武汉->湖北）");
            agency.setDeparture("79fd8642a11d4811887dec4268097a82");
            agency.setDestination("03161e050c2448378eb863bfcbe744f3");
            travelBasicDAO.addAgency(agency);
            candidates.add(agency);
        }

        {
            Agency agency = new Agency();
            a3 = UUID.randomUUID().toString();
            agency.setUuid(a3);
            agency.setName("中青旅（长沙->湖南）");
            agency.setDeparture("675b8393ac04418786ccb1d1618f33f1");
            agency.setDestination("92d55e093025479db3f64b6aa38de051");
            travelBasicDAO.addAgency(agency);
            candidates.add(agency);
        }

        {
            Agency agency = new Agency();
            a4 = UUID.randomUUID().toString();
            agency.setUuid(a4);
            agency.setName("国旅（北京->中国）");
            agency.setDeparture("689ddfcdeffd4937b56707c4c8907378");
            agency.setDestination("af70a55ceb4c415c837588081716f8b8");
            travelBasicDAO.addAgency(agency);
            candidates.add(agency);
        }

        {
            Agency agency = new Agency();
            a5 = UUID.randomUUID().toString();
            agency.setUuid(a5);
            agency.setName("国旅（上海->亚洲）");
            agency.setDeparture("301cdd76923047d28eb28e85932d9f53");
            agency.setDestination("1");
            travelBasicDAO.addAgency(agency);
            candidates.add(agency);
        }

        {
            Agency agency = new Agency();
            a6 = UUID.randomUUID().toString();
            agency.setUuid(a6);
            agency.setName("众信（伦敦->伦敦）");
            agency.setDeparture("54053f1a057a4ded88c854c5e56c63f4");
            agency.setDestination("54053f1a057a4ded88c854c5e56c63f4");
            travelBasicDAO.addAgency(agency);
            candidates.add(agency);
        }

        {
            Agency agency = new Agency();
            a7 = UUID.randomUUID().toString();
            agency.setUuid(a7);
            agency.setName("众信（巴塞罗纳->西班牙）");
            agency.setDeparture("84844276303647dd90e0f095cfa98da5");
            agency.setDestination("fe80acb2816e45f0a98819caa587c6fc");
            travelBasicDAO.addAgency(agency);
            candidates.add(agency);
        }

        {
            Agency agency = new Agency();
            a8 = UUID.randomUUID().toString();
            agency.setUuid(a8);
            agency.setName("众信（马德里->欧洲）");
            agency.setDeparture("990d650cfdc84ad8842f59d0f27c1f65");
            agency.setDestination("2");
            travelBasicDAO.addAgency(agency);
            candidates.add(agency);
        }

        {
            Agency agency = new Agency();
            a9 = UUID.randomUUID().toString();
            agency.setUuid(a9);
            agency.setName("众信（纽约->美国）");
            agency.setDeparture("b6890ab6c23d405fa1c74b9dd5dd2e0c");
            agency.setDestination("cc0968e70fe34cc99f5b3a6898a04506");
            travelBasicDAO.addAgency(agency);
            candidates.add(agency);
        }

        {
            Agency agency = new Agency();
            a10 = UUID.randomUUID().toString();
            agency.setUuid(a10);
            agency.setName("Ali（北京->北京）");
            agency.setDeparture("689ddfcdeffd4937b56707c4c8907378");
            agency.setDestination("689ddfcdeffd4937b56707c4c8907378");
            travelBasicDAO.addAgency(agency);
            candidates.add(agency);
        }

        {
            /*
             * Enshi
             */
            travelBasicDAO.associateLocation("e98c3585f7474eca9b66b58f57362fb7", "79fd8642a11d4811887dec4268097a82"); // Wuhan
            travelBasicDAO.associateLocation("e98c3585f7474eca9b66b58f57362fb7", "cda48bcd9ab64669994013897321a3fb"); // Yichang
            travelBasicDAO.associateLocation("e98c3585f7474eca9b66b58f57362fb7", "03161e050c2448378eb863bfcbe744f3"); // Hubei

            /*
             * Beijing
             */
            ;

            /*
             * Wuhan
             */
            travelBasicDAO.associateLocation("79fd8642a11d4811887dec4268097a82", "03161e050c2448378eb863bfcbe744f3"); // Hubei

            /*
             * Hubei
             */
            travelBasicDAO.associateLocation("03161e050c2448378eb863bfcbe744f3", "79fd8642a11d4811887dec4268097a82"); // Wuhan

            /*
             * Hunan
             */
            travelBasicDAO.associateLocation("92d55e093025479db3f64b6aa38de051", "03161e050c2448378eb863bfcbe744f3"); // Hubei
            travelBasicDAO.associateLocation("92d55e093025479db3f64b6aa38de051", "675b8393ac04418786ccb1d1618f33f1"); // Changsha
            travelBasicDAO.associateLocation("92d55e093025479db3f64b6aa38de051", "af70a55ceb4c415c837588081716f8b8"); // China

            /*
             * London
             */
            travelBasicDAO.associatePortLocation("54053f1a057a4ded88c854c5e56c63f4", "689ddfcdeffd4937b56707c4c8907378"); // Beijing

            /*
             * Hawaii
             */
            travelBasicDAO.associateLocation("8f18986005ee469b9f1dcdf5eb897804", "fbb3821586c04c1abca1edc25ddde4fc"); // Arizona
            travelBasicDAO.associateLocation("8f18986005ee469b9f1dcdf5eb897804", "cc0968e70fe34cc99f5b3a6898a04506"); // USA

            /*
             * Spain
             */
            travelBasicDAO.associateLocation("fe80acb2816e45f0a98819caa587c6fc", "b164126cb75e4d17a9f63e3f05172e73"); // Britain
            travelBasicDAO.associateLocation("fe80acb2816e45f0a98819caa587c6fc", "2"); // Eropean

            /*
             * AS
             */
            travelBasicDAO.associateLocation("1", "af70a55ceb4c415c837588081716f8b8"); // China
            travelBasicDAO.associateLocation("1", "ef72692dbd2b49a9bc73b74b5ce0b0cb"); // Korea

            /*
             * EU
             */
            travelBasicDAO.associateLocation("2", "b164126cb75e4d17a9f63e3f05172e73"); // China
            travelBasicDAO.associateLocation("2", "fe80acb2816e45f0a98819caa587c6fc"); // Spain

            /*
             * NA
             */
            travelBasicDAO.associateLocation("3", "cc0968e70fe34cc99f5b3a6898a04506"); // USA
        }
    }

    @Test
    public void testGetAllCountries() throws DatabaseAccessException {
        List<Country> allCountries = travelBasicDAO.getAllCountries();
        assertNotNull(allCountries);
        assertFalse(allCountries.isEmpty());

        Country first = allCountries.get(0);
        assertNotNull(first);
        assertNotNull(first.getUuid());
        assertNotNull(first.getName());
        assertNotNull(first.getChineseName());
        assertNotNull(first.getPinyinName());

        Country last = allCountries.get(allCountries.size() - 1);
        assertNotNull(last);
        assertNotNull(last.getUuid());
        assertNotNull(last.getName());
        assertNotNull(last.getChineseName());
        assertNotNull(last.getPinyinName());
    }

    @Test
    public void testGetAllProvinces() throws DatabaseAccessException {
        List<Province> allProvinces = travelBasicDAO.getAllProvinces();
        assertNotNull(allProvinces);
        assertFalse(allProvinces.isEmpty());

        Province first = allProvinces.get(0);
        assertNotNull(first);
        assertNotNull(first.getUuid());
        assertNotNull(first.getName());
        assertNotNull(first.getChineseName());
        assertNotNull(first.getPinyinName());

        Province last = allProvinces.get(allProvinces.size() - 1);
        assertNotNull(last);
        assertNotNull(last.getUuid());
        assertNotNull(last.getName());
        assertNotNull(last.getChineseName());
        assertNotNull(last.getPinyinName());
    }

    @Test
    public void testGetAllCities() throws DatabaseAccessException {
        List<City> allCities = travelBasicDAO.getAllCities();
        assertNotNull(allCities);
        assertFalse(allCities.isEmpty());

        City first = allCities.get(0);
        assertNotNull(first);
        assertNotNull(first.getUuid());
        assertNotNull(first.getName());
        assertNotNull(first.getChineseName());
        assertNotNull(first.getPinyinName());

        City last = allCities.get(allCities.size() - 1);
        assertNotNull(last);
        assertNotNull(last.getUuid());
        assertNotNull(last.getName());
        assertNotNull(last.getChineseName());
        assertNotNull(last.getPinyinName());
    }

    @Test
    public void testHasLocationByTerm() throws DatabaseAccessException {
        assertTrue(travelBasicDAO.hasLocationByTerm("Wuhan"));
        assertTrue(travelBasicDAO.hasLocationByTerm("wuhan"));
        assertTrue(travelBasicDAO.hasLocationByTerm("武汉"));

        assertTrue(travelBasicDAO.hasLocationByTerm("Hubei"));
        assertTrue(travelBasicDAO.hasLocationByTerm("hubei"));
        assertTrue(travelBasicDAO.hasLocationByTerm("湖北"));

        assertTrue(travelBasicDAO.hasLocationByTerm("China"));
        assertTrue(travelBasicDAO.hasLocationByTerm("zhongguo"));
        assertTrue(travelBasicDAO.hasLocationByTerm("中国"));

        assertTrue(travelBasicDAO.hasLocationByTerm("London"));
        assertTrue(travelBasicDAO.hasLocationByTerm("lundun"));
        assertTrue(travelBasicDAO.hasLocationByTerm("伦敦"));

        assertTrue(travelBasicDAO.hasLocationByTerm("Hawaii"));
        assertTrue(travelBasicDAO.hasLocationByTerm("xiaweiyi"));
        assertTrue(travelBasicDAO.hasLocationByTerm("夏威夷"));

        assertTrue(travelBasicDAO.hasLocationByTerm("Nepal"));
        assertTrue(travelBasicDAO.hasLocationByTerm("niboer"));
        assertTrue(travelBasicDAO.hasLocationByTerm("尼泊尔"));

        assertFalse(travelBasicDAO.hasLocationByTerm("汉城"));
        assertFalse(travelBasicDAO.hasLocationByTerm("麻省"));
        assertFalse(travelBasicDAO.hasLocationByTerm("南斯拉夫"));
    }

    @Test
    public void testHasLocationByUuid() throws DatabaseAccessException {
        assertTrue(travelBasicDAO.hasLocationByUuid("79fd8642a11d4811887dec4268097a82")); // Wuhan

        assertTrue(travelBasicDAO.hasLocationByUuid("03161e050c2448378eb863bfcbe744f3")); // Hubei

        assertTrue(travelBasicDAO.hasLocationByUuid("af70a55ceb4c415c837588081716f8b8")); // China

        assertTrue(travelBasicDAO.hasLocationByUuid("54053f1a057a4ded88c854c5e56c63f4")); // London

        assertTrue(travelBasicDAO.hasLocationByUuid("8f18986005ee469b9f1dcdf5eb897804")); // Hawaii

        assertTrue(travelBasicDAO.hasLocationByUuid("b2f93dcef0394598bda64cc1d49b501b")); // Nepal

        assertFalse(travelBasicDAO.hasLocationByUuid(UUID.randomUUID().toString()));
    }

    @Test
    public void testIsDomesticLocationByTerm() throws DatabaseAccessException {
        assertTrue(travelBasicDAO.isDomesticLocationByTerm("Wuhan"));
        assertTrue(travelBasicDAO.isDomesticLocationByTerm("wuhan"));
        assertTrue(travelBasicDAO.isDomesticLocationByTerm("武汉"));

        assertTrue(travelBasicDAO.isDomesticLocationByTerm("Hubei"));
        assertTrue(travelBasicDAO.isDomesticLocationByTerm("hubei"));
        assertTrue(travelBasicDAO.isDomesticLocationByTerm("湖北"));

        assertTrue(travelBasicDAO.isDomesticLocationByTerm("China"));
        assertTrue(travelBasicDAO.isDomesticLocationByTerm("zhongguo"));
        assertTrue(travelBasicDAO.isDomesticLocationByTerm("中国"));

        assertFalse(travelBasicDAO.isDomesticLocationByTerm("London"));
        assertFalse(travelBasicDAO.isDomesticLocationByTerm("lundun"));
        assertFalse(travelBasicDAO.isDomesticLocationByTerm("伦敦"));

        assertFalse(travelBasicDAO.isDomesticLocationByTerm("Hawaii"));
        assertFalse(travelBasicDAO.isDomesticLocationByTerm("xiaweiyi"));
        assertFalse(travelBasicDAO.isDomesticLocationByTerm("夏威夷"));

        assertFalse(travelBasicDAO.isDomesticLocationByTerm("Nepal"));
        assertFalse(travelBasicDAO.isDomesticLocationByTerm("niboer"));
        assertFalse(travelBasicDAO.isDomesticLocationByTerm("尼泊尔"));

        assertFalse(travelBasicDAO.isDomesticLocationByTerm("汉城"));
        assertFalse(travelBasicDAO.isDomesticLocationByTerm("麻省"));
        assertFalse(travelBasicDAO.isDomesticLocationByTerm("南斯拉夫"));
    }

    @Test
    public void testIsDomesticLocationByUuid() throws DatabaseAccessException {
        assertTrue(travelBasicDAO.isDomesticLocationByUuid("79fd8642a11d4811887dec4268097a82")); // Wuhan

        assertTrue(travelBasicDAO.isDomesticLocationByUuid("03161e050c2448378eb863bfcbe744f3")); // Hubei

        assertTrue(travelBasicDAO.isDomesticLocationByUuid("af70a55ceb4c415c837588081716f8b8")); // China

        assertFalse(travelBasicDAO.isDomesticLocationByUuid("54053f1a057a4ded88c854c5e56c63f4")); // London

        assertFalse(travelBasicDAO.isDomesticLocationByUuid("8f18986005ee469b9f1dcdf5eb897804")); // Hawaii

        assertFalse(travelBasicDAO.isDomesticLocationByUuid("b2f93dcef0394598bda64cc1d49b501b")); // Nepal

        assertFalse(travelBasicDAO.isDomesticLocationByUuid(UUID.randomUUID().toString()));
    }

    @Test
    public void testGetAgencies4DomesticDestination() throws DatabaseAccessException {
        {
            List<Agency> agencies = travelBasicDAO.getAgencies4DomesticDestination("e98c3585f7474eca9b66b58f57362fb7",
                "79fd8642a11d4811887dec4268097a82"); // Enshi => Wuhan
            assertNotNull(agencies);
            assertFalse(agencies.isEmpty());
            for (int i = 0; i < agencies.size(); i++) {
                assertTrue(agencies.get(i).getName().contains("武汉")
                    || agencies.get(i).getName().contains("宜昌")
                    || agencies.get(i).getName().contains("湖北"));
            }
        }

        {
            List<Agency> agencies = travelBasicDAO.getAgencies4DomesticDestination("e98c3585f7474eca9b66b58f57362fb7",
                "689ddfcdeffd4937b56707c4c8907378"); // Enshi => Beijing
            assertNotNull(agencies);
            assertFalse(agencies.isEmpty());
            for (int i = 0; i < agencies.size(); i++) {
                assertTrue(agencies.get(i).getName().contains("武汉")
                    || agencies.get(i).getName().contains("宜昌")
                    || agencies.get(i).getName().contains("湖北")
                    || agencies.get(i).getName().contains("北京"));
            }
        }

        {
            List<Agency> agencies = travelBasicDAO.getAgencies4DomesticDestination("e98c3585f7474eca9b66b58f57362fb7",
                "92d55e093025479db3f64b6aa38de051"); // Enshi => Hunan
            assertNotNull(agencies);
            assertFalse(agencies.isEmpty());
            for (int i = 0; i < agencies.size(); i++) {
                assertTrue(agencies.get(i).getName().contains("武汉")
                    || agencies.get(i).getName().contains("宜昌")
                    || agencies.get(i).getName().contains("湖北")
                    || agencies.get(i).getName().contains("长沙")
                    || agencies.get(i).getName().contains("湖南")
                    || agencies.get(i).getName().contains("中国"));
            }
        }

        {
            List<Agency> agencies = travelBasicDAO.getAgencies4DomesticDestination("79fd8642a11d4811887dec4268097a82",
                "689ddfcdeffd4937b56707c4c8907378"); // Wuhan => Beijing
            assertNotNull(agencies);
            assertFalse(agencies.isEmpty());
            for (int i = 0; i < agencies.size(); i++) {
                assertTrue(agencies.get(i).getName().contains("武汉")
                    || agencies.get(i).getName().contains("湖北")
                    || agencies.get(i).getName().contains("北京"));
            }
        }

        {
            List<Agency> agencies = travelBasicDAO.getAgencies4DomesticDestination("79fd8642a11d4811887dec4268097a82",
                "92d55e093025479db3f64b6aa38de051"); // Wuhan => Hunan
            assertNotNull(agencies);
            assertFalse(agencies.isEmpty());
            for (int i = 0; i < agencies.size(); i++) {
                assertTrue(agencies.get(i).getName().contains("武汉")
                    || agencies.get(i).getName().contains("湖北")
                    || agencies.get(i).getName().contains("长沙")
                    || agencies.get(i).getName().contains("湖南")
                    || agencies.get(i).getName().contains("中国"));
            }
        }

        {
            List<Agency> agencies = travelBasicDAO.getAgencies4DomesticDestination("689ddfcdeffd4937b56707c4c8907378",
                "92d55e093025479db3f64b6aa38de051"); // Beijing => Hunan
            assertNotNull(agencies);
            assertFalse(agencies.isEmpty());
            for (int i = 0; i < agencies.size(); i++) {
                assertTrue(agencies.get(i).getName().contains("北京")
                    || agencies.get(i).getName().contains("长沙")
                    || agencies.get(i).getName().contains("湖南")
                    || agencies.get(i).getName().contains("湖北")
                    || agencies.get(i).getName().contains("中国"));
            }
        }
    }

    @Test
    public void testGetAgencies4InternationalDestination() throws DatabaseAccessException {
        {
            List<Agency> agencies = travelBasicDAO.getAgencies4InternationalDestination(
                "e98c3585f7474eca9b66b58f57362fb7", "54053f1a057a4ded88c854c5e56c63f4"); // Enshi => London
            assertNotNull(agencies);
            assertFalse(agencies.isEmpty());
            for (int i = 0; i < agencies.size(); i++) {
                assertTrue(agencies.get(i).getName().contains("武汉")
                    || agencies.get(i).getName().contains("宜昌")
                    || agencies.get(i).getName().contains("湖北")
                    || agencies.get(i).getName().contains("北京")
                    || agencies.get(i).getName().contains("伦敦"));
            }
        }

        {
            List<Agency> agencies = travelBasicDAO.getAgencies4InternationalDestination(
                "e98c3585f7474eca9b66b58f57362fb7", "8f18986005ee469b9f1dcdf5eb897804"); // Enshi => Hawaii
            assertNotNull(agencies);
            assertFalse(agencies.isEmpty());
            for (int i = 0; i < agencies.size(); i++) {
                assertTrue(agencies.get(i).getName().contains("武汉")
                    || agencies.get(i).getName().contains("宜昌")
                    || agencies.get(i).getName().contains("湖北")
                    || agencies.get(i).getName().contains("火奴鲁鲁")
                    || agencies.get(i).getName().contains("夏威夷")
                    || agencies.get(i).getName().contains("美国"));
            }
        }

        {
            List<Agency> agencies = travelBasicDAO.getAgencies4InternationalDestination(
                "e98c3585f7474eca9b66b58f57362fb7", "fe80acb2816e45f0a98819caa587c6fc"); // Enshi => Spain
            assertNotNull(agencies);
            assertFalse(agencies.isEmpty());
            for (int i = 0; i < agencies.size(); i++) {
                assertTrue(agencies.get(i).getName().contains("武汉")
                    || agencies.get(i).getName().contains("宜昌")
                    || agencies.get(i).getName().contains("湖北")
                    || agencies.get(i).getName().contains("西班牙")
                    || agencies.get(i).getName().contains("欧洲"));
            }
        }

        {
            List<Agency> agencies = travelBasicDAO.getAgencies4InternationalDestination(
                "e98c3585f7474eca9b66b58f57362fb7", "2"); // Enshi => EU
            assertNotNull(agencies);
            assertFalse(agencies.isEmpty());
            for (int i = 0; i < agencies.size(); i++) {
                assertTrue(agencies.get(i).getName().contains("武汉")
                    || agencies.get(i).getName().contains("宜昌")
                    || agencies.get(i).getName().contains("湖北")
                    || agencies.get(i).getName().contains("欧洲"));
            }
        }

//        travelBasicDAO.getAgencies4DomesticDestination("79fd8642a11d4811887dec4268097a82",
//            "54053f1a057a4ded88c854c5e56c63f4"); // Wuhan => London
//        travelBasicDAO.getAgencies4DomesticDestination("79fd8642a11d4811887dec4268097a82",
//            "8f18986005ee469b9f1dcdf5eb897804"); // Wuhan => Hawaii
//        travelBasicDAO.getAgencies4DomesticDestination("79fd8642a11d4811887dec4268097a82",
//            "fe80acb2816e45f0a98819caa587c6fc"); // Wuhan => Spain
//        travelBasicDAO.getAgencies4DomesticDestination("79fd8642a11d4811887dec4268097a82", "6"); // Wuhan => Africa
//
//        travelBasicDAO.getAgencies4DomesticDestination("689ddfcdeffd4937b56707c4c8907378",
//            "54053f1a057a4ded88c854c5e56c63f4"); // Beijing => London
//        travelBasicDAO.getAgencies4DomesticDestination("689ddfcdeffd4937b56707c4c8907378",
//            "8f18986005ee469b9f1dcdf5eb897804"); // Beijing => Hawaii
//        travelBasicDAO.getAgencies4DomesticDestination("689ddfcdeffd4937b56707c4c8907378",
//            "fe80acb2816e45f0a98819caa587c6fc"); // Beijing => Spain
//        travelBasicDAO.getAgencies4DomesticDestination("689ddfcdeffd4937b56707c4c8907378", "6"); // Beijing => Africa
    }

    @Test
    public void testGetAgencies4DangleDestination() throws DatabaseAccessException {
        {
            List<Agency> agencies = travelBasicDAO.getAgencies4DangleDestination("e98c3585f7474eca9b66b58f57362fb7"); // Enshi
            assertNotNull(agencies);
            assertFalse(agencies.isEmpty());
        }

        {
            List<Agency> agencies = travelBasicDAO.getAgencies4DangleDestination("79fd8642a11d4811887dec4268097a82"); // Wuhan
            assertNotNull(agencies);
            assertFalse(agencies.isEmpty());
        }

        {
            List<Agency> agencies = travelBasicDAO.getAgencies4DangleDestination("03161e050c2448378eb863bfcbe744f3"); // Hubei
            assertNotNull(agencies);
            assertFalse(agencies.isEmpty());
        }

        {
            List<Agency> agencies = travelBasicDAO.getAgencies4DangleDestination("689ddfcdeffd4937b56707c4c8907378"); // Beijing
            assertNotNull(agencies);
            assertFalse(agencies.isEmpty());
        }

        {
            List<Agency> agencies = travelBasicDAO.getAgencies4DangleDestination("54053f1a057a4ded88c854c5e56c63f4"); // London
            assertNotNull(agencies);
            assertFalse(agencies.isEmpty());
        }

        {
            List<Agency> agencies = travelBasicDAO.getAgencies4DangleDestination("8f18986005ee469b9f1dcdf5eb897804"); // Hawaii
            assertNotNull(agencies);
            assertTrue(agencies.isEmpty());
        }
        
        {
            List<Agency> agencies = travelBasicDAO.getAgencies4DangleDestination("af70a55ceb4c415c837588081716f8b8"); // China
            assertNotNull(agencies);
            assertTrue(agencies.isEmpty());
        }

        {
            List<Agency> agencies = travelBasicDAO.getAgencies4DangleDestination("cc0968e70fe34cc99f5b3a6898a04506"); // USA
            assertNotNull(agencies);
            assertTrue(agencies.isEmpty());
        }

        {
            List<Agency> agencies = travelBasicDAO.getAgencies4DangleDestination("6"); // Africa
            assertNotNull(agencies);
            assertTrue(agencies.isEmpty());
        }
    }

    @Test
    public void testGetHottestTags() throws DatabaseAccessException {
        List<Tag> hottestTags = travelBasicDAO.getHottestTags(10);
        assertNotNull(hottestTags);
        assertFalse(hottestTags.isEmpty());
        assertTrue(hottestTags.size() <= 10);

        Tag first = hottestTags.get(0);
        assertNotNull(first);
        assertNotNull(first.getName());
        assertTrue(first.getFrequency() > 0);

        Tag last = hottestTags.get(hottestTags.size() - 1);
        assertNotNull(last);
        assertNotNull(last.getName());
        assertTrue(last.getFrequency() > 0);
    }

    @Test
    public void testAddAgencyCandidates4Proposal() throws DatabaseAccessException {
        String proposalId = UUID.randomUUID().toString();
        travelBasicDAO.addAgencyCandidates4Proposal(proposalId, "Mock Proposal Summary A", candidates);

        List<Agency> agencies = travelBasicDAO.getAgencyCandidates4Proposal(proposalId);
        assertNotNull(agencies);
        assertFalse(agencies.isEmpty());
        assertEquals(candidates.size(), agencies.size());
    }

    @Test
    public void testGetProposals4Agency() throws DatabaseAccessException {
        {
            String proposalId = UUID.randomUUID().toString();
            travelBasicDAO.addAgencyCandidates4Proposal(proposalId, "Mock Proposal Summary A", candidates);

            Map<String, String> proposalSummaries = travelBasicDAO.getProposals4AgencyCandidate(a1, 10);
            assertNotNull(proposalSummaries);
            assertEquals(1, proposalSummaries.values().size());
            assertTrue("Mock Proposal Summary A".equals(proposalSummaries.values().iterator().next()));
        }

        {
            String proposalId = UUID.randomUUID().toString();
            travelBasicDAO.addAgencyCandidates4Proposal(proposalId, "Mock Proposal Summary B", candidates);

            Map<String, String> proposalSummaries = travelBasicDAO.getProposals4AgencyCandidate(a1, 10);
            assertNotNull(proposalSummaries);
            assertEquals(2, proposalSummaries.values().size());
            Iterator<String> summaries = proposalSummaries.values().iterator();
            String s1 = summaries.next();
            String s2 = summaries.next();
            assertTrue("Mock Proposal Summary A".equals(s1) || "Mock Proposal Summary B".equals(s1));
            assertTrue("Mock Proposal Summary A".equals(s2) || "Mock Proposal Summary B".equals(s2));
        }
    }

    @Test
    public void testMarkAgencyCandidatesAsResponded() throws DatabaseAccessException {
        String proposalId = UUID.randomUUID().toString();
        travelBasicDAO.addAgencyCandidates4Proposal(proposalId, "Mock Proposal Summary B", candidates);

        for (int i = 0; i < candidates.size() / 2; i++) {
            travelBasicDAO.markAgencyCandidateAsResponded(proposalId, candidates.get(i).getUuid().toString());
        }

        List<Agency> agencies = travelBasicDAO.getRespondedAgencyCandidates4Proposal(proposalId);
        assertNotNull(agencies);
        assertFalse(agencies.isEmpty());
        assertEquals(candidates.size() / 2, agencies.size());

        List<Agency> agenciesAll = travelBasicDAO.getAgencyCandidates4Proposal(proposalId);
        assertNotNull(agenciesAll);
        assertFalse(agenciesAll.isEmpty());
        assertEquals(candidates.size(), agenciesAll.size());
    }

    @Test
    public void testMarkAgencyCandidatesAsElected() throws DatabaseAccessException {
        String proposalId = UUID.randomUUID().toString();
        travelBasicDAO.addAgencyCandidates4Proposal(proposalId, "Mock Proposal Summary B", candidates);
        List<String> agencyIds = new ArrayList<String>(candidates.size());
        for (int i = 0; i < candidates.size() / 2; i++) {
            agencyIds.add(candidates.get(i).getUuid().toString());
        }
        travelBasicDAO.markAgencyCandidatesAsElected(proposalId, agencyIds);

        List<Agency> agenciesElected = travelBasicDAO.getElectedAgencyCandidates4Proposal(proposalId);
        assertNotNull(agenciesElected);
        assertFalse(agenciesElected.isEmpty());
        assertEquals(candidates.size() / 2, agenciesElected.size());

        List<Agency> agenciesNotElected = travelBasicDAO.getNotElectedAgencyCandidates4Proposal(proposalId);
        assertNotNull(agenciesNotElected);
        assertFalse(agenciesNotElected.isEmpty());
        assertEquals(candidates.size() - candidates.size() / 2, agenciesNotElected.size());

        List<Agency> agenciesAll = travelBasicDAO.getAgencyCandidates4Proposal(proposalId);
        assertNotNull(agenciesAll);
        assertFalse(agenciesAll.isEmpty());
        assertEquals(candidates.size(), agenciesAll.size());
    }

    @After
    public void after() throws DatabaseAccessException {
        travelBasicDAO.removeAgency(a1);
        travelBasicDAO.removeAgency(a2);
        travelBasicDAO.removeAgency(a3);
        travelBasicDAO.removeAgency(a4);
        travelBasicDAO.removeAgency(a5);
        travelBasicDAO.removeAgency(a6);
        travelBasicDAO.removeAgency(a7);
        travelBasicDAO.removeAgency(a8);
        travelBasicDAO.removeAgency(a9);
        travelBasicDAO.removeAgency(a10);

        {
            /*
             * Enshi
             */
            travelBasicDAO.deassociateLocation("e98c3585f7474eca9b66b58f57362fb7", "79fd8642a11d4811887dec4268097a82"); // Wuhan
            travelBasicDAO.deassociateLocation("e98c3585f7474eca9b66b58f57362fb7", "cda48bcd9ab64669994013897321a3fb"); // Yichang
            travelBasicDAO.deassociateLocation("e98c3585f7474eca9b66b58f57362fb7", "03161e050c2448378eb863bfcbe744f3"); // Hubei

            /*
             * Beijing
             */
            ;

            /*
             * Wuhan
             */
            travelBasicDAO.deassociateLocation("79fd8642a11d4811887dec4268097a82", "03161e050c2448378eb863bfcbe744f3"); // Hubei

            /*
             * Hubei
             */
            travelBasicDAO.deassociateLocation("03161e050c2448378eb863bfcbe744f3", "79fd8642a11d4811887dec4268097a82"); // Wuhan

            /*
             * Hunan
             */
            travelBasicDAO.deassociateLocation("92d55e093025479db3f64b6aa38de051", "03161e050c2448378eb863bfcbe744f3"); // Hubei
            travelBasicDAO.deassociateLocation("92d55e093025479db3f64b6aa38de051", "675b8393ac04418786ccb1d1618f33f1"); // Changsha
            travelBasicDAO.deassociateLocation("92d55e093025479db3f64b6aa38de051", "af70a55ceb4c415c837588081716f8b8"); // China

            /*
             * London
             */
            travelBasicDAO.deassociatePortLocation("54053f1a057a4ded88c854c5e56c63f4", "689ddfcdeffd4937b56707c4c8907378"); // Beijing

            /*
             * Hawaii
             */
            travelBasicDAO.deassociateLocation("8f18986005ee469b9f1dcdf5eb897804", "fbb3821586c04c1abca1edc25ddde4fc"); // Arizona
            travelBasicDAO.deassociateLocation("8f18986005ee469b9f1dcdf5eb897804", "cc0968e70fe34cc99f5b3a6898a04506"); // USA

            /*
             * Spain
             */
            travelBasicDAO.deassociateLocation("fe80acb2816e45f0a98819caa587c6fc", "b164126cb75e4d17a9f63e3f05172e73"); // Britain
            travelBasicDAO.deassociateLocation("fe80acb2816e45f0a98819caa587c6fc", "2"); // Eropean

            /*
             * AS
             */
            travelBasicDAO.deassociateLocation("1", "af70a55ceb4c415c837588081716f8b8"); // China
            travelBasicDAO.deassociateLocation("1", "ef72692dbd2b49a9bc73b74b5ce0b0cb"); // Korea

            /*
             * EU
             */
            travelBasicDAO.deassociateLocation("2", "b164126cb75e4d17a9f63e3f05172e73"); // China
            travelBasicDAO.deassociateLocation("2", "fe80acb2816e45f0a98819caa587c6fc"); // Spain

            /*
             * NA
             */
            travelBasicDAO.deassociateLocation("3", "cc0968e70fe34cc99f5b3a6898a04506"); // USA
        }
    }
}
