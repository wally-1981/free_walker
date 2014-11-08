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
import com.free.walker.service.itinerary.basic.City;
import com.free.walker.service.itinerary.basic.Introspection;
import com.free.walker.service.itinerary.basic.TravelLocation;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.req.HotelRequirement;
import com.free.walker.service.itinerary.req.ItineraryRequirement;
import com.free.walker.service.itinerary.req.TrafficToolSeatRequirement;
import com.free.walker.service.itinerary.req.TravelProposal;
import com.free.walker.service.itinerary.req.TravelRequirement;

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
        travelRequirementDAO.createProposal(null);
    }

    @Test
    public void testCreateTravelProposal() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(travelProposal);

        assertNotNull(travelRequirementDAO.getRequirement(proposalId));
        assertNotNull(travelRequirementDAO.getRequirement(itineraryRequirement.getUUID()));

        assertNotNull(travelRequirementDAO.getItineraryRequirements(proposalId));
        assertEquals(1, travelRequirementDAO.getItineraryRequirements(proposalId).size());

        assertNotNull(travelRequirementDAO.getRequirements(proposalId, itineraryRequirement.getUUID()));
        assertEquals(0, travelRequirementDAO.getRequirements(proposalId, itineraryRequirement.getUUID()).size());

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
        travelRequirementDAO.addRequirement(null, itineraryRequirement);
    }

    @Test
    public void testAddTravelRequirementWithNullRequirement() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(travelProposal);

        thrown.expect(NullPointerException.class);
        travelRequirementDAO.addRequirement(proposalId, null);
    }

    @Test
    public void testAddTravelRequirementWithWrongProposalId() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        travelRequirementDAO.createProposal(travelProposal);

        TravelLocation destination2 = new TravelLocation(City.BOSTON);
        TravelLocation departure2 = new TravelLocation(City.LONDON);
        ItineraryRequirement itineraryRequirement2 = new ItineraryRequirement(destination2, departure2);

        UUID wrongProposalId = UUID.randomUUID();
        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_requirement, wrongProposalId));
        travelRequirementDAO.addRequirement(wrongProposalId, itineraryRequirement2);
    }

    @Test
    public void testAddTravelRequirementWithDuplicateRequirement() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(travelProposal);

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.existed_travel_requirement,
            itineraryRequirement.getUUID()));
        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement);
    }

    @Test
    public void testAddTravelRequirementWithNewItineraryRequirement() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(travelProposal);

        TravelLocation destination2 = new TravelLocation(City.BOSTON);
        TravelLocation departure2 = new TravelLocation(City.LONDON);
        ItineraryRequirement itineraryRequirement2 = new ItineraryRequirement(destination2, departure2);

        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement2);

        assertNotNull(travelRequirementDAO.getRequirement(proposalId));
        assertNotNull(travelRequirementDAO.getRequirement(itineraryRequirement.getUUID()));
        assertNotNull(travelRequirementDAO.getRequirement(itineraryRequirement2.getUUID()));

        assertNotNull(travelRequirementDAO.getItineraryRequirements(proposalId));
        assertEquals(2, travelRequirementDAO.getItineraryRequirements(proposalId).size());
        assertTrue(travelRequirementDAO.getItineraryRequirements(proposalId).get(0) instanceof ItineraryRequirement);
        assertTrue(travelRequirementDAO.getItineraryRequirements(proposalId).get(1) instanceof ItineraryRequirement);

        assertNotNull(travelRequirementDAO.getRequirements(proposalId, itineraryRequirement.getUUID()));
        assertEquals(0, travelRequirementDAO.getRequirements(proposalId, itineraryRequirement.getUUID()).size());
        assertNotNull(travelRequirementDAO.getRequirements(proposalId, itineraryRequirement2.getUUID()));
        assertEquals(0, travelRequirementDAO.getRequirements(proposalId, itineraryRequirement2.getUUID()).size());

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

        UUID proposalId = travelRequirementDAO.createProposal(travelProposal);

        TravelRequirement hotelRequirement = new HotelRequirement(12);

        travelRequirementDAO.addRequirement(proposalId, hotelRequirement);

        assertNotNull(travelRequirementDAO.getRequirement(proposalId));
        assertNotNull(travelRequirementDAO.getRequirement(itineraryRequirement.getUUID()));
        assertNotNull(travelRequirementDAO.getRequirement(hotelRequirement.getUUID()));

        assertNotNull(travelRequirementDAO.getItineraryRequirements(proposalId));
        assertEquals(1, travelRequirementDAO.getItineraryRequirements(proposalId).size());
        assertTrue(travelRequirementDAO.getItineraryRequirements(proposalId).get(0) instanceof ItineraryRequirement);

        assertNotNull(travelRequirementDAO.getRequirements(proposalId, itineraryRequirement.getUUID()));
        assertEquals(1, travelRequirementDAO.getRequirements(proposalId, itineraryRequirement.getUUID()).size());

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
        travelRequirementDAO.addRequirement(null, itineraryRequirement.getUUID(), hotelRequirement);
    }

    @Test
    public void testInsertTravelRequirementWithNullItinerary() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(travelProposal);

        TravelRequirement hotelRequirement = new HotelRequirement(12);

        thrown.expect(NullPointerException.class);
        travelRequirementDAO.addRequirement(proposalId, null, hotelRequirement);
    }

    @Test
    public void testInsertTravelRequirementWithNullRequirement() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(travelProposal);

        thrown.expect(NullPointerException.class);
        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement.getUUID(), null);
    }

    @Test
    public void testInsertTravelRequirementWithWrongProposalId() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        travelRequirementDAO.createProposal(travelProposal);

        TravelRequirement hotelRequirement = new HotelRequirement(12);

        UUID wrongProposalId = UUID.randomUUID();
        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_requirement, wrongProposalId));
        travelRequirementDAO.addRequirement(wrongProposalId, itineraryRequirement.getUUID(), hotelRequirement);
    }

    @Test
    public void testInsertTravelRequirementWithWrongRequirementId() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(travelProposal);

        TravelRequirement hotelRequirement = new HotelRequirement(12);
        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement.getUUID(), hotelRequirement);

        TravelRequirement trafficToolSeatRequirement = new TrafficToolSeatRequirement(
            Introspection.JSONValues.CLASS_2ND);

        UUID wrongRequirementId = UUID.randomUUID();
        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_itinerary_requirement,
            wrongRequirementId));
        travelRequirementDAO.addRequirement(proposalId, wrongRequirementId, trafficToolSeatRequirement);
    }

    @Test
    public void testInsertTravelRequirementWithDuplicateRequirement() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(travelProposal);

        TravelRequirement hotelRequirement = new HotelRequirement(12);
        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement.getUUID(), hotelRequirement);

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.existed_travel_requirement,
            hotelRequirement.getUUID()));
        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement.getUUID(), hotelRequirement);
    }

    @Test
    public void testInsertTravelRequirementWithItineraryRequirement() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(travelProposal);

        TravelLocation destination2 = new TravelLocation(City.BOSTON);
        TravelLocation departure2 = new TravelLocation(City.BEIJING);
        ItineraryRequirement itineraryRequirement2 = new ItineraryRequirement(destination2, departure2);

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.illegal_add_travel_requirement_operation,
            itineraryRequirement.getUUID(), itineraryRequirement2.getUUID()));
        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement.getUUID(), itineraryRequirement2);
    }

    @Test
    public void testInsertTravelRequirement() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(travelProposal);

        TravelRequirement hotelRequirement = new HotelRequirement(12);

        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement.getUUID(), hotelRequirement);

        assertNotNull(travelRequirementDAO.getRequirement(proposalId));
        assertNotNull(travelRequirementDAO.getRequirement(itineraryRequirement.getUUID()));
        assertNotNull(travelRequirementDAO.getRequirement(hotelRequirement.getUUID()));

        assertNotNull(travelRequirementDAO.getItineraryRequirements(proposalId));
        assertEquals(1, travelRequirementDAO.getItineraryRequirements(proposalId).size());
        assertTrue(travelRequirementDAO.getItineraryRequirements(proposalId).get(0) instanceof ItineraryRequirement);

        assertNotNull(travelRequirementDAO.getRequirements(proposalId, itineraryRequirement.getUUID()));
        assertEquals(1, travelRequirementDAO.getRequirements(proposalId, itineraryRequirement.getUUID()).size());

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

        UUID proposalId = travelRequirementDAO.createProposal(travelProposal);

        TravelLocation destination2 = new TravelLocation(City.BOSTON);
        TravelLocation departure2 = new TravelLocation(City.LA);
        ItineraryRequirement itineraryRequirement2 = new ItineraryRequirement(destination2, departure2);
        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement2);
        TravelRequirement hotelRequirement = new HotelRequirement(12);
        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement2.getUUID(), hotelRequirement);

        TravelRequirement hotelRequirement2 = new HotelRequirement(3);
        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement.getUUID(), hotelRequirement2);
        TravelRequirement trafficToolRequirement = new TrafficToolSeatRequirement(Introspection.JSONValues.CLASS_1ST);
        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement.getUUID(), trafficToolRequirement);

        assertNotNull(travelRequirementDAO.getRequirement(proposalId));
        assertNotNull(travelRequirementDAO.getRequirement(itineraryRequirement.getUUID()));
        assertNotNull(travelRequirementDAO.getRequirement(hotelRequirement.getUUID()));
        assertNotNull(travelRequirementDAO.getRequirement(itineraryRequirement2.getUUID()));
        assertNotNull(travelRequirementDAO.getRequirement(hotelRequirement2.getUUID()));
        assertNotNull(travelRequirementDAO.getRequirement(trafficToolRequirement.getUUID()));

        assertNotNull(travelRequirementDAO.getItineraryRequirements(proposalId));
        assertEquals(2, travelRequirementDAO.getItineraryRequirements(proposalId).size());
        assertTrue(travelRequirementDAO.getItineraryRequirements(proposalId).get(0) instanceof ItineraryRequirement);
        assertTrue(travelRequirementDAO.getItineraryRequirements(proposalId).get(1) instanceof ItineraryRequirement);

        assertNotNull(travelRequirementDAO.getRequirements(proposalId, itineraryRequirement.getUUID()));
        assertEquals(2, travelRequirementDAO.getRequirements(proposalId, itineraryRequirement.getUUID()).size());
        assertNotNull(travelRequirementDAO.getRequirements(proposalId, itineraryRequirement2.getUUID()));
        assertEquals(1, travelRequirementDAO.getRequirements(proposalId, itineraryRequirement2.getUUID()).size());

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
    public void testGetRequirementsByItineraryWithNullProposal() throws InvalidTravelReqirementException {
        thrown.expect(NullPointerException.class);
        travelRequirementDAO.getRequirements(null, UUID.randomUUID());
    }

    @Test
    public void testGetRequirementsByItineraryWithNullItinerary() throws InvalidTravelReqirementException {
        thrown.expect(NullPointerException.class);
        travelRequirementDAO.getRequirements(UUID.randomUUID(), null);
    }

    @Test
    public void testGetRequirementsByItineraryWithWrongProposal() throws InvalidTravelReqirementException {
        UUID proposalId = UUID.randomUUID();

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_requirement, proposalId));
        travelRequirementDAO.getRequirements(proposalId, UUID.randomUUID());
    }

    @Test
    public void testGetRequirementsByItineraryWithWrongItinerary() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(travelProposal);

        UUID itineraryId = UUID.randomUUID();

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_itinerary_requirement, itineraryId));
        travelRequirementDAO.getRequirements(proposalId, itineraryId);
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

        UUID proposalId = travelRequirementDAO.createProposal(travelProposal);

        UUID requirementId = UUID.randomUUID();

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_requirement, requirementId));
        travelRequirementDAO.getPrevItineraryRequirement(proposalId, requirementId);
    }

    @Test
    public void testGetTravelRequirementWithNullRequirment() throws InvalidTravelReqirementException {
        thrown.expect(NullPointerException.class);
        travelRequirementDAO.getRequirement(null);
    }

    @Test
    public void testGetTravelRequirementWithWrongRequirment() throws InvalidTravelReqirementException {
        UUID requirementId = UUID.randomUUID();

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_requirement, requirementId));
        travelRequirementDAO.getRequirement(requirementId);
    }

    @Test
    public void testUpdateTravelRequirementWithNull() throws InvalidTravelReqirementException {
        TravelRequirement hotelRequirement = new HotelRequirement(12);

        thrown.expect(NullPointerException.class);
        travelRequirementDAO.updateRequirement(null, hotelRequirement);

        thrown.expect(NullPointerException.class);
        travelRequirementDAO.updateRequirement(UUID.randomUUID(), null);
    }

    @Test
    public void testUpdateTravelRequirementWithTheSame() throws InvalidTravelReqirementException {
        TravelRequirement hotelRequirement = new HotelRequirement(2);

        thrown.expect(IllegalArgumentException.class);
        travelRequirementDAO.updateRequirement(hotelRequirement.getUUID(), hotelRequirement);
    }

    @Test
    public void testUpdateTravelRequirementWithDiffClass() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(travelProposal);

        TravelRequirement hotelRequirement = new HotelRequirement(12);
        TravelRequirement trafficToolSeatRequirement = new TrafficToolSeatRequirement(
            Introspection.JSONValues.CLASS_3RD);

        travelRequirementDAO.addRequirement(proposalId, hotelRequirement);
        travelRequirementDAO.addRequirement(proposalId, trafficToolSeatRequirement);

        thrown.expect(IllegalArgumentException.class);
        travelRequirementDAO.updateRequirement(hotelRequirement.getUUID(), trafficToolSeatRequirement);
    }

    @Test
    public void testUpdateTravelRequirementWithItinerary() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.illegal_update_travel_requirement_operation,
            itineraryRequirement.getUUID()));
        travelRequirementDAO.updateRequirement(UUID.randomUUID(), itineraryRequirement);
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
        travelRequirementDAO.updateRequirement(UUID.randomUUID(), travelProposal);
    }

    @Test
    public void testUpdateTravelRequirementWithWrongRequirement() throws InvalidTravelReqirementException {
        UUID requirementId = UUID.randomUUID();
        TravelRequirement hotelRequirement = new HotelRequirement(2);

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_requirement, requirementId));
        travelRequirementDAO.updateRequirement(requirementId, hotelRequirement);
    }

    @Test
    public void testUpdateTravelRequirementWithProposalId() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(travelProposal);

        TravelRequirement hotelRequirement = new HotelRequirement(12);

        travelRequirementDAO.addRequirement(proposalId, hotelRequirement);

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.illegal_update_travel_requirement_operation,
            proposalId));
        travelRequirementDAO.updateRequirement(proposalId, hotelRequirement);
    }

    @Test
    public void testUpdateTravelRequirement() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(travelProposal);
        TravelRequirement hotelRequirement = new HotelRequirement(12);
        travelRequirementDAO.addRequirement(proposalId, hotelRequirement);

        TravelRequirement hotelRequirement2 = new HotelRequirement(15);

        UUID requirementId = travelRequirementDAO.updateRequirement(hotelRequirement.getUUID(), hotelRequirement2);

        assertNotNull(travelRequirementDAO.getRequirement(proposalId));
        assertNotNull(travelRequirementDAO.getRequirement(itineraryRequirement.getUUID()));
        assertNotNull(travelRequirementDAO.getRequirement(requirementId));

        assertNotNull(travelRequirementDAO.getItineraryRequirements(proposalId));
        assertEquals(1, travelRequirementDAO.getItineraryRequirements(proposalId).size());
        assertTrue(travelRequirementDAO.getItineraryRequirements(proposalId).get(0) instanceof ItineraryRequirement);

        assertNotNull(travelRequirementDAO.getRequirements(proposalId, itineraryRequirement.getUUID()));
        assertEquals(1, travelRequirementDAO.getRequirements(proposalId, itineraryRequirement.getUUID()).size());

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
        travelRequirementDAO.removeRequirement(null);
    }

    @Test
    public void testRemoveTravelRequirementWithWrongRequirment() throws InvalidTravelReqirementException {
        UUID requirementId = UUID.randomUUID();

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_requirement, requirementId));
        travelRequirementDAO.removeRequirement(requirementId);
    }

    @Test
    public void testRemoveTravelRequirementWithProposal() throws InvalidTravelReqirementException {
        TravelLocation destination = new TravelLocation(City.BEIJING);
        TravelLocation departure = new TravelLocation(City.BOSTON);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(travelProposal);

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.illegal_delete_travel_requirement_operation,
            proposalId));
        travelRequirementDAO.removeRequirement(proposalId);
    }

    @After
    public void after() {
        memoImpl.travelProposals.clear();
        memoImpl.travelRequirements.clear();
    }
}
