package com.free.walker.service.itinerary.rest;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

public class Order {
    private long id;
    private String description;
    private Map<Long, Product> products = new HashMap<Long, Product>();

    public Order() {
        init();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String d) {
        this.description = d;
    }

    @GET
    @Path("products/{productId}/")
    public Product getProduct(@PathParam("productId") int productId) {
        System.out.println("----invoking getProduct with id: " + productId);
        Product p = products.get(new Long(productId));
        return p;
    }

    final void init() {
        Product p = new Product();
        p.setId(323);
        p.setDescription("product 323");
        products.put(p.getId(), p);
    }
}
