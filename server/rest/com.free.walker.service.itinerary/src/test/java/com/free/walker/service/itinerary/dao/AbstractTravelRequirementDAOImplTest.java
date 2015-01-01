package com.free.walker.service.itinerary.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.TravelLocation;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.req.HotelRequirement;
import com.free.walker.service.itinerary.req.ItineraryRequirement;
import com.free.walker.service.itinerary.req.TrafficToolSeatRequirement;
import com.free.walker.service.itinerary.req.TravelProposal;
import com.free.walker.service.itinerary.req.TravelRequirement;
import com.free.walker.service.itinerary.util.UuidUtil;
import com.ibm.icu.util.Calendar;

public abstract class AbstractTravelRequirementDAOImplTest {
    protected TravelRequirementDAO travelRequirementDAO;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() {
        ;
    }

    @Test
    public void testCreateTravelProposalWithNullAccount() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelRequirementDAO.createProposal(null, null);
    }

    @Test
    public void testCreateTravelProposalWithNullProposal() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelRequirementDAO.createProposal(UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), null);
    }

    @Test
    public void testCreateTravelProposal() throws InvalidTravelReqirementException, DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        assertNotNull(travelRequirementDAO.getRequirement(proposalId,
            Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL));
        assertNotNull(travelRequirementDAO.getRequirement(itineraryRequirement.getUUID(),
            Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY));

        assertNotNull(travelRequirementDAO.getItineraryRequirements(proposalId));
        assertEquals(1, travelRequirementDAO.getItineraryRequirements(proposalId).size());

        assertNotNull(travelRequirementDAO.getRequirements(proposalId, itineraryRequirement.getUUID()));
        assertEquals(0, travelRequirementDAO.getRequirements(proposalId, itineraryRequirement.getUUID()).size());

        assertEquals(1, travelRequirementDAO.getRequirements(proposalId).size());

        assertNull(travelRequirementDAO.getNextItineraryRequirement(proposalId, itineraryRequirement.getUUID()));
        assertNull(travelRequirementDAO.getPrevItineraryRequirement(proposalId, itineraryRequirement.getUUID()));
    }

    @Test
    public void testAddTravelRequirementWithNullProposal() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);

        thrown.expect(NullPointerException.class);
        travelRequirementDAO.addRequirement(null, itineraryRequirement);
    }

    @Test
    public void testAddTravelRequirementWithNullRequirement() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        thrown.expect(NullPointerException.class);
        travelRequirementDAO.addRequirement(proposalId, null);
    }

    @Test
    public void testAddTravelRequirementWithWrongProposalId() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        travelRequirementDAO.createProposal(UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        TravelLocation destination2 = new TravelLocation(Constants.BARCELONA);
        TravelLocation departure2 = new TravelLocation(Constants.WUHAN);
        ItineraryRequirement itineraryRequirement2 = new ItineraryRequirement(destination2, departure2);

        UUID wrongProposalId = UUID.randomUUID();
        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_proposal, wrongProposalId));
        travelRequirementDAO.addRequirement(wrongProposalId, itineraryRequirement2);
    }

    @Test
    public void testAddTravelRequirementWithDuplicateRequirement() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.existed_travel_requirement,
            itineraryRequirement.getUUID()));
        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement);
    }

    @Test
    public void testAddTravelRequirementWithNewItineraryRequirement() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        TravelLocation destination2 = new TravelLocation(Constants.BARCELONA);
        TravelLocation departure2 = new TravelLocation(Constants.WUHAN);
        ItineraryRequirement itineraryRequirement2 = new ItineraryRequirement(destination2, departure2);

        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement2);

        assertNotNull(travelRequirementDAO.getRequirement(proposalId,
            Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL));
        assertNotNull(travelRequirementDAO.getRequirement(itineraryRequirement.getUUID(),
            Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY));
        assertNotNull(travelRequirementDAO.getRequirement(itineraryRequirement2.getUUID(),
            Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY));

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
    public void testAddTravelRequirementWithNewTravelRequirement() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        TravelRequirement hotelRequirement = new HotelRequirement(12);

        travelRequirementDAO.addRequirement(proposalId, hotelRequirement);

        assertNotNull(travelRequirementDAO.getRequirement(proposalId,
            Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL));
        assertNotNull(travelRequirementDAO.getRequirement(itineraryRequirement.getUUID(),
            Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY));
        assertNotNull(travelRequirementDAO.getRequirement(hotelRequirement.getUUID(),
            Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT));

        assertNotNull(travelRequirementDAO.getItineraryRequirements(proposalId));
        assertEquals(1, travelRequirementDAO.getItineraryRequirements(proposalId).size());
        assertTrue(travelRequirementDAO.getItineraryRequirements(proposalId).get(0) instanceof ItineraryRequirement);

        assertNotNull(travelRequirementDAO.getRequirements(proposalId, itineraryRequirement.getUUID()));
        assertEquals(1, travelRequirementDAO.getRequirements(proposalId, itineraryRequirement.getUUID()).size());

        assertEquals(2, travelRequirementDAO.getRequirements(proposalId).size());
        assertTrue(travelRequirementDAO.getRequirements(proposalId).get(0) instanceof ItineraryRequirement);
        assertTrue(travelRequirementDAO.getRequirements(proposalId).get(1) instanceof HotelRequirement);

        assertNull(travelRequirementDAO.getNextItineraryRequirement(proposalId, itineraryRequirement.getUUID()));
        assertNull(travelRequirementDAO.getPrevItineraryRequirement(proposalId, itineraryRequirement.getUUID()));
    }

    @Test
    public void testInsertTravelRequirementWithNullProposal() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);

        TravelRequirement hotelRequirement = new HotelRequirement(12);

        thrown.expect(NullPointerException.class);
        travelRequirementDAO.addRequirement(null, itineraryRequirement.getUUID(), hotelRequirement);
    }

    @Test
    public void testInsertTravelRequirementWithNullItinerary() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        TravelRequirement hotelRequirement = new HotelRequirement(12);

        thrown.expect(NullPointerException.class);
        travelRequirementDAO.addRequirement(proposalId, null, hotelRequirement);
    }

    @Test
    public void testInsertTravelRequirementWithNullRequirement() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        thrown.expect(NullPointerException.class);
        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement.getUUID(), null);
    }

    @Test
    public void testInsertTravelRequirementWithWrongProposalId() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        travelRequirementDAO.createProposal(UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        TravelRequirement hotelRequirement = new HotelRequirement(12);

        UUID wrongProposalId = UUID.randomUUID();
        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_proposal, wrongProposalId));
        travelRequirementDAO.addRequirement(wrongProposalId, itineraryRequirement.getUUID(), hotelRequirement);
    }

    @Test
    public void testInsertTravelRequirementWithWrongRequirementId() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        TravelRequirement hotelRequirement = new HotelRequirement(12);
        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement.getUUID(), hotelRequirement);

        TravelRequirement trafficToolSeatRequirement = new TrafficToolSeatRequirement(
            Introspection.JSONValues.TRAFFIC_TOOL_SEAT_CLASS_2ND);

        UUID wrongRequirementId = UUID.randomUUID();
        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_itinerary, wrongRequirementId));
        travelRequirementDAO.addRequirement(proposalId, wrongRequirementId, trafficToolSeatRequirement);
    }

    @Test
    public void testInsertTravelRequirementWithDuplicateRequirement() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        TravelRequirement hotelRequirement = new HotelRequirement(12);
        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement.getUUID(), hotelRequirement);

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.existed_travel_requirement,
            hotelRequirement.getUUID()));
        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement.getUUID(), hotelRequirement);
    }

    @Test
    public void testInsertTravelRequirementWithItineraryRequirement() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        TravelLocation destination2 = new TravelLocation(Constants.BARCELONA);
        TravelLocation departure2 = new TravelLocation(Constants.TAIBEI);
        ItineraryRequirement itineraryRequirement2 = new ItineraryRequirement(destination2, departure2);

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.illegal_add_itinerary_as_requirement,
            itineraryRequirement2.getUUID(), itineraryRequirement.getUUID()));
        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement.getUUID(), itineraryRequirement2);
    }

    @Test
    public void testInsertTravelRequirement() throws InvalidTravelReqirementException, DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        TravelRequirement hotelRequirement = new HotelRequirement(12);

        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement.getUUID(), hotelRequirement);

        assertNotNull(travelRequirementDAO.getRequirement(proposalId,
            Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL));
        assertNotNull(travelRequirementDAO.getRequirement(itineraryRequirement.getUUID(),
            Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY));
        assertNotNull(travelRequirementDAO.getRequirement(hotelRequirement.getUUID(),
            Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT));

        assertNotNull(travelRequirementDAO.getItineraryRequirements(proposalId));
        assertEquals(1, travelRequirementDAO.getItineraryRequirements(proposalId).size());
        assertTrue(travelRequirementDAO.getItineraryRequirements(proposalId).get(0) instanceof ItineraryRequirement);

        assertNotNull(travelRequirementDAO.getRequirements(proposalId, itineraryRequirement.getUUID()));
        assertEquals(1, travelRequirementDAO.getRequirements(proposalId, itineraryRequirement.getUUID()).size());

        assertEquals(2, travelRequirementDAO.getRequirements(proposalId).size());
        assertTrue(travelRequirementDAO.getRequirements(proposalId).get(0) instanceof ItineraryRequirement);
        assertTrue(travelRequirementDAO.getRequirements(proposalId).get(1) instanceof HotelRequirement);

        assertNull(travelRequirementDAO.getNextItineraryRequirement(proposalId, itineraryRequirement.getUUID()));
        assertNull(travelRequirementDAO.getPrevItineraryRequirement(proposalId, itineraryRequirement.getUUID()));
    }

    @Test
    public void testInsertTravelRequirementInTheMiddle() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        TravelLocation destination2 = new TravelLocation(Constants.BARCELONA);
        TravelLocation departure2 = new TravelLocation(Constants.GENEVA);
        ItineraryRequirement itineraryRequirement2 = new ItineraryRequirement(destination2, departure2);
        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement2);
        TravelRequirement hotelRequirement = new HotelRequirement(12);
        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement2.getUUID(), hotelRequirement);

        TravelRequirement hotelRequirement2 = new HotelRequirement(3);
        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement.getUUID(), hotelRequirement2);
        TravelRequirement trafficToolRequirement = new TrafficToolSeatRequirement(
            Introspection.JSONValues.TRAFFIC_TOOL_SEAT_CLASS_1ST);
        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement.getUUID(), trafficToolRequirement);

        assertNotNull(travelRequirementDAO.getRequirement(proposalId,
            Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL));
        assertNotNull(travelRequirementDAO.getRequirement(itineraryRequirement.getUUID(),
            Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY));
        assertNotNull(travelRequirementDAO.getRequirement(hotelRequirement.getUUID(),
            Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT));
        assertNotNull(travelRequirementDAO.getRequirement(itineraryRequirement2.getUUID(),
            Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY));
        assertNotNull(travelRequirementDAO.getRequirement(hotelRequirement2.getUUID(),
            Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT));
        assertNotNull(travelRequirementDAO.getRequirement(trafficToolRequirement.getUUID(),
            Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT));

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
    public void testGetRequirementsWithNullProposal() throws InvalidTravelReqirementException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelRequirementDAO.getRequirements(null);
    }

    @Test
    public void testGetRequirementsWithWrongProposal() throws InvalidTravelReqirementException, DatabaseAccessException {
        UUID proposalId = UUID.randomUUID();

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_proposal, proposalId));
        travelRequirementDAO.getRequirements(proposalId);
    }

    @Test
    public void testGetItineraryRequirementsWithNullProposal() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelRequirementDAO.getItineraryRequirements(null);
    }

    @Test
    public void testGetItineraryRequirementsWithWrongProposal() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        UUID wrongProposalId = UUID.randomUUID();

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_proposal, wrongProposalId));
        travelRequirementDAO.getItineraryRequirements(wrongProposalId);
    }

    @Test
    public void testGetRequirementsByItineraryWithNullProposal() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelRequirementDAO.getRequirements(null, UUID.randomUUID());
    }

    @Test
    public void testGetRequirementsByItineraryWithNullItinerary() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelRequirementDAO.getRequirements(UUID.randomUUID(), null);
    }

    @Test
    public void testGetRequirementsByItineraryWithWrongProposal() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        UUID proposalId = UUID.randomUUID();

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_proposal, proposalId));
        travelRequirementDAO.getRequirements(proposalId, UUID.randomUUID());
    }

    @Test
    public void testGetRequirementsByItineraryWithWrongItinerary() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        UUID itineraryId = UUID.randomUUID();

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_itinerary, itineraryId));
        travelRequirementDAO.getRequirements(proposalId, itineraryId);
    }

    @Test
    public void testGetPrevItineraryRequirementWithNull() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelRequirementDAO.getPrevItineraryRequirement(null, UUID.randomUUID());

        thrown.expect(NullPointerException.class);
        travelRequirementDAO.getPrevItineraryRequirement(UUID.randomUUID(), null);
    }

    @Test
    public void testGetPrevItineraryRequirementWithWrongProposal() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        UUID proposalId = UUID.randomUUID();

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_proposal, proposalId));
        travelRequirementDAO.getPrevItineraryRequirement(proposalId, UUID.randomUUID());
    }

    @Test
    public void testGetPrevItineraryRequirementWithWrongRequirement() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        UUID requirementId = UUID.randomUUID();

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_requirement, requirementId));
        travelRequirementDAO.getPrevItineraryRequirement(proposalId, requirementId);
    }

    @Test
    public void testGetNextItineraryRequirementWithNull() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelRequirementDAO.getPrevItineraryRequirement(null, UUID.randomUUID());

        thrown.expect(NullPointerException.class);
        travelRequirementDAO.getNextItineraryRequirement(UUID.randomUUID(), null);
    }

    @Test
    public void testGetNextItineraryRequirementWithWrongProposal() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        UUID proposalId = UUID.randomUUID();

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_proposal, proposalId));
        travelRequirementDAO.getNextItineraryRequirement(proposalId, UUID.randomUUID());
    }

    @Test
    public void testGetNextItineraryRequirementWithWrongRequirement() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        UUID requirementId = UUID.randomUUID();

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_requirement, requirementId));
        travelRequirementDAO.getNextItineraryRequirement(proposalId, requirementId);
    }

    @Test
    public void testGetTravelRequirementWithNullRequirment() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelRequirementDAO.getRequirement(null, null);
    }

    @Test
    public void testGetTravelRequirementWithWrongRequirment() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        UUID requirementId = UUID.randomUUID();

        assertNull(travelRequirementDAO.getRequirement(requirementId, null));
    }

    @Test
    public void testUpdateTravelRequirementWithNull() throws InvalidTravelReqirementException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelRequirementDAO.updateRequirement(null);
    }

    @Test
    public void testUpdateTravelRequirementWithItinerary() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.illegal_update_travel_requirement_operation,
            itineraryRequirement.getUUID()));
        travelRequirementDAO.updateRequirement(itineraryRequirement);
    }

    @Test
    public void testUpdateTravelRequirementWithProposal() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.illegal_update_travel_requirement_operation,
            travelProposal.getUUID()));
        travelRequirementDAO.updateRequirement(travelProposal);
    }

    @Test
    public void testUpdateTravelRequirementWithWrongRequirement() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        TravelRequirement hotelRequirement = new HotelRequirement(2);

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_requirement, hotelRequirement
            .getUUID().toString()));
        travelRequirementDAO.updateRequirement(hotelRequirement);
    }

    @Test
    public void testUpdateTravelRequirement() throws InvalidTravelReqirementException, DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);
        TravelRequirement hotelRequirement = new HotelRequirement(12);
        travelRequirementDAO.addRequirement(proposalId, hotelRequirement);

        UUID requirementId = travelRequirementDAO.updateRequirement(hotelRequirement);

        assertNotNull(travelRequirementDAO.getRequirement(proposalId,
            Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL));
        assertNotNull(travelRequirementDAO.getRequirement(itineraryRequirement.getUUID(),
            Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY));
        assertNotNull(travelRequirementDAO.getRequirement(requirementId,
            Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT));

        assertNotNull(travelRequirementDAO.getItineraryRequirements(proposalId));
        assertEquals(1, travelRequirementDAO.getItineraryRequirements(proposalId).size());
        assertTrue(travelRequirementDAO.getItineraryRequirements(proposalId).get(0) instanceof ItineraryRequirement);

        assertNotNull(travelRequirementDAO.getRequirements(proposalId, itineraryRequirement.getUUID()));
        assertEquals(1, travelRequirementDAO.getRequirements(proposalId, itineraryRequirement.getUUID()).size());

        assertEquals(2, travelRequirementDAO.getRequirements(proposalId).size());
        assertTrue(travelRequirementDAO.getRequirements(proposalId).get(0) instanceof ItineraryRequirement);
        assertTrue(travelRequirementDAO.getRequirements(proposalId).get(1) instanceof HotelRequirement);

        assertNull(travelRequirementDAO.getNextItineraryRequirement(proposalId, itineraryRequirement.getUUID()));
        assertNull(travelRequirementDAO.getPrevItineraryRequirement(proposalId, itineraryRequirement.getUUID()));
    }

    @Test
    public void testRemoveTravelRequirementWithNullProposal() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelRequirementDAO.removeRequirement(null, null);
    }

    @Test
    public void testRemoveTravelRequirementWithWrongProposal() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        UUID wrongProposalId = UUID.randomUUID();
        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_proposal, wrongProposalId));
        travelRequirementDAO.removeRequirement(wrongProposalId, UUID.randomUUID());
    }

    @Test
    public void testRemoveTravelRequirementWithNullRequirment() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        thrown.expect(NullPointerException.class);
        travelRequirementDAO.removeRequirement(proposalId, null);
    }

    @Test
    public void testRemoveTravelRequirementWithWrongRequirment() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        UUID wrongRequirementId = UUID.randomUUID();

        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        TravelLocation destination2 = new TravelLocation(Constants.BARCELONA);
        TravelLocation departure2 = new TravelLocation(Constants.TAIBEI);
        ItineraryRequirement itineraryRequirement2 = new ItineraryRequirement(destination2, departure2);
        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement2);

        assertNull(travelRequirementDAO.removeRequirement(proposalId, wrongRequirementId));
    }

    @Test
    public void testRemoveTravelRequirementWithProposal() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.illegal_delete_travel_requirement_operation,
            proposalId));
        travelRequirementDAO.removeRequirement(proposalId, proposalId);
    }

    @Test
    public void testRemoveTravelRequirementLastItinerary() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.illegal_delete_travel_requirement_operation,
            proposalId));
        travelRequirementDAO.removeRequirement(proposalId, itineraryRequirement.getUUID());
    }

    @Test
    public void testRemoveTravelRequirement() throws InvalidTravelReqirementException, DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        TravelLocation destination2 = new TravelLocation(Constants.BARCELONA);
        TravelLocation departure2 = new TravelLocation(Constants.TAIBEI);
        ItineraryRequirement itineraryRequirement2 = new ItineraryRequirement(destination2, departure2);
        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement2);

        TravelRequirement hotelRequirement = new HotelRequirement(12);

        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement.getUUID(), hotelRequirement);
        travelRequirementDAO.removeRequirement(proposalId, hotelRequirement.getUUID());

        assertNotNull(travelRequirementDAO.getRequirement(proposalId,
            Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL));
        assertNotNull(travelRequirementDAO.getRequirement(itineraryRequirement.getUUID(),
            Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY));
        assertNotNull(travelRequirementDAO.getRequirement(itineraryRequirement2.getUUID(),
            Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY));
        assertNull(travelRequirementDAO.getRequirement(hotelRequirement.getUUID(),
            Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT));

        assertNotNull(travelRequirementDAO.getItineraryRequirements(proposalId));
        assertEquals(2, travelRequirementDAO.getItineraryRequirements(proposalId).size());
        assertTrue(travelRequirementDAO.getItineraryRequirements(proposalId).get(0) instanceof ItineraryRequirement);
        assertTrue(travelRequirementDAO.getItineraryRequirements(proposalId).get(1) instanceof ItineraryRequirement);

        assertNotNull(travelRequirementDAO.getRequirements(proposalId, itineraryRequirement.getUUID()));
        assertEquals(0, travelRequirementDAO.getRequirements(proposalId, itineraryRequirement.getUUID()).size());

        assertEquals(2, travelRequirementDAO.getRequirements(proposalId).size());
        assertTrue(travelRequirementDAO.getRequirements(proposalId).get(0) instanceof ItineraryRequirement);
        assertTrue(travelRequirementDAO.getRequirements(proposalId).get(1) instanceof ItineraryRequirement);

        assertNotNull(travelRequirementDAO.getNextItineraryRequirement(proposalId, itineraryRequirement.getUUID()));
        assertNotNull(travelRequirementDAO.getPrevItineraryRequirement(proposalId, itineraryRequirement2.getUUID()));
    }

    @Test
    public void testRemoveTravelItinerary() throws InvalidTravelReqirementException, DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        TravelRequirement hotelRequirement = new HotelRequirement(12);
        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement.getUUID(), hotelRequirement);

        TravelLocation destination2 = new TravelLocation(Constants.BARCELONA);
        TravelLocation departure2 = new TravelLocation(Constants.WUHAN);
        ItineraryRequirement itineraryRequirement2 = new ItineraryRequirement(destination2, departure2);

        travelRequirementDAO.addRequirement(proposalId, itineraryRequirement2);
        travelRequirementDAO.removeRequirement(proposalId, itineraryRequirement.getUUID());

        assertNotNull(travelRequirementDAO.getRequirement(proposalId,
            Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL));
        assertNull(travelRequirementDAO.getRequirement(itineraryRequirement.getUUID(),
            Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY));
        assertNotNull(travelRequirementDAO.getRequirement(itineraryRequirement2.getUUID(),
            Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY));
        assertNull(travelRequirementDAO.getRequirement(hotelRequirement.getUUID(),
            Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT));

        assertNotNull(travelRequirementDAO.getItineraryRequirements(proposalId));
        assertEquals(1, travelRequirementDAO.getItineraryRequirements(proposalId).size());
        assertTrue(travelRequirementDAO.getItineraryRequirements(proposalId).get(0) instanceof ItineraryRequirement);

        assertNotNull(travelRequirementDAO.getRequirements(proposalId, itineraryRequirement2.getUUID()));
        assertEquals(0, travelRequirementDAO.getRequirements(proposalId, itineraryRequirement2.getUUID()).size());

        assertEquals(1, travelRequirementDAO.getRequirements(proposalId).size());
        assertTrue(travelRequirementDAO.getRequirements(proposalId).get(0) instanceof ItineraryRequirement);

        assertNull(travelRequirementDAO.getNextItineraryRequirement(proposalId, itineraryRequirement2.getUUID()));
        assertNull(travelRequirementDAO.getPrevItineraryRequirement(proposalId, itineraryRequirement2.getUUID()));
    }

    @Test
    public void testStartProposalBidWithNullProposal() throws InvalidTravelReqirementException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelRequirementDAO.startProposalBid(null, null);
    }

    @Test
    public void testStartProposalBidWithNullAccount() throws InvalidTravelReqirementException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelRequirementDAO.startProposalBid(UUID.randomUUID(), null);
    }

    @Test
    public void testStartProposalBidWithWrongProposal() throws InvalidTravelReqirementException, DatabaseAccessException {
        UUID wrongProposalId = UUID.randomUUID();
        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_proposal, wrongProposalId));
        travelRequirementDAO.startProposalBid(wrongProposalId,
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()));
    }

    @Test
    public void testStartProposalBid() throws InvalidTravelReqirementException, DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        assertEquals(proposalId, travelRequirementDAO.startProposalBid(proposalId,
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid())));
    }

    @Test
    public void testStartProposalBidWithDifferentAccount() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        travelRequirementDAO.startProposalBid(proposalId, UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()));

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.illegal_submit_proposal_operation, proposalId,
            Constants.ADMIN_ACCOUNT.getUuid()));
        travelRequirementDAO.startProposalBid(proposalId, UuidUtil.fromUuidStr(Constants.ADMIN_ACCOUNT.getUuid()));
    }

    @Test
    public void testJoinProposalBidWithNullProposal() throws InvalidTravelReqirementException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelRequirementDAO.joinProposalBid(null, null);
    }

    @Test
    public void testJoinProposalBidWithNullAgency() throws InvalidTravelReqirementException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelRequirementDAO.joinProposalBid(UUID.randomUUID(), null);
    }

    @Test
    public void testJoinProposalBidWrongProposal() throws InvalidTravelReqirementException, DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_proposal_bidding, proposalId));
        travelRequirementDAO.joinProposalBid(proposalId, UUID.randomUUID());
    }

    @Test
    public void testJoinProposalBid() throws InvalidTravelReqirementException, DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        travelRequirementDAO.startProposalBid(proposalId, UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()));

        UUID agencyId = UUID.randomUUID();
        assertEquals(agencyId, travelRequirementDAO.joinProposalBid(proposalId, agencyId));
    }

    @Test
    public void testJoinProposalBid4AnotherAgency() throws InvalidTravelReqirementException, DatabaseAccessException {
        TravelLocation destination = new TravelLocation(Constants.TAIBEI);
        TravelLocation departure = new TravelLocation(Constants.BARCELONA);
        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);

        UUID proposalId = travelRequirementDAO.createProposal(
            UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);

        travelRequirementDAO.startProposalBid(proposalId, UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()));

        UUID agencyId1 = UUID.randomUUID();
        assertEquals(agencyId1, travelRequirementDAO.joinProposalBid(proposalId, agencyId1));

        UUID agencyId2 = UUID.randomUUID();
        assertEquals(agencyId2, travelRequirementDAO.joinProposalBid(proposalId, agencyId2));
    }

    @Test
    public void testGetProposalsByAccountIdWithNullAccount() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelRequirementDAO.getTravelProposalsByAccount(null, Calendar.getInstance(), 1);
    }

    @Test
    public void testGetProposalsByAccountId() throws InvalidTravelReqirementException, DatabaseAccessException {
        UUID proposalId1 = null;
        {            
            TravelLocation destination = new TravelLocation(Constants.TAIBEI);
            TravelLocation departure = new TravelLocation(Constants.BARCELONA);
            ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
            TravelProposal travelProposal = new TravelProposal(itineraryRequirement);
            
            proposalId1 = travelRequirementDAO.createProposal(
                UuidUtil.fromUuidStr(Constants.ADMIN_ACCOUNT.getUuid()), travelProposal);
            travelRequirementDAO.startProposalBid(proposalId1,
                UuidUtil.fromUuidStr(Constants.ADMIN_ACCOUNT.getUuid()));
        }

        UUID proposalId2 = null;
        {            
            TravelLocation destination = new TravelLocation(Constants.TAIBEI);
            TravelLocation departure = new TravelLocation(Constants.BARCELONA);
            ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
            TravelProposal travelProposal = new TravelProposal(itineraryRequirement);
            
            proposalId2 = travelRequirementDAO.createProposal(
                UuidUtil.fromUuidStr(Constants.ADMIN_ACCOUNT.getUuid()), travelProposal);
            travelRequirementDAO.startProposalBid(proposalId2,
                UuidUtil.fromUuidStr(Constants.ADMIN_ACCOUNT.getUuid()));
        }

        Calendar since = Calendar.getInstance();
        since.add(Calendar.SECOND, -5);

        List<TravelProposal> travelProposals = travelRequirementDAO.getTravelProposalsByAccount(
            UuidUtil.fromUuidStr(Constants.ADMIN_ACCOUNT.getUuid()), since, 0);
        assertNotNull(travelProposals);
        assertEquals(2, travelProposals.size());
    }

    @Test
    public void testGetProposalsByAgencyIdWithNullAgency() throws InvalidTravelReqirementException,
        DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelRequirementDAO.getTravelProposalsByAgency(null, Calendar.getInstance(), 1);
    }

    @Test
    public void testGetProposalsByAgencyId() throws InvalidTravelReqirementException, DatabaseAccessException {
        UUID proposalId1 = null;
        {
            TravelLocation destination = new TravelLocation(Constants.TAIBEI);
            TravelLocation departure = new TravelLocation(Constants.BARCELONA);
            ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
            TravelProposal travelProposal = new TravelProposal(itineraryRequirement);
            
            proposalId1 = travelRequirementDAO.createProposal(
                UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);
            travelRequirementDAO.startProposalBid(proposalId1,
                UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()));
        }

        UUID proposalId2 = null;
        {            
            TravelLocation destination = new TravelLocation(Constants.TAIBEI);
            TravelLocation departure = new TravelLocation(Constants.BARCELONA);
            ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
            TravelProposal travelProposal = new TravelProposal(itineraryRequirement);
            
            proposalId2 = travelRequirementDAO.createProposal(
                UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()), travelProposal);
            travelRequirementDAO.startProposalBid(proposalId2,
                UuidUtil.fromUuidStr(Constants.DEFAULT_ACCOUNT.getUuid()));
        }

        UUID agencyId1 = UUID.randomUUID();
        travelRequirementDAO.joinProposalBid(proposalId1, agencyId1);
        travelRequirementDAO.joinProposalBid(proposalId2, agencyId1);

        UUID agencyId2 = UUID.randomUUID();
        travelRequirementDAO.joinProposalBid(proposalId2, agencyId2);

        Calendar since = Calendar.getInstance();
        since.add(Calendar.DATE, -1);

        List<TravelProposal> travelProposals4Agency1 = travelRequirementDAO.getTravelProposalsByAgency(
            agencyId1, since, 1);
        List<TravelProposal> travelProposals4Agency2 = travelRequirementDAO.getTravelProposalsByAgency(
            agencyId2, since, 1);

        assertNotNull(travelProposals4Agency1);
        assertEquals(2, travelProposals4Agency1.size());
        if (proposalId1.toString().equals(travelProposals4Agency1.get(0).getUUID().toString())) {
            assertEquals(proposalId1.toString(), travelProposals4Agency1.get(0).getUUID().toString());
            assertEquals(proposalId2.toString(), travelProposals4Agency1.get(1).getUUID().toString());
        } else {            
            assertEquals(proposalId1.toString(), travelProposals4Agency1.get(1).getUUID().toString());
            assertEquals(proposalId2.toString(), travelProposals4Agency1.get(0).getUUID().toString());
        }

        assertNotNull(travelProposals4Agency2);
        assertEquals(1, travelProposals4Agency2.size());
        assertEquals(proposalId2.toString(), travelProposals4Agency2.get(0).getUUID().toString());
    }

    @After
    public void after() {
        ;
    }
}
