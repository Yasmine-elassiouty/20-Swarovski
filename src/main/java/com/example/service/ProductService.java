package com.example.service;

import com.example.model.Product;
import com.example.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class ProductService extends MainService<Product> {

    @Autowired
    public ProductService(ProductRepository productRepository) {

        super(productRepository);
    }

    /**
     * Adds a new product to the system.
     *
     * @param product The product to add.
     * @return The added product.
     */
    public Product addProduct(Product product) {
        save(product); // Uses the save() method from MainService
        return product;
    }

    /**
     * Retrieves all products from the system.
     *
     * @return A list of all products.
     */
    public ArrayList<Product> getProducts() {
        return findAll(); // Uses the findAll() method from MainService
    }

    /**
     * Retrieves a specific product by its ID.
     *
     * @param productId The ID of the product to retrieve.
     * @return The product with the specified ID, or null if not found.
     */
    public Product getProductById(UUID productId) {
        ArrayList<Product> products = findAll(); // Uses the findAll() method from MainService
        return products.stream()
                .filter(product -> product.getId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Updates a product with a new name and price.
     *
     * @param productId The ID of the product to update.
     * @param newName   The new name for the product.
     * @param newPrice  The new price for the product.
     * @return The updated product.
     */
    public Product updateProduct(UUID productId, String newName, double newPrice) {
        ArrayList<Product> products = findAll(); // Uses the findAll() method from MainService
        for (Product product : products) {
            if (product.getId().equals(productId)) {
                product.setName(newName);
                product.setPrice(newPrice);
                saveAll(products); // Uses the saveAll() method from MainService
                return product;
            }
        }
        return null;
    }

    /**
     * Applies a discount to a list of products.
     *
     * @param discount  The discount percentage to apply (e.g., 60 means 60% discount).
     * @param productIds The list of product IDs to apply the discount to.
     */
    public void applyDiscount(double discount, ArrayList<UUID> productIds) {
        ArrayList<Product> products = findAll(); // Uses the findAll() method from MainService
        for (Product product : products) {
            if (productIds.contains(product.getId())) {
                double newPrice = product.getPrice() * (1 - discount / 100);
                product.setPrice(newPrice);
            }
        }
        saveAll(products); // Uses the saveAll() method from MainService
    }

    /**
     * Deletes a product by its ID.
     *
     * @param productId The ID of the product to delete.
     */
    public void deleteProductById(UUID productId) {
        ArrayList<Product> products = findAll(); // Uses the findAll() method from MainService
        products.removeIf(product -> product.getId().equals(productId));
        saveAll(products); // Uses the saveAll() method from MainService
    }
}