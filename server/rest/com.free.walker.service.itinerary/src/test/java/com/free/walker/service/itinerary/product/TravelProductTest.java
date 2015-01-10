package com.free.walker.service.itinerary.product;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.basic.Flight;
import com.free.walker.service.itinerary.basic.Hotel;
import com.free.walker.service.itinerary.basic.TravelLocation;
import com.free.walker.service.itinerary.infra.PlatformInitializer;
import com.free.walker.service.itinerary.req.ItineraryRequirement;
import com.free.walker.service.itinerary.req.TravelProposal;
import com.ibm.icu.util.Calendar;

public class TravelProductTest {
    @Before
    public void before() {
        PlatformInitializer.init();
    }

    @Test
    public void testMe() {
        TravelLocation departure = new TravelLocation(Constants.TAIBEI);
        TravelLocation destination = new TravelLocation(Constants.BARCELONA);
        List<Calendar> departureDateTimeSelections = new ArrayList<Calendar>();

        Calendar departureDateTime1 = Calendar.getInstance();
        departureDateTime1.add(Calendar.DAY_OF_YEAR, 10);
        Calendar departureDateTime2 = Calendar.getInstance();
        departureDateTime2.add(Calendar.DAY_OF_YEAR, 11);
        departureDateTimeSelections.add(departureDateTime1);
        departureDateTimeSelections.add(departureDateTime2);

        ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure,
            departureDateTimeSelections);

        TravelProposal travelProposal = new TravelProposal(itineraryRequirement);
        Calendar deadlineDate = Calendar.getInstance();
        deadlineDate.add(Calendar.DATE, 10);
        Calendar departureDate = Calendar.getInstance();
        departureDate.add(Calendar.DATE, 18);
        TravelProduct aTravelProduct = new SimpleTravelProduct(travelProposal.getUUID(), 12, deadlineDate,
            departureDate);

        TravelLocation departure1 = new TravelLocation(Constants.BARCELONA);
        TravelLocation destination1 = new TravelLocation(Constants.GENEVA);
        Flight flight1 = new Flight("CA1981", departure1, destination1);
        flight1.setDepartureDateTime(Calendar.getInstance());
        flight1.setTicketFee(1.1);
        flight1.setTaxFee(198);
        aTravelProduct = new TrafficItem(aTravelProduct, flight1, Calendar.getInstance());
        assertEquals(199.1, aTravelProduct.getCost(), 0.1);
        assertEquals(1, aTravelProduct.getTravelProductItems().size());
        assertTrue(aTravelProduct.getTravelProductItems().get(0) instanceof TrafficItem);

        Calendar arrival = Calendar.getInstance();
        arrival.set(2014, Calendar.NOVEMBER, 30);
        Calendar depart = Calendar.getInstance();
        depart.set(2014, Calendar.DECEMBER, 1);
        aTravelProduct = new HotelItem(aTravelProduct, new Hotel(), arrival, depart);
        assertEquals(199.1, aTravelProduct.getCost(), 0.1);
        assertEquals(2, aTravelProduct.getTravelProductItems().size());
        assertTrue(aTravelProduct.getTravelProductItems().get(0) instanceof TrafficItem);
        assertTrue(aTravelProduct.getTravelProductItems().get(1) instanceof HotelItem);
    }
}
