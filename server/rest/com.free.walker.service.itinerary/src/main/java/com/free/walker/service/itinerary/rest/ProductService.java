package com.free.walker.service.itinerary.rest;

import java.util.List;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.free.walker.service.itinerary.dao.DAOFactory;
import com.free.walker.service.itinerary.dao.TravelProductDAO;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidTravelProductException;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.product.Bidding;
import com.free.walker.service.itinerary.product.HotelItem;
import com.free.walker.service.itinerary.product.ResortItem;
import com.free.walker.service.itinerary.product.TrafficItem;
import com.free.walker.service.itinerary.product.TravelProduct;
import com.free.walker.service.itinerary.product.TravelProductItem;
import com.free.walker.service.itinerary.product.TrivItem;
import com.free.walker.service.itinerary.util.JsonObjectHelper;
import com.free.walker.service.itinerary.util.UuidUtil;

@Path("/service/product/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductService {
    private TravelProductDAO travelProductDAO;

    public ProductService(Class<?> daoClass) {
        travelProductDAO = DAOFactory.getTravelProductDAO(daoClass.getName());
    }

    @GET
    @Path("/products/")
    public Response searchProducts(@QueryParam("pageNum") String pageNum, @QueryParam("pageSize") String pageSize,
        @QueryParam("searchTerm") String searchTerm) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @POST
    @Path("/products/public/{productId}")
    public Response publishProduct(@PathParam("productId") String productId) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    @Path("/products/public/{productId}")
    public Response depublishProduct(@PathParam("productId") String productId) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("/products/{id}/")
    public Response getProduct(@PathParam("id") String id, @QueryParam("idType") String idType) {
        try {
            if (Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL.equals(idType)) {
                UUID proposalId = UuidUtil.fromUuidStr(id);
                List<TravelProduct> products = travelProductDAO.getProducts(proposalId);
                JsonArrayBuilder resBuilder = Json.createArrayBuilder();
                for (int i = 0; i < products.size(); i++) {
                    resBuilder.add(products.get(i).toJSON());
                }
                return Response.ok(resBuilder.build()).build();
            } else {
                UUID productId = UuidUtil.fromUuidStr(id);
                TravelProduct product = travelProductDAO.getProduct(productId);
                return Response.ok(product.toJSON()).build();
            }
        } catch (InvalidTravelProductException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    @GET
    @Path("/products/{productId}/hotels/")
    public Response getHotels(@PathParam("productId") String productId) {
        try {
            List<TravelProductItem> hotelItems = travelProductDAO.getItems(UuidUtil.fromUuidStr(productId),
                HotelItem.SUB_TYPE);
            JsonArrayBuilder resBuilder = Json.createArrayBuilder();
            for (int i = 0; i < hotelItems.size(); i++) {
                resBuilder.add(hotelItems.get(i).toJSON());
            }
            return Response.ok(resBuilder.build()).build();
        } catch (InvalidTravelProductException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    @GET
    @Path("/products/{productId}/traffics/")
    public Response getTraffics(@PathParam("productId") String productId) {
        try {
            List<TravelProductItem> trafficItems = travelProductDAO.getItems(UuidUtil.fromUuidStr(productId),
                TrafficItem.SUB_TYPE);
            JsonArrayBuilder resBuilder = Json.createArrayBuilder();
            for (int i = 0; i < trafficItems.size(); i++) {
                resBuilder.add(trafficItems.get(i).toJSON());
            }
            return Response.ok(resBuilder.build()).build();
        } catch (InvalidTravelProductException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    @GET
    @Path("/products/{productId}/resorts/")
    public Response getResorts(@PathParam("productId") String productId) {
        try {
            List<TravelProductItem> resortItems = travelProductDAO.getItems(UuidUtil.fromUuidStr(productId),
                ResortItem.SUB_TYPE);
            JsonArrayBuilder resBuilder = Json.createArrayBuilder();
            for (int i = 0; i < resortItems.size(); i++) {
                resBuilder.add(resortItems.get(i).toJSON());
            }
            return Response.ok(resBuilder.build()).build();
        } catch (InvalidTravelProductException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    @GET
    @Path("/products/{productId}/items/")
    public Response getItems(@PathParam("productId") String productId) {
        try {
            List<TravelProductItem> trivItems = travelProductDAO.getItems(UuidUtil.fromUuidStr(productId),
                TrivItem.SUB_TYPE);
            JsonArrayBuilder resBuilder = Json.createArrayBuilder();
            for (int i = 0; i < trivItems.size(); i++) {
                resBuilder.add(trivItems.get(i).toJSON());
            }
            return Response.ok(resBuilder.build()).build();
        } catch (InvalidTravelProductException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    @GET
    @Path("/products/{productId}/bidding/")
    public Response getBidding(@PathParam("productId") String productId) {
        try {
            Bidding bidding = travelProductDAO.getBidding(UuidUtil.fromUuidStr(productId));
            if (bidding == null) {
                JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, productId).build();
                return Response.status(Status.NOT_FOUND).entity(res).build();
            } else {
                return Response.ok(bidding.toJSON()).build();
            }
        } catch (InvalidTravelProductException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    @POST
    @Path("/products/")
    public Response addProduct(JsonObject travelProduct) {
        try {
            TravelProduct product = JsonObjectHelper.toProduct(travelProduct);
            String productId = travelProductDAO.createProduct(product).toString();
            JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, productId).build();
            return Response.ok(res).build();
        } catch (InvalidTravelProductException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    @DELETE
    @Path("/products/{productId}/hotels/{hotelItemId}")
    public Response deleteHotelItem(@PathParam("productId") String productId,
        @PathParam("hotelItemId") String hotelItemId) {
        try {
            UUID deletedHotelItemId = travelProductDAO.removeHotelItem(UuidUtil.fromUuidStr(productId),
                UuidUtil.fromUuidStr(hotelItemId));
            if (deletedHotelItemId == null) {
                JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, hotelItemId).build();
                return Response.status(Status.NOT_FOUND).entity(res).build();
            }

            JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, deletedHotelItemId.toString())
                .build();
            return Response.ok(res).build();
        } catch (InvalidTravelProductException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    @DELETE
    @Path("/products/{productId}/traffics/{trafficItemId}")
    public Response deleteTrafficItem(@PathParam("productId") String productId,
        @PathParam("trafficItemId") String trafficItemId) {
        try {
            UUID deletedTrafficItemId = travelProductDAO.removeTrafficItem(UuidUtil.fromUuidStr(productId),
                UuidUtil.fromUuidStr(trafficItemId));
            if (deletedTrafficItemId == null) {
                JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, trafficItemId).build();
                return Response.status(Status.NOT_FOUND).entity(res).build();
            }

            JsonObject res = Json.createObjectBuilder()
                .add(Introspection.JSONKeys.UUID, deletedTrafficItemId.toString()).build();
            return Response.ok(res).build();
        } catch (InvalidTravelProductException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    @DELETE
    @Path("/products/{productId}/resorts/{resortItemId}")
    public Response deleteResortItem(@PathParam("productId") String productId,
        @PathParam("resortItemId") String resortItemId) {
        try {
            UUID deletedResortItemId = travelProductDAO.removeResortItem(UuidUtil.fromUuidStr(productId),
                UuidUtil.fromUuidStr(resortItemId));
            if (deletedResortItemId == null) {
                JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, resortItemId).build();
                return Response.status(Status.NOT_FOUND).entity(res).build();
            }

            JsonObject res = Json.createObjectBuilder()
                .add(Introspection.JSONKeys.UUID, deletedResortItemId.toString()).build();
            return Response.ok(res).build();
        } catch (InvalidTravelProductException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    @DELETE
    @Path("/products/{productId}/items/{itemId}")
    public Response deleteTrivItem(@PathParam("productId") String productId, @PathParam("itemId") String itemId) {
        try {
            UUID deletedTrivItemId = travelProductDAO.removeTrivItem(UuidUtil.fromUuidStr(productId),
                UuidUtil.fromUuidStr(itemId));
            if (deletedTrivItemId == null) {
                JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, itemId).build();
                return Response.status(Status.NOT_FOUND).entity(res).build();
            }

            JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, deletedTrivItemId.toString())
                .build();
            return Response.ok(res).build();
        } catch (InvalidTravelProductException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    @DELETE
    @Path("/products/{productId}/bidding")
    public Response deleteBidding(@PathParam("productId") String productId) {
        try {
            Bidding bidding = travelProductDAO.unsetBidding(UuidUtil.fromUuidStr(productId));
            if (bidding == null) {
                JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, productId).build();
                return Response.status(Status.NOT_FOUND).entity(res).build();
            }

            JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, productId).build();
            return Response.ok(res).build();
        } catch (InvalidTravelProductException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    @POST
    @Path("/products/{productId}/hotels/")
    public Response addHotel(@PathParam("productId") String productId, JsonObject hotelItem) {
        try {
            TravelProductItem productItem = JsonObjectHelper.toProductItem(hotelItem);
            String hotelItemId = travelProductDAO.addItem(UuidUtil.fromUuidStr(productId), productItem).toString();
            JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, hotelItemId).build();
            return Response.ok(res).build();
        } catch (InvalidTravelProductException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    @POST
    @Path("/products/{productId}/traffics/")
    public Response addTraffic(@PathParam("productId") String productId, JsonObject trafficItem) {
        try {
            TravelProductItem productItem = JsonObjectHelper.toProductItem(trafficItem);
            String trafficItemId = travelProductDAO.addItem(UuidUtil.fromUuidStr(productId), productItem).toString();
            JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, trafficItemId).build();
            return Response.ok(res).build();
        } catch (InvalidTravelProductException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    @POST
    @Path("/products/{productId}/resorts/")
    public Response addResort(@PathParam("productId") String productId, JsonObject resortItem) {
        try {
            TravelProductItem productItem = JsonObjectHelper.toProductItem(resortItem);
            String resortItemId = travelProductDAO.addItem(UuidUtil.fromUuidStr(productId), productItem).toString();
            JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, resortItemId).build();
            return Response.ok(res).build();
        } catch (InvalidTravelProductException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    @POST
    @Path("/products/{productId}/items/")
    public Response addItem(@PathParam("productId") String productId, JsonObject item) {
        try {
            TravelProductItem productItem = JsonObjectHelper.toProductItem(item);
            String trivItemId = travelProductDAO.addItem(UuidUtil.fromUuidStr(productId), productItem).toString();
            JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, trivItemId).build();
            return Response.ok(res).build();
        } catch (InvalidTravelProductException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    @POST
    @Path("/products/{productId}/bidding/")
    public Response setBidding(@PathParam("productId") String productId, JsonObject bidding) {
        try {
            Bidding biddingItem = new Bidding().newFromJSON(bidding);
            travelProductDAO.setBidding(UuidUtil.fromUuidStr(productId), biddingItem);
            JsonObject res = Json.createObjectBuilder().build();
            return Response.ok(res).build();
        } catch (InvalidTravelProductException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }
}
