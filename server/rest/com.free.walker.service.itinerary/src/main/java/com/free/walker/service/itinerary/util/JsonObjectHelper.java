package com.free.walker.service.itinerary.util;

import java.util.UUID;

import javax.json.JsonObject;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.exp.InvalidTravelProductException;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.product.HotelItem;
import com.free.walker.service.itinerary.product.ResortItem;
import com.free.walker.service.itinerary.product.SimpleTravelProduct;
import com.free.walker.service.itinerary.product.TrafficItem;
import com.free.walker.service.itinerary.product.TravelProduct;
import com.free.walker.service.itinerary.product.TravelProductItem;
import com.free.walker.service.itinerary.product.TrivItem;
import com.free.walker.service.itinerary.req.DestinationRequirement;
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
        String uuidStr = strict ? travelRequirement.getString(Introspection.JSONKeys.UUID, null) : null;
        UUID uuid = uuidStr == null ? null : UuidUtil.fromUuidStr(uuidStr);

        if (!travelRequirement.containsKey(Introspection.JSONKeys.TYPE)) {
            throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                LocalMessages.invalid_parameter_with_value, Introspection.JSONKeys.TYPE, null), uuid);
        }
        String requirementType = travelRequirement.getString(Introspection.JSONKeys.TYPE, null);

        if (Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL.equals(requirementType)) {
            return strict ? (TravelProposal) new TravelProposal().fromJSON(travelRequirement)
                : (TravelProposal) new TravelProposal().newFromJSON(travelRequirement);
        } else if (Introspection.JSONValues.REQUIREMENT_TYPE_ITINERARY.equals(requirementType)) {
            return strict ? (ItineraryRequirement) new ItineraryRequirement().fromJSON(travelRequirement)
                : (ItineraryRequirement) new ItineraryRequirement().newFromJSON(travelRequirement);
        } else {
            if (!travelRequirement.containsKey(Introspection.JSONKeys.SUB_TYPE)) {
                throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                    LocalMessages.invalid_parameter_with_value, Introspection.JSONKeys.SUB_TYPE, null), uuid);
            }
            String requirementSubType = travelRequirement.getString(Introspection.JSONKeys.SUB_TYPE, null);

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
            } else if (DestinationRequirement.SUB_TYPE.equals(requirementSubType)) {
                return strict ? (DestinationRequirement) new DestinationRequirement().fromJSON(travelRequirement)
                    : (DestinationRequirement) new DestinationRequirement().newFromJSON(travelRequirement);
            } else {
                throw new InvalidTravelReqirementException(LocalMessages.getMessage(
                    LocalMessages.invalid_parameter_with_value, Introspection.JSONKeys.TYPE + ":"
                        + Introspection.JSONKeys.SUB_TYPE, requirementType + ":" + requirementSubType), uuid);
            }
        }
    }

    public static TravelProduct toProduct(JsonObject travelProduct) throws InvalidTravelProductException {
        return toProduct(travelProduct, false);
    }

    public static TravelProduct toProduct(JsonObject travelProduct, boolean strict)
        throws InvalidTravelProductException {
        return strict ? (TravelProduct) new SimpleTravelProduct().fromJSON(travelProduct)
            : (TravelProduct) new SimpleTravelProduct().newFromJSON(travelProduct);
    }

    public static TravelProductItem toProductItem(JsonObject travelProductItem) throws InvalidTravelProductException {
        return toProductItem(travelProductItem, false);
    }

    public static TravelProductItem toProductItem(JsonObject travelProductItem, boolean strict)
        throws InvalidTravelProductException {        
        String uuidStr = strict ? travelProductItem.getString(Introspection.JSONKeys.UUID, null) : null;
        UUID uuid = uuidStr == null ? null : UuidUtil.fromUuidStr(uuidStr);

        if (!travelProductItem.containsKey(Introspection.JSONKeys.SUB_TYPE)) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(
                LocalMessages.invalid_parameter_with_value, Introspection.JSONKeys.SUB_TYPE, null), uuid);
        }
        String itemType = travelProductItem.getString(Introspection.JSONKeys.SUB_TYPE, null);

        if (HotelItem.SUB_TYPE.equals(itemType)) {
            return strict ? new HotelItem().fromJSON(travelProductItem) : new HotelItem()
                .newFromJSON(travelProductItem);
        } else if (TrafficItem.SUB_TYPE.equals(itemType)) {
            return strict ? new TrafficItem().fromJSON(travelProductItem) : new TrafficItem()
                .newFromJSON(travelProductItem);
        } else if (ResortItem.SUB_TYPE.equals(itemType)) {
            return strict ? new ResortItem().fromJSON(travelProductItem) : new ResortItem()
                .newFromJSON(travelProductItem);
        } else if (TrivItem.SUB_TYPE.equals(itemType)) {
            return strict ? new TrivItem().fromJSON(travelProductItem) : new TrivItem().newFromJSON(travelProductItem);
        } else {
            throw new InvalidTravelProductException(LocalMessages.getMessage(
                LocalMessages.invalid_parameter_with_value, Introspection.JSONKeys.SUB_TYPE, itemType), uuid);
        }
    }
}
