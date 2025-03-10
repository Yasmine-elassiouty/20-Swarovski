package com.example.service;

import com.example.model.Cart;
import com.example.model.Product;
import com.example.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.UUID;

@Service
@SuppressWarnings("rawtypes")
public class CartService extends MainService<Cart> {

    //The Dependency Injection Variables

    private final CartRepository cartRepository;

    //The Constructor with the requried variables mapping the Dependency Injection.
    @Autowired
    public CartService(CartRepository cartRepository, Cart cart) {
        super(cartRepository);
        this.cartRepository = cartRepository;
    }


    public Cart addCart(Cart cart) {
        return cartRepository.addCart(cart);
    }


    public ArrayList<Cart> getCarts() {
        return cartRepository.getCarts();
    }


    public Cart getCartById(UUID cartId) {
        return cartRepository.getCartById(cartId);
    }


    public Cart getCartByUserId(UUID userId) {
        return cartRepository.getCartByUserId(userId);
    }


    public void addProductToCart(UUID cartId, Product product) {
        Cart cart = cartRepository.getCartById(cartId);
        if (cart != null) {
            cartRepository.addProductToCart(cartId, product);
        } else {
            throw new IllegalArgumentException("Cart not found");
        }
    }


    public void deleteProductFromCart(UUID cartId, Product product) {
        Cart cart = cartRepository.getCartById(cartId);

        if (cart == null) {
            throw new IllegalArgumentException("Cart not found");
        }

        boolean productExists = cart.getProducts().stream()
                .anyMatch(p -> p.getId().equals(product.getId()));

        if (!productExists) {
            throw new IllegalArgumentException("Product not found in cart");
        }

        cartRepository.deleteProductFromCart(cartId, product.getId());
    }




    public void deleteCartById(UUID cartId) {
        if (cartRepository.getCartById(cartId) != null) {
            cartRepository.deleteCartById(cartId);
        } else {
            throw new IllegalArgumentException("Cart not found");
        }
    }
}