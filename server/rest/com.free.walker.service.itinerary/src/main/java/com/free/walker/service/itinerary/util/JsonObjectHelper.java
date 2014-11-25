package com.free.walker.service.itinerary.util;

import javax.json.JsonObject;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.req.HotelRequirement;
import com.free.walker.service.itinerary.req.ItineraryRequirement;
import com.free.walker.service.itinerary.req.ResortRequirement;
import com.free.walker.service.itinerary.req.TrafficRequirement;
import com.free.walker.service.itinerary.req.TrafficToolSeatRequirement;
import com.free.walker.service.itinerary.req.TravelProposal;
import com.free.walker.service.itinerary.req.TravelRequirement;

public class JsonObjectHelper {
    public static TravelRequirement toRequirement(JsonObject travelRequirement) throws InvalidTravelReqirementException {
        return toRequirement(travelRequirement, false);
    }

    public static TravelRequirement toRequirement(JsonObject travelRequirement, boolean strict)
        throws InvalidTravelReqirementException {
        if (!travelRequirement.containsKey(Introspection.JSONKeys.TYPE)) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.invalid_parameter_with_value, Introspection.JSONKeys.TYPE, null),
                UuidUtil.fromUuidStr(travelRequirement.getString(Introspection.JSONKeys.UUID)));
        }
        String requirementType = travelRequirement.getString(Introspection.JSONKeys.TYPE);

        if (Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL.equals(requirementType)) {
            return strict ? (TravelProposal) new TravelProposal().fromJSON(travelRequirement)
                : (TravelProposal) new TravelProposal().newFromJSON(travelRequirement);
        } else if (Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY.equals(requirementType)) {
            return strict ? (ItineraryRequirement) new ItineraryRequirement().fromJSON(travelRequirement)
                : (ItineraryRequirement) new ItineraryRequirement().newFromJSON(travelRequirement);
        } else {
            if (!travelRequirement.containsKey(Introspection.JSONKeys.SUB_TYPE)) {
                throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                    LocalMessages.invalid_parameter_with_value, Introspection.JSONKeys.SUB_TYPE, null),
                    UuidUtil.fromUuidStr(travelRequirement.getString(Introspection.JSONKeys.UUID)));
            }
            String requirementSubType = travelRequirement.getString(Introspection.JSONKeys.SUB_TYPE);

            if (HotelRequirement.SUB_TYPE.equals(requirementSubType)) {
                return strict ? (HotelRequirement) new HotelRequirement().fromJSON(travelRequirement)
                    : (HotelRequirement) new HotelRequirement().newFromJSON(travelRequirement);
            } else if (ResortRequirement.SUB_TYPE.equals(requirementSubType)) {
                return strict ? (ResortRequirement) new ResortRequirement().fromJSON(travelRequirement)
                    : (ResortRequirement) new ResortRequirement().newFromJSON(travelRequirement);
            } else if (TrafficRequirement.SUB_TYPE.equals(requirementSubType)) {
                return strict ? (TrafficRequirement) new TrafficRequirement().fromJSON(travelRequirement)
                    : (TrafficRequirement) new TrafficRequirement().newFromJSON(travelRequirement);
            } else if (TrafficToolSeatRequirement.SUB_TYPE.equals(requirementSubType)) {
                return strict ? (TrafficToolSeatRequirement) new TrafficToolSeatRequirement()
                    .fromJSON(travelRequirement) : (TrafficToolSeatRequirement) new TrafficToolSeatRequirement()
                    .newFromJSON(travelRequirement);
            } else {
                throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                    LocalMessages.invalid_parameter_with_value, Introspection.JSONKeys.TYPE + ":"
                        + Introspection.JSONKeys.SUB_TYPE, requirementType + ":" + requirementSubType),
                    UuidUtil.fromUuidStr(travelRequirement.getString(Introspection.JSONKeys.UUID)));
            }
        }
    }
}
