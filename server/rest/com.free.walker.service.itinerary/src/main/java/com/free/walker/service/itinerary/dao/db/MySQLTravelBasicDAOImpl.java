package com.free.walker.service.itinerary.dao.db;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.Agency;
import com.free.walker.service.itinerary.basic.City;
import com.free.walker.service.itinerary.basic.Country;
import com.free.walker.service.itinerary.basic.Province;
import com.free.walker.service.itinerary.basic.Tag;
import com.free.walker.service.itinerary.dao.DAOConstants;
import com.free.walker.service.itinerary.dao.TravelBasicDAO;
import com.free.walker.service.itinerary.dao.mapper.BasicMapper;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.util.MySQLDbClientBuilder;
import com.free.walker.service.itinerary.util.SystemConfigUtil;
import com.free.walker.service.itinerary.util.UuidUtil;

public class MySQLTravelBasicDAOImpl implements TravelBasicDAO {
    private static final Logger LOG = LoggerFactory.getLogger(MySQLTravelBasicDAOImpl.class);
    private static final int BASE_LOCATION_LEVEL = 200;
    private static final int LOCATION_LEVEL_INTERVAL = 200;

    private SqlSessionFactory sqlSessionFactory;
    private String mysqlDatabaseUrl;
    private String mysqlDatabaseDriver;

    private static class SingletonHolder {
        private static final TravelBasicDAO INSTANCE = new MySQLTravelBasicDAOImpl();
    }

    public static TravelBasicDAO getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private MySQLTravelBasicDAOImpl() {
        try {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(MySQLDbClientBuilder.getMasterConfig(),
                SystemConfigUtil.getApplicationConfig());
            mysqlDatabaseUrl = sqlSessionFactory.getConfiguration().getVariables()
                .getProperty(DAOConstants.mysql_database_url);
            mysqlDatabaseDriver = sqlSessionFactory.getConfiguration().getVariables()
                .getProperty(DAOConstants.mysql_database_driver);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean pingPersistence() {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            BasicMapper basicMapper = session.getMapper(BasicMapper.class);
            int table_count = basicMapper.pingPersistence();
            if (table_count == 3) {
                return true;
            } else {
                return false;
            }
        } catch(Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_init_failure, mysqlDatabaseUrl, mysqlDatabaseDriver), e);
            return false;
        } finally {
            session.close();
        }
    }

    public List<Country> getAllCountries() throws DatabaseAccessException {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            BasicMapper basicMapper = session.getMapper(BasicMapper.class);
            List<Country> countries = basicMapper.getAllCounties();
            return countries;
        }  catch(Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_operation_failure), e);
            throw new DatabaseAccessException(e);
        } finally {
            session.close();
        }
    }

    public List<Province> getAllProvinces() throws DatabaseAccessException {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            BasicMapper basicMapper = session.getMapper(BasicMapper.class);
            List<Province> provinces = basicMapper.getAllProvinces();
            return provinces;
        }  catch(Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_operation_failure), e);
            throw new DatabaseAccessException(e);
        } finally {
            session.close();
        }
    }

    public List<City> getAllCities() throws DatabaseAccessException {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            BasicMapper basicMapper = session.getMapper(BasicMapper.class);
            List<City> cities = basicMapper.getAllCities();
            return cities;
        }  catch(Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_operation_failure), e);
            throw new DatabaseAccessException(e);
        } finally {
            session.close();
        }
    }

    public boolean hasLocationByTerm(String term) throws DatabaseAccessException {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            BasicMapper basicMapper = session.getMapper(BasicMapper.class);
            String locationUuid = basicMapper.hasLocationByTerm(term);
            return locationUuid != null && !locationUuid.isEmpty();
        }  catch(Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_operation_failure), e);
            throw new DatabaseAccessException(e);
        } finally {
            session.close();
        }
    }

    public boolean hasLocationByUuid(String uuid) throws DatabaseAccessException {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            BasicMapper basicMapper = session.getMapper(BasicMapper.class);
            if (UuidUtil.isCmpUuidStr(uuid)) {
                String locatoinUuid = basicMapper.hasLocationByUuid(uuid);
                return locatoinUuid != null && !locatoinUuid.isEmpty();
            } else {
                String locatoinUuid = basicMapper.hasLocationByUuid(UuidUtil.toCmpUuidStr(uuid));
                return locatoinUuid != null && !locatoinUuid.isEmpty();
            }
            
        }  catch(Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_operation_failure), e);
            throw new DatabaseAccessException(e);
        } finally {
            session.close();
        }
    }

    public boolean isDomesticLocationByTerm(String term) throws DatabaseAccessException {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            BasicMapper basicMapper = session.getMapper(BasicMapper.class);
            return basicMapper.countDomesticLocationByTerm(term) > 0;
        }  catch(Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_operation_failure), e);
            throw new DatabaseAccessException(e);
        } finally {
            session.close();
        }
    }

    public boolean isDomesticLocationByUuid(String uuid) throws DatabaseAccessException {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            BasicMapper basicMapper = session.getMapper(BasicMapper.class);
            return basicMapper.countDomesticLocationByUuid(uuid) > 0;
        }  catch(Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_operation_failure), e);
            throw new DatabaseAccessException(e);
        } finally {
            session.close();
        }
    }

    public List<Agency> getAgencies4DomesticDestination(String departureLocationUuid, String destinationLocationUuid)
        throws DatabaseAccessException {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            BasicMapper basicMapper = session.getMapper(BasicMapper.class);
            List<Agency> agencies = basicMapper.getAgencies4DomesticDestination(departureLocationUuid,
                destinationLocationUuid);
            return agencies;
        } catch (Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_operation_failure), e);
            throw new DatabaseAccessException(e);
        } finally {
            session.close();
        }
    }

    public List<Agency> getAgencies4InternationalDestination(String departureLocationUuid, String destinationLocationUuid)
        throws DatabaseAccessException {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            BasicMapper basicMapper = session.getMapper(BasicMapper.class);
            List<Agency> agencies = basicMapper.getAgencies4InternationalDestination(departureLocationUuid,
                destinationLocationUuid);
            return agencies;
        } catch (Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_operation_failure), e);
            throw new DatabaseAccessException(e);
        } finally {
            session.close();
        }
    }

    public List<Agency> getAgencies4DangleDestination(String departureLocationUuid) throws DatabaseAccessException {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            BasicMapper basicMapper = session.getMapper(BasicMapper.class);
            List<Agency> agencies = basicMapper.getAgencies4DangleDestination(departureLocationUuid);
            return agencies;
        } catch (Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_operation_failure), e);
            throw new DatabaseAccessException(e);
        } finally {
            session.close();
        }
    }

    public List<Tag> getHottestTags(int topN) throws DatabaseAccessException {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            BasicMapper basicMapper = session.getMapper(BasicMapper.class);
            List<Tag> tags = basicMapper.getHottestTags(topN);
            return tags;
        } catch(Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_operation_failure), e);
            throw new DatabaseAccessException(e);
        } finally {
            session.close();
        }
    }

    public void associateLocation(String primary, String secondary) throws DatabaseAccessException {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            BasicMapper basicMapper = session.getMapper(BasicMapper.class);
            int level1 = getLocationLevel(basicMapper, primary);
            int level2 = getLocationLevel(basicMapper, secondary);
            if (level1 == 0) {
                throw new IllegalArgumentException(LocalMessages.getMessage(LocalMessages.missing_location, primary));
            } else if (level2 == 0) {
                throw new IllegalArgumentException(LocalMessages.getMessage(LocalMessages.missing_location, secondary));
            } else if (Math.abs(level1 - level2) > LOCATION_LEVEL_INTERVAL) {
                throw new IllegalArgumentException(LocalMessages.getMessage(LocalMessages.illegal_location_association,
                    primary, level1, secondary, level2));
            } else {
                basicMapper.associateLocatoin(primary, secondary);
            }

            session.commit();
            return;
        } catch(Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_operation_failure), e);
            throw new DatabaseAccessException(e);
        } finally {
            session.close();
        }
    }

    public void deassociateLocation(String primary, String secondary) throws DatabaseAccessException {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            BasicMapper basicMapper = session.getMapper(BasicMapper.class);
            basicMapper.deassociateLocatoin(primary, secondary);

            session.commit();
            return;
        } catch (Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_operation_failure), e);
            throw new DatabaseAccessException(e);
        } finally {
            session.close();
        }
    }

    public void associatePortLocation(String primary, String secondary) throws DatabaseAccessException {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            BasicMapper basicMapper = session.getMapper(BasicMapper.class);
            int level1 = getLocationLevel(basicMapper, primary);
            int level2 = getLocationLevel(basicMapper, secondary);
            if (level1 == 0) {
                throw new IllegalArgumentException(LocalMessages.getMessage(LocalMessages.missing_location, primary));
            } else if (level2 == 0) {
                throw new IllegalArgumentException(LocalMessages.getMessage(LocalMessages.missing_location, secondary));
            } else if (level1 != BASE_LOCATION_LEVEL || level2 != BASE_LOCATION_LEVEL) {
                throw new IllegalArgumentException(LocalMessages.getMessage(LocalMessages.illegal_port_association,
                    primary, level1, secondary, level2));
            } else {
                basicMapper.associatePortLocatoin(primary, secondary);
            }

            session.commit();
            return;
        } catch(Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_operation_failure), e);
            throw new DatabaseAccessException(e);
        } finally {
            session.close();
        }
    }

    public void deassociatePortLocation(String primary, String secondary) throws DatabaseAccessException {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            BasicMapper basicMapper = session.getMapper(BasicMapper.class);
            basicMapper.deassociatePortLocatoin(primary, secondary);

            session.commit();
            return;
        } catch (Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_operation_failure), e);
            throw new DatabaseAccessException(e);
        } finally {
            session.close();
        }
    }

    public String addAgency(Agency agency) throws DatabaseAccessException {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            BasicMapper basicMapper = session.getMapper(BasicMapper.class);
            int level1 = getLocationLevel(basicMapper, agency.getDeparture());
            int level2 = getLocationLevel(basicMapper, agency.getDestination());
            if (level1 == 0) {
                throw new IllegalArgumentException(LocalMessages.getMessage(LocalMessages.missing_location,
                    agency.getDeparture()));
            } else if (level2 == 0) {
                throw new IllegalArgumentException(LocalMessages.getMessage(LocalMessages.missing_location,
                    agency.getDestination()));
            } else if (level1 != BASE_LOCATION_LEVEL) {
                throw new IllegalArgumentException(LocalMessages.getMessage(LocalMessages.illegal_add_agency_operation,
                    agency.getDeparture(), level1));
            } else {
                basicMapper.addAgency(agency);
            }

            session.commit();
            return agency.getUuid().toString();
        } catch (Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_operation_failure), e);
            throw new DatabaseAccessException(e);
        } finally {
            session.close();
        }
    }

    public String removeAgency(String uuid) throws DatabaseAccessException {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            BasicMapper basicMapper = session.getMapper(BasicMapper.class);
            basicMapper.deleteAgency(uuid);

            session.commit();
            return uuid;
        } catch(Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_operation_failure), e);
            throw new DatabaseAccessException(e);
        } finally {
            session.close();
        }
    }

    public void addAgencyCandidates4Proposal(String proposalId, String proposalSummary, List<Agency> candidates)
        throws DatabaseAccessException {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            BasicMapper basicMapper = session.getMapper(BasicMapper.class);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("proposalId", proposalId);
            params.put("proposalSummary", proposalSummary);
            params.put("candidates", candidates);
            basicMapper.addAgencyCandidates4Proposal(params);

            session.commit();
            return;
        } catch (Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_operation_failure), e);
            throw new DatabaseAccessException(e);
        } finally {
            session.close();
        }
    }

    public List<Agency> getAgencyCandidates4Proposal(String proposalId) throws DatabaseAccessException {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            BasicMapper basicMapper = session.getMapper(BasicMapper.class);
            return basicMapper.getAgencyCandidates4Proposal(proposalId);
        } catch (Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_operation_failure), e);
            throw new DatabaseAccessException(e);
        } finally {
            session.close();
        }
    }

    public void markAgencyCandidatesAsElected(String proposalId, List<String> agencyIds) throws DatabaseAccessException {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            BasicMapper basicMapper = session.getMapper(BasicMapper.class);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("proposalId", proposalId);
            params.put("agencyIds", agencyIds);
            basicMapper.markAgencyCandidatesAsElected(params);

            session.commit();
            return;
        } catch (Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_operation_failure), e);
            throw new DatabaseAccessException(e);
        } finally {
            session.close();
        }
    }

    public List<Agency> getElectedAgencyCandidates4Proposal(String proposalId) throws DatabaseAccessException {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            BasicMapper basicMapper = session.getMapper(BasicMapper.class);
            return basicMapper.getElectedAgencyCandidates4Proposal(proposalId);
        } catch (Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_operation_failure), e);
            throw new DatabaseAccessException(e);
        } finally {
            session.close();
        }
    }

    public List<Agency> getNotElectedAgencyCandidates4Proposal(String proposalId) throws DatabaseAccessException {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            BasicMapper basicMapper = session.getMapper(BasicMapper.class);
            return basicMapper.getNotElectedAgencyCandidates4Proposal(proposalId);
        } catch (Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_operation_failure), e);
            throw new DatabaseAccessException(e);
        } finally {
            session.close();
        }
    }

    public void markAgencyCandidateAsResponded(String proposalId, String agencyId) throws DatabaseAccessException {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            BasicMapper basicMapper = session.getMapper(BasicMapper.class);
            basicMapper.markAgencyCandidateAsResponded(proposalId, agencyId);

            session.commit();
            return;
        } catch (Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_operation_failure), e);
            throw new DatabaseAccessException(e);
        } finally {
            session.close();
        }
    }

    public List<Agency> getRespondedAgencyCandidates4Proposal(String proposalId) throws DatabaseAccessException {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            BasicMapper basicMapper = session.getMapper(BasicMapper.class);
            return basicMapper.getRespondedAgencyCandidates4Proposal(proposalId);
        } catch (Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_operation_failure), e);
            throw new DatabaseAccessException(e);
        } finally {
            session.close();
        }
    }

    private int getLocationLevel(BasicMapper basicMapper, String uuid) {
        int base = BASE_LOCATION_LEVEL;
        if (basicMapper.getCity(uuid) != null) {
            return base;
        } else if (basicMapper.getProvince(uuid) != null) {
            return base + LOCATION_LEVEL_INTERVAL;
        } else if (basicMapper.getCountry(uuid) != null) {
            return base + LOCATION_LEVEL_INTERVAL * 2;
        } else if (uuid.matches("[1-7]")) {
            return base + LOCATION_LEVEL_INTERVAL * 3;
        } else {
            return 0;
        }
    }
}
