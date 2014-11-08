package com.free.walker.service.itinerary.rest;

import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/service/platform/admin/")
@Produces(MediaType.APPLICATION_JSON)
public class PlatformAdminService {
    public PlatformAdminService() {
        ;
    }

    @GET
    @Path("/countries/{countryId}")
    public Response getCountry() {
        return Response.ok().build();
    }

    @POST
    @Path("/countries/")
    public Response addCountry(JsonObject country) {
        return Response.ok().build();
    }

    @GET
    @Path("/cities/{cityId}")
    public Response getCity() {
        return Response.ok().build();
    }

    @POST
    @Path("/cities/")
    public Response addCity(JsonObject city) {
        return Response.ok().build();
    }

    @GET
    @Path("/resorts/{resortId}")
    public Response getResort() {
        return Response.ok().build();
    }

    @POST
    @Path("/resorts/")
    public Response addResort(JsonObject resort) {
        return Response.ok().build();
    }

    @GET
    @Path("/hotels/{hotelId}")
    public Response getHotel() {
        return Response.ok().build();
    }

    @POST
    @Path("/hotels/")
    public Response addHotel(JsonObject hotel) {
        return Response.ok().build();
    }

    @GET
    @Path("/flights/{flightId}")
    public Response getFlight() {
        return Response.ok().build();
    }

    @GET
    @Path("/trains/{trainId}")
    public Response getTrain() {
        return Response.ok().build();
    }
}
