package com.free.walker.service.itinerary.traffic;

import com.free.walker.service.itinerary.Serializable;
import com.free.walker.service.itinerary.basic.TravelLocation;
import com.free.walker.service.itinerary.primitive.TrafficToolType;
import com.ibm.icu.util.Calendar;

public abstract class TrafficTool implements Serializable {
    public double getTotalFee() {
        return this.getTicketFee() + this.getTaxFee();
    }

    public abstract String getPublicId();

    public abstract TravelLocation getDeparture();

    public abstract TravelLocation getDestination();

    public abstract Calendar getDepartureDateTime();

    public abstract Calendar getArrivalDateTime();

    public abstract TrafficToolType getType();

    public abstract double getTicketFee();

    public abstract double getTaxFee();
}
