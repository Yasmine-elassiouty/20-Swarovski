package com.example.MiniProject1;

import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.OrderRepository;
import com.example.service.OrderService;
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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ComponentScan(basePackages = "com.example.*")
@SpringBootTest
public class OrderTests {

    @Value("${spring.application.orderDataPath}")
    private String orderDataPath;

    @Value("${spring.application.productDataPath}")
    private String productDataPath;

    @Value("${spring.application.userDataPath}")
    private String userDataPath;


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    private UUID userId;
    private UUID productId;
    private Product testProduct;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        // Clear all data before each test
        clearAllData();

        // Create test data
        userId = UUID.randomUUID();
        productId = UUID.randomUUID();

        // Create a test product
        testProduct = new Product(productId, "Test Product", 99.99);
        saveProduct(testProduct);

        // Create a test order
        List<Product> products = new ArrayList<>();
        products.add(testProduct);
        testOrder = new Order(userId, products);
    }

    private void clearAllData() {
        try {
            objectMapper.writeValue(new File(orderDataPath), new ArrayList<Order>());
            objectMapper.writeValue(new File(productDataPath), new ArrayList<Product>());
            objectMapper.writeValue(new File(userDataPath), new ArrayList<User>());
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

    private void saveOrder(Order order) {
        try {
            File file = new File(orderDataPath);
            ArrayList<Order> orders;
            if (!file.exists()) {
                orders = new ArrayList<>();
            } else {
                orders = new ArrayList<>(Arrays.asList(objectMapper.readValue(file, Order[].class)));
            }
            orders.add(order);
            objectMapper.writeValue(file, orders);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save order", e);
        }
    }

    private ArrayList<Order> getOrders() {
        try {
            File file = new File(orderDataPath);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return new ArrayList<>(Arrays.asList(objectMapper.readValue(file, Order[].class)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read orders", e);
        }
    }

    // ===== addOrder Tests (3) =====
    @Test
    void testAddOrder_Success() {
        // Act
        orderService.addOrder(testOrder);

        // Assert
        ArrayList<Order> savedOrders = getOrders();
        assertEquals(1, savedOrders.size(), "Should have one order saved");
        assertEquals(testOrder.getId(), savedOrders.get(0).getId(), "Order ID should match");
        assertEquals(userId, savedOrders.get(0).getUserId(), "User ID should match");
        assertEquals(1, savedOrders.get(0).getProducts().size(), "Order should have one product");
        assertEquals(productId, savedOrders.get(0).getProducts().get(0).getId(), "Product ID should match");
    }

    @Test
    void testAddOrder_WithMultipleProducts() {
        // Arrange
        Product secondProduct = new Product(UUID.randomUUID(), "Second Product", 49.99);
        saveProduct(secondProduct);
        testOrder.getProducts().add(secondProduct);

        // Act
        orderService.addOrder(testOrder);

        // Assert
        ArrayList<Order> savedOrders = getOrders();
        assertEquals(1, savedOrders.size(), "Should have one order saved");

        // Ensure the correct order is retrieved
        assertEquals(testOrder.getId(), savedOrders.get(0).getId(), "Order ID should match the inserted order");

        // Validate order details
        assertEquals(2, savedOrders.get(0).getProducts().size(), "Order should have two products");
        assertEquals(149.98, savedOrders.get(0).getTotalPrice(), 0.01, "Total price should be sum of product prices");
    }


    @Test
    void testAddOrder_MultipleOrders() {
        // Arrange: Create two unique orders
        Order secondOrder = new Order(UUID.randomUUID(), new ArrayList<>());

        // Act: Add both orders
        orderService.addOrder(testOrder);
        orderService.addOrder(secondOrder);

        // Assert: Retrieve all saved orders
        ArrayList<Order> savedOrders = getOrders();

        // ✅ Ensure exactly two unique orders are saved
        assertEquals(2, savedOrders.size(), "Should have exactly two orders saved");

        // ✅ Verify both orders exist
        assertTrue(savedOrders.stream().anyMatch(o -> o.getId().equals(testOrder.getId())), "First order should be saved");
        assertTrue(savedOrders.stream().anyMatch(o -> o.getId().equals(secondOrder.getId())), "Second order should be saved");

        // ✅ Check for duplicate orders (no duplicate IDs should exist)
        long uniqueOrderCount = savedOrders.stream().map(Order::getId).distinct().count();
        assertEquals(2, uniqueOrderCount, "Orders should not be duplicated");

        // ✅ Validate that product lists are correct
        Order savedFirstOrder = savedOrders.stream().filter(o -> o.getId().equals(testOrder.getId())).findFirst().orElse(null);
        assertNotNull(savedFirstOrder, "First order should be retrievable");
        assertEquals(testOrder.getProducts().size(), savedFirstOrder.getProducts().size(), "First order should retain its products");

        Order savedSecondOrder = savedOrders.stream().filter(o -> o.getId().equals(secondOrder.getId())).findFirst().orElse(null);
        assertNotNull(savedSecondOrder, "Second order should be retrievable");
        assertEquals(0, savedSecondOrder.getProducts().size(), "Second order should have no products");
    }


    // ===== getOrders Tests (3) =====
    @Test
    void testGetOrders_Empty() {
        // Act
        ArrayList<Order> orders = orderService.getOrders();

        // Assert
        assertNotNull(orders, "Returned order list should not be null");
        assertEquals(0, orders.size(), "Should return empty list when no orders exist");
    }

    @Test
    void testGetOrders_Single() {
        // Arrange
        saveOrder(testOrder);

        // Act
        ArrayList<Order> orders = orderService.getOrders();

        // Assert
        assertNotNull(orders, "Returned order list should not be null");
        assertEquals(1, orders.size(), "Should return one order");
        assertEquals(testOrder.getId(), orders.get(0).getId(), "Order ID should match");
    }

    @Test
    void testGetOrders_Multiple() {
        // Arrange: Create multiple orders with unique IDs
        Order order1 = new Order(UUID.randomUUID(), new ArrayList<>());
        Order order2 = new Order(UUID.randomUUID(), new ArrayList<>());

        // Save the orders
        saveOrder(testOrder);
        saveOrder(order1);
        saveOrder(order2);

        // Act: Retrieve all orders
        ArrayList<Order> orders = orderService.getOrders();

        // Assert: Validate order count and presence
        assertNotNull(orders, "Returned order list should not be null");
        assertEquals(3, orders.size(), "Should return exactly three orders");

        // Check each order's existence
        assertTrue(orders.stream().anyMatch(o -> o.getId().equals(testOrder.getId())), "Should contain test order");
        assertTrue(orders.stream().anyMatch(o -> o.getId().equals(order1.getId())), "Should contain first order");
        assertTrue(orders.stream().anyMatch(o -> o.getId().equals(order2.getId())), "Should contain second order");

        // Ensure no duplicate IDs exist
        long uniqueIds = orders.stream().map(Order::getId).distinct().count();
        assertEquals(orders.size(), uniqueIds, "No duplicate order IDs should exist");

        // Ensure correct user associations (if applicable)
        if (!orders.isEmpty()) {
            for (Order order : orders) {
                assertNotNull(order.getUserId(), "Each order should have a valid user ID");
            }
        }
    }


    // ===== getOrderById Tests (3) =====
    @Test
    void testGetOrderById_Success() {
        // Arrange: Save a valid order
        saveOrder(testOrder);

        // Act: Retrieve order by ID
        Order retrievedOrder = orderService.getOrderById(testOrder.getId());

        // Assert: Validate retrieved order
        assertNotNull(retrievedOrder, "Retrieved order should not be null");
        assertEquals(testOrder.getId(), retrievedOrder.getId(), "Order ID should match");
        assertEquals(userId, retrievedOrder.getUserId(), "User ID should match");
        assertEquals(1, retrievedOrder.getProducts().size(), "Order should have one product");
        assertEquals(testOrder.getProducts().get(0).getId(), retrievedOrder.getProducts().get(0).getId(), "Product ID should match");
    }


    @Test
    void testGetOrderById_NotFound() {
        // Act
        Order retrievedOrder = orderService.getOrderById(UUID.randomUUID());

        // Assert
        assertNull(retrievedOrder, "Should return null for non-existent order ID");
    }

    @Test
    void testGetOrderById_NullId() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.getOrderById(null);
        }, "Should throw an exception for null order ID");
    }

    // ===== deleteOrderById Tests (3) =====
    @Test
    void testDeleteOrderById_Success() {
        // Arrange
        saveOrder(testOrder);

        // Verify order exists
        assertNotNull(orderService.getOrderById(testOrder.getId()), "Order should exist before deletion");

        // Act
        orderService.deleteOrderById(testOrder.getId());

        // Assert
        assertNull(orderService.getOrderById(testOrder.getId()), "Order should be deleted");
    }

    @Test
    void testDeleteOrderById_OrderNotFound() {
        // Act & Assert: Try deleting a non-existent order and expect an exception
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.deleteOrderById(UUID.randomUUID());
        }, "Should throw IllegalArgumentException when order not found");
    }


    @Test
    void testDeleteOrderById_MultipleOrders() {
        // Arrange - Add multiple orders
        Order order1 = new Order(UUID.randomUUID(), new ArrayList<>());
        Order order2 = new Order(UUID.randomUUID(), new ArrayList<>());
        saveOrder(testOrder);
        saveOrder(order1);
        saveOrder(order2);

        // Verify orders exist
        assertEquals(3, orderService.getOrders().size(), "Should have three orders before deletion");

        // Act - Delete first order
        orderService.deleteOrderById(testOrder.getId());

        // Assert
        assertEquals(2, orderService.getOrders().size(), "Should have two orders after deletion");
        assertNull(orderService.getOrderById(testOrder.getId()), "Deleted order should not be found");
        assertNotNull(orderService.getOrderById(order1.getId()), "Other orders should still exist");
        assertNotNull(orderService.getOrderById(order2.getId()), "Other orders should still exist");
    }
}