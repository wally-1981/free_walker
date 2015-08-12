package com.free.walker.service.itinerary.rest;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.Enumable;
import com.free.walker.service.itinerary.Imaginable;
import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.Agency;
import com.free.walker.service.itinerary.basic.City;
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
import com.free.walker.service.itinerary.product.Bidding;
import com.free.walker.service.itinerary.product.Bidding.BiddingItem;
import com.free.walker.service.itinerary.product.HotelItem;
import com.free.walker.service.itinerary.product.ResortItem;
import com.free.walker.service.itinerary.product.SimpleTravelProduct;
import com.free.walker.service.itinerary.product.TrafficItem;
import com.free.walker.service.itinerary.product.TravelProduct;
import com.free.walker.service.itinerary.product.TrivItem;
import com.free.walker.service.itinerary.req.HotelRequirement;
import com.free.walker.service.itinerary.req.ItineraryRequirement;
import com.free.walker.service.itinerary.req.ResortRequirement;
import com.free.walker.service.itinerary.req.TrafficRequirement;
import com.free.walker.service.itinerary.req.TravelProposal;
import com.free.walker.service.itinerary.req.TravelRequirement;
import com.ibm.icu.util.Calendar;

/**
 * <b>PlatformService</b> provides fundamental data management capabilities. The
 * data managed by this service include data for agency, city, hotel, traffic,
 * resort, etc. The API consumers can add agency, associate agency locations for
 * sending or receiving tourists, retrieve hotel or resort, etc. Typically, this
 * service will be consumed by the user interface for system administration.<br>
 * <br>
 * This service supports consuming and producing data in below listed MIME
 * types:
 * <ul>
 * <li>application/json
 * </ul>
 */
@Path("/service/platform")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PlatformService {
    private static final Logger LOG = LoggerFactory.getLogger(PlatformService.class);
    private TravelBasicDAO travelBasicDAO;

    public PlatformService() {
        travelBasicDAO = DAOFactory.getTravelBasicDAO();
    }

    /**
     * <b>GET</b><br>
     * <br>
     * Retrieve introspection data for all services, including PlatformService,
     * ItineraryService as well as ProductService.<br>
     * <br>
     * The introspection data include key, static value as well as payload
     * sample for consumer references.<br>
     */
    @GET
    @Path("/introspection/")
    public Response getIntrospection(@QueryParam("section") String section, @QueryParam("part") int part,
        @QueryParam("range") int range) {
        JsonObjectBuilder resBuilder = Json.createObjectBuilder();
        part = part == 0 ? 0 : Math.abs(part);
        range = range == 0 ? part + 1 : Math.abs(range);

        if (section == null || "test_data".equals(section))
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

        if (section == null || "key_data".equals(section))
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

        if (section == null || "value_data".equals(section))
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

        if (section == null || "basic_sample_data".equals(section))
        {
            JsonArrayBuilder basicSampleDataBuilder = Json.createArrayBuilder();

            Agency agency = new Agency();
            agency.setUuid(UUID.randomUUID().toString());
            agency.setName("中青旅（湖北分公司）");
            agency.setTitle("中青旅");
            agency.setHmd(86);
            agency.setExp(99999);
            basicSampleDataBuilder.add(agency.toJSON());

            if (section == null) {
                resBuilder.add("basic_sample_data", basicSampleDataBuilder.build());
            } else {
                if (!splitIntrospection(resBuilder, basicSampleDataBuilder, "basic_sample_data", part, range)) {
                    return Response.status(Status.NOT_FOUND).build();
                }
            }
        }

        if (section == null || "requirement_sample_data".equals(section))
        {
            JsonArrayBuilder requirementSampleDataBuilder = Json.createArrayBuilder();

            TravelLocation dept = new TravelLocation(Constants.TAIBEI);
            TravelLocation dest = new TravelLocation(Constants.BARCELONA);
            ItineraryRequirement itineraryRequirement = new ItineraryRequirement(dept, dest);
            TravelProposal proposal = new TravelProposal("台北到巴萨看梅西", itineraryRequirement);
            requirementSampleDataBuilder.add(proposal.toJSON());
            requirementSampleDataBuilder.add(itineraryRequirement.toJSON());

            TravelRequirement hotelRequirementA = new HotelRequirement(6);
            TravelRequirement hotelRequirementB = new HotelRequirement(6, Introspection.JSONValues.HOTEL_STAR_STD_5);
            TravelRequirement hotelRequirementC = new HotelRequirement(6, new Hotel(), Calendar.getInstance());
            requirementSampleDataBuilder.add(hotelRequirementA.toJSON());
            requirementSampleDataBuilder.add(hotelRequirementB.toJSON());
            requirementSampleDataBuilder.add(hotelRequirementC.toJSON());

            TravelRequirement resortRequirementA = new ResortRequirement(Introspection.JSONValues.TIME_RANGE_12_18);
            TravelRequirement resortRequirementB = new ResortRequirement(Introspection.JSONValues.TIME_RANGE_12_18,
                Introspection.JSONValues.RESORT_STAR_STD_2A);
            TravelRequirement resortRequirementC = new ResortRequirement(Introspection.JSONValues.TIME_RANGE_12_18,
                new Resort());
            requirementSampleDataBuilder.add(resortRequirementA.toJSON());
            requirementSampleDataBuilder.add(resortRequirementB.toJSON());
            requirementSampleDataBuilder.add(resortRequirementC.toJSON());

            TravelRequirement trafficRequirementA = new TrafficRequirement(
                Introspection.JSONValues.TRAFFIC_TOOL_TYPE_TRAIN);
            TravelRequirement trafficRequirementB = new TrafficRequirement(
                Introspection.JSONValues.TRAFFIC_TOOL_TYPE_FLIGHT, Introspection.JSONValues.TIME_RANGE_18_24);
            TravelRequirement trafficRequirementC = new TrafficRequirement(
                Introspection.JSONValues.TRAFFIC_TOOL_TYPE_FLIGHT, Arrays.asList(
                    Introspection.JSONValues.TIME_RANGE_00_06, Introspection.JSONValues.TIME_RANGE_18_24));
            TravelRequirement trafficRequirementD = new TrafficRequirement(new Flight("CA1981"));
            TravelRequirement trafficRequirementE = new TrafficRequirement(new Train("Z38"));
            requirementSampleDataBuilder.add(trafficRequirementA.toJSON());
            requirementSampleDataBuilder.add(trafficRequirementB.toJSON());
            requirementSampleDataBuilder.add(trafficRequirementC.toJSON());
            requirementSampleDataBuilder.add(trafficRequirementD.toJSON());
            requirementSampleDataBuilder.add(trafficRequirementE.toJSON());

            if (section == null) {
                resBuilder.add("requirement_sample_data", requirementSampleDataBuilder.build());
            } else {
                if (!splitIntrospection(resBuilder, requirementSampleDataBuilder, "requirement_sample_data", part, range)) {
                    return Response.status(Status.NOT_FOUND).build();
                }
            }
        }

        if (section == null || "product_sample_data".equals(section))
        {
            JsonArrayBuilder productSampleDataBuilder = Json.createArrayBuilder();

            TravelLocation dept = new TravelLocation(Constants.TAIBEI);
            TravelLocation dest = new TravelLocation(Constants.BARCELONA);
            ItineraryRequirement itineraryRequirement = new ItineraryRequirement(dept, dest);
            TravelProposal proposal = new TravelProposal("台北到巴萨看梅西", itineraryRequirement);

            Calendar deadline = Calendar.getInstance();
            deadline.add(Calendar.DATE, 3);
            Calendar departure = Calendar.getInstance();
            departure.add(Calendar.DATE, 5);
            TravelProduct travelProduct = new SimpleTravelProduct(proposal.getUUID(), 68, deadline, departure, dept);

            Calendar hotelArrival = Calendar.getInstance();
            hotelArrival.add(Calendar.DATE, 7);
            Calendar hotelDeparture = Calendar.getInstance();
            hotelDeparture.add(Calendar.DATE, 14);
            HotelItem hotelItem = new HotelItem(travelProduct, new Hotel(), hotelArrival, hotelDeparture);
            productSampleDataBuilder.add(hotelItem.toJSON());

            Calendar trafficDeparture = Calendar.getInstance();
            trafficDeparture.add(Calendar.DATE, 1);
            TrafficItem trafficItem = new TrafficItem(travelProduct, new Flight("CA1982", dept, dest), trafficDeparture);
            productSampleDataBuilder.add(trafficItem.toJSON());

            Calendar resortArrival = Calendar.getInstance();
            resortArrival.add(Calendar.DATE, 2);
            ResortItem resortItem = new ResortItem(travelProduct, new Resort(), resortArrival);
            productSampleDataBuilder.add(resortItem.toJSON());

            TrivItem trivItem = new TrivItem(travelProduct);
            productSampleDataBuilder.add(trivItem.toJSON());

            BiddingItem[] biddingItems = new BiddingItem[] { new BiddingItem(1, 5, 9999), new BiddingItem(6, 8888)};
            Bidding bidding = new Bidding(travelProduct, biddingItems);
            productSampleDataBuilder.add(bidding.toJSON());

            productSampleDataBuilder.add(travelProduct.toJSON());

            if (section == null) {
                resBuilder.add("product_sample_data", productSampleDataBuilder.build());
            } else {
                if (!splitIntrospection(resBuilder, productSampleDataBuilder, "product_sample_data", part, range)) {
                    return Response.status(Status.NOT_FOUND).build();
                }
            }
        }

        if (section == null || "permissions_and_roles".equals(section)) {
            Map<String, Method> permissionMethods = new HashMap<String, Method>();
            Map<String, Method> roleMethods = new HashMap<String, Method>();

            Method[] methods = null;
            methods = ArrayUtils.addAll(methods, AccountService.class.getMethods());
            methods = ArrayUtils.addAll(methods, PlatformService.class.getMethods());
            methods = ArrayUtils.addAll(methods, ResourceService.class.getMethods());
            methods = ArrayUtils.addAll(methods, ItineraryService.class.getMethods());
            methods = ArrayUtils.addAll(methods, ProductService.class.getMethods());

            for (int i = 0; i < methods.length; i++) {
                RequiresPermissions permissions = methods[i].getAnnotation(RequiresPermissions.class);
                RequiresRoles roles = methods[i].getAnnotation(RequiresRoles.class);
                for (int m = 0; permissions != null && m < permissions.value().length; m++) {
                    permissionMethods.put(permissions.value()[m], methods[i]);
                }
                for (int n = 0; roles != null && n < roles.value().length; n++) {
                    roleMethods.put(roles.value()[n], methods[i]);
                }
            }

            JsonArrayBuilder permissionBuilder = Json.createArrayBuilder();
            Iterator<String> permissionIter = new TreeSet<String>(permissionMethods.keySet()).iterator();
            while (permissionIter.hasNext()) {
                permissionBuilder.add(permissionIter.next());
            }

            JsonArrayBuilder roleBuilder = Json.createArrayBuilder();
            Iterator<String> roleIter = new TreeSet<String>(roleMethods.keySet()).iterator();
            while (roleIter.hasNext()) {
                roleBuilder.add(roleIter.next());
            }

            resBuilder.add("permissions", permissionBuilder);
            resBuilder.add("roles", roleBuilder);
        }

        return Response.ok(resBuilder.build()).build();
    }

    @GET
    @Path("/regions/{regionId}/")
    @RequiresPermissions("ManagePlatform")
    public Response getRegion() {
        return Response.ok().build();
    }

    @POST
    @Path("/regions/")
    @RequiresPermissions("ManagePlatform")
    public Response addRegion(JsonObject region) {
        return Response.ok().build();
    }

    @GET
    @Path("/countries/{countryId}/")
    @RequiresPermissions("ManagePlatform")
    public Response getCountry() {
        return Response.ok().build();
    }

    @POST
    @Path("/countries/")
    @RequiresPermissions("ManagePlatform")
    public Response addCountry(JsonObject country) {
        return Response.ok().build();
    }

    @GET
    @Path("/cities/{cityId}/")
    @RequiresPermissions("ManagePlatform")
    public Response getCity() {
        return Response.ok().build();
    }

    @GET
    @Path("/cities/")
    @RequiresPermissions("ManagePlatform")
    public Response getCities(@QueryParam("isDomestic") String isDomestic, @QueryParam("isHottest") boolean isHottest) {
        try {
            List<City> cities;
            if ("Y".equalsIgnoreCase(isDomestic)) {
                cities = travelBasicDAO.getDomesticCities();
            } else if ("N".equalsIgnoreCase(isDomestic)) {
                cities = travelBasicDAO.getInternationalCities();
            } else {
                cities = travelBasicDAO.getAllCities();
            }

            Set<City> sortedCities = new TreeSet<City>(new Comparator<City>() {
                public int compare(City cityA, City cityB) {
                    Character leadingCharA = new Character(Character.toUpperCase(cityA.getPinyinName().charAt(0)));
                    Character leadingCharB = new Character(Character.toUpperCase(cityB.getPinyinName().charAt(0)));
                    int sortResult = leadingCharA.compareTo(leadingCharB);
                    return sortResult == 0 ? cityA.getPinyinName().compareTo(cityB.getPinyinName()) : sortResult;
                }
            });
            sortedCities.addAll(cities);

            JsonArrayBuilder citiesBuilder = Json.createArrayBuilder();

            Iterator<City> sotredCityIter = sortedCities.iterator();
            Character indexingKey = null;
            JsonObjectBuilder cityIndexBuilder = null;
            JsonArrayBuilder indexCitiesBuilder = null;
            while (sotredCityIter.hasNext()) {
                City city = sotredCityIter.next();
                Character leadingChar = new Character(Character.toUpperCase(city.getPinyinName().charAt(0)));
                if (!leadingChar.equals(indexingKey)) {
                    if (cityIndexBuilder != null) {
                        cityIndexBuilder.add(Introspection.JSONKeys.CITIES, indexCitiesBuilder);
                        citiesBuilder.add(cityIndexBuilder);
                    }
                    cityIndexBuilder = Json.createObjectBuilder();
                    indexCitiesBuilder = Json.createArrayBuilder();
                    cityIndexBuilder.add(Introspection.JSONKeys.ABBR, (indexingKey = leadingChar).toString());
                }

                JsonObjectBuilder indexCityBuilder = Json.createObjectBuilder();
                indexCityBuilder.add(Introspection.JSONKeys.UUID, city.getUuid());
                indexCityBuilder.add(Introspection.JSONKeys.NAME, city.getName());
                indexCityBuilder.add(Introspection.JSONKeys.CHINESE_NAME, city.getChineseName());
                indexCityBuilder.add(Introspection.JSONKeys.PINYIN_NAME, city.getPinyinName());
                indexCityBuilder.add(Introspection.JSONKeys.COUNTRY, city.getCountryUuid());
                indexCitiesBuilder.add(indexCityBuilder);
            }

            return Response.ok(citiesBuilder.build()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    @POST
    @Path("/cities/")
    @RequiresPermissions("ManagePlatform")
    public Response addCity(JsonObject city) {
        return Response.ok().build();
    }

    @GET
    @Path("/resorts/{resortId}/")
    @RequiresPermissions("ManagePlatform")
    public Response getResort() {
        return Response.ok().build();
    }

    @POST
    @Path("/resorts/")
    @RequiresPermissions("ManagePlatform")
    public Response addResort(JsonObject resort) {
        return Response.ok().build();
    }

    @GET
    @Path("/hotels/{hotelId}/")
    @RequiresPermissions("ManagePlatform")
    public Response getHotel() {
        return Response.ok().build();
    }

    @POST
    @Path("/hotels/")
    @RequiresPermissions("ManagePlatform")
    public Response addHotel(JsonObject hotel) {
        return Response.ok().build();
    }

    @GET
    @Path("/flights/{flightId}/")
    @RequiresPermissions("ManagePlatform")
    public Response getFlight() {
        return Response.ok().build();
    }

    @GET
    @Path("/trains/{trainId}/")
    @RequiresPermissions("ManagePlatform")
    public Response getTrain() {
        return Response.ok().build();
    }

    /**
     * <b>GET</b><br>
     * <br>
     * Retrieve the top n most popular tags of travel proposals and products.<br>
     * <br>
     * The popular tags are calculated based on daily travel proposals and
     * products data in the background jobs.<br>
     */
    @GET
    @Path("/tags/top/{n}/")
    @RequiresPermissions("RetrieveTag")
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

    /**
     * <b>POST</b><br>
     * <br>
     * Add agency given in the post payload. Specify the query parameter
     * <i>?batch=true</i> and given agencies into a JSONArray to add multiple
     * agencies in batch mode.<br>
     * <br>
     * <b>Sample Payload:</b><br>
     * <br>
     * <i>http://&lt;host_name&gt;:&lt;port&gt;/service/platform/introspection?section=basic_sample_data&part=0</i><br>
     * <br>
     */
    @POST
    @Path("/agencies/")
    @RequiresPermissions("ManagePlatform")
    public Response addAgency(JsonObject agencyJs, @QueryParam("batch") boolean batch) {
        try {
            if (batch) {
                JsonArray agencyArray = agencyJs.getJsonArray(Introspection.JSONKeys.AGENCIES);
                if (agencyArray.size() > 500) {
                    return Response.status(Status.REQUEST_ENTITY_TOO_LARGE).build();
                }

                List<Agency> agencies = new ArrayList<Agency>();
                Map<String, Map<String, List<String>>> agencyLocations = new HashMap<String, Map<String, List<String>>>();
                for (int i = 0; i < agencyArray.size(); i++) {
                    JsonObject aJs = agencyArray.getJsonObject(i);
                    Agency agency = new Agency().fromJSON(aJs);
                    agencies.add(agency);

                    List<String> sendLocations = new ArrayList<String>();
                    List<String> recvLocations = new ArrayList<String>();
                    Map<String, List<String>> locations = new HashMap<String, List<String>>();
                    agencyLocations.put(agency.getUuid(), locations);
                    JsonArray sendLoctionArray = aJs.getJsonArray(Introspection.JSONKeys.SEND);
                    for (int j = 0; j < sendLoctionArray.size(); j++) {
                        String locationId = sendLoctionArray.getString(j).trim();
                        if (!locationId.isEmpty()) sendLocations.add(locationId);
                    }
                    locations.put(Introspection.JSONKeys.SEND, sendLocations);
                    JsonArray recvLoctionArray = aJs.getJsonArray(Introspection.JSONKeys.RECV);
                    for (int j = 0; j < recvLoctionArray.size(); j++) {
                        String locationId = recvLoctionArray.getString(j).trim();
                        if (!locationId.isEmpty()) recvLocations.add(locationId);
                    }
                    locations.put(Introspection.JSONKeys.RECV, recvLocations);
                }

                List<String> agencyIds = travelBasicDAO.addAgencies(agencies, agencyLocations);

                JsonArrayBuilder agencyIdArray = Json.createArrayBuilder();
                for (int i = 0; i < agencyIds.size(); i++) {
                    agencyIdArray.add(agencyIds.get(i));
                }
                JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, agencyIdArray).build();
                return Response.ok(res).build();
            } else {
                Agency agency = new Agency().fromJSON(agencyJs);
                String agencyId = travelBasicDAO.addAgency(agency);
                JsonObject res = Json.createObjectBuilder().add(Introspection.JSONKeys.UUID, agencyId).build();
                return Response.ok(res).build();
            }
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    /**
     * <b>GET</b><br>
     * <br>
     * Retrieve the agency by the given agency identifier.<br>
     */
    @GET
    @Path("/agencies/{agencyId}/")
    @RequiresPermissions("ManagePlatform")
    public Response getAgency(@PathParam("agencyId") String agencyId) {
        try {
            Agency agency = travelBasicDAO.getAgency(agencyId);
            if (agency == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            return Response.ok(agency.toJSON()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    /**
     * <b>POST</b><br>
     * <br>
     * Associate agency, specified by the agency identifier, to the location,
     * specified by the location identifier, for sending tourists.<br>
     * <br>
     * These associations will enable the system to electe the most suitable
     * agencies for the submitted proposals by tourists.<br>
     * <br>
     * <b>Sample Payload:</b><br>
     * <br>
     * <i>N/A</i><br>
     * <br>
     */
    @POST
    @Path("/agencies/{agencyId}/locations/send/{locationId}/")
    @RequiresPermissions("ManagePlatform")
    public Response addAgencySendLocation(@PathParam("agencyId") String agencyId,
        @PathParam("locationId") String locationId) {
        try {
            List<String> sendLocations = new ArrayList<String>();
            sendLocations.add(locationId);
            travelBasicDAO.relAgencyLocation(agencyId, sendLocations, null);
            return Response.ok().build();
        } catch(IllegalArgumentException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    /**
     * <b>DELETE</b><br>
     * <br>
     * Deassociate agency from the location of sending tourists.<br>
     */
    @DELETE
    @Path("/agencies/{agencyId}/locations/send/{locationId}/")
    @RequiresPermissions("ManagePlatform")
    public Response removeAgencySendLocation(@PathParam("agencyId") String agencyId,
        @PathParam("locationId") String locationId) {
        try {
            List<String> sendLocations = new ArrayList<String>();
            sendLocations.add(locationId);
            travelBasicDAO.unrelAgencyLocation(agencyId, sendLocations, null);
            return Response.ok().build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    /**
     * <b>POST</b><br>
     * <br>
     * Associate agency, specified by the agency identifier, to the location,
     * specified by the location identifier, for receiving tourists.<br>
     * <br>
     * These associations will enable the system to electe the most suitable
     * agencies for the submitted proposals by tourists.<br>
     * <br>
     * <b>Sample Payload:</b><br>
     * <br>
     * <i>N/A</i><br>
     * <br>
     */
    @POST
    @Path("/agencies/{agencyId}/locations/recv/{locationId}/")
    @RequiresPermissions("ManagePlatform")
    public Response addAgencyRecvLocation(@PathParam("agencyId") String agencyId,
        @PathParam("locationId") String locationId) {
        try {
            List<String> recvLocations = new ArrayList<String>();
            recvLocations.add(locationId);
            travelBasicDAO.relAgencyLocation(agencyId, null, recvLocations);
            return Response.ok().build();
        } catch(IllegalArgumentException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    /**
     * <b>DELETE</b><br>
     * <br>
     * Deassociate agency from the location of receiving tourists.<br>
     */
    @DELETE
    @Path("/agencies/{agencyId}/locations/recv/{locationId}/")
    @RequiresPermissions("ManagePlatform")
    public Response removeAgencyRecvLocation(@PathParam("agencyId") String agencyId,
        @PathParam("locationId") String locationId) {
        try {
            List<String> recvLocations = new ArrayList<String>();
            recvLocations.add(locationId);
            travelBasicDAO.unrelAgencyLocation(agencyId, null, recvLocations);
            return Response.ok().build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    /**
     * <b>GET</b><br>
     * <br>
     * Retrieve agency locations by the given agency identifier and the query
     * parameter <i>?sendRecv=0</i> for sending locations or <i>?sendRecv=1</i>
     * for receiving locations.<br>
     */
    @GET
    @Path("/agencies/{agencyId}/locations/")
    @RequiresPermissions("ManagePlatform")
    public Response getAgencyLocations(@PathParam("agencyId") String agencyId, @QueryParam("sendRecv") int sendRecv) {
        try {
            List<String> agenciesLocation = travelBasicDAO.getAgencyLocations(agencyId, sendRecv);
            JsonArrayBuilder agencyLocations = Json.createArrayBuilder();
            Iterator<String> agencyLocationsIter = agenciesLocation.iterator();
            while (agencyLocationsIter.hasNext()) {
                agencyLocations.add(agencyLocationsIter.next());
            }
            return Response.ok(Json.createObjectBuilder().add(Introspection.JSONKeys.LOCATION,
                agencyLocations).build()).build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    /**
     * <b>DELETE</b><br>
     * <br>
     * Remove the agency specified by the agency identifier. The associated
     * locations for both sending and receiving tourists will be removed as
     * well.<br>
     */
    @DELETE
    @Path("/agencies/{agencyId}/")
    @RequiresPermissions("ManagePlatform")
    public Response removeAgency(@PathParam("agencyId") String agencyId) {
        try {
            travelBasicDAO.removeAgency(agencyId);
            return Response.ok().build();
        } catch (DatabaseAccessException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.toJSON()).build();
        }
    }

    private boolean splitIntrospection(JsonObjectBuilder builder, JsonArrayBuilder sectionBuilder, String section,
        int part, int range) {
        JsonArray sampleData = sectionBuilder.build();
        List<JsonValue> data = sampleData.subList(part, Math.min(sampleData.size(), part + range));
        JsonArrayBuilder tmpBuilder = Json.createArrayBuilder();
        for (int i = 0; i < data.size(); i++) {
            tmpBuilder.add(data.get(i));
        }
        builder.add(section, tmpBuilder);
        return true;
    }
}
