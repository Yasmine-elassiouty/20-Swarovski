package com.example.model;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class Cart {
    private UUID id;
    private UUID userId;
    private List<Product> products;

    // Empty Constructor
    public Cart() {
        this.id = UUID.randomUUID();
        this.products = new ArrayList<>();
    }

    // without ID (For Creating New Carts)
    public Cart(UUID userId, List<Product> products) {
        this.id = UUID.randomUUID();  // Generate a new ID automatically
        this.userId = userId;
        this.products = products != null ? products : new ArrayList<>();
    }

    // Full, for Loading Existing Carts
    public Cart(UUID id, UUID userId, List<Product> products) {
        this.id = id;
        this.userId = userId;
        this.products = products != null ? products : new ArrayList<>();
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void addProduct(Product product) {
        this.products.add(product);
    }

    public void removeProduct(Product product) {
        this.products.remove(product);
    }
}
