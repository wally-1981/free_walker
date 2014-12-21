package com.free.walker.service.itinerary.rest;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.Enumable;
import com.free.walker.service.itinerary.Imaginable;
import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.Flight;
import com.free.walker.service.itinerary.basic.Hotel;
import com.free.walker.service.itinerary.basic.Resort;
import com.free.walker.service.itinerary.basic.Tag;
import com.free.walker.service.itinerary.basic.Train;
import com.free.walker.service.itinerary.basic.TravelLocation;
import com.free.walker.service.itinerary.dao.DAOFactory;
import com.free.walker.service.itinerary.dao.TravelBasicDAO;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.req.HotelRequirement;
import com.free.walker.service.itinerary.req.ItineraryRequirement;
import com.free.walker.service.itinerary.req.ResortRequirement;
import com.free.walker.service.itinerary.req.TrafficRequirement;
import com.free.walker.service.itinerary.req.TravelProposal;
import com.free.walker.service.itinerary.req.TravelRequirement;
import com.ibm.icu.util.Calendar;

@Path("/service/platform")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PlatformService {
    private static final Logger LOG = LoggerFactory.getLogger(PlatformService.class);
    private TravelBasicDAO travelBasicDAO;

    public PlatformService() {
        travelBasicDAO = DAOFactory.getTravelBasicDAO();
    }

    @GET
    @Path("/introspection")
    public Response getIntrospection() {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();

        {
            JsonObjectBuilder valueDataBuilder = Json.createObjectBuilder();
            Field[] fields = Introspection.TestValues.class.getFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    try {
                        if (field.get(Introspection.TestValues.class) instanceof Enumable) {
                            valueDataBuilder.add(field.getName(),
                                ((Enumable) field.get(Introspection.TestValues.class)).enumValue());
                        } else if (field.get(Introspection.TestValues.class) instanceof Integer) {
                            valueDataBuilder.add(field.getName(),
                                ((Integer) field.get(Introspection.TestValues.class)).intValue());
                        } else if (field.get(Introspection.TestValues.class) instanceof Imaginable) {
                            valueDataBuilder.add(field.getName(),
                                ((Imaginable) field.get(Introspection.TestValues.class)).realValue() + ":"
                                    + ((Imaginable) field.get(Introspection.TestValues.class)).imaginaryValue());
                        } else {
                            valueDataBuilder.add(field.getName(), (String) field.get(Introspection.TestValues.class));
                        }
                    } catch (JsonException e) {
                        LOG.error(LocalMessages.introspection_failure, e);
                        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                    } catch (Exception e) {
                        LOG.error(LocalMessages.introspection_failure, e);
                        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                    }
                }
            }
            resBuilder.add("test_data", valueDataBuilder);
        }

        {
            JsonObjectBuilder keyDataBuilder = Json.createObjectBuilder();
            Field[] fields = Introspection.JSONKeys.class.getFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    try {
                        keyDataBuilder.add(field.getName(), (String) field.get(Introspection.JSONKeys.class));
                    } catch (JsonException e) {
                        LOG.error(LocalMessages.introspection_failure, e);
                        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                    } catch (Exception e) {
                        LOG.error(LocalMessages.introspection_failure, e);
                        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                    }
                }
            }
            resBuilder.add("key_data", keyDataBuilder);
        }

        {
            JsonObjectBuilder valueDataBuilder = Json.createObjectBuilder();
            Field[] fields = Introspection.JSONValues.class.getFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    try {
                        if (field.get(Introspection.JSONValues.class) instanceof Enumable) {
                            valueDataBuilder.add(field.getName(),
                                ((Enumable) field.get(Introspection.JSONValues.class)).enumValue());
                        } else if (field.get(Introspection.JSONValues.class) instanceof Integer) {
                            valueDataBuilder.add(field.getName(),
                                ((Integer) field.get(Introspection.JSONValues.class)).intValue());
                        } else if (field.get(Introspection.JSONValues.class) instanceof Imaginable) {
                            valueDataBuilder.add(field.getName(),
                                ((Imaginable) field.get(Introspection.JSONValues.class)).realValue() + ":"
                                    + ((Imaginable) field.get(Introspection.JSONValues.class)).imaginaryValue());
                        } else {
                            valueDataBuilder.add(field.getName(), (String) field.get(Introspection.JSONKeys.class));
                        }
                    } catch (JsonException e) {
                        LOG.error(LocalMessages.introspection_failure, e);
                        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                    } catch (Exception e) {
                        LOG.error(LocalMessages.introspection_failure, e);
                        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                    }
                }
            }
            resBuilder.add("value_data", valueDataBuilder);
        }

        {
            JsonArrayBuilder sampleDataBuilder = Json.createArrayBuilder();

            TravelLocation dept = new TravelLocation(Constants.TAIBEI);
            TravelLocation dest = new TravelLocation(Constants.BARCELONA);
            ItineraryRequirement itineraryRequirement = new ItineraryRequirement(dept, dest);
            TravelProposal proposal = new TravelProposal("台北到巴萨看梅西", itineraryRequirement);
            sampleDataBuilder.add(proposal.toJSON());

            TravelRequirement hotelRequirementA = new HotelRequirement(6);
            TravelRequirement hotelRequirementB = new HotelRequirement(6, Introspection.JSONValues.HOTEL_STAR_STD_5);
            TravelRequirement hotelRequirementC = new HotelRequirement(6, new Hotel(), Calendar.getInstance());
            sampleDataBuilder.add(hotelRequirementA.toJSON());
            sampleDataBuilder.add(hotelRequirementB.toJSON());
            sampleDataBuilder.add(hotelRequirementC.toJSON());

            TravelRequirement resortRequirementA = new ResortRequirement(Introspection.JSONValues.TIME_RANGE_12_18);
            TravelRequirement resortRequirementB = new ResortRequirement(Introspection.JSONValues.TIME_RANGE_12_18,
                Introspection.JSONValues.RESORT_STAR_STD_2A);
            TravelRequirement resortRequirementC = new ResortRequirement(Introspection.JSONValues.TIME_RANGE_12_18,
                new Resort());
            sampleDataBuilder.add(resortRequirementA.toJSON());
            sampleDataBuilder.add(resortRequirementB.toJSON());
            sampleDataBuilder.add(resortRequirementC.toJSON());

            TravelRequirement trafficRequirementA = new TrafficRequirement(
                Introspection.JSONValues.TRAFFIC_TOOL_TYPE_TRAIN);
            TravelRequirement trafficRequirementB = new TrafficRequirement(
                Introspection.JSONValues.TRAFFIC_TOOL_TYPE_FLIGHT, Introspection.JSONValues.TIME_RANGE_18_24);
            TravelRequirement trafficRequirementC = new TrafficRequirement(
                Introspection.JSONValues.TRAFFIC_TOOL_TYPE_FLIGHT, Arrays.asList(
                    Introspection.JSONValues.TIME_RANGE_00_06, Introspection.JSONValues.TIME_RANGE_18_24));
            TravelRequirement trafficRequirementD = new TrafficRequirement(new Flight("CA1981"));
            TravelRequirement trafficRequirementE = new TrafficRequirement(new Train("Z38"));
            sampleDataBuilder.add(trafficRequirementA.toJSON());
            sampleDataBuilder.add(trafficRequirementB.toJSON());
            sampleDataBuilder.add(trafficRequirementC.toJSON());
            sampleDataBuilder.add(trafficRequirementD.toJSON());
            sampleDataBuilder.add(trafficRequirementE.toJSON());

//            TODO

            resBuilder.add("sample_data", sampleDataBuilder);
        }

        return Response.ok(resBuilder.build()).build();
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

    @GET
    @Path("/tags/top/{n}")
    public Response getTags(@PathParam("n") int n) {
        try {
            List<Tag> tags = travelBasicDAO.getHottestTags(n);
            JsonArrayBuilder resBuilder = Json.createArrayBuilder();
            for (int i = 0; i < tags.size(); i++) {
                resBuilder.add(tags.get(i).toJSON());
            }
            return Response.ok(resBuilder.build()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }
}
