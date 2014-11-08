package com.free.walker.service.itinerary.rest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.basic.City;
import com.free.walker.service.itinerary.basic.Country;
import com.free.walker.service.itinerary.basic.Hotel;
import com.free.walker.service.itinerary.basic.Resort;

@Path("/service/platform/admin/")
@Produces(MediaType.APPLICATION_JSON)
public class PlatformAdminService {
    private static Logger LOG = LoggerFactory.getLogger(PlatformAdminService.class);

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
    public Response addCountry(Country country) {
        return Response.ok().build();
    }

    @GET
    @Path("/cities/{cityId}")
    public Response getCity() {
        return Response.ok().build();
    }

    @POST
    @Path("/cities/")
    public Response addCity(City city) {
        return Response.ok().build();
    }

    @GET
    @Path("/resorts/{resortId}")
    public Response getResort() {
        return Response.ok().build();
    }

    @POST
    @Path("/resorts/")
    public Response addResort(Resort resort) {
        return Response.ok().build();
    }

    @GET
    @Path("/hotels/{hotelId}")
    public Response getHotel() {
        return Response.ok().build();
    }

    @POST
    @Path("/hotels/")
    public Response addResort(Hotel hotel) {
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
