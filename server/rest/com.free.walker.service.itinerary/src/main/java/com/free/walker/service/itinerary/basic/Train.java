package com.free.walker.service.itinerary.basic;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.free.walker.service.itinerary.Serializable;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.primitive.TrafficToolType;
import com.free.walker.service.itinerary.traffic.TrafficTool;
import com.ibm.icu.util.Calendar;

public class Train extends TrafficTool implements Serializable {
    private String number;
    private TravelLocation departure;
    private TravelLocation destination;

    private Calendar departureDateTime;

    private double ticketFee = 0;
    private double taxFee = 0;

    public Train(String number) {
        if (number == null) {
            throw new NullPointerException();
        }

        this.number = number;
    }

    public Train(String number, TravelLocation departure, TravelLocation destination) {
        if (number == null || departure == null || destination == null) {
            throw new IllegalArgumentException();
        }

        this.number = number;
        this.departure = departure;
        this.destination = destination;
    }

    public String getPublicId() {
        return number;
    }

    public TravelLocation getDeparture() {
        return departure;
    }

    public TravelLocation getDestination() {
        return destination;
    }

    public TrafficToolType getType() {
        return Introspection.JSONValues.TRAFFIC_TOOL_TRAIN;
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
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        return resBuilder.build();
    }

    public Object fromJSON(JsonObject jsObject) throws JsonException {
        return this;
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.OBJECT;
    }
}
