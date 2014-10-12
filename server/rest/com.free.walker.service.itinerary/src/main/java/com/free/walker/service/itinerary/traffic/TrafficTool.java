package com.free.walker.service.itinerary.traffic;

import com.free.walker.service.itinerary.TravelLocation;
import com.ibm.icu.util.Calendar;

public abstract class TrafficTool {
    public double getTotalFee() {
        return this.getTicketFee() + this.getTaxFee();
    }

    public abstract String getPublicId();

    public abstract TravelLocation getDeparture();

    public abstract TravelLocation getDestination();

    public abstract Calendar getDepartureDateTime();

    public abstract TrafficToolType getType();

    public abstract double getTicketFee();

    public abstract double getTaxFee();
}
