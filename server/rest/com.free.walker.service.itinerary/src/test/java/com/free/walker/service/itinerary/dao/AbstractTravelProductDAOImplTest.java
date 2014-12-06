package com.free.walker.service.itinerary.dao;

import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.Bidding;
import com.free.walker.service.itinerary.basic.Bidding.BiddingItem;
import com.free.walker.service.itinerary.basic.Flight;
import com.free.walker.service.itinerary.basic.Hotel;
import com.free.walker.service.itinerary.basic.Resort;
import com.free.walker.service.itinerary.basic.Train;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidTravelProductException;
import com.free.walker.service.itinerary.product.HotelItem;
import com.free.walker.service.itinerary.product.ResortItem;
import com.free.walker.service.itinerary.product.SimpleTravelProduct;
import com.free.walker.service.itinerary.product.TrafficItem;
import com.free.walker.service.itinerary.product.TravelProduct;
import com.free.walker.service.itinerary.traffic.TrafficTool;
import com.ibm.icu.util.Calendar;

public abstract class AbstractTravelProductDAOImplTest {
    protected TravelProductDAO travelProductDAO;

    private TravelProduct travelProduct;
    private HotelItem hotelItem;
    private TrafficItem trafficItem;
    private ResortItem resortItem;
    private Bidding bidding;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.existed_travel_product, travelProduct.getUUID()));
        travelProductDAO.createProduct(travelProduct);
    }

    @Test
    public void testCreateTravelProduct() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productId = travelProductDAO.createProduct(travelProduct);
        assertNotNull(productId);
    }

    @Test
    public void testAddHotelWithNull() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelProductDAO.addItem(null, HotelItem.SUB_TYPE);
    }

    @Test
    public void testAddHotelWithWrongProductId() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(InvalidTravelProductException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_product, hotelItem.getUUID()));
        travelProductDAO.addItem(hotelItem, HotelItem.SUB_TYPE);
    }

    @Test
    public void testAddHotel() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productId = travelProductDAO.createProduct(travelProduct);
        assertNotNull(productId);

        productId = travelProductDAO.addItem(hotelItem, HotelItem.SUB_TYPE);
        assertNotNull(productId);
    }

    @Test
    public void testAddTrafficWithNull() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelProductDAO.addItem(null, TrafficItem.SUB_TYPE);
    }

    @Test
    public void testAddTrafficWithWrongProductId() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(InvalidTravelProductException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_product, trafficItem.getUUID()));
        travelProductDAO.addItem(trafficItem, TrafficItem.SUB_TYPE);
    }

    @Test
    public void testAddTraffic() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productId = travelProductDAO.createProduct(travelProduct);
        assertNotNull(productId);

        productId = travelProductDAO.addItem(trafficItem, TrafficItem.SUB_TYPE);
        assertNotNull(productId);
    }

    @Test
    public void testAddResortWithNull() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelProductDAO.addItem(null, ResortItem.SUB_TYPE);
    }

    @Test
    public void testAddResortWithWrongProductId() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(InvalidTravelProductException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_product, resortItem.getUUID()));
        travelProductDAO.addItem(resortItem, ResortItem.SUB_TYPE);
    }

    @Test
    public void testAddResort() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productId = travelProductDAO.createProduct(travelProduct);
        assertNotNull(productId);

        productId = travelProductDAO.addItem(resortItem, ResortItem.SUB_TYPE);
        assertNotNull(productId);
    }

    @Test
    public void testSetBiddingWithNull() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(NullPointerException.class);
        travelProductDAO.setBidding(null);
    }

    @Test
    public void testSetBiddingWithWrongProductId() throws InvalidTravelProductException, DatabaseAccessException {
        thrown.expect(InvalidTravelProductException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.missing_travel_product, bidding.getUUID()));
        travelProductDAO.setBidding(bidding);
    }

    @Test
    public void testSetBidding() throws InvalidTravelProductException, DatabaseAccessException {
        UUID productId = travelProductDAO.createProduct(travelProduct);
        assertNotNull(productId);

        productId = travelProductDAO.setBidding(bidding);
        assertNotNull(productId);
    }

    @After
    public void after() {
        ;
    }
}
