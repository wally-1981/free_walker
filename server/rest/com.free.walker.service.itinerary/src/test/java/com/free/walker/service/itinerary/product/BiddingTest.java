package com.free.walker.service.itinerary.product;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.free.walker.service.itinerary.basic.Flight;
import com.free.walker.service.itinerary.basic.Hotel;
import com.free.walker.service.itinerary.basic.Resort;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.product.Bidding;
import com.free.walker.service.itinerary.product.HotelItem;
import com.free.walker.service.itinerary.product.ResortItem;
import com.free.walker.service.itinerary.product.SimpleTravelProduct;
import com.free.walker.service.itinerary.product.TrafficItem;
import com.free.walker.service.itinerary.product.TravelProduct;
import com.free.walker.service.itinerary.traffic.TrafficTool;
import com.ibm.icu.util.Calendar;

public class BiddingTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private TravelProduct travelProduct;

    @Before
    public void before() {
        {
            UUID proposalId = UUID.randomUUID();
            travelProduct = new SimpleTravelProduct(proposalId);

            Hotel hotelA = new Hotel();
            Calendar arrivalA = Calendar.getInstance();
            Calendar departureA = Calendar.getInstance();
            departureA.add(Calendar.DAY_OF_MONTH, 3);
            travelProduct = new HotelItem(travelProduct, hotelA, arrivalA, departureA);

            Hotel hotelB = new Hotel();
            Calendar arrivalB = Calendar.getInstance();
            arrivalB.add(Calendar.DAY_OF_MONTH, 5);
            Calendar departureB = Calendar.getInstance();
            departureB.add(Calendar.DAY_OF_MONTH, 10);
            travelProduct = new HotelItem(travelProduct, hotelB, arrivalB, departureB);

            TrafficTool trafficA = new Flight("CA1982");
            Calendar dateA = Calendar.getInstance();
            travelProduct = new TrafficItem(travelProduct, trafficA, dateA);

            TrafficTool trafficB = new Flight("CA1981");
            Calendar dateB = Calendar.getInstance();
            dateB.add(Calendar.DAY_OF_MONTH, 4);
            travelProduct = new TrafficItem(travelProduct, trafficB, dateB);

            Resort resort = new Resort();
            Calendar dateC = Calendar.getInstance();
            dateC.add(Calendar.DAY_OF_MONTH, 3);
            travelProduct = new ResortItem(travelProduct, resort, dateC);
        }
    }

    @Test
    public void testFromToJSON() {
        JsonObjectBuilder biddingBuilder = Json.createObjectBuilder();

        JsonArrayBuilder biddingItemsbuilder = Json.createArrayBuilder();

        JsonObjectBuilder biddingItembuilder1 = Json.createObjectBuilder();
        biddingItembuilder1.add(Introspection.JSONKeys.MIN, 1);
        biddingItembuilder1.add(Introspection.JSONKeys.MAX, 5);
        biddingItembuilder1.add(Introspection.JSONKeys.PRICE, 8999.99);

        JsonObjectBuilder biddingItembuilder2 = Json.createObjectBuilder();
        biddingItembuilder2.add(Introspection.JSONKeys.MIN, 6);
        biddingItembuilder2.add(Introspection.JSONKeys.MAX, 20);
        biddingItembuilder2.add(Introspection.JSONKeys.PRICE, 8666.66);

        JsonObjectBuilder biddingItembuilder3 = Json.createObjectBuilder();
        biddingItembuilder3.add(Introspection.JSONKeys.MIN, 21);
        biddingItembuilder3.add(Introspection.JSONKeys.PRICE, 6666.66);

        biddingItemsbuilder.add(biddingItembuilder1);
        biddingItemsbuilder.add(biddingItembuilder2);
        biddingItemsbuilder.add(biddingItembuilder3);

        biddingBuilder.add(Introspection.JSONKeys.BIDDING, biddingItemsbuilder);

        Bidding bidding = new Bidding(travelProduct).fromJSON(biddingBuilder.build());

        JsonObject biddingJs = bidding.toJSON();
        assertNotNull(biddingJs);
        JsonArray jsonArray = biddingJs.getJsonArray(Introspection.JSONKeys.BIDDING);
        assertEquals(3, jsonArray.size());

        JsonObject biddingItem1 = jsonArray.getJsonObject(0);
        assertEquals(1, biddingItem1.getInt(Introspection.JSONKeys.MIN));
        assertEquals(5, biddingItem1.getInt(Introspection.JSONKeys.MAX));
        assertEquals(8999.99, biddingItem1.getJsonNumber(Introspection.JSONKeys.PRICE).doubleValue(), 0.01);

        JsonObject biddingItem2 = jsonArray.getJsonObject(1);
        assertEquals(6, biddingItem2.getInt(Introspection.JSONKeys.MIN));
        assertEquals(20, biddingItem2.getInt(Introspection.JSONKeys.MAX));
        assertEquals(8666.66, biddingItem2.getJsonNumber(Introspection.JSONKeys.PRICE).doubleValue(), 0.01);

        JsonObject biddingItem3 = jsonArray.getJsonObject(2);
        assertEquals(21, biddingItem3.getInt(Introspection.JSONKeys.MIN));
        assertEquals(Integer.MAX_VALUE, biddingItem3.getInt(Introspection.JSONKeys.MAX));
        assertEquals(6666.66, biddingItem3.getJsonNumber(Introspection.JSONKeys.PRICE).doubleValue(), 0.01);
    }
}
