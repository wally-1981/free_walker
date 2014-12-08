package com.free.walker.service.itinerary.dao;

import java.util.List;
import java.util.UUID;

import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidTravelProductException;
import com.free.walker.service.itinerary.product.Bidding;
import com.free.walker.service.itinerary.product.TravelProduct;
import com.free.walker.service.itinerary.product.TravelProductItem;

public interface TravelProductDAO extends HealthyDAO {
    public UUID createProduct(TravelProduct travelProduct) throws InvalidTravelProductException,
        DatabaseAccessException;

    public UUID addItem(TravelProductItem travelProductItem) throws InvalidTravelProductException,
        DatabaseAccessException;

    public UUID setBidding(Bidding bidding) throws InvalidTravelProductException, DatabaseAccessException;

    public TravelProduct getProduct(UUID productId) throws InvalidTravelProductException, DatabaseAccessException;

    public List<TravelProductItem> getItems(UUID productId, String itemType) throws InvalidTravelProductException,
        DatabaseAccessException;

    public Bidding getBidding(UUID productId) throws InvalidTravelProductException, DatabaseAccessException;

    public UUID removeHotelItem(UUID productId, UUID hotelItemId) throws InvalidTravelProductException,
        DatabaseAccessException;

    public UUID removeTrafficItem(UUID productId, UUID trafficItemId) throws InvalidTravelProductException,
        DatabaseAccessException;

    public UUID removeResortItem(UUID productId, UUID resortItemId) throws InvalidTravelProductException,
        DatabaseAccessException;

    public UUID removeTrivItem(UUID productId, UUID trivItemId) throws InvalidTravelProductException,
        DatabaseAccessException;

    public Bidding unsetBidding(UUID productId) throws InvalidTravelProductException, DatabaseAccessException;
}
