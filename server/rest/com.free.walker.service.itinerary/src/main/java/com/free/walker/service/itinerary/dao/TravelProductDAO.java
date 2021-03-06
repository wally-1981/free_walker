package com.free.walker.service.itinerary.dao;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.json.JsonObject;

import com.free.walker.service.itinerary.basic.Account;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidTravelProductException;
import com.free.walker.service.itinerary.primitive.ProductStatus;
import com.free.walker.service.itinerary.primitive.QueryTemplate;
import com.free.walker.service.itinerary.product.Bidding;
import com.free.walker.service.itinerary.product.TravelProduct;
import com.free.walker.service.itinerary.product.TravelProductItem;
import com.free.walker.service.itinerary.req.TravelProposal;

public interface TravelProductDAO extends HealthyDAO {
    /**
     * Create the specified product, and all attached initial product items will
     * be created either.
     * 
     * @param account
     * @param travelProduct
     * @return
     * @throws InvalidTravelProductException
     * @throws DatabaseAccessException
     */
    public UUID createProduct(Account account, TravelProduct travelProduct) throws InvalidTravelProductException,
        DatabaseAccessException;

    /**
     * Add the product item to the specified product.
     * 
     * @param productId
     * @param travelProductItem
     * @return
     * @throws InvalidTravelProductException
     * @throws DatabaseAccessException
     */
    public UUID addItem(UUID productId, TravelProductItem travelProductItem) throws InvalidTravelProductException,
        DatabaseAccessException;

    /**
     * Set the bidding for the product identified the specified product
     * identifier.
     * 
     * @param productId
     * @param bidding
     * @return
     * @throws InvalidTravelProductException
     * @throws DatabaseAccessException
     */
    public UUID setBidding(UUID productId, Bidding bidding) throws InvalidTravelProductException,
        DatabaseAccessException;

    /**
     * Retrieve the product by the specified product id. No product item will be
     * included in the returned product.
     * 
     * @param productId
     * @return
     * @throws InvalidTravelProductException
     * @throws DatabaseAccessException
     */
    public TravelProduct getProduct(UUID productId) throws InvalidTravelProductException, DatabaseAccessException;

    /**
     * Retrieve all products by the specified proposal id. No product item will
     * be included in every returned product.
     * 
     * @param proposalId
     * @return
     * @throws InvalidTravelProductException
     * @throws DatabaseAccessException
     */
    public List<TravelProduct> getProducts(UUID proposalId) throws InvalidTravelProductException,
        DatabaseAccessException;

    /**
     * 
     * @param account
     * @param status
     * @return
     * @throws InvalidTravelProductException
     * @throws DatabaseAccessException
     */
    public List<TravelProduct> getProducts(Account account, ProductStatus status) throws InvalidTravelProductException,
        DatabaseAccessException;

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

    /**
     * Unset the bidding for the product identified the specified product
     * identifier.
     * 
     * @param productId
     * @return
     * @throws InvalidTravelProductException
     * @throws DatabaseAccessException
     */
    public Bidding unsetBidding(UUID productId) throws InvalidTravelProductException, DatabaseAccessException;

    /**
     * Update the product status from the old status to the new status for the
     * product given by the product identifier.
     * 
     * @param account
     * @param productId
     * @param oldStatus
     * @param newStatus
     * @return
     * @throws InvalidTravelProductException
     * @throws DatabaseAccessException
     */
    public TravelProduct updateProductStatus(Account account, UUID productId, ProductStatus oldStatus, ProductStatus newStatus)
        throws InvalidTravelProductException, DatabaseAccessException;

    /**
     * Publish the product to the search engine for indexing.
     * 
     * @param product
     * @param proposal
     * @return
     * @throws InvalidTravelProductException
     * @throws DatabaseAccessException
     */
    public UUID publishProduct(TravelProduct product, TravelProposal proposal) throws InvalidTravelProductException,
        DatabaseAccessException;

    /**
     * Unpublish the product from the search engine.
     * 
     * @param productId
     * @return
     * @throws InvalidTravelProductException
     * @throws DatabaseAccessException
     */
    public UUID unpublishProduct(UUID productId) throws InvalidTravelProductException, DatabaseAccessException;

    /**
     * Search the travel product by the given template name and parameters
     * result set specified by the pageNum and pageSize.
     * 
     * @param queryTemplate
     * @param templageParams
     * @return
     * @throws DatabaseAccessException
     */
    public JsonObject searchProduct(QueryTemplate queryTemplate, Map<String, Object> templageParams)
        throws DatabaseAccessException;

    /**
     * Retrieve the owner of the product specified by the product identifier.
     * 
     * @param travelProposalId
     * @return
     * @throws DatabaseAccessException
     */
    public Account getTravelProductOwner(UUID productId) throws DatabaseAccessException;
}
