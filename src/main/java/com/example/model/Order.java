package com.example.model;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class Order {
    private UUID id;
    private UUID userId;
    private double totalPrice;
    private List<Product> products=new ArrayList<>();

    // Constructors
    public Order() {
        this.id = UUID.randomUUID(); // Generate ID if not provided
    }

    public Order(UUID id, UUID userId, double totalPrice,List<Product> products) {
        this.id = id;
        this.userId = userId;
        this.products = products;
        this.totalPrice = calculateTotalPrice();
    }

    public Order(UUID userId, List<Product> products) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.products = products;
        this.totalPrice = calculateTotalPrice();
    }

    // Getters and Setters
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
        this.totalPrice = calculateTotalPrice();
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    // Helper method to calculate total price
    private double calculateTotalPrice() {
        return products.stream().mapToDouble(Product::getPrice).sum();
    }
}
