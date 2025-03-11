package com.example.MiniProject1;

import com.example.model.Cart;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.CartRepository;
import com.example.service.CartService;
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
public class CartTests {

    @Value("${spring.application.cartDataPath}")
    private String cartDataPath;

    @Value("${spring.application.userDataPath}")
    private String userDataPath;

    @Value("${spring.application.productDataPath}")
    private String productDataPath;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    private UUID userId;
    private UUID productId;
    private Product testProduct;
    private Cart testCart;

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

        // Create a test cart
        testCart = new Cart(userId, new ArrayList<>());
    }

    private void clearAllData() {
        try {
            objectMapper.writeValue(new File(cartDataPath), new ArrayList<Cart>());
            objectMapper.writeValue(new File(userDataPath), new ArrayList<User>());
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

    private ArrayList<Cart> getCarts() {
        try {
            File file = new File(cartDataPath);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return new ArrayList<>(Arrays.asList(objectMapper.readValue(file, Cart[].class)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read carts", e);
        }
    }


    //------------------------------------- CART TEST CASES ------------------------------------


    // ===== addCart Tests (3) =====
    @Test
    void testAddCart_Success() {
        // Act
        Cart addedCart = cartService.addCart(testCart);

        // Assert
        assertNotNull(addedCart, "Added cart should not be null");
        assertEquals(userId, addedCart.getUserId(), "User ID should match");

        // Verify cart was saved to repository
        ArrayList<Cart> savedCarts = getCarts();
        assertEquals(1, savedCarts.size(), "Should have one cart saved");
        assertEquals(addedCart.getId(), savedCarts.get(0).getId(), "Saved cart ID should match");
    }

    @Test
    void testAddCart_WithProducts() {
        // Arrange
        testCart.addProduct(testProduct);

        // Act
        Cart addedCart = cartService.addCart(testCart);

        // Assert
        assertNotNull(addedCart, "Added cart should not be null");
        assertEquals(1, addedCart.getProducts().size(), "Cart should have one product");
        assertEquals(productId, addedCart.getProducts().get(0).getId(), "Product ID should match");
    }

    @Test
    void testAddCart_MultipleAdditions() {
        // Act
        Cart firstCart = cartService.addCart(new Cart(UUID.randomUUID(), new ArrayList<>()));
        Cart secondCart = cartService.addCart(new Cart(UUID.randomUUID(), new ArrayList<>()));

        // Assert
        ArrayList<Cart> savedCarts = getCarts();
        assertEquals(2, savedCarts.size(), "Should have two carts saved");
        assertTrue(savedCarts.stream().anyMatch(c -> c.getId().equals(firstCart.getId())), "First cart should be saved");
        assertTrue(savedCarts.stream().anyMatch(c -> c.getId().equals(secondCart.getId())), "Second cart should be saved");
    }

    // ===== getCarts Tests (3) =====
    @Test
    void testGetCarts_Empty() {
        // Act
        ArrayList<Cart> carts = cartService.getCarts();

        // Assert
        assertNotNull(carts, "Returned cart list should not be null");
        assertEquals(0, carts.size(), "Should return empty list when no carts exist");
    }

    @Test
    void testGetCarts_Multiple() {
        // Arrange
        Cart cart1 = new Cart(UUID.randomUUID(), new ArrayList<>());
        Cart cart2 = new Cart(UUID.randomUUID(), new ArrayList<>());
        cartService.addCart(cart1);
        cartService.addCart(cart2);

        // Act
        ArrayList<Cart> carts = cartService.getCarts();

        // Assert
        assertEquals(2, carts.size(), "Should return two carts");
        assertTrue(carts.stream().anyMatch(c -> c.getId().equals(cart1.getId())), "Should contain first cart");
        assertTrue(carts.stream().anyMatch(c -> c.getId().equals(cart2.getId())), "Should contain second cart");
    }

    @Test
    void testGetCarts_WithProducts() {
        // Arrange
        Cart cart = new Cart(UUID.randomUUID(), new ArrayList<>());
        cart.addProduct(testProduct);
        cartService.addCart(cart);

        // Act
        ArrayList<Cart> carts = cartService.getCarts();

        // Assert
        assertEquals(1, carts.size(), "Should return one cart");
        assertEquals(1, carts.get(0).getProducts().size(), "Cart should contain one product");
        assertEquals(productId, carts.get(0).getProducts().get(0).getId(), "Product ID should match");
    }

    // ===== getCartById Tests (3) =====
    @Test
    void testGetCartById_Success() {
        // Arrange
        Cart savedCart = cartService.addCart(testCart);
        UUID cartId = savedCart.getId();

        // Act
        Cart retrievedCart = cartService.getCartById(cartId);

        // Assert
        assertNotNull(retrievedCart, "Retrieved cart should not be null");
        assertEquals(cartId, retrievedCart.getId(), "Cart ID should match");
        assertEquals(userId, retrievedCart.getUserId(), "User ID should match");
    }

    @Test
    void testGetCartById_NotFound() {
        // Act
        Cart retrievedCart = cartService.getCartById(UUID.randomUUID());

        // Assert
        assertNull(retrievedCart, "Should return null for non-existent cart ID");
    }

    @Test
    void testGetCartById_WithProducts() {
        // Arrange
        testCart.addProduct(testProduct);
        Cart savedCart = cartService.addCart(testCart);
        UUID cartId = savedCart.getId();

        // Act
        Cart retrievedCart = cartService.getCartById(cartId);

        // Assert
        assertNotNull(retrievedCart, "Retrieved cart should not be null");
        assertEquals(1, retrievedCart.getProducts().size(), "Cart should have one product");
        assertEquals(productId, retrievedCart.getProducts().get(0).getId(), "Product ID should match");
    }

    // ===== getCartByUserId Tests (3) =====
    @Test
    void testGetCartByUserId_Success() {
        // Arrange
        cartService.addCart(testCart);

        // Act
        Cart retrievedCart = cartService.getCartByUserId(userId);

        // Assert
        assertNotNull(retrievedCart, "Retrieved cart should not be null");
        assertEquals(userId, retrievedCart.getUserId(), "User ID should match");
    }

    @Test
    void testGetCartByUserId_NotFound() {
        // Act
        Cart retrievedCart = cartService.getCartByUserId(UUID.randomUUID());

        // Assert
        assertNull(retrievedCart, "Should return null for non-existent user ID");
    }

    @Test
    void testGetCartByUserId_WithProducts() {
        // Arrange
        testCart.addProduct(testProduct);
        cartService.addCart(testCart);

        // Act
        Cart retrievedCart = cartService.getCartByUserId(userId);

        // Assert
        assertNotNull(retrievedCart, "Retrieved cart should not be null");
        assertEquals(1, retrievedCart.getProducts().size(), "Cart should have one product");
        assertEquals(productId, retrievedCart.getProducts().get(0).getId(), "Product ID should match");
    }

    // ===== addProductToCart Tests (3) =====
    @Test
    void testAddProductToCart_Success() {
        // Arrange
        Cart savedCart = cartService.addCart(testCart);
        UUID cartId = savedCart.getId();

        // Act
        cartService.addProductToCart(cartId, testProduct);

        // Assert
        Cart updatedCart = cartService.getCartById(cartId);
        assertNotNull(updatedCart, "Updated cart should not be null");
        assertEquals(1, updatedCart.getProducts().size(), "Cart should have one product");
        assertEquals(productId, updatedCart.getProducts().get(0).getId(), "Product ID should match");
    }

    @Test
    void testAddProductToCart_CartNotFound() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            cartService.addProductToCart(UUID.randomUUID(), testProduct);
        });

        assertEquals("Cart not found", exception.getMessage());
    }

    @Test
    void testAddProductToCart_MultipleProducts() {
        // Arrange
        Cart savedCart = cartService.addCart(testCart);
        UUID cartId = savedCart.getId();
        Product secondProduct = new Product(UUID.randomUUID(), "Second Product", 49.99);
        saveProduct(secondProduct);

        // Act
        cartService.addProductToCart(cartId, testProduct);
        cartService.addProductToCart(cartId, secondProduct);

        // Assert
        Cart updatedCart = cartService.getCartById(cartId);
        assertEquals(2, updatedCart.getProducts().size(), "Cart should have two products");
        assertTrue(updatedCart.getProducts().stream().anyMatch(p -> p.getId().equals(testProduct.getId())),
                "Cart should contain first product");
        assertTrue(updatedCart.getProducts().stream().anyMatch(p -> p.getId().equals(secondProduct.getId())),
                "Cart should contain second product");
    }

    // ===== deleteProductFromCart Tests (3) =====
    @Test
    void testDeleteProductFromCart_Success() {
        // Arrange
        Cart cart = new Cart(userId, new ArrayList<>());
        cart.addProduct(testProduct);
        Cart savedCart = cartService.addCart(cart);
        UUID cartId = savedCart.getId();

        // Verify product is in cart
        Cart cartWithProduct = cartService.getCartById(cartId);
        assertEquals(1, cartWithProduct.getProducts().size(), "Cart should have one product");

        // Act
        cartService.deleteProductFromCart(cartId, testProduct);

        // Assert
        Cart updatedCart = cartService.getCartById(cartId);
        assertEquals(0, updatedCart.getProducts().size(), "Cart should have no products");
    }

    @Test
    void testDeleteProductFromCart_CartNotFound() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            cartService.deleteProductFromCart(UUID.randomUUID(), testProduct);
        });

        assertEquals("Cart not found", exception.getMessage());
    }

    @Test
    void testDeleteProductFromCart_ProductNotFound() {
        // Arrange
        Cart savedCart = cartService.addCart(testCart);
        UUID cartId = savedCart.getId();

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            cartService.deleteProductFromCart(cartId, null);
        });

        assertEquals("Product not found in cart", exception.getMessage());
    }

    // ===== deleteCartById Tests (3) =====
    @Test
    void testDeleteCartById_Success() {
        // Arrange
        Cart savedCart = cartService.addCart(testCart);
        UUID cartId = savedCart.getId();

        // Verify cart exists
        assertNotNull(cartService.getCartById(cartId), "Cart should exist before deletion");

        // Act
        cartService.deleteCartById(cartId);

        // Assert
        assertNull(cartService.getCartById(cartId), "Cart should be deleted");
    }

    @Test
    void testDeleteCartById_CartNotFound() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            cartService.deleteCartById(UUID.randomUUID());
        });

        assertEquals("Cart not found", exception.getMessage());
    }

    @Test
    void testDeleteCartById_WithProducts() {
        // Arrange
        testCart.addProduct(testProduct);
        Cart savedCart = cartService.addCart(testCart);
        UUID cartId = savedCart.getId();

        // Verify cart exists with product
        Cart cartWithProduct = cartService.getCartById(cartId);
        assertNotNull(cartWithProduct, "Cart should exist before deletion");
        assertEquals(1, cartWithProduct.getProducts().size(), "Cart should have one product");

        // Act
        cartService.deleteCartById(cartId);

        // Assert
        assertNull(cartService.getCartById(cartId), "Cart should be deleted");
    }


}