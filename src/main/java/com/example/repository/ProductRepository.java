package com.example.repository;

import com.example.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.UUID;

@Repository
public class ProductRepository extends MainRepository<Product> {

    // Constructor
    public ProductRepository() {
        super();
    }

    // Provide the path to the JSON file for products
    @Override
    protected String getDataPath() {
        return "data/products.json";
    }

    // Provide the array type for deserialization
    @Override
    protected Class<Product[]> getArrayType() {
        return Product[].class;
    }

    // Add a new product
    public Product addProduct(Product product) {
        ArrayList<Product> products = findAll();
        products.add(product);
        saveAll(products);
        return product;
    }

    // Get all products
    public ArrayList<Product> getProducts() {
        return findAll();
    }

    // Get a specific product by ID
    public Product getProductById(UUID productId) {
        ArrayList<Product> products = findAll();
        return products.stream()
                .filter(product -> product.getId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    // Update a product
    public Product updateProduct(UUID productId, String newName, double newPrice) {
        ArrayList<Product> products = findAll();
        for (Product product : products) {
            if (product.getId().equals(productId)) {
                product.setName(newName);
                product.setPrice(newPrice);
                saveAll(products);
                return product;
            }
        }
        return null;
    }

    // Apply discount to a list of products
    public void applyDiscount(double discount, ArrayList<UUID> productIds) {
        ArrayList<Product> products = findAll();
        for (Product product : products) {
            if (productIds.contains(product.getId())) {
                double newPrice = product.getPrice() * (1 - discount / 100);
                product.setPrice(newPrice);
            }
        }
        saveAll(products);
    }

    // Delete a product by ID
    public void deleteProductById(UUID productId) {
        ArrayList<Product> products = findAll();
        products.removeIf(product -> product.getId().equals(productId));
        saveAll(products);
    }
}