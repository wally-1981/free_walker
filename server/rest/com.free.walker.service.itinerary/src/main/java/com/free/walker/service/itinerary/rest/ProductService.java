package com.free.walker.service.itinerary.rest;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
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
import com.free.walker.service.itinerary.product.TravelProduct;
import com.free.walker.service.itinerary.util.JsonObjectHelper;

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
    public Response searchProposals(@QueryParam("pageNum") String pageNum, @QueryParam("pageSize") String pageSize,
        @QueryParam("searchTerm") String searchTerm) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("/products/{id}/")
    public Response getProduct(@PathParam("id") String id, @QueryParam("idType") String idType) {
        if (Introspection.JSONValues.REQUIREMENT_TYPE_PROPOSAL.equals(idType)) {
            return Response.status(Status.NOT_IMPLEMENTED).build();
        } else {
            return Response.status(Status.NOT_IMPLEMENTED).build();
        }
    }

    @GET
    @Path("/products/{productId}/hotels/")
    public Response getHotels(@PathParam("productId") String productId) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("/products/{productId}/traffics/")
    public Response getTraffics(@PathParam("productId") String productId) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("/products/{productId}/items/")
    public Response getItems(@PathParam("productId") String productId) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("/products/{productId}/biddings/")
    public Response getBiddings(@PathParam("productId") String productId) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @POST
    @Path("/products/{proposalId}")
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

    @POST
    @Path("/products/{productId}/hotels")
    public Response addHotel(JsonObject hotel) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @POST
    @Path("/products/{productId}/traffics")
    public Response addTraffic(JsonObject traffic) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @POST
    @Path("/products/{productId}/items")
    public Response addItem(JsonObject item) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    @POST
    @Path("/products/{productId}/biddings")
    public Response addBidding(JsonObject item) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }
}
