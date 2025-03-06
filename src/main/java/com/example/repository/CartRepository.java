package com.example.repository;

import com.example.model.Cart;
import com.example.model.Product;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class CartRepository extends MainRepository<Cart> {

    @Override
    protected String getDataPath() {
        return "src/main/java/com/example/data/carts.json";
    }

    @Override
    protected Class<Cart[]> getArrayType() {
        return Cart[].class;
    }


    public Cart addCart(Cart cart) {
        save(cart);
        return cart;
    }


    public ArrayList<Cart> getCarts() {
        return findAll();
    }


    public Cart getCartById(UUID cartId) {
        return findAll().stream()
                .filter(cart -> cart.getId().equals(cartId))
                .findFirst()
                .orElse(null);
    }


    public Cart getCartByUserId(UUID userId) {
        return findAll().stream()
                .filter(cart -> cart.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }


    public void addProductToCart(UUID cartId, Product product) {
        ArrayList<Cart> carts = findAll();
        boolean updated = false;

        for (Cart cart : carts) {
            if (cart.getId().equals(cartId)) {
                cart.addProduct(product);
                updated = true;
                break;
            }
        }

        if (updated) {
            saveAll(carts);
        }
    }


    public void deleteProductFromCart(UUID cartId, UUID productId) {
        ArrayList<Cart> carts = findAll();
        boolean updated = false;

        for (Cart cart : carts) {
            if (cart.getId().equals(cartId)) {
                boolean removed = cart.getProducts().removeIf(product -> product.getId().equals(productId));
                if (removed) {
                    updated = true;
                }
                break; // Stop searching once the cart is found
            }
        }

        if (updated) {
            saveAll(carts);
        }
    }


    public void deleteCartById(UUID cartId) {
        ArrayList<Cart> carts = findAll();
        boolean removed = carts.removeIf(cart -> cart.getId().equals(cartId));

        if (removed) {
            saveAll(carts);
        }
    }

//    public void addProductToCart(UUID cartId, Product product) {
//        ArrayList<Cart> carts = findAll();
//        for (Cart cart : carts) {
//            if (cart.getId().equals(cartId)) {
//                cart.addProduct(product);
//                saveAll(carts);
//                return;
//            }
//        }
//    }
//
//    public void deleteProductFromCart(UUID cartId, UUID productId) {
//        ArrayList<Cart> carts = findAll();
//        for (Cart cart : carts) {
//            if (cart.getId().equals(cartId)) {
//                cart.getProducts().removeIf(product -> product.getId().equals(productId));
//                saveAll(carts);
//                return;
//            }
//        }
//    }
//
//
//
//    public void deleteCartById(UUID cartId) {
//        ArrayList<Cart> carts = findAll();
//        carts.removeIf(cart -> cart.getId().equals(cartId));
//        overrideData(carts);
//    }
}
