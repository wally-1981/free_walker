package com.free.walker.service.itinerary.dao.memo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.free.walker.service.itinerary.Constants;
import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.basic.Account;
import com.free.walker.service.itinerary.dao.DAOFactory;
import com.free.walker.service.itinerary.dao.TravelProductDAO;
import com.free.walker.service.itinerary.dao.TravelRequirementDAO;
import com.free.walker.service.itinerary.exp.DatabaseAccessException;
import com.free.walker.service.itinerary.exp.InvalidTravelProductException;
import com.free.walker.service.itinerary.primitive.AccountType;
import com.free.walker.service.itinerary.primitive.Introspection;
import com.free.walker.service.itinerary.primitive.ProductStatus;
import com.free.walker.service.itinerary.primitive.QueryTemplate;
import com.free.walker.service.itinerary.product.Bidding;
import com.free.walker.service.itinerary.product.HotelItem;
import com.free.walker.service.itinerary.product.ResortItem;
import com.free.walker.service.itinerary.product.SimpleTravelProduct;
import com.free.walker.service.itinerary.product.TrafficItem;
import com.free.walker.service.itinerary.product.TravelProduct;
import com.free.walker.service.itinerary.product.TravelProductItem;
import com.free.walker.service.itinerary.product.TrivItem;
import com.free.walker.service.itinerary.req.TravelProposal;

public class InMemoryTravelProductDAOImpl implements TravelProductDAO {
    protected Map<UUID, TravelProduct> travelProducts;
    protected Map<UUID, List<TravelProductItem>> travelProductItems;
    protected Map<UUID, Bidding> travelProductBiddings;
    protected Map<String, Set<TravelProduct>> travelProductsByAccount;

    private TravelRequirementDAO travelRequirementDao;

    private static class SingletonHolder {
        private static final TravelProductDAO INSTANCE = new InMemoryTravelProductDAOImpl();
    }

    private InMemoryTravelProductDAOImpl() {
        travelProducts = new HashMap<UUID, TravelProduct>();
        travelProductItems = new HashMap<UUID, List<TravelProductItem>>();
        travelProductBiddings = new HashMap<UUID, Bidding>();
        travelProductsByAccount = new HashMap<String, Set<TravelProduct>>();
        travelRequirementDao = DAOFactory.getTravelRequirementDAO(InMemoryTravelRequirementDAOImpl.class.getName());
    }

    public static TravelProductDAO getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public boolean pingPersistence() {
        return true;
    }

    public UUID createProduct(Account account, TravelProduct travelProduct) throws InvalidTravelProductException,
        DatabaseAccessException {
        if (account == null || travelProduct == null) {
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

        if (travelProductsByAccount.containsKey(account.getUuid())) {
            travelProductsByAccount.get(account.getUuid()).add(travelProduct);
        } else {
            Set<TravelProduct> products = new HashSet<TravelProduct>();
            products.add(travelProduct);
            travelProductsByAccount.put(account.getUuid(), products);
        }

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

    public List<TravelProduct> getProducts(Account account, ProductStatus status) throws InvalidTravelProductException,
        DatabaseAccessException {
        if (account == null || status == null) {
            throw new NullPointerException();
        }

        List<TravelProduct> result = new ArrayList<TravelProduct>();

        if (AccountType.isTouristAccount(account.getAccountType().ordinal())) {
            Iterator<TravelProduct> products = travelProducts.values().iterator();
            while (products.hasNext()) {
                TravelProduct product = (TravelProduct) products.next();
                Account proposalOwner = travelRequirementDao.getTravelProposalOwner(product.getProposalUUID());
                if (status.equals(product.getStatus()) && account.equals(proposalOwner)) {
                    result.add(product);
                }
            }
        } else {
            Set<TravelProduct> products = travelProductsByAccount.get(account.getUuid());
            if (products == null) {
                return result;
            }

            for (Iterator<TravelProduct> iterator = products.iterator(); iterator.hasNext();) {
                TravelProduct travelProduct = iterator.next();
                if (status.equals(travelProduct.getStatus())) {
                    result.add(travelProduct);
                }
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

    public UUID updateProductStatus(Account account, UUID productId, ProductStatus oldStatus, ProductStatus newStatus)
        throws InvalidTravelProductException, DatabaseAccessException {
        if (account == null || productId == null || newStatus == null) {
            throw new NullPointerException();
        }

        TravelProduct travelProduct = travelProducts.get(productId);
        if (travelProduct == null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.missing_travel_product,
                productId), productId);
        }

        if (oldStatus != null) {
            ProductStatus currentStatus = travelProduct.getStatus();
            if (!oldStatus.equals(currentStatus)) {
                throw new InvalidTravelProductException(LocalMessages.getMessage(
                    LocalMessages.miss_travel_product_status, productId, oldStatus.enumValue(),
                    currentStatus.enumValue()));
            }
        }

        Set<TravelProduct> products = travelProductsByAccount.get(account.getUuid());
        if (products == null || !products.contains(travelProduct)) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(
                LocalMessages.illegal_submit_product_operation, productId, account.getUuid()), productId);
        }

        ((SimpleTravelProduct) travelProduct.getCore()).setStatus(newStatus);

        return productId;
    }

    public UUID publishProduct(TravelProduct product, TravelProposal proposal) throws InvalidTravelProductException, DatabaseAccessException {
        if (product == null || proposal == null) {
            throw new NullPointerException();
        }

        if (!product.getProposalUUID().equals(proposal.getUUID())) {
            throw new IllegalArgumentException();
        }

        Account proposalOwner = travelRequirementDao.getTravelProposalOwner(proposal.getUUID());
        if (proposalOwner == null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.miss_travel_proposal_owner,
                proposal.getUUID()), proposal.getUUID());
        }

        Account productOwner = this.getTravelProductOwner(product.getProductUUID());
        if (productOwner == null) {
            throw new InvalidTravelProductException(LocalMessages.getMessage(LocalMessages.miss_travel_product_owner,
                product.getProductUUID()), product.getProductUUID());
        }

        travelProducts.put(product.getProductUUID(), product);

        return product.getProductUUID();
    }

    public UUID unpublishProduct(UUID productId) throws InvalidTravelProductException, DatabaseAccessException {
        if (productId == null) {
            throw new NullPointerException();
        }

        if (!travelProducts.containsKey(productId)) {
            return null;
        }

        return productId;
    }

    public JsonObject searchProduct(QueryTemplate template, Map<String, String> templageParams)
        throws DatabaseAccessException {
        if (template == null || templageParams == null) {
            throw new NullPointerException();
        }

        JsonObjectBuilder resultBuilder = Json.createObjectBuilder();
        resultBuilder.add(Introspection.JSONKeys.TOTAL_HITS_NUMBER, 1000);
        resultBuilder.add(Introspection.JSONKeys.HITS, Json.createArrayBuilder()
            .add(Json.createObjectBuilder().build()));

        return resultBuilder.build();
    }

    public Account getTravelProductOwner(UUID productId) throws DatabaseAccessException {
        if (productId == null) {
            throw new NullPointerException();
        }

        Iterator<String> accountIdIter = travelProductsByAccount.keySet().iterator();
        while (accountIdIter.hasNext()) {
            String accountId = (String) accountIdIter.next();
            Iterator<TravelProduct> productIter = travelProductsByAccount.get(accountId).iterator();
            while (productIter.hasNext()) {
                TravelProduct product = (TravelProduct) productIter.next();
                if (productId.equals(product.getProductUUID())) {
                    if (Constants.DEFAULT_ACCOUNT.getUuid().equals(accountId)) {
                        return Constants.DEFAULT_ACCOUNT;
                    } else if (Constants.DEFAULT_AGENCY_ACCOUNT.getUuid().equals(accountId)) {
                        return Constants.DEFAULT_AGENCY_ACCOUNT;
                    } else if (Constants.ADMIN_ACCOUNT.getUuid().equals(accountId)) {
                        return Constants.ADMIN_ACCOUNT;
                    } else {
                        continue;
                    }
                }
            }
        }
        return null;
    }
}
