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

/**
 * <b>ProductService</b> provides support for travel product and product item
 * management. A travel product is consist of product items for hotel, traffic,
 * resort as well as trivival items. Besides that, a travel product will also
 * contain a bidding, and product items in a product with valid bidding can not
 * be modified.<br>
 * <br>
 * Meanwhile, this service also provides product public and depublish as well as
 * product search API.<br>
 * <br>
 * This service supports consuming and producing data in below listed MIME
 * types:
 * <ul>
 * <li>application/json
 * </ul>
 */
@Path("/service/product/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductService {
    private TravelProductDAO travelProductDAO;

    public ProductService(Class<?> daoClass) {
        travelProductDAO = DAOFactory.getTravelProductDAO(daoClass.getName());
    }

    /**
     * <b>GET</b><br>
     * <br>
     * Search travel products by simple search term. The result will be returned
     * with pagination support.<br>
     */
    @GET
    @Path("/products/")
    public Response searchProducts(@QueryParam("pageNum") int pageNum, @QueryParam("pageSize") int pageSize,
        @QueryParam("searchTerm") String searchTerm) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    /**
     * <b>POST</b><br>
     * <br>
     * Publish the product specified by the product identifier, after which the
     * product will be public and searchable to all.<br>
     * <br>
     * Implicitly, the travel product will be published from database to search
     * engine.<br>
     * <br>
     * <b>Sample Payload:</b><br>
     * <br>
     * <i>N/A</i><br>
     * <br>
     */
    @POST
    @Path("/products/public/{productId}")
    public Response publishProduct(@PathParam("productId") String productId) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    /**
     * <b>DELETE</b><br>
     * <br>
     * Unpublish the product specified by the product identifier, after which
     * the product will be private.<br>
     * <br>
     * Implicitly, the travel product will be unpublished from search engine.<br>
     */
    @DELETE
    @Path("/products/public/{productId}")
    public Response unpublishProduct(@PathParam("productId") String productId) {
        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    /**
     * <b>GET</b><br>
     * <br>
     * Retrieve the travel products by the given product identifier or travel
     * proposal identifier.<br>
     * <br>
     * By specifying <i>idType=proposal</i>, all the products for the proposal
     * will be retrieved; or the identifier will be taken as product id and the
     * only one product will be retrieved if found.<br>
     */
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

    /**
     * <b>GET</b><br>
     * <br>
     * Retrieve all hotel items by the given product identifier.<br>
     */
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

    /**
     * <b>GET</b><br>
     * <br>
     * Retrieve all traffic items by the given product identifier.<br>
     */
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

    /**
     * <b>GET</b><br>
     * <br>
     * Retrieve all resort items by the given product identifier.<br>
     */
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

    /**
     * <b>GET</b><br>
     * <br>
     * Retrieve all trivial items by the given product identifier.<br>
     */
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

    /**
     * <b>GET</b><br>
     * <br>
     * Retrieve the product bidding by the given product identifier.<br>
     */    
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

    /**
     * <b>POST</b><br>
     * <br>
     * Create a travel product by the given posy payload.<br>
     * <br>
     * <b>Sample Payload:</b><br>
     * <br>
     * <i>http://&lt;host_name&gt;:&lt;port&gt;/service/platform/introspection?section=product_sample_data&part=5</i><br>
     * <br>
     */
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

    /**
     * <b>DELETE</b><br>
     * <br>
     * Remove the hotel item by the given hotel item identifier.<br>
     */
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

    /**
     * <b>DELETE</b><br>
     * <br>
     * Remove the traffic item by the given traffic item identifier.<br>
     */
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

    /**
     * <b>DELETE</b><br>
     * <br>
     * Remove the resort item by the given resort item identifier.<br>
     */
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

    /**
     * <b>DELETE</b><br>
     * <br>
     * Remove the trivial item by the given trivial item identifier.<br>
     */
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

    /**
     * <b>DELETE</b><br>
     * <br>
     * Remove the product bidding by the given product identifier.<br>
     */
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

    /**
     * <b>POST</b><br>
     * <br>
     * Add the hotel item by the given product identifier and the hotel item in
     * the post payload.<br>
     * <br>
     * <b>Sample Payload:</b><br>
     * <br>
     * <i>http://&lt;host_name&gt;:&lt;port&gt;/service/platform/introspection?section=product_sample_data&part=0</i><br>
     * <br>
     */
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

    /**
     * <b>POST</b><br>
     * <br>
     * Add the traffic item by the given product identifier and the traffic item
     * in the post payload.<br>
     * <br>
     * <b>Sample Payload:</b><br>
     * <br>
     * <i>http://&lt;host_name&gt;:&lt;port&gt;/service/platform/introspection?section=product_sample_data&part=1</i><br>
     * <br>
     */
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

    /**
     * <b>POST</b><br>
     * <br>
     * Add the resort item by the given product identifier and the resort item
     * in the post payload.<br>
     * <br>
     * <b>Sample Payload:</b><br>
     * <br>
     * <i>http://&lt;host_name&gt;:&lt;port&gt;/service/platform/introspection?section=product_sample_data&part=2</i><br>
     * <br>
     */
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

    /**
     * <b>POST</b><br>
     * <br>
     * Add the trivial item by the given product identifier and the trivial item
     * in the post payload.<br>
     * <br>
     * <b>Sample Payload:</b><br>
     * <br>
     * <i>http://&lt;host_name&gt;:&lt;port&gt;/service/platform/introspection?section=product_sample_data&part=3</i><br>
     * <br>
     */
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

    /**
     * <b>POST</b><br>
     * <br>
     * Set the product bidding by the given product identifier and the bidding
     * in the post payload.<br>
     * <br>
     * <b>Sample Payload:</b><br>
     * <br>
     * <i>http://&lt;host_name&gt;:&lt;port&gt;/service/platform/introspection?section=product_sample_data&part=4</i><br>
     * <br>
     */
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
