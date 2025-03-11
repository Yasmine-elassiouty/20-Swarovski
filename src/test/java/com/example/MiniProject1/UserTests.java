package com.example.MiniProject1;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.CartRepository;
import com.example.repository.OrderRepository;
import com.example.repository.ProductRepository;
import com.example.repository.UserRepository;
import com.example.service.CartService;
import com.example.service.OrderService;
import com.example.service.ProductService;
import com.example.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ComponentScan(basePackages = "com.example.*")


@SpringBootTest
public class UserTests {
    @Value("${spring.application.userDataPath}")
    private String userDataPath;

    @Value("${spring.application.productDataPath}")
    private String productDataPath;

    @Value("${spring.application.orderDataPath}")
    private String orderDataPath;

    @Value("${spring.application.cartDataPath}")
    private String cartDataPath;

    @Autowired
    private ObjectMapper objectMapper;



    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;


    public void overRideAll(){
        try{
            objectMapper.writeValue(new File(userDataPath), new ArrayList<User>());
            objectMapper.writeValue(new File(productDataPath), new ArrayList<Product>());
            objectMapper.writeValue(new File(orderDataPath), new ArrayList<Order>());
            objectMapper.writeValue(new File(cartDataPath), new ArrayList<Cart>());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to JSON file", e);
        }
    }

    public Object find(String typeString, Object toFind){
        switch(typeString){
            case "User":
                ArrayList<User> users = getUsers();

                for(User user: users){
                    if(user.getId().equals(((User)toFind).getId())){
                        return user;
                    }
                }
                break;
            case "Product":
                ArrayList<Product> products = getProducts();
                for(Product product: products){
                    if(product.getId().equals(((Product)toFind).getId())){
                        return product;
                    }
                }
                break;
            case "Order":
                ArrayList<Order> orders = getOrders();
                for(Order order: orders){
                    if(order.getId().equals(((Order)toFind).getId())){
                        return order;
                    }
                }
                break;
            case "Cart":
                ArrayList<Cart> carts = getCarts();
                for(Cart cart: carts){
                    if(cart.getId().equals(((Cart)toFind).getId())){
                        return cart;
                    }
                }
                break;
        }
        return null;
    }

    public Product addProduct(Product product) {
        try {
            File file = new File(productDataPath);
            ArrayList<Product> products;
            if (!file.exists()) {
                products = new ArrayList<>();
            }
            else {
                products = new ArrayList<>(Arrays.asList(objectMapper.readValue(file, Product[].class)));
            }
            products.add(product);
            objectMapper.writeValue(file, products);
            return product;
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to JSON file", e);
        }
    }
    public ArrayList<Product> getProducts() {
        try {
            File file = new File(productDataPath);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return new ArrayList<Product>(Arrays.asList(objectMapper.readValue(file, Product[].class)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read from JSON file", e);
        }
    }

    public User addUser(User user) {
        try {
            File file = new File(userDataPath);
            ArrayList<User> users;
            if (!file.exists()) {
                users = new ArrayList<>();
            }
            else {
                users = new ArrayList<>(Arrays.asList(objectMapper.readValue(file, User[].class)));
            }
            users.add(user);
            objectMapper.writeValue(file, users);
            return user;
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to JSON file", e);
        }
    }
    public ArrayList<User> getUsers() {
        try {
            File file = new File(userDataPath);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return new ArrayList<User>(Arrays.asList(objectMapper.readValue(file, User[].class)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read from JSON file", e);
        }
    }
    public Cart addCart(Cart cart){
        try{
            File file = new File(cartDataPath);
            ArrayList<Cart> carts;
            if (!file.exists()) {
                carts = new ArrayList<>();
            }
            else {
                carts = new ArrayList<>(Arrays.asList(objectMapper.readValue(file, Cart[].class)));
            }
            carts.add(cart);
            objectMapper.writeValue(file, carts);
            return cart;
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to JSON file", e);
        }
    }
    public ArrayList<Cart> getCarts() {
        try {
            File file = new File(cartDataPath);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return new ArrayList<Cart>(Arrays.asList(objectMapper.readValue(file, Cart[].class)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read from JSON file", e);
        }
    }
    public Order addOrder(Order order){
        try{
            File file = new File(orderDataPath);
            ArrayList<Order> orders;
            if (!file.exists()) {
                orders = new ArrayList<>();
            }
            else {
                orders = new ArrayList<>(Arrays.asList(objectMapper.readValue(file, Order[].class)));
            }
            orders.add(order);
            objectMapper.writeValue(file, orders);
            return order;
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to JSON file", e);
        }
    }
    public ArrayList<Order> getOrders() {
        try {
            File file = new File(orderDataPath);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return new ArrayList<Order>(Arrays.asList(objectMapper.readValue(file, Order[].class)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read from JSON file", e);
        }
    }



    private UUID userId;
    private User testUser;
    private UUID productId;
    private Product testProduct;
    private Cart testCart;
    @BeforeEach
    void setUp() {
        testUser = new User();
        userId = UUID.randomUUID();
        testUser.setId(userId);
        testUser.setName("My Test User");
        testProduct = new Product(UUID.randomUUID(),"Chipsy", 10);
        testCart = new Cart();
        testCart.setId(UUID.randomUUID());
        testCart.setUserId(testUser.getId());
        overRideAll();
    }

    private static UUID getUserId() {
        return UUID.randomUUID();
    }

    //------- User TCs -------
    // 1) AddUser
    @Test
    void AddUser() throws Exception {
        User testUser2 = new User();
        testUser2.setId(UUID.randomUUID());
        testUser2.setName("My Test User2");

        userService.addUser(testUser2);

        User user = (User) find("User", testUser2);
        Assertions.assertNotNull(user, "User should not be null.");
        Assertions.assertEquals(testUser2.getName(), user.getName(), "User name not equal.");
        Assertions.assertEquals(testUser2.getId(), user.getId(), "User Id not equal.");
    }
    @Test
    void AddUserDuplicate() throws Exception {
        User duplicateUser = new User();
        duplicateUser.setId(UUID.randomUUID());
        duplicateUser.setName("Duplicate User");
        userService.addUser(duplicateUser);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.addUser(duplicateUser);
        });
    }

    @Test
    void AddUserNullName() throws Exception {
        User nullNameUser = new User();
        nullNameUser.setId(UUID.randomUUID());
        nullNameUser.setName(null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.addUser(nullNameUser);
        });
    }

    // 2) Get Users
    @Test
    void GetUsers() throws Exception {
        User testUser2 = new User();
        testUser2.setId(UUID.randomUUID());
        testUser2.setName("My Test User2");
        userService.addUser(testUser2);
        ArrayList<User> users = userService.getUsers();
        Assertions.assertNotNull(users, "Users should not be null.");
        Assertions.assertEquals(1, users.size(), "Users size not equal.");
        Assertions.assertEquals(testUser2.getName(), users.get(0).getName(), "User name not equal.");
        Assertions.assertEquals(testUser2.getId(), users.get(0).getId(), "User Id not equal.");
    }

    @Test
    void GetUsers_Empty() {
        ArrayList<User> users = userService.getUsers();
        assertNotNull(users, "Returned user list should not be null");
        assertEquals(0, users.size(), "Should return empty list when no users exist");
    }

    @Test
    void GetUsers_LargeDataSet() {
        for (int i = 0; i < 1000; i++) {
            userService.addUser(new User(UUID.randomUUID(), "User" + i));
        }
        List<User> users = userService.getUsers();
        assertEquals(1000, users.size());
    }

    // 3) GetUserById
    @Test
    void GetUserById_Success() {
        User user = new User(UUID.randomUUID(), "User to fetch");
        userService.addUser(user);
        User fetchedUser = userService.getUserById(user.getId());
        assertEquals(user.getId(), fetchedUser.getId());
    }

    @Test
    void GetUserById_NotFound() {
        User user = userService.getUserById(userId);
        Assertions.assertNull(user, "User should be null.");
    }

    @Test
    void GetUserById_NullId() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserById(null);
        });
    }

    // 4) GetOrdersByUserId
    @Test
    void GetOrdersByUserId_NoOrders() {
        addUser(testUser);
        List<Order> orders = userService.getOrdersByUserId(testUser.getId());
        Assertions.assertTrue(orders.isEmpty());
    }

    @Test
    void GetOrdersByUserId_WithOrders() {
        Order order = new Order(userId, new ArrayList<>());
        testUser.getOrders().add(order);
        addUser(testUser);

        List<Order> orders = userService.getOrdersByUserId(userId);

        assertNotNull(orders, "Orders list should not be null");
        assertEquals(1, orders.size(), "Should have one order");
    }

    @Test
    void GetOrdersByUserId_InvalidUser() {
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            userService.getOrdersByUserId(UUID.randomUUID());
        });
    }

//    // 5) AddOrderToUser
//    @Test
//    void AddOrderToUser_Success(){
//       testCart.addProduct(testProduct);
//
//
//       userService.addOrderToUser(userId);
//
//        User updatedUser = userService.getUserById(userId);
//        Assertions.assertNotNull(updatedUser, "User should exist");
//        Assertions.assertEquals(1, updatedUser.getOrders().size(), "User should have one order");
//        Assertions.assertEquals(1, updatedUser.getOrders().get(0).getProducts().size(), "Order should have one product");
//        Assertions.assertEquals(productId, updatedUser.getOrders().get(0).getProducts().get(0).getId(), "Product ID should match");
//    }
















}