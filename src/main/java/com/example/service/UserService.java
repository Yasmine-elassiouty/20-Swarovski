package com.example.service;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.ArrayList;

@Service
@SuppressWarnings("rawtypes")
public class UserService extends MainService<User>{
    //The Dependency Injection Variables
    //The Constructor with the required variables mapping the Dependency Injection.

    UserRepository userRepository;
    CartService cartService;


    @Autowired
    public UserService(UserRepository userRepository, CartService cartService) {
        super(userRepository);
        this.userRepository = userRepository;
        this.cartService = cartService;
    }


    // 7.2.2.1
    public User addUser(User user) {
        return userRepository.addUser(user);

    }

    // 7.2.2.2
    public ArrayList<User> getUsers() {

        return userRepository.getUsers();
    }

    // 7.2.2.3 Get a Specific User
    public User getUserById(UUID userId) {
        return userRepository.getUserById(userId);
    }
    // 7.2.2.4
    public List<Order> getOrdersByUserId(UUID userId){
        return userRepository.getOrdersByUserId(userId);
    }

    // 7.2.2.5
    public void addOrderToUser(UUID userId) {
        //Here the user checks out his cart by creating a new order. The user should empty his cart and calculate
        //everything related to his order and add the new order to his list of orders. It should call methods from

// Retrieve user and add order
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new NoSuchElementException("User not found");
        }
        // Retrieve user's cart
        Cart userCart = cartService.getCartByUserId(userId);
        if (userCart == null) {
            throw new IllegalStateException("Cart not found");
        }
        if (userCart.getProducts().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        // Create a new Order based on cart items
        Order newOrder = new Order();
        newOrder.setUserId(userId);
        newOrder.setProducts(new ArrayList<>(userCart.getProducts()));
        cartService.deleteCartById(userCart.getId());

        userRepository.addOrderToUser(userId,newOrder);

    }

    //7.2.2.6 Empty Cart
    public void emptyCart(UUID userId) {
        //check if user exists first
        if (userRepository.getUserById(userId) == null) {
            throw new NoSuchElementException("User not found");
        }
        Cart cart = cartService.getCartByUserId(userId);

        if (cart != null) {
            for (Product product : new ArrayList<>(cart.getProducts())) {
                cartService.deleteProductFromCart(cart.getId(), product);
            }
        } else {
            throw new IllegalArgumentException("Cart not found!");
        }
    }


    //    7.2.2.7 Remove Order
//    To remove a specific order from the list of orders of the user.
    public void removeOrderFromUser(UUID userId, UUID orderId){
        userRepository.removeOrderFromUser(userId, orderId);
    }

    //    7.2.2.8 Delete the User
//    To delete a specific user by passing his ID.
    public void deleteUserById(UUID userId){
        userRepository.deleteUserById(userId);
    }

}