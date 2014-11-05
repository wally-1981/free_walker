package com.free.walker.service.itinerary.basic;

import javax.json.JsonObject;
import javax.json.JsonValue;

import com.free.walker.service.itinerary.SerializableJSON;
import com.free.walker.service.itinerary.traffic.TrafficTool;
import com.free.walker.service.itinerary.traffic.TrafficToolType;
import com.ibm.icu.util.Calendar;

public class Flight extends TrafficTool implements SerializableJSON {
    private String mumber;
    private TravelLocation departure;
    private TravelLocation destination;

    private Calendar departureDateTime;

    private double ticketFee = 0;
    private double taxFee = 0;

    public Flight(String flightNumber, TravelLocation departure, TravelLocation destination) {
        if (flightNumber == null || departure == null || destination == null) {
            throw new IllegalArgumentException();
        }

        this.mumber = flightNumber;
        this.departure = departure;
        this.destination = destination;
    }

    public String getPublicId() {
        return mumber;
    }

    public TravelLocation getDeparture() {
        return departure;
    }

    public TravelLocation getDestination() {
        return destination;
    }

    public TrafficToolType getType() {
        return TrafficToolType.FLIGHT;
    }

    public double getTicketFee() {
        return this.ticketFee;
    }

    public void setTicketFee(double ticketFee) {
        this.ticketFee = ticketFee;
    }

    public double getTaxFee() {
        return this.taxFee;
    }

    public void setTaxFee(double taxFee) {
        this.taxFee = taxFee;
    }

    public Calendar getDepartureDateTime() {
        return departureDateTime;
    }

    public void setDepartureDateTime(Calendar departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    public JsonObject toJSON() {
        return null;
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.OBJECT;
    }
}
