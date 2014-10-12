package com.free.walker.service.itinerary.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.TravelLocation;
import com.free.walker.service.itinerary.basic.City;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.req.HotelRequirement;
import com.free.walker.service.itinerary.req.ItineraryRequirement;
import com.free.walker.service.itinerary.req.TrafficToolSeatRequirement;
import com.free.walker.service.itinerary.req.TravelProposal;
import com.free.walker.service.itinerary.req.TravelRequirement;
import com.free.walker.service.itinerary.traffic.TrafficToolSeatClass;

public class InMemoryTravelRequirementDAOImplTest {
    private TravelRequirementDAO travelRequirementDAO;
    private InMemoryTravelRequirementDAOImpl memoImpl;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() {
        travelRequirementDAO = DAOFactory.getTravelRequirementDAO(InMemoryTravelRequirementDAOImpl.class.getName());
        memoImpl = ((InMemoryTravelRequirementDAOImpl) travelRequirementDAO);
    }

    @Test
    public void testCreateTravelProposalWithNullProposal() {
        TravelRequirementDAO travelRequirementDAO = DAOFactory
            .getTravelRequirementDAO(InMemoryTravelRequirementDAOImpl.class.getName());

        thrown.expect(NullPointerException.class);
        travelRequirementDAO.createTravelProposal(null);
    }

    @Test
    public void testCreateTravelProposal() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createTravelProposal(travelProposal);

        assertNotNull(travelRequirementDAO.getTravelRequirement(proposalId));
        assertNotNull(travelRequirementDAO.getTravelRequirement(itineraryRequirement.getUUID()));

        assertNotNull(travelRequirementDAO.getItineraryRequirements(proposalId));
        assertEquals(1, travelRequirementDAO.getItineraryRequirements(proposalId).size());

        assertEquals(1, travelRequirementDAO.getRequirements(proposalId).size());

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.not_found_itinerary_requirement, "next",
            proposalId, itineraryRequirement.getUUID()));
        travelRequirementDAO.getNextItineraryRequirement(proposalId, itineraryRequirement.getUUID());
        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.not_found_itinerary_requirement, "previous",
            proposalId, itineraryRequirement.getUUID()));
        travelRequirementDAO.getPrevItineraryRequirement(proposalId, itineraryRequirement.getUUID());
    }

    @Test
    public void testAddTravelRequirementWithNullProposal() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);

        thrown.expect(NullPointerException.class);
        travelRequirementDAO.addTravelRequirement(null, itineraryRequirement);
    }

    @Test
    public void testAddTravelRequirementWithNullRequirement() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createTravelProposal(travelProposal);

        thrown.expect(NullPointerException.class);
        travelRequirementDAO.addTravelRequirement(proposalId, null);
    }

    @Test
    public void testAddTravelRequirementWithWrongProposalId() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        travelRequirementDAO.createTravelProposal(travelProposal);

        TravelLocation destination2 = new TravelLocation(City.BOSTON);
        TravelLocation departure2 = new TravelLocation(City.LONDON);
        ItineraryRequirement itineraryRequirement2 = new ItineraryRequirement(destination2, departure2);

        UUID wrongProposalId = UUID.randomUUID();
        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_requirement, wrongProposalId));
        travelRequirementDAO.addTravelRequirement(wrongProposalId, itineraryRequirement2);
    }

    @Test
    public void testAddTravelRequirementWithDuplicateRequirement() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createTravelProposal(travelProposal);

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.existed_travel_requirement,
            itineraryRequirement.getUUID()));
        travelRequirementDAO.addTravelRequirement(proposalId, itineraryRequirement);
    }

    @Test
    public void testAddTravelRequirementWithNewItineraryRequirement() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createTravelProposal(travelProposal);

        TravelLocation destination2 = new TravelLocation(City.BOSTON);
        TravelLocation departure2 = new TravelLocation(City.LONDON);
        ItineraryRequirement itineraryRequirement2 = new ItineraryRequirement(destination2, departure2);

        travelRequirementDAO.addTravelRequirement(proposalId, itineraryRequirement2);

        assertNotNull(travelRequirementDAO.getTravelRequirement(proposalId));
        assertNotNull(travelRequirementDAO.getTravelRequirement(itineraryRequirement.getUUID()));
        assertNotNull(travelRequirementDAO.getTravelRequirement(itineraryRequirement2.getUUID()));

        assertNotNull(travelRequirementDAO.getItineraryRequirements(proposalId));
        assertEquals(2, travelRequirementDAO.getItineraryRequirements(proposalId).size());
        assertTrue(travelRequirementDAO.getItineraryRequirements(proposalId).get(0) instanceof ItineraryRequirement);
        assertTrue(travelRequirementDAO.getItineraryRequirements(proposalId).get(1) instanceof ItineraryRequirement);

        assertEquals(2, travelRequirementDAO.getRequirements(proposalId).size());
        assertTrue(travelRequirementDAO.getRequirements(proposalId).get(0) instanceof ItineraryRequirement);
        assertTrue(travelRequirementDAO.getRequirements(proposalId).get(1) instanceof ItineraryRequirement);

        assertNotNull(travelRequirementDAO.getNextItineraryRequirement(proposalId, itineraryRequirement.getUUID()));
        assertNotNull(travelRequirementDAO.getPrevItineraryRequirement(proposalId, itineraryRequirement2.getUUID()));
    }

    @Test
    public void testAddTravelRequirementWithNewTravelRequirement() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createTravelProposal(travelProposal);

        TravelRequirement hotelRequirement = new HotelRequirement(12);

        travelRequirementDAO.addTravelRequirement(proposalId, hotelRequirement);

        assertNotNull(travelRequirementDAO.getTravelRequirement(proposalId));
        assertNotNull(travelRequirementDAO.getTravelRequirement(itineraryRequirement.getUUID()));
        assertNotNull(travelRequirementDAO.getTravelRequirement(hotelRequirement.getUUID()));

        assertNotNull(travelRequirementDAO.getItineraryRequirements(proposalId));
        assertEquals(1, travelRequirementDAO.getItineraryRequirements(proposalId).size());
        assertTrue(travelRequirementDAO.getItineraryRequirements(proposalId).get(0) instanceof ItineraryRequirement);

        assertEquals(2, travelRequirementDAO.getRequirements(proposalId).size());
        assertTrue(travelRequirementDAO.getRequirements(proposalId).get(0) instanceof ItineraryRequirement);
        assertTrue(travelRequirementDAO.getRequirements(proposalId).get(1) instanceof HotelRequirement);

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.not_found_itinerary_requirement, "next",
            proposalId, itineraryRequirement.getUUID()));
        travelRequirementDAO.getNextItineraryRequirement(proposalId, itineraryRequirement.getUUID());
        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.not_found_itinerary_requirement, "previous",
            proposalId, itineraryRequirement.getUUID()));
        travelRequirementDAO.getPrevItineraryRequirement(proposalId, itineraryRequirement.getUUID());
    }

    @Test
    public void testInsertTravelRequirementWithNullProposal() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);

        TravelRequirement hotelRequirement = new HotelRequirement(12);

        thrown.expect(NullPointerException.class);
        travelRequirementDAO.addTravelRequirement(null, itineraryRequirement.getUUID(), hotelRequirement);
    }

    @Test
    public void testInsertTravelRequirementWithNullItinerary() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createTravelProposal(travelProposal);

        TravelRequirement hotelRequirement = new HotelRequirement(12);

        thrown.expect(NullPointerException.class);
        travelRequirementDAO.addTravelRequirement(proposalId, null, hotelRequirement);
    }

    @Test
    public void testInsertTravelRequirementWithNullRequirement() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createTravelProposal(travelProposal);

        thrown.expect(NullPointerException.class);
        travelRequirementDAO.addTravelRequirement(proposalId, itineraryRequirement.getUUID(), null);
    }

    @Test
    public void testInsertTravelRequirementWithWrongProposalId() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        travelRequirementDAO.createTravelProposal(travelProposal);

        TravelRequirement hotelRequirement = new HotelRequirement(12);

        UUID wrongProposalId = UUID.randomUUID();
        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_requirement, wrongProposalId));
        travelRequirementDAO.addTravelRequirement(wrongProposalId, itineraryRequirement.getUUID(), hotelRequirement);
    }

    @Test
    public void testInsertTravelRequirementWithWrongRequirementId() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createTravelProposal(travelProposal);

        TravelRequirement hotelRequirement = new HotelRequirement(12);
        travelRequirementDAO.addTravelRequirement(proposalId, itineraryRequirement.getUUID(), hotelRequirement);

        TravelRequirement trafficToolSeatRequirement = new TrafficToolSeatRequirement(TrafficToolSeatClass.CLASS_2ND);

        UUID wrongRequirementId = UUID.randomUUID();
        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_itinerary_requirement,
            wrongRequirementId));
        travelRequirementDAO.addTravelRequirement(proposalId, wrongRequirementId, trafficToolSeatRequirement);
    }

    @Test
    public void testInsertTravelRequirementWithDuplicateRequirement() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createTravelProposal(travelProposal);

        TravelRequirement hotelRequirement = new HotelRequirement(12);
        travelRequirementDAO.addTravelRequirement(proposalId, itineraryRequirement.getUUID(), hotelRequirement);

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.existed_travel_requirement,
            hotelRequirement.getUUID()));
        travelRequirementDAO.addTravelRequirement(proposalId, itineraryRequirement.getUUID(), hotelRequirement);
    }

    @Test
    public void testInsertTravelRequirementWithItineraryRequirement() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createTravelProposal(travelProposal);

        TravelLocation destination2 = new TravelLocation(City.BOSTON);
        TravelLocation departure2 = new TravelLocation(City.BEIJING);
        ItineraryRequirement itineraryRequirement2 = new ItineraryRequirement(destination2, departure2);

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.illegal_add_travel_requirement_operation,
            itineraryRequirement.getUUID(), itineraryRequirement2.getUUID()));
        travelRequirementDAO.addTravelRequirement(proposalId, itineraryRequirement.getUUID(), itineraryRequirement2);
    }

    @Test
    public void testInsertTravelRequirement() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createTravelProposal(travelProposal);

        TravelRequirement hotelRequirement = new HotelRequirement(12);

        travelRequirementDAO.addTravelRequirement(proposalId, itineraryRequirement.getUUID(), hotelRequirement);

        assertNotNull(travelRequirementDAO.getTravelRequirement(proposalId));
        assertNotNull(travelRequirementDAO.getTravelRequirement(itineraryRequirement.getUUID()));
        assertNotNull(travelRequirementDAO.getTravelRequirement(hotelRequirement.getUUID()));

        assertNotNull(travelRequirementDAO.getItineraryRequirements(proposalId));
        assertEquals(1, travelRequirementDAO.getItineraryRequirements(proposalId).size());
        assertTrue(travelRequirementDAO.getItineraryRequirements(proposalId).get(0) instanceof ItineraryRequirement);

        assertEquals(2, travelRequirementDAO.getRequirements(proposalId).size());
        assertTrue(travelRequirementDAO.getRequirements(proposalId).get(0) instanceof ItineraryRequirement);
        assertTrue(travelRequirementDAO.getRequirements(proposalId).get(1) instanceof HotelRequirement);

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.not_found_itinerary_requirement, "next",
            proposalId, itineraryRequirement.getUUID()));
        travelRequirementDAO.getNextItineraryRequirement(proposalId, itineraryRequirement.getUUID());
        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.not_found_itinerary_requirement, "previous",
            proposalId, itineraryRequirement.getUUID()));
        travelRequirementDAO.getPrevItineraryRequirement(proposalId, itineraryRequirement.getUUID());
    }

    @Test
    public void testInsertTravelRequirementInTheMiddle() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createTravelProposal(travelProposal);

        TravelLocation destination2 = new TravelLocation(City.BOSTON);
        TravelLocation departure2 = new TravelLocation(City.LA);
        ItineraryRequirement itineraryRequirement2 = new ItineraryRequirement(destination2, departure2);
        travelRequirementDAO.addTravelRequirement(proposalId, itineraryRequirement2);
        TravelRequirement hotelRequirement = new HotelRequirement(12);
        travelRequirementDAO.addTravelRequirement(proposalId, itineraryRequirement2.getUUID(), hotelRequirement);

        TravelRequirement hotelRequirement2 = new HotelRequirement(3);
        travelRequirementDAO.addTravelRequirement(proposalId, itineraryRequirement.getUUID(), hotelRequirement2);
        TravelRequirement trafficToolRequirement = new TrafficToolSeatRequirement(TrafficToolSeatClass.CLASS_1ST);
        travelRequirementDAO.addTravelRequirement(proposalId, itineraryRequirement.getUUID(), trafficToolRequirement);

        assertNotNull(travelRequirementDAO.getTravelRequirement(proposalId));
        assertNotNull(travelRequirementDAO.getTravelRequirement(itineraryRequirement.getUUID()));
        assertNotNull(travelRequirementDAO.getTravelRequirement(hotelRequirement.getUUID()));
        assertNotNull(travelRequirementDAO.getTravelRequirement(itineraryRequirement2.getUUID()));
        assertNotNull(travelRequirementDAO.getTravelRequirement(hotelRequirement2.getUUID()));
        assertNotNull(travelRequirementDAO.getTravelRequirement(trafficToolRequirement.getUUID()));

        assertNotNull(travelRequirementDAO.getItineraryRequirements(proposalId));
        assertEquals(2, travelRequirementDAO.getItineraryRequirements(proposalId).size());
        assertTrue(travelRequirementDAO.getItineraryRequirements(proposalId).get(0) instanceof ItineraryRequirement);
        assertTrue(travelRequirementDAO.getItineraryRequirements(proposalId).get(1) instanceof ItineraryRequirement);

        assertEquals(5, travelRequirementDAO.getRequirements(proposalId).size());
        assertTrue(travelRequirementDAO.getRequirements(proposalId).get(0) instanceof ItineraryRequirement);
        assertTrue(travelRequirementDAO.getRequirements(proposalId).get(2) instanceof HotelRequirement);
        assertTrue(travelRequirementDAO.getRequirements(proposalId).get(1) instanceof TrafficToolSeatRequirement);
        assertTrue(travelRequirementDAO.getRequirements(proposalId).get(3) instanceof ItineraryRequirement);
        assertTrue(travelRequirementDAO.getRequirements(proposalId).get(4) instanceof HotelRequirement);

        assertNotNull(travelRequirementDAO.getNextItineraryRequirement(proposalId, itineraryRequirement.getUUID()));
        assertNotNull(travelRequirementDAO.getPrevItineraryRequirement(proposalId, itineraryRequirement2.getUUID()));
    }

    @Test
    public void testGetRequirementsWithNullProposal() throws InvalidTravelReqirementException {
        thrown.expect(NullPointerException.class);
        travelRequirementDAO.getRequirements(null);
    }

    @Test
    public void testGetRequirementsWithWrongProposal() throws InvalidTravelReqirementException {
        UUID proposalId = UUID.randomUUID();

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_requirement, proposalId));
        travelRequirementDAO.getRequirements(proposalId);
    }

    @Test
    public void testGetItineraryRequirementsWithNullProposal() throws InvalidTravelReqirementException {
        thrown.expect(NullPointerException.class);
        travelRequirementDAO.getItineraryRequirements(null);
    }

    @Test
    public void testGetItineraryRequirementsWithWrongProposal() throws InvalidTravelReqirementException {
        UUID proposalId = UUID.randomUUID();

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_requirement, proposalId));
        travelRequirementDAO.getItineraryRequirements(proposalId);
    }

    @Test
    public void testGetPrevItineraryRequirementWithNull() throws InvalidTravelReqirementException {
        thrown.expect(NullPointerException.class);
        travelRequirementDAO.getPrevItineraryRequirement(null, UUID.randomUUID());

        thrown.expect(NullPointerException.class);
        travelRequirementDAO.getPrevItineraryRequirement(UUID.randomUUID(), null);
    }

    @Test
    public void testGetPrevItineraryRequirementWithWrongProposal() throws InvalidTravelReqirementException {
        UUID proposalId = UUID.randomUUID();

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_requirement, proposalId));
        travelRequirementDAO.getPrevItineraryRequirement(proposalId, UUID.randomUUID());
    }

    @Test
    public void testGetPrevItineraryRequirementWithWrongRequirement() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createTravelProposal(travelProposal);

        UUID requirementId = UUID.randomUUID();

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_requirement, requirementId));
        travelRequirementDAO.getPrevItineraryRequirement(proposalId, requirementId);
    }

    @Test
    public void testGetTravelRequirementWithNullRequirment() throws InvalidTravelReqirementException {
        thrown.expect(NullPointerException.class);
        travelRequirementDAO.getTravelRequirement(null);
    }

    @Test
    public void testGetTravelRequirementWithWrongRequirment() throws InvalidTravelReqirementException {
        UUID requirementId = UUID.randomUUID();

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_requirement, requirementId));
        travelRequirementDAO.getTravelRequirement(requirementId);
    }

    @Test
    public void testUpdateTravelRequirementWithNull() throws InvalidTravelReqirementException {
        TravelRequirement hotelRequirement = new HotelRequirement(12);

        thrown.expect(NullPointerException.class);
        travelRequirementDAO.updateTravelRequirement(null, hotelRequirement);

        thrown.expect(NullPointerException.class);
        travelRequirementDAO.updateTravelRequirement(UUID.randomUUID(), null);
    }

    @Test
    public void testUpdateTravelRequirementWithTheSame() throws InvalidTravelReqirementException {
        TravelRequirement hotelRequirement = new HotelRequirement(2);

        thrown.expect(IllegalArgumentException.class);
        travelRequirementDAO.updateTravelRequirement(hotelRequirement.getUUID(), hotelRequirement);
    }

    @Test
    public void testUpdateTravelRequirementWithDiffClass() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createTravelProposal(travelProposal);

        TravelRequirement hotelRequirement = new HotelRequirement(12);
        TravelRequirement trafficToolSeatRequirement = new TrafficToolSeatRequirement(TrafficToolSeatClass.CLASS_3RD);

        travelRequirementDAO.addTravelRequirement(proposalId, hotelRequirement);
        travelRequirementDAO.addTravelRequirement(proposalId, trafficToolSeatRequirement);

        thrown.expect(IllegalArgumentException.class);
        travelRequirementDAO.updateTravelRequirement(hotelRequirement.getUUID(), trafficToolSeatRequirement);
    }

    @Test
    public void testUpdateTravelRequirementWithItinerary() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.illegal_update_travel_requirement_operation,
            itineraryRequirement.getUUID()));
        travelRequirementDAO.updateTravelRequirement(UUID.randomUUID(), itineraryRequirement);
    }

    @Test
    public void testUpdateTravelRequirementWithProposal() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.illegal_update_travel_requirement_operation,
            travelProposal.getUUID()));
        travelRequirementDAO.updateTravelRequirement(UUID.randomUUID(), travelProposal);
    }

    @Test
    public void testUpdateTravelRequirementWithWrongRequirement() throws InvalidTravelReqirementException {
        UUID requirementId = UUID.randomUUID();
        TravelRequirement hotelRequirement = new HotelRequirement(2);

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_requirement, requirementId));
        travelRequirementDAO.updateTravelRequirement(requirementId, hotelRequirement);
    }

    @Test
    public void testUpdateTravelRequirementWithProposalId() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createTravelProposal(travelProposal);

        TravelRequirement hotelRequirement = new HotelRequirement(12);

        travelRequirementDAO.addTravelRequirement(proposalId, hotelRequirement);

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.illegal_update_travel_requirement_operation,
            proposalId));
        travelRequirementDAO.updateTravelRequirement(proposalId, hotelRequirement);
    }

    @Test
    public void testUpdateTravelRequirement() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createTravelProposal(travelProposal);
        TravelRequirement hotelRequirement = new HotelRequirement(12);
        travelRequirementDAO.addTravelRequirement(proposalId, hotelRequirement);

        TravelRequirement hotelRequirement2 = new HotelRequirement(15);

        UUID requirementId = travelRequirementDAO.updateTravelRequirement(hotelRequirement.getUUID(), hotelRequirement2);

        assertNotNull(travelRequirementDAO.getTravelRequirement(proposalId));
        assertNotNull(travelRequirementDAO.getTravelRequirement(itineraryRequirement.getUUID()));
        assertNotNull(travelRequirementDAO.getTravelRequirement(requirementId));

        assertNotNull(travelRequirementDAO.getItineraryRequirements(proposalId));
        assertEquals(1, travelRequirementDAO.getItineraryRequirements(proposalId).size());
        assertTrue(travelRequirementDAO.getItineraryRequirements(proposalId).get(0) instanceof ItineraryRequirement);

        assertEquals(2, travelRequirementDAO.getRequirements(proposalId).size());
        assertTrue(travelRequirementDAO.getRequirements(proposalId).get(0) instanceof ItineraryRequirement);
        assertTrue(travelRequirementDAO.getRequirements(proposalId).get(1) instanceof HotelRequirement);

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.not_found_itinerary_requirement, "next",
            proposalId, itineraryRequirement.getUUID()));
        travelRequirementDAO.getNextItineraryRequirement(proposalId, itineraryRequirement.getUUID());
        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.not_found_itinerary_requirement, "previous",
            proposalId, itineraryRequirement.getUUID()));
        travelRequirementDAO.getPrevItineraryRequirement(proposalId, itineraryRequirement.getUUID());

    }

    @Test
    public void testRemoveTravelRequirementWithNullRequirment() throws InvalidTravelReqirementException {
        thrown.expect(NullPointerException.class);
        travelRequirementDAO.removeTravelRequirement(null);
    }

    @Test
    public void testRemoveTravelRequirementWithWrongRequirment() throws InvalidTravelReqirementException {
        UUID requirementId = UUID.randomUUID();

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_requirement, requirementId));
        travelRequirementDAO.removeTravelRequirement(requirementId);
    }

    @After
    public void after() {
        memoImpl.travelProposals.clear();
        memoImpl.travelRequirements.clear();
    }
}
