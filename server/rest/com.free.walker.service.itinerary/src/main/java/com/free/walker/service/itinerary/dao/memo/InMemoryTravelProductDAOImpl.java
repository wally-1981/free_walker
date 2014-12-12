package com.free.walker.service.itinerary.dao.memo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.dao.TravelProductDAO;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidTravelProductException;
import com.free.walker.service.itinerary.product.Bidding;
import com.free.walker.service.itinerary.product.HotelItem;
import com.free.walker.service.itinerary.product.ResortItem;
import com.free.walker.service.itinerary.product.TrafficItem;
import com.free.walker.service.itinerary.product.TravelProduct;
import com.free.walker.service.itinerary.product.TravelProductItem;
import com.free.walker.service.itinerary.product.TrivItem;

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

        if (travelProducts.containsKey(travelProduct.getProductUUID())) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.existed_travel_product,
                travelProduct.getProductUUID()), travelProduct.getProductUUID());
        }

        if (travelProductItems.containsKey(travelProduct.getProductUUID())) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.existed_travel_product,
                travelProduct.getProductUUID()), travelProduct.getProductUUID());
        }

        travelProducts.put(travelProduct.getProductUUID(), travelProduct);
        List<TravelProductItem> items = new ArrayList<TravelProductItem>(travelProduct.getTravelProductItems());
        travelProduct.getTravelProductItems().clear();
        travelProductItems.put(travelProduct.getProductUUID(), items);
        return travelProduct.getProductUUID();
    }

    public UUID addItem(UUID productId, TravelProductItem travelProductItem) throws InvalidTravelProductException,
        DatabaseAccessException {
        if (productId == null || travelProductItem == null) {
            throw new NullPointerException();
        }

        if (!travelProducts.containsKey(productId)) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.missing_travel_product,
                travelProductItem.getProductUUID()), travelProductItem.getProductUUID());
        }

        travelProductItems.get(productId).add(travelProductItem);

        return travelProductItem.getUUID();
    }

    public UUID setBidding(UUID productId, Bidding bidding) throws InvalidTravelProductException,
        DatabaseAccessException {
        if (productId == null || bidding == null) {
            throw new NullPointerException();
        }

        if (!travelProducts.containsKey(productId)) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.missing_travel_product,
                productId), productId);
        }

        if (travelProductBiddings.containsKey(productId)) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.existed_product_bidding,
                productId), productId);
        }

        travelProductBiddings.put(productId, bidding);

        return productId;
    }

    public TravelProduct getProduct(UUID productId) throws InvalidTravelProductException, DatabaseAccessException {
        if (productId == null) {
            throw new NullPointerException();
        }

        return travelProducts.get(productId);
    }

    public List<TravelProduct> getProducts(UUID proposalId) throws InvalidTravelProductException,
        DatabaseAccessException {
        if (proposalId == null) {
            throw new NullPointerException();
        }

        List<TravelProduct> result = new ArrayList<TravelProduct>();
        Iterator<TravelProduct> iter = travelProducts.values().iterator();
        while (iter.hasNext()) {
            TravelProduct product = iter.next();
            if (product.getProposalUUID().equals(proposalId)) {
                result.add(product);
            }
        }

        return result;
    }

    public List<TravelProductItem> getItems(UUID productId, String itemType) throws InvalidTravelProductException,
        DatabaseAccessException {
        if (productId == null) {
            throw new NullPointerException();
        }

        if (!travelProducts.containsKey(productId)) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.missing_travel_product,
                productId), productId);
        }

        List<TravelProductItem> items = travelProductItems.get(productId);
        List<TravelProductItem> results = new ArrayList<TravelProductItem>();
        for (int i = 0; items != null && i < items.size(); i++) {
            if (itemType != null && itemType.equals(items.get(i).getType())) {
                results.add(items.get(i));
            }
        }

        return results;
    }

    public Bidding getBidding(UUID productId) throws InvalidTravelProductException, DatabaseAccessException {
        if (productId == null) {
            throw new NullPointerException();
        }

        return travelProductBiddings.get(productId);
    }

    public UUID removeHotelItem(UUID productId, UUID hotelItemId) throws InvalidTravelProductException,
        DatabaseAccessException {
        if (productId == null || hotelItemId == null) {
            throw new NullPointerException();
        }

        if (!travelProducts.containsKey(productId)) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.missing_travel_product,
                productId), productId);
        }

        if (travelProductBiddings.containsKey(productId)) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(
                LocalMessages.illegal_remove_product_item_operation, productId), productId);
        }

        List<TravelProductItem> items = travelProductItems.get(productId);
        for (int i = 0; i < items.size(); i++) {
            TravelProductItem hotelItem = items.get(i);
            if (hotelItemId.equals(hotelItem.getUUID()) && HotelItem.SUB_TYPE.equals(hotelItem.getType())) {
                return items.remove(i).getUUID();
            }
        }

        return null;
    }

    public UUID removeTrafficItem(UUID productId, UUID trafficItemId) throws InvalidTravelProductException,
        DatabaseAccessException {
        if (productId == null || trafficItemId == null) {
            throw new NullPointerException();
        }

        if (!travelProducts.containsKey(productId)) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.missing_travel_product,
                productId), productId);
        }

        if (travelProductBiddings.containsKey(productId)) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(
                LocalMessages.illegal_remove_product_item_operation, productId), productId);
        }

        List<TravelProductItem> items = travelProductItems.get(productId);
        for (int i = 0; i < items.size(); i++) {
            TravelProductItem trafficItem = items.get(i);
            if (trafficItemId.equals(trafficItem.getUUID()) && TrafficItem.SUB_TYPE.equals(trafficItem.getType())) {
                return items.remove(i).getUUID();
            }
        }

        return null;
    }

    public UUID removeResortItem(UUID productId, UUID resortItemId) throws InvalidTravelProductException,
        DatabaseAccessException {
        if (productId == null || resortItemId == null) {
            throw new NullPointerException();
        }

        if (!travelProducts.containsKey(productId)) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.missing_travel_product,
                productId), productId);
        }

        if (travelProductBiddings.containsKey(productId)) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(
                LocalMessages.illegal_remove_product_item_operation, productId), productId);
        }

        List<TravelProductItem> items = travelProductItems.get(productId);
        for (int i = 0; i < items.size(); i++) {
            TravelProductItem resortItem = items.get(i);
            if (resortItemId.equals(resortItem.getUUID()) && ResortItem.SUB_TYPE.equals(resortItem.getType())) {
                return items.remove(i).getUUID();
            }
        }

        return null;
    }

    public UUID removeTrivItem(UUID productId, UUID trivItemId) throws InvalidTravelProductException,
        DatabaseAccessException {
        if (productId == null || trivItemId == null) {
            throw new NullPointerException();
        }

        if (!travelProducts.containsKey(productId)) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.missing_travel_product,
                productId), productId);
        }

        List<TravelProductItem> items = travelProductItems.get(productId);
        for (int i = 0; i < items.size(); i++) {
            TravelProductItem trivItem = items.get(i);
            if (trivItemId.equals(trivItem.getUUID()) && TrivItem.SUB_TYPE.equals(trivItem.getType())) {
                return items.remove(i).getUUID();
            }
        }

        return null;

    }

    public Bidding unsetBidding(UUID productId) throws InvalidTravelProductException, DatabaseAccessException {
        if (productId == null) {
            throw new NullPointerException();
        }

        if (!travelProducts.containsKey(productId)) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.missing_travel_product,
                productId), productId);
        }

        return travelProductBiddings.remove(productId);
    }
}
