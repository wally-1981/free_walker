package com.free.walker.service.itinerary.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.json.JsonObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.Flight;
import com.free.walker.service.itinerary.basic.Hotel;
import com.free.walker.service.itinerary.basic.Resort;
import com.free.walker.service.itinerary.basic.Train;
import com.free.walker.service.itinerary.basic.TravelLocation;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidTravelProductException;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.primitive.QueryTemplate;
import com.free.walker.service.itinerary.product.Bidding;
import com.free.walker.service.itinerary.product.Bidding.BiddingItem;
import com.free.walker.service.itinerary.product.HotelItem;
import com.free.walker.service.itinerary.product.ResortItem;
import com.free.walker.service.itinerary.product.SimpleTravelProduct;
import com.free.walker.service.itinerary.product.TrafficItem;
import com.free.walker.service.itinerary.product.TravelProduct;
import com.free.walker.service.itinerary.product.TravelProductItem;
import com.free.walker.service.itinerary.product.TrivItem;
import com.free.walker.service.itinerary.req.ItineraryRequirement;
import com.free.walker.service.itinerary.req.TravelProposal;
import com.free.walker.service.itinerary.traffic.TrafficTool;
import com.ibm.icu.util.Calendar;

public abstract class AbstractTravelProductDAOImplTest {
    protected TravelProductDAO travelProductDAO;

    private TravelProduct travelProduct;
    private HotelItem hotelItem;
    private TrafficItem trafficItem;
    private ResortItem resortItem;
    private TrivItem trivItem;
    private Bidding bidding;
    private TravelProposal travelProposal;
    private TravelProduct travelProductWrongProposal;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() {
        {
            TravelLocation destination = new TravelLocation(Constants.TAIBEI);
            TravelLocation departure = new TravelLocation(Constants.BARCELONA);
            ItineraryRequirement itineraryRequirement = new ItineraryRequirement(destination, departure);
            travelProposal = new TravelProposal(itineraryRequirement);
        }

        {
            UUID proposalId = UUID.randomUUID();
            Calendar deadline = Calendar.getInstance();
            deadline.add(Calendar.DATE, 10);
            Calendar departure = Calendar.getInstance();
            departure.add(Calendar.DATE, 18);
            travelProduct = new SimpleTravelProduct(travelProposal.getUUID(), 36, deadline, departure);
            travelProductWrongProposal = new SimpleTravelProduct(proposalId, 36, deadline, departure);

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

        {
            Hotel hotel = new Hotel();
            Calendar arrival = Calendar.getInstance();
            arrival.add(Calendar.DAY_OF_MONTH, 5);
            Calendar departure = Calendar.getInstance();
            departure.add(Calendar.DAY_OF_MONTH, 10);
            hotelItem = new HotelItem(travelProduct, hotel, arrival, departure);
        }

        {
            TrafficTool train = new Train("Z38");
            Calendar date = Calendar.getInstance();
            date.add(Calendar.DAY_OF_MONTH, 12);
            trafficItem = new TrafficItem(travelProduct, train, date);
        }

        {
            Resort resort = new Resort();
            Calendar date = Calendar.getInstance();
            date.add(Calendar.DAY_OF_MONTH, 13);
            resortItem = new ResortItem(travelProduct, resort, date);
        }

        {
            trivItem = new TrivItem(travelProduct);
        }

        {
            BiddingItem[] items = new BiddingItem[5];
            items[0] = new Bidding.BiddingItem(1, 5, 8999.99);
            items[1] = new Bidding.BiddingItem(6, 20, 8666.66);
            items[2] = new Bidding.BiddingItem(21, 50, 7888.88);
            items[3] = new Bidding.BiddingItem(51, 100, 7666.66);
            items[4] = new Bidding.BiddingItem(101, 6666.66);
            bidding = new Bidding(travelProduct, items);
        }
    }

    @Test
    public void testCreateTravelProductWithNullProduct() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelProductDAO.createProduct(null);
    }

    @Test
    public void testCreateTravelProductWithDup() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productId = travelProductDAO.createProduct(travelProduct);
        assertNotNull(productId);

        thrown.expect(InvalidTravelProductException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.existed_travel_product,
            travelProduct.getProductUUID()));
        travelProductDAO.createProduct(travelProduct);
    }

    @Test
    public void testCreateTravelProduct() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productId = travelProductDAO.createProduct(travelProduct);
        assertNotNull(productId);
    }

    @Test
    public void testAddItemWithNull() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelProductDAO.addItem(null, null);

        thrown.expect(NullPointerException.class);
        travelProductDAO.addItem(UUID.randomUUID(), null);
    }

    @Test
    public void testAddHotelWithWrongProductId() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(InvalidTravelProductException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_product, hotelItem.getProductUUID()));
        travelProductDAO.addItem(hotelItem.getProductUUID(), hotelItem);
    }

    @Test
    public void testAddHotel() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productId = travelProductDAO.createProduct(travelProduct);
        assertNotNull(productId);

        productId = travelProductDAO.addItem(productId, hotelItem);
        assertNotNull(productId);
    }

    @Test
    public void testAddTrafficWithWrongProductId() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(InvalidTravelProductException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_product,
            trafficItem.getProductUUID()));
        travelProductDAO.addItem(trafficItem.getProductUUID(), trafficItem);
    }

    @Test
    public void testAddTraffic() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productId = travelProductDAO.createProduct(travelProduct);
        assertNotNull(productId);

        productId = travelProductDAO.addItem(productId, trafficItem);
        assertNotNull(productId);
    }

    @Test
    public void testAddResortWithWrongProductId() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(InvalidTravelProductException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_product, resortItem.getProductUUID()));
        travelProductDAO.addItem(resortItem.getProductUUID(), resortItem);
    }

    @Test
    public void testAddResort() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productId = travelProductDAO.createProduct(travelProduct);
        assertNotNull(productId);

        productId = travelProductDAO.addItem(productId, resortItem);
        assertNotNull(productId);
    }

    @Test
    public void testAddTrivWithWrongProductId() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(InvalidTravelProductException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_product, trivItem.getProductUUID()));
        travelProductDAO.addItem(trivItem.getProductUUID(), trivItem);
    }

    @Test
    public void testAddTriv() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productId = travelProductDAO.createProduct(travelProduct);
        assertNotNull(productId);

        productId = travelProductDAO.addItem(productId, trivItem);
        assertNotNull(productId);
    }

    @Test
    public void testSetBiddingWithNull() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelProductDAO.setBidding(null, null);

        thrown.expect(NullPointerException.class);
        travelProductDAO.setBidding(UUID.randomUUID(), null);
    }

    @Test
    public void testSetBiddingWithWrongProductId() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(InvalidTravelProductException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_product, bidding.getProductUUID()));
        travelProductDAO.setBidding(bidding.getProductUUID(), bidding);
    }

    @Test
    public void testSetBiddingTwice() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productId = travelProductDAO.createProduct(travelProduct);
        assertNotNull(productId);

        productId = travelProductDAO.setBidding(productId, bidding);
        assertNotNull(productId);

        thrown.expect(InvalidTravelProductException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.existed_product_bidding, bidding.getProductUUID()));
        travelProductDAO.setBidding(bidding.getProductUUID(), bidding);
    }

    @Test
    public void testSetBidding() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productId = travelProductDAO.createProduct(travelProduct);
        assertNotNull(productId);

        productId = travelProductDAO.setBidding(productId, bidding);
        assertNotNull(productId);
    }

    @Test
    public void testGetProductWithNullProductId() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelProductDAO.getProduct(null);
    }

    @Test
    public void testGetProductWithWrongProductId() throws InvalidTravelProductException, DatabaseAccessException {
        assertNull(travelProductDAO.getProduct(UUID.randomUUID()));
    }

    @Test
    public void testGetProduct() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productId = travelProductDAO.createProduct(travelProduct);

        TravelProduct travelProduct = travelProductDAO.getProduct(productId);
        assertNotNull(travelProduct);
    }

    @Test
    public void testGetHotelItemsWithNullProductId() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelProductDAO.getItems(null, HotelItem.SUB_TYPE);
    }

    @Test
    public void testGetHotelItemsWithWrongProductId() throws InvalidTravelProductException, DatabaseAccessException {
        UUID wrongProductId = UUID.randomUUID();
        thrown.expect(InvalidTravelProductException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_product, wrongProductId));
        travelProductDAO.getItems(wrongProductId, HotelItem.SUB_TYPE);
    }

    @Test
    public void testGetHotelItems() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productId = travelProductDAO.createProduct(travelProduct);
        UUID itemId = travelProductDAO.addItem(productId, hotelItem);
        assertNotNull(itemId);

        List<TravelProductItem> items = travelProductDAO.getItems(productId, HotelItem.SUB_TYPE);
        assertNotNull(items);
        assertEquals(4, items.size());
        assertNotNull(items.get(0));
        assertNotNull(items.get(1));
        assertNotNull(items.get(2));
        assertNotNull(items.get(3));
    }

    @Test
    public void testGetTrafficItemsWithNullProductId() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelProductDAO.getItems(null, TrafficItem.SUB_TYPE);
    }

    @Test
    public void testGetTrafficItemsWithWrongProductId() throws InvalidTravelProductException, DatabaseAccessException {
        UUID wrongProductId = UUID.randomUUID();
        thrown.expect(InvalidTravelProductException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_product, wrongProductId));
        travelProductDAO.getItems(wrongProductId, TrafficItem.SUB_TYPE);
    }

    @Test
    public void testGetTrafficItems() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productId = travelProductDAO.createProduct(travelProduct);
        UUID itemId = travelProductDAO.addItem(productId, trafficItem);
        assertNotNull(itemId);

        List<TravelProductItem> items = travelProductDAO.getItems(productId, TrafficItem.SUB_TYPE);
        assertNotNull(items);
        assertEquals(4, items.size());
        assertNotNull(items.get(0));
        assertNotNull(items.get(1));
        assertNotNull(items.get(2));
        assertNotNull(items.get(3));
    }

    @Test
    public void testGetResortItemsWithNullProductId() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelProductDAO.getItems(null, ResortItem.SUB_TYPE);
    }

    @Test
    public void testGetResortItemsWithWrongProductId() throws InvalidTravelProductException, DatabaseAccessException {
        UUID wrongProductId = UUID.randomUUID();
        thrown.expect(InvalidTravelProductException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_product, wrongProductId));
        travelProductDAO.getItems(wrongProductId, ResortItem.SUB_TYPE);
    }

    @Test
    public void testGetResortItems() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productId = travelProductDAO.createProduct(travelProduct);
        UUID itemId = travelProductDAO.addItem(productId, resortItem);
        assertNotNull(itemId);

        List<TravelProductItem> items = travelProductDAO.getItems(productId, ResortItem.SUB_TYPE);
        assertNotNull(items);
        assertEquals(3, items.size());
        assertNotNull(items.get(0));
        assertNotNull(items.get(1));
        assertNotNull(items.get(2));
    }

    @Test
    public void testGetBiddingWithNullProductId() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelProductDAO.getBidding(null);
    }

    @Test
    public void testGetBidding() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productId = travelProductDAO.createProduct(travelProduct);
        productId = travelProductDAO.setBidding(productId, bidding);
        Bidding bidding = travelProductDAO.getBidding(productId);
        assertNotNull(bidding);
    }

    @Test
    public void testRemoveHotelItemWithNullProductId() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelProductDAO.removeHotelItem(null, UUID.randomUUID());
    }

    @Test
    public void testRemoveHotelItemWithNullItemId() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelProductDAO.removeHotelItem(UUID.randomUUID(), null);
    }

    @Test
    public void testRemoveHotelItemWithWrongProductId() throws InvalidTravelProductException, DatabaseAccessException {
        UUID wrongProductId = UUID.randomUUID();
        thrown.expect(InvalidTravelProductException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_product, wrongProductId));
        travelProductDAO.removeHotelItem(wrongProductId, UUID.randomUUID());
    }

    @Test
    public void testRemoveHotelItemWithBidding() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productId = travelProductDAO.createProduct(travelProduct);
        productId = travelProductDAO.setBidding(productId, bidding);
        thrown.expect(InvalidTravelProductException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.illegal_remove_product_item_operation, productId));
        travelProductDAO.removeHotelItem(productId, hotelItem.getUUID());
    }

    @Test
    public void testRemoveHotelItem() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productId = travelProductDAO.createProduct(travelProduct);
        UUID hotelItemId = travelProductDAO.removeHotelItem(productId, hotelItem.getUUID());
        assertNotNull(hotelItemId);
    }

    @Test
    public void testRemoveTrafficItemWithNullProductId() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelProductDAO.removeTrafficItem(null, UUID.randomUUID());
    }

    @Test
    public void testRemoveTrafficItemWithNullItemId() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelProductDAO.removeTrafficItem(UUID.randomUUID(), null);
    }

    @Test
    public void testRemoveTrafficItemWithWrongProductId() throws InvalidTravelProductException, DatabaseAccessException {
        UUID wrongProductId = UUID.randomUUID();
        thrown.expect(InvalidTravelProductException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_product, wrongProductId));
        travelProductDAO.removeTrafficItem(wrongProductId, UUID.randomUUID());
    }

    @Test
    public void testRemoveTrafficItemWithBidding() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productId = travelProductDAO.createProduct(travelProduct);
        productId = travelProductDAO.setBidding(productId, bidding);
        thrown.expect(InvalidTravelProductException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.illegal_remove_product_item_operation, productId));
        travelProductDAO.removeTrafficItem(productId, trafficItem.getUUID());
    }

    @Test
    public void testRemoveTrafficItem() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productId = travelProductDAO.createProduct(travelProduct);
        UUID trafficItemId = travelProductDAO.removeTrafficItem(productId, trafficItem.getUUID());
        assertNotNull(trafficItemId);
    }

    @Test
    public void testRemoveResortItemWithNullProductId() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelProductDAO.removeResortItem(null, UUID.randomUUID());
    }

    @Test
    public void testRemoveResortItemWithNullItemId() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelProductDAO.removeResortItem(UUID.randomUUID(), null);
    }

    @Test
    public void testRemoveResortItemWithWrongProductId() throws InvalidTravelProductException, DatabaseAccessException {
        UUID wrongProductId = UUID.randomUUID();
        thrown.expect(InvalidTravelProductException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_product, wrongProductId));
        travelProductDAO.removeResortItem(wrongProductId, UUID.randomUUID());
    }

    @Test
    public void testRemoveResortItemWithBidding() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productId = travelProductDAO.createProduct(travelProduct);
        productId = travelProductDAO.setBidding(productId, bidding);
        thrown.expect(InvalidTravelProductException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.illegal_remove_product_item_operation, productId));
        travelProductDAO.removeResortItem(productId, resortItem.getUUID());
    }

    @Test
    public void testRemoveResortItem() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productId = travelProductDAO.createProduct(travelProduct);
        UUID resortItemId = travelProductDAO.removeResortItem(productId, resortItem.getUUID());
        assertNotNull(resortItemId);
    }

    @Test
    public void testRemoveTrivItemWithNullProductId() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelProductDAO.removeTrivItem(null, UUID.randomUUID());
    }

    @Test
    public void testRemoveTrivItemWithNullItemId() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelProductDAO.removeTrivItem(UUID.randomUUID(), null);
    }

    @Test
    public void testRemoveTrivItemWithWrongProductId() throws InvalidTravelProductException, DatabaseAccessException {
        UUID wrongProductId = UUID.randomUUID();
        thrown.expect(InvalidTravelProductException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_product, wrongProductId));
        travelProductDAO.removeTrivItem(wrongProductId, UUID.randomUUID());
    }

    @Test
    public void testRemoveTrivItem() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productId = travelProductDAO.createProduct(travelProduct);
        productId = travelProductDAO.setBidding(productId, bidding);
        UUID trivItemId = travelProductDAO.removeTrivItem(productId, trivItem.getUUID());
        assertNotNull(trivItemId);
    }

    @Test
    public void testUnsetBiddingWithNullProductId() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelProductDAO.unsetBidding(null);
    }

    @Test
    public void testUnsetBiddingWithWrongProductId() throws InvalidTravelProductException, DatabaseAccessException {
        UUID wrongProductId = UUID.randomUUID();
        thrown.expect(InvalidTravelProductException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_product, wrongProductId));
        travelProductDAO.unsetBidding(wrongProductId);
    }

    @Test
    public void testUnsetBidding() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productId = travelProductDAO.createProduct(travelProduct);
        productId = travelProductDAO.setBidding(productId, bidding);
        Bidding bidding = travelProductDAO.unsetBidding(productId);
        assertNotNull(bidding);
    }

    @Test
    public void testPublishProductWithNullProductId() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelProductDAO.publishProduct(null, travelProposal);
    }

    @Test
    public void testPublishProductWithNullProposalId() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelProductDAO.publishProduct(travelProduct, null);
    }

    @Test
    public void testPublishProductWithMisProposalId() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(IllegalArgumentException.class);
        travelProductDAO.publishProduct(travelProductWrongProposal, travelProposal);
    }

    @Test
    public void testPublishProduct() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productId = travelProductDAO.createProduct(travelProduct);
        productId = travelProductDAO.setBidding(productId, bidding);
        UUID productUuid = travelProductDAO.publishProduct(travelProduct.getCore(), travelProposal);
        assertNotNull(productUuid);
        assertEquals(productId.toString(), productUuid.toString());

        assertNotNull(travelProductDAO.publishProduct(travelProduct, travelProposal));
        assertEquals(productId.toString(), travelProductDAO.publishProduct(travelProduct, travelProposal).toString());
    }

    @Test
    public void testSearchProductWithNullTemplateName() throws DatabaseAccessException {
        Map<String, String> templateParams = new HashMap<String, String>();
        templateParams.put("test_template_key", "test_template_value");
        thrown.expect(NullPointerException.class);
        travelProductDAO.searchProduct(null, templateParams);
    }

    @Test
    public void testSearchProductWithNullTemplateParams() throws DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelProductDAO.searchProduct(QueryTemplate.TEST_TEMPLACE, null);
    }

    @Test
    public void testSearchProduct() throws DatabaseAccessException {
        Map<String, String> templateParams = new HashMap<String, String>();
        templateParams.put("from", String.valueOf(0 * 2));
        templateParams.put("size", String.valueOf(2));
        JsonObject products = travelProductDAO.searchProduct(QueryTemplate.TEST_TEMPLACE, templateParams);
        assertNotNull(products);
        assertTrue(products.containsKey(Introspection.JSONKeys.TOTAL_HITS_NUMBER));
        assertTrue(products.containsKey(Introspection.JSONKeys.MAX_HIT_SCORE));
        assertTrue(products.containsKey(Introspection.JSONKeys.HITS));
        assertTrue(products.getJsonArray(Introspection.JSONKeys.HITS).size() == 0
            || products.getJsonArray(Introspection.JSONKeys.HITS).size() == 2);
    }

    @Test
    public void testUnpublishProductWithNullProductId() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelProductDAO.unpublishProduct(null);
    }

    @Test
    public void testUnpublishNotExistedProduct() throws InvalidTravelProductException, DatabaseAccessException {
        UUID wrongProductId = UUID.randomUUID();
        assertNull(travelProductDAO.unpublishProduct(wrongProductId));
    }

    @Test
    public void testUnpublishProduct() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productUuid = travelProductDAO.publishProduct(travelProduct, travelProposal);
        UUID unpublishedProductUuid = travelProductDAO.unpublishProduct(productUuid);
        assertNotNull(unpublishedProductUuid);
        assertEquals(productUuid.toString(), unpublishedProductUuid.toString());
    }

    @After
    public void after() {
        ;
    }
}
