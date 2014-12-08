package com.free.walker.service.itinerary.basic;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.primitive.TrafficToolType;
import com.free.walker.service.itinerary.traffic.TrafficTool;
import com.ibm.icu.util.Calendar;

public class Flight extends TrafficTool {
    private String number;
    private TravelLocation departure;
    private TravelLocation destination;

    private Calendar departureDateTime;
    private Calendar arrivalDateTime;

    private double ticketFee = 0;
    private double taxFee = 0;

    public Flight() {
        ;
    }

    public Flight(String number) {
        if (number == null) {
            throw new NullPointerException();
        }

        this.number = number;
    }

    public Flight(String number, TravelLocation departure, TravelLocation destination) {
        if (number == null || departure == null || destination == null) {
            throw new NullPointerException();
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
        return Introspection.JSONValues.TRAFFIC_TOOL_TYPE_FLIGHT;
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

    public Calendar getArrivalDateTime() {
        return arrivalDateTime;
    }

    public void setArrivalDateTime(Calendar arrivalDateTime) {
        this.arrivalDateTime = arrivalDateTime;
    }

    public JsonObject toJSON() {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Introspection.JSONKeys.TRAFFIC_TOOL_TYPE, getType().enumValue());
        return resBuilder.build();
    }

    public Flight fromJSON(JsonObject jsObject) throws JsonException {
        int toolType = jsObject.getInt(Introspection.JSONKeys.TRAFFIC_TOOL_TYPE, 0);
        if (toolType != Introspection.JSONValues.TRAFFIC_TOOL_TYPE_FLIGHT.enumValue()) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.TRAFFIC_TOOL_TYPE, toolType));
        }

        return this;
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.NULL;
    }

}
