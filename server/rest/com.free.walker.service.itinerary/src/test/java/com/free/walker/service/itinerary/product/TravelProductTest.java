package com.free.walker.service.itinerary.product;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.basic.Flight;
import com.free.walker.service.itinerary.basic.TravelLocation;
import com.free.walker.service.itinerary.req.ItineraryRequirement;
import com.free.walker.service.itinerary.req.TravelProposal;
import com.ibm.icu.util.Calendar;

public class TravelProductTest {
    @Test
    public void testMe() {
        TravelLocation departure = new TravelLocation(Constants.BEIJING);
        TravelLocation destination = new TravelLocation(Constants.BOSTON);
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
        TravelProduct aTravelProduct = new NoneTravelProduct(travelProposal);

        TravelLocation departure1 = new TravelLocation(Constants.BOSTON);
        TravelLocation destination1 = new TravelLocation(Constants.LA);
        Flight flight1 = new Flight("CA1981", departure1, destination1);
        flight1.setDepartureDateTime(Calendar.getInstance());
        flight1.setTicketFee(1.1);
        flight1.setTaxFee(198);
        aTravelProduct = new TrafficDecorator(aTravelProduct, flight1);
        aTravelProduct.getCost();
        assertEquals(199.1, aTravelProduct.getCost(), 0.1);

        TravelLocation departure2 = new TravelLocation(Constants.LA);
        TravelLocation destination2 = new TravelLocation(Constants.BEIJING);
        Flight flight2 = new Flight("CA1982", departure2, destination2);
        flight2.setDepartureDateTime(Calendar.getInstance());
        flight2.setTicketFee(2.8);
        flight2.setTaxFee(190);
        aTravelProduct = new TrafficDecorator(aTravelProduct, flight2);
        assertEquals(391.9, aTravelProduct.getCost(), 0.1);
    }
}
