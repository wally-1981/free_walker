package com.free.walker.service.itinerary.req;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.commons.lang3.StringUtils;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.TravelLocation;
import com.free.walker.service.itinerary.basic.TravelLocation.LocationType;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.util.UuidUtil;

public class DestinationRequirement extends BaseTravelRequirement implements TravelRequirement {
    public static final String SUB_TYPE;

    static {
        String[] names = StringUtils.splitByCharacterTypeCamelCase(DestinationRequirement.class.getSimpleName());
        SUB_TYPE = StringUtils.join(names, '_', 0, names.length - 1).toLowerCase();
    }

    private TravelLocation travelLocation;

    public DestinationRequirement() {
        ;
    }

    public DestinationRequirement(TravelLocation travelLocation) {
        super();

        if (travelLocation == null) {
            throw new NullPointerException();
        }

        this.travelLocation = travelLocation;
    }

    public JsonObject toJSON() throws JsonException {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        resBuilder.add(Introspection.JSONKeys.UUID, getUUID().toString());
        resBuilder.add(Introspection.JSONKeys.TYPE, Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT);
        resBuilder.add(Introspection.JSONKeys.SUB_TYPE, SUB_TYPE);
        resBuilder.add(Introspection.JSONKeys.LOCATION, travelLocation.getUuid());
        if (travelLocation.isCity()) {
            resBuilder.add(Introspection.JSONKeys.LOCATION_TYPE, LocationType.CITY.name());
        } else if (travelLocation.isProvince()) {
            resBuilder.add(Introspection.JSONKeys.PROVINCE, LocationType.PROVINCE.name());
        } else if (travelLocation.isCountry()) {
            resBuilder.add(Introspection.JSONKeys.COUNTRY, LocationType.COUNTRY.name());
        } else if (travelLocation.isContinent()) {
            resBuilder.add(Introspection.JSONKeys.CONTINENT, LocationType.CONTINENT.name());
        } else {
            ;
        }

        return resBuilder.build();
    }

    public DestinationRequirement fromJSON(JsonObject jsObject) throws JsonException {
        String requirementId = jsObject.getString(Introspection.JSONKeys.UUID, null);

        if (requirementId != null) {
            this.requirementId = UuidUtil.fromUuidStr(requirementId);
        } else {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.UUID, requirementId));
        }

        return newFromJSON(jsObject);
    }

    public DestinationRequirement newFromJSON(JsonObject jsObject) throws JsonException {
        String type = jsObject.getString(Introspection.JSONKeys.TYPE, null);
        if (type != null && !Introspection.JSONValues.REQUIREMENT_TYPE_REQUIREMENT.equals(type)) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.TYPE, type));
        }

        String subType = jsObject.getString(Introspection.JSONKeys.SUB_TYPE, null);
        if (subType != null && !SUB_TYPE.equals(subType)) {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.SUB_TYPE, subType));
        }

        String location = jsObject.getString(Introspection.JSONKeys.LOCATION);
        String locationTypeStr = jsObject.getString(Introspection.JSONKeys.LOCATION_TYPE);
        LocationType locationType = LocationType.valueOf(locationTypeStr);
        if (location != null && locationType != null) {
            if (LocationType.CONTINENT.equals(locationType)) {
                this.travelLocation = new TravelLocation(Integer.valueOf(location));
            } else {
                this.travelLocation = new TravelLocation(UuidUtil.fromUuidStr(location), locationType);
            }
        } else {
            throw new JsonException(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
                Introspection.JSONKeys.LOCATION, location + ":" + locationTypeStr));
        }

        return this;
    }
}
