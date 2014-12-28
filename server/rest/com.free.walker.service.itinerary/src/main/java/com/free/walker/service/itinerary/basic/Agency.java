package com.free.walker.service.itinerary.basic;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.Serializable;
import com.free.walker.service.itinerary.primitive.Introspection;

public class Agency implements Serializable {
    private String uuid;
    private String name;
    private String departure;
    private String destination;

    public Agency() {
        ;
    }

    public JsonObject toJSON() {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Introspection.JSONKeys.UUID, uuid.toString());
        resBuilder.add(Introspection.JSONKeys.NAME, name);
        return resBuilder.build();
    }

    public Agency fromJSON(JsonObject jsObject) throws JsonException {
        String uuid = jsObject.getString(Introspection.JSONKeys.UUID, null);
        if (uuid == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.UUID, uuid));
        } else {
            this.uuid = uuid;
        }

        String name = jsObject.getString(Introspection.JSONKeys.NAME, null);
        if (name == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.NAME, name));
        } else {
            this.name = name;
        }

        String departure = jsObject.getString(Introspection.JSONKeys.DEPARTURE, null);
        if (departure == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.DEPARTURE, departure));
        } else {
            this.departure = departure;
        }

        String destination = jsObject.getString(Introspection.JSONKeys.DESTINATION, null);
        if (destination == null) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.DESTINATION, destination));
        } else {
            this.destination = destination;
        }

        return this;
    }

    public String getUuid() {
        return uuid.toString();
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDeparture() {
        return departure.toString();
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination.toString();
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ValueType getValueType() {
        return JsonValue.ValueType.NULL;
    }
}
