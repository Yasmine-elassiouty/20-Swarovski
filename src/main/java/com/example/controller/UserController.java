package com.example.controller;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import com.example.service.CartService;
import com.example.service.ProductService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {
//The Dependency Injection Variables
//The Constructor with the required variables mapping the Dependency Injection.
    UserService userService;
    CartService cartService;
    ProductService productService;

    @Autowired
    public UserController(UserService userService, CartService cartService, ProductService productService) {
        this.userService = userService;
        this.cartService = cartService;
        this.productService = productService;
    }

    //8.1.2.1
    @PostMapping("/")
    public User addUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    //8.1.2.2
    @GetMapping("/")
    public ArrayList<User> getUsers() {
        return userService.getUsers();
    }

    //8.1.2.3
    @GetMapping("/{userId}")
    public User getUserById(@PathVariable UUID userId){
        return userService.getUserById(userId);
    }

    //8.1.2.4
    @GetMapping("/{userId}/orders")
    public List<Order> getOrdersByUserId(@PathVariable UUID userId) {
        return userService.getOrdersByUserId(userId);
    }

    //8.1.2.5
    @PostMapping("/{userId}/checkout")
    public String addOrderToUser(@PathVariable UUID userId){
        userService.addOrderToUser(userId);
        return "Order added successfully";
    }

    @PostMapping("/{userId}/removeOrder")
    public String removeOrderFromUser(@PathVariable UUID userId, @RequestParam UUID orderId){
        userService.removeOrderFromUser(userId, orderId);
        return "Order removed successfully";
    }

    @DeleteMapping("/{userId}/emptyCart")
    public String emptyCart(@PathVariable UUID userId){
        userService.emptyCart(userId);
        return "Cart emptied successfully";
    }

//    8.1.2.8 Add Product To the Cart
    @PutMapping("/addProductToCart")
    public String addProductToCart(@RequestParam UUID userId, @RequestParam UUID productId){
        // First check if the user exists
        User user = userService.getUserById(userId);
        if (user == null) {
            return "User not found";
        }
        Cart cart = cartService.getCartByUserId(userId);
        Product product = productService.getProductById(productId);
        if (product == null) {
            return "Product not found";
        }
        if (cart == null) {
            // create a new cart to this user
            cart = new Cart();
            cart.setUserId(userId);
            cart = cartService.addCart(cart);
        }

        cartService.addProductToCart(cart.getId(), product);
        return "Product added to cart";
    }

//    8.1.2.9 Delete Product from the Cart
//    Put Request to delete a specific product from the Cart
    @PutMapping("/deleteProductFromCart")
    public String deleteProductFromCart(@RequestParam UUID userId, @RequestParam UUID productId){
        // First check if the user exists
        User user = userService.getUserById(userId);
        if (user == null) {
            return "User not found";
        }
        Cart cart = cartService.getCartByUserId(userId);
        Product product = productService.getProductById(productId);
        if (cart == null || cart.getProducts().isEmpty()) {
            return "Cart is empty";
        }

        if (product == null) {
            return "Product not found";
        }
        cartService.deleteProductFromCart(cart.getId(), product);
        return "Product deleted from cart";

    }

//    8.1.2.10 Delete User
//    Delete Request to delete a specific user.
    @DeleteMapping("/delete/{userId}")
    public String deleteUserById(@PathVariable UUID userId){
        try {
            userService.deleteUserById(userId);
            return "User deleted successfully";
        } catch (NoSuchElementException e) {
            return "User not found";  // Keep return type as String
        }

    }





}