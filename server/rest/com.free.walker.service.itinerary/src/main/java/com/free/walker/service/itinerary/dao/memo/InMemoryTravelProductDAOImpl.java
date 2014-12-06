package com.free.walker.service.itinerary.dao.memo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.Bidding;
import com.free.walker.service.itinerary.dao.TravelProductDAO;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidTravelProductException;
import com.free.walker.service.itinerary.product.TravelProduct;
import com.free.walker.service.itinerary.product.TravelProductItem;

public class InMemoryTravelProductDAOImpl implements TravelProductDAO {
    protected Map<UUID, TravelProduct> travelProducts;
    protected Map<UUID, List<TravelProductItem>> travelProductItems;
    protected Map<UUID, Bidding> travelProductBiddings;

    private static class SingletonHolder {
        private static final TravelProductDAO INSTANCE = new InMemoryTravelProductDAOImpl();
    }

    private InMemoryTravelProductDAOImpl() {
        travelProducts = new HashMap<UUID, TravelProduct>();
        travelProductItems = new HashMap<UUID, List<TravelProductItem>>();
        travelProductBiddings = new HashMap<UUID, Bidding>();
    }

    public static TravelProductDAO getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public boolean pingPersistence() {
        return true;
    }

    public UUID createProduct(TravelProduct travelProduct) throws InvalidTravelProductException,
        DatabaseAccessException {
        if (travelProduct == null) {
            throw new NullPointerException();
        }

        if (travelProducts.containsKey(travelProduct.getUUID())) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.existed_travel_product,
                travelProduct.getUUID()), travelProduct.getUUID());
        }

        if (travelProductItems.containsKey(travelProduct.getUUID())) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.existed_travel_product,
                travelProduct.getUUID()), travelProduct.getUUID());
        }

        travelProducts.put(travelProduct.getUUID(), travelProduct);
        List<TravelProductItem> items = new ArrayList<TravelProductItem>(travelProduct.getTravelProductItems());
        travelProduct.getTravelProductItems().clear();
        travelProductItems.put(travelProduct.getUUID(), items);
        return travelProduct.getUUID();
    }

    public UUID addItem(TravelProductItem travelProductItem, String itemType) throws InvalidTravelProductException,
        DatabaseAccessException {
        if (travelProductItem == null) {
            throw new NullPointerException();
        }

        UUID productId = travelProductItem.getUUID();
        if (!travelProducts.containsKey(productId)) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.missing_travel_product,
                travelProductItem.getUUID()), travelProductItem.getUUID());
        }

        travelProductItems.get(productId).add(travelProductItem);

        return productId;
    }

    public UUID setBidding(Bidding bidding) throws InvalidTravelProductException, DatabaseAccessException {
        if (bidding == null) {
            throw new NullPointerException();
        }

        if (!travelProducts.containsKey(bidding.getUUID())) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.missing_travel_product,
                bidding.getUUID()), bidding.getUUID());
        }

        travelProductBiddings.put(bidding.getUUID(), bidding);

        return bidding.getUUID();
    }

    public TravelProduct getProduct(UUID productId) throws InvalidTravelProductException, DatabaseAccessException {
        // TODO Auto-generated method stub
        return null;
    }

    public List<TravelProductItem> getHotelItems(UUID productId) throws InvalidTravelProductException,
        DatabaseAccessException {
        // TODO Auto-generated method stub
        return null;
    }

    public List<TravelProductItem> getTrafficItems(UUID productId) throws InvalidTravelProductException,
        DatabaseAccessException {
        // TODO Auto-generated method stub
        return null;
    }

    public List<TravelProductItem> getResortItems(UUID productId) throws InvalidTravelProductException,
        DatabaseAccessException {
        // TODO Auto-generated method stub
        return null;
    }

    public List<TravelProductItem> getTrivItems(UUID productId) throws InvalidTravelProductException,
        DatabaseAccessException {
        // TODO Auto-generated method stub
        return null;
    }

    public Bidding getBidding(UUID productId) throws InvalidTravelProductException, DatabaseAccessException {
        // TODO Auto-generated method stub
        return null;
    }

    public UUID removeHotelItem(UUID productId, UUID hotelItemId) throws InvalidTravelProductException,
        DatabaseAccessException {
        // TODO Auto-generated method stub
        return null;
    }

    public UUID removeTrafficItem(UUID productId, UUID trafficItemId) throws InvalidTravelProductException,
        DatabaseAccessException {
        // TODO Auto-generated method stub
        return null;
    }

    public UUID removeResortItem(UUID productId, UUID resortItemId) throws InvalidTravelProductException,
        DatabaseAccessException {
        // TODO Auto-generated method stub
        return null;
    }

    public UUID removeTrivItem(UUID productId, UUID trivItemId) throws InvalidTravelProductException,
        DatabaseAccessException {
        // TODO Auto-generated method stub
        return null;
    }
}
