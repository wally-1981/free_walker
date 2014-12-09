package com.free.walker.service.itinerary.dao;

import java.util.List;
import java.util.UUID;

import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidTravelProductException;
import com.free.walker.service.itinerary.product.Bidding;
import com.free.walker.service.itinerary.product.TravelProduct;
import com.free.walker.service.itinerary.product.TravelProductItem;

public interface TravelProductDAO extends HealthyDAO {
    /**
     * Create the specified product, and all attached initial product items will be
     * created either.
     * 
     * @param travelProduct
     * @return
     * @throws InvalidTravelProductException
     * @throws DatabaseAccessException
     */
    public UUID createProduct(TravelProduct travelProduct) throws InvalidTravelProductException,
        DatabaseAccessException;

    /**
     * Add the product item to the specified product.
     * 
     * @param travelProductItem
     * @return
     * @throws InvalidTravelProductException
     * @throws DatabaseAccessException
     */
    public UUID addItem(TravelProductItem travelProductItem) throws InvalidTravelProductException,
        DatabaseAccessException;

    /**
     * Set the specicied bidding for the product assciating with the specified
     * bidding.
     * 
     * @param bidding
     * @return
     * @throws InvalidTravelProductException
     * @throws DatabaseAccessException
     */
    public UUID setBidding(Bidding bidding) throws InvalidTravelProductException, DatabaseAccessException;

    /**
     * Retrieve the product by the specified product id. No product item will be
     * included in the return.
     * 
     * @param productId
     * @return
     * @throws InvalidTravelProductException
     * @throws DatabaseAccessException
     */
    public TravelProduct getProduct(UUID productId) throws InvalidTravelProductException, DatabaseAccessException;

    /**
     * Retrieve all product items by the specified product id and item type. The
     * item type could be "hotel", "resort", "traffic" or "triv".
     * 
     * @param productId
     * @param itemType
     * @return
     * @throws InvalidTravelProductException
     * @throws DatabaseAccessException
     */
    public List<TravelProductItem> getItems(UUID productId, String itemType) throws InvalidTravelProductException,
        DatabaseAccessException;

    /**
     * Retrieve the product bidding by the specified product id.
     * 
     * @param productId
     * @return
     * @throws InvalidTravelProductException
     * @throws DatabaseAccessException
     */
    public Bidding getBidding(UUID productId) throws InvalidTravelProductException, DatabaseAccessException;

    /**
     * Remove the specified hotel item by product id and the item id. The items
     * in a product having bidding can not be removed. null will be returned if
     * the removal does not found the removing hotel item.
     * 
     * @param productId
     * @param hotelItemId
     * @return
     * @throws InvalidTravelProductException
     * @throws DatabaseAccessException
     */
    public UUID removeHotelItem(UUID productId, UUID hotelItemId) throws InvalidTravelProductException,
        DatabaseAccessException;

    /**
     * Remove the specified traffic item by product id and the item id. The
     * items in a product having bidding can not be removed. null will be
     * returned if the removal does not found the removing traffic item.
     * 
     * @param productId
     * @param trafficItemId
     * @return
     * @throws InvalidTravelProductException
     * @throws DatabaseAccessException
     */
    public UUID removeTrafficItem(UUID productId, UUID trafficItemId) throws InvalidTravelProductException,
        DatabaseAccessException;

    /**
     * Remove the specified resort item by product id and the item id. The items
     * in a product having bidding can not be removed. null will be returned if
     * the removal does not found the removing resort item.
     * 
     * @param productId
     * @param resortItemId
     * @return
     * @throws InvalidTravelProductException
     * @throws DatabaseAccessException
     */
    public UUID removeResortItem(UUID productId, UUID resortItemId) throws InvalidTravelProductException,
        DatabaseAccessException;

    /**
     * Remove the specified triv item by product id and the item id. null will
     * be returned if the removal does not found the removing resort item.
     * 
     * @param productId
     * @param trivItemId
     * @return
     * @throws InvalidTravelProductException
     * @throws DatabaseAccessException
     */
    public UUID removeTrivItem(UUID productId, UUID trivItemId) throws InvalidTravelProductException,
        DatabaseAccessException;

    public Bidding unsetBidding(UUID productId) throws InvalidTravelProductException, DatabaseAccessException;
}
