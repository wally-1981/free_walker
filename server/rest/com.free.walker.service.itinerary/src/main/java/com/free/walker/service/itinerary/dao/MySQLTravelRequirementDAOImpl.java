package com.free.walker.service.itinerary.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.dao.map.RequirementMapper;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.req.TravelProposal;
import com.free.walker.service.itinerary.req.TravelRequirement;

public class MySQLTravelRequirementDAOImpl implements TravelRequirementDAO {
    private static Logger LOG = LoggerFactory.getLogger(MySQLTravelRequirementDAOImpl.class);
    private SqlSessionFactory sqlSessionFactory;
    private String databaseUrl;
    private String databaseDriver;

    private static class SingletonHolder {
        private static final TravelRequirementDAO INSTANCE = new MySQLTravelRequirementDAOImpl();
    }

    public static TravelRequirementDAO getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private MySQLTravelRequirementDAOImpl() {
        String resource = "com/free/walker/service/itinerary/dao/mybatis-config.xml";
        InputStream inputStream;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        databaseUrl = sqlSessionFactory.getConfiguration().getVariables().getProperty("database_url");
        databaseDriver = sqlSessionFactory.getConfiguration().getVariables().getProperty("database_driver");
    }

    public boolean pingPersistence() {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE);
        try {
            RequirementMapper requirementMapper = session.getMapper(RequirementMapper.class);
            String version = requirementMapper.pingPersistence();
            if (version == null) {
                return false;
            } else {
                return true;
            }
        } catch(Exception e) {
            LOG.error(LocalMessages.getMessage(LocalMessages.dao_init_failure, databaseUrl, databaseDriver), e);
            return false;
        } finally {
            session.close();
        }
    }

    public UUID createProposal(TravelProposal travelProposal) throws InvalidTravelReqirementException {
        // TODO Auto-generated method stub
        return null;
    }

    public UUID addRequirement(UUID travelProposalId, TravelRequirement travelRequirement)
        throws InvalidTravelReqirementException {
        // TODO Auto-generated method stub
        return null;
    }

    public UUID addRequirement(UUID travelProposalId, UUID itineraryRequirementId,
        TravelRequirement travelRequirement) throws InvalidTravelReqirementException {
        // TODO Auto-generated method stub
        return null;
    }

    public List<TravelRequirement> getRequirements(UUID travelProposalId) throws InvalidTravelReqirementException {
        // TODO Auto-generated method stub
        return null;
    }

    public List<TravelRequirement> getItineraryRequirements(UUID travelProposalId)
        throws InvalidTravelReqirementException {
        // TODO Auto-generated method stub
        return null;
    }

    public List<TravelRequirement> getRequirements(UUID travelProposalId, UUID itineraryRequirementId)
        throws InvalidTravelReqirementException {
        // TODO Auto-generated method stub
        return null;
    }

    public TravelRequirement getPrevItineraryRequirement(UUID travelProposalId, UUID travelRequirementId)
        throws InvalidTravelReqirementException {
        // TODO Auto-generated method stub
        return null;
    }

    public TravelRequirement getNextItineraryRequirement(UUID travelProposalId, UUID travelRequirementId)
        throws InvalidTravelReqirementException {
        // TODO Auto-generated method stub
        return null;
    }

    public TravelRequirement getRequirement(UUID travelRequirementId) throws InvalidTravelReqirementException {
        // TODO Auto-generated method stub
        return null;
    }

    public UUID updateRequirement(UUID travelRequirementId, TravelRequirement travelRequirement)
        throws InvalidTravelReqirementException {
        // TODO Auto-generated method stub
        return null;
    }

    public UUID removeRequirement(UUID travelRequirementId) throws InvalidTravelReqirementException {
        // TODO Auto-generated method stub
        return null;
    }
}
