package com.free.walker.service.itinerary.dao.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.City;
import com.free.walker.service.itinerary.basic.Country;
import com.free.walker.service.itinerary.basic.Province;
import com.free.walker.service.itinerary.basic.Tag;
import com.free.walker.service.itinerary.dao.BasicMapper;
import com.free.walker.service.itinerary.dao.DAOConstants;
import com.free.walker.service.itinerary.dao.TravelBasicDAO;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;

public class MySQLTravelBasicDAOImpl implements TravelBasicDAO {
    private static Logger LOG = LoggerFactory.getLogger(MySQLTravelBasicDAOImpl.class);

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
        String resource = "com/free/walker/service/itinerary/dao/mybatis-config.xml";
        InputStream inputStream;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        mysqlDatabaseUrl = sqlSessionFactory.getConfiguration().getVariables()
            .getProperty(DAOConstants.mysql_database_url);
        mysqlDatabaseDriver = sqlSessionFactory.getConfiguration().getVariables()
            .getProperty(DAOConstants.mysql_database_driver);
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

    public List<Tag> getHottestTags(int topN) throws DatabaseAccessException {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            BasicMapper basicMapper = session.getMapper(BasicMapper.class);
            List<Tag> tags = basicMapper.getHottestTags(topN);
            return tags;
        }  catch(Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_operation_failure), e);
            throw new DatabaseAccessException(e);
        } finally {
            session.close();
        }
    }
}
