package com.example.MiniProject1;

import com.example.model.Product;
import com.example.repository.ProductRepository;
import com.example.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ComponentScan(basePackages = "com.example.*")
@SpringBootTest
public class ProductTests {

    @Value("${spring.application.productDataPath}")
    private String productDataPath;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private UUID productId;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        // Clear all data before each test
        clearAllData();

        // Create test data
        productId = UUID.randomUUID();
        testProduct = new Product(productId, "Test Product", 99.99);
    }

    private void clearAllData() {
        try {
            objectMapper.writeValue(new File(productDataPath), new ArrayList<Product>());
        } catch (IOException e) {
            throw new RuntimeException("Failed to clear data files", e);
        }
    }

    private void saveProduct(Product product) {
        try {
            File file = new File(productDataPath);
            ArrayList<Product> products;
            if (!file.exists()) {
                products = new ArrayList<>();
            } else {
                products = new ArrayList<>(Arrays.asList(objectMapper.readValue(file, Product[].class)));
            }
            products.add(product);
            objectMapper.writeValue(file, products);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save product", e);
        }
    }

    private ArrayList<Product> getProducts() {
        try {
            File file = new File(productDataPath);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return new ArrayList<>(Arrays.asList(objectMapper.readValue(file, Product[].class)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read products", e);
        }
    }

    // ===== addProduct Tests (3) =====
    @Test
    void testAddProduct_Success() {
        // Act
        Product addedProduct = productService.addProduct(testProduct);

        // Assert
        assertNotNull(addedProduct, "Added product should not be null");
        assertEquals(productId, addedProduct.getId(), "Product ID should match");
        assertEquals("Test Product", addedProduct.getName(), "Product name should match");
        assertEquals(99.99, addedProduct.getPrice(), 0.01, "Product price should match");

        // Verify product was saved to repository
        ArrayList<Product> savedProducts = getProducts();
        assertEquals(1, savedProducts.size(), "Should have one product saved");
        assertEquals(productId, savedProducts.get(0).getId(), "Saved product ID should match");
    }

    @Test
    void testAddProduct_MultipleProducts() {
        // Arrange
        Product secondProduct = new Product(UUID.randomUUID(), "Second Product", 49.99);

        // Act
        productService.addProduct(testProduct);
        productService.addProduct(secondProduct);

        // Assert
        ArrayList<Product> savedProducts = getProducts();
        assertEquals(2, savedProducts.size(), "Should have two products saved");
        assertTrue(savedProducts.stream().anyMatch(p -> p.getId().equals(testProduct.getId())), "First product should be saved");
        assertTrue(savedProducts.stream().anyMatch(p -> p.getId().equals(secondProduct.getId())), "Second product should be saved");
    }

    @Test
    void testAddProduct_WithCustomPrice() {
        // Arrange
        Product expensiveProduct = new Product(UUID.randomUUID(), "Expensive Product", 999.99);

        // Act
        productService.addProduct(expensiveProduct);

        // Assert
        ArrayList<Product> savedProducts = getProducts();
        assertEquals(1, savedProducts.size(), "Should have one product saved");
        assertEquals(999.99, savedProducts.get(0).getPrice(), 0.01, "Product price should match");
    }

    // ===== getProducts Tests (3) =====
    @Test
    void testGetProducts_Empty() {
        // Act
        ArrayList<Product> products = productService.getProducts();

        // Assert
        assertNotNull(products, "Returned product list should not be null");
        assertEquals(0, products.size(), "Should return empty list when no products exist");
    }

    @Test
    void testGetProducts_Single() {
        // Arrange
        saveProduct(testProduct);

        // Act
        ArrayList<Product> products = productService.getProducts();

        // Assert
        assertNotNull(products, "Returned product list should not be null");
        assertEquals(1, products.size(), "Should return one product");
        assertEquals(testProduct.getId(), products.get(0).getId(), "Product ID should match");
    }

    @Test
    void testGetProducts_Multiple() {
        // Arrange
        Product product1 = new Product(UUID.randomUUID(), "Product 1", 10.99);
        Product product2 = new Product(UUID.randomUUID(), "Product 2", 20.99);
        saveProduct(testProduct);
        saveProduct(product1);
        saveProduct(product2);

        // Act
        ArrayList<Product> products = productService.getProducts();

        // Assert
        assertEquals(3, products.size(), "Should return three products");
        assertTrue(products.stream().anyMatch(p -> p.getId().equals(testProduct.getId())), "Should contain test product");
        assertTrue(products.stream().anyMatch(p -> p.getId().equals(product1.getId())), "Should contain first product");
        assertTrue(products.stream().anyMatch(p -> p.getId().equals(product2.getId())), "Should contain second product");
    }

    // ===== getProductById Tests (3) =====
    @Test
    void testGetProductById_Success() {
        // Arrange
        saveProduct(testProduct);

        // Act
        Product retrievedProduct = productService.getProductById(productId);

        // Assert
        assertNotNull(retrievedProduct, "Retrieved product should not be null");
        assertEquals(productId, retrievedProduct.getId(), "Product ID should match");
        assertEquals("Test Product", retrievedProduct.getName(), "Product name should match");
        assertEquals(99.99, retrievedProduct.getPrice(), 0.01, "Product price should match");
    }

    @Test
    void testGetProductById_NotFound() {
        // Act
        Product retrievedProduct = productService.getProductById(UUID.randomUUID());

        // Assert
        assertNull(retrievedProduct, "Should return null for non-existent product ID");
    }

    @Test
    void testGetProductById_MultipleProducts() {
        // Arrange
        Product product1 = new Product(UUID.randomUUID(), "Product 1", 10.99);
        Product product2 = new Product(UUID.randomUUID(), "Product 2", 20.99);
        saveProduct(testProduct);
        saveProduct(product1);
        saveProduct(product2);

        // Act
        Product retrievedProduct = productService.getProductById(product1.getId());

        // Assert
        assertNotNull(retrievedProduct, "Retrieved product should not be null");
        assertEquals(product1.getId(), retrievedProduct.getId(), "Product ID should match");
        assertEquals("Product 1", retrievedProduct.getName(), "Product name should match");
    }

    // ===== updateProduct Tests (3) =====
    @Test
    void testUpdateProduct_Success() {
        // Arrange
        saveProduct(testProduct);

        // Act
        Product updatedProduct = productService.updateProduct(productId, "Updated Product", 149.99);

        // Assert
        assertNotNull(updatedProduct, "Updated product should not be null");
        assertEquals(productId, updatedProduct.getId(), "Product ID should remain the same");
        assertEquals("Updated Product", updatedProduct.getName(), "Product name should be updated");
        assertEquals(149.99, updatedProduct.getPrice(), 0.01, "Product price should be updated");

        // Verify changes were saved
        ArrayList<Product> savedProducts = getProducts();
        assertEquals(1, savedProducts.size(), "Should still have one product");
        assertEquals("Updated Product", savedProducts.get(0).getName(), "Saved product name should be updated");
        assertEquals(149.99, savedProducts.get(0).getPrice(), 0.01, "Saved product price should be updated");
    }

    @Test
    void testUpdateProduct_NotFound() {
        // Act
        Product updatedProduct = productService.updateProduct(UUID.randomUUID(), "Updated Product", 149.99);

        // Assert
        assertNull(updatedProduct, "Should return null for non-existent product ID");
    }

    @Test
    void testUpdateProduct_OnlyName() {
        // Arrange
        saveProduct(testProduct);
        double originalPrice = testProduct.getPrice();

        // Act
        Product updatedProduct = productService.updateProduct(productId, "Updated Product", originalPrice);

        // Assert
        assertNotNull(updatedProduct, "Updated product should not be null");
        assertEquals("Updated Product", updatedProduct.getName(), "Product name should be updated");
        assertEquals(originalPrice, updatedProduct.getPrice(), 0.01, "Product price should remain the same");
    }

    // ===== applyDiscount Tests (3) =====
    @Test
    void testApplyDiscount_SingleProduct() {
        // Arrange
        saveProduct(testProduct);
        ArrayList<UUID> productIds = new ArrayList<>();
        productIds.add(productId);

        // Act
        productService.applyDiscount(10.0, productIds);

        // Assert
        ArrayList<Product> savedProducts = getProducts();
        assertEquals(1, savedProducts.size(), "Should still have one product");
        assertEquals(89.99, savedProducts.get(0).getPrice(), 0.01, "Product price should be discounted by 10%");
    }

    @Test
    void testApplyDiscount_MultipleProducts() {
        // Arrange
        Product product1 = new Product(UUID.randomUUID(), "Product 1", 100.0);
        Product product2 = new Product(UUID.randomUUID(), "Product 2", 200.0);
        saveProduct(product1);
        saveProduct(product2);

        ArrayList<UUID> productIds = new ArrayList<>();
        productIds.add(product1.getId());
        productIds.add(product2.getId());

        // Act
        productService.applyDiscount(20.0, productIds);

        // Assert
        ArrayList<Product> savedProducts = getProducts();
        assertEquals(2, savedProducts.size(), "Should have two products");

        for (Product product : savedProducts) {
            if (product.getId().equals(product1.getId())) {
                assertEquals(80.0, product.getPrice(), 0.01, "First product price should be discounted by 20%");
            } else if (product.getId().equals(product2.getId())) {
                assertEquals(160.0, product.getPrice(), 0.01, "Second product price should be discounted by 20%");
            }
        }
    }

    @Test
    void testApplyDiscount_SelectiveProducts() {
        // Arrange
        Product product1 = new Product(UUID.randomUUID(), "Product 1", 100.0);
        Product product2 = new Product(UUID.randomUUID(), "Product 2", 200.0);
        saveProduct(product1);
        saveProduct(product2);

        ArrayList<UUID> productIds = new ArrayList<>();
        productIds.add(product1.getId()); // Only apply discount to first product

        // Act
        productService.applyDiscount(50.0, productIds);

        // Assert
        ArrayList<Product> savedProducts = getProducts();
        assertEquals(2, savedProducts.size(), "Should have two products");

        for (Product product : savedProducts) {
            if (product.getId().equals(product1.getId())) {
                assertEquals(50.0, product.getPrice(), 0.01, "First product price should be discounted by 50%");
            } else if (product.getId().equals(product2.getId())) {
                assertEquals(200.0, product.getPrice(), 0.01, "Second product price should remain unchanged");
            }
        }
    }

    // ===== deleteProductById Tests (3) =====
    @Test
    void testDeleteProductById_Success() {
        // Arrange
        saveProduct(testProduct);

        // Act
        productService.deleteProductById(productId);

        // Assert
        ArrayList<Product> savedProducts = getProducts();
        assertEquals(0, savedProducts.size(), "Should have no products after deletion");
    }

    @Test
    void testDeleteProductById_NotFound() {
        // Arrange - empty repository

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            productService.deleteProductById(UUID.randomUUID());
        }, "Should throw exception when product not found");
    }

    @Test
    void testDeleteProductById_SelectiveDelete() {
        // Arrange
        Product product1 = new Product(UUID.randomUUID(), "Product 1", 100.0);
        Product product2 = new Product(UUID.randomUUID(), "Product 2", 200.0);
        saveProduct(testProduct);
        saveProduct(product1);
        saveProduct(product2);
        
        // Verify we have 3 products before deletion
        ArrayList<Product> productsBeforeDeletion = getProducts();
        assertEquals(3, productsBeforeDeletion.size(), "Should have three products before deletion");
        
        // Act
        productService.deleteProductById(product1.getId());
        
        // Assert
        ArrayList<Product> remainingProducts = getProducts();
        assertEquals(2, remainingProducts.size(), "Should have two products after selective deletion");
        assertTrue(remainingProducts.stream().anyMatch(p -> p.getId().equals(testProduct.getId())), "Test product should still exist");
        assertTrue(remainingProducts.stream().anyMatch(p -> p.getId().equals(product2.getId())), "Product 2 should still exist");
        assertFalse(remainingProducts.stream().anyMatch(p -> p.getId().equals(product1.getId())), "Product 1 should be deleted");
    }
}