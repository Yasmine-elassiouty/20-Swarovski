package com.example.repository;

import com.example.model.Order;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class OrderRepository extends MainRepository<Order> {

    public OrderRepository() {
    }

    @Override
    protected String getDataPath() {
        return "orders.json"; // Path to store JSON data
    }

    @Override
    protected Class<Order[]> getArrayType() {
        return Order[].class;
    }

    // 6.5.2.1 Add Order
    public void addOrder(Order order) {
        save(order);
    }

    // 6.5.2.2 Get All Orders
    public ArrayList<Order> getOrders() {
        return findAll();
    }

    // 6.5.2.3 Get a Specific Order
    public Order getOrderById(UUID orderId) {
        return findAll().stream()
                .filter(order -> order.getId().equals(orderId))
                .findFirst()
                .orElse(null);
    }

    // 6.5.2.4 Delete a Specific Order
    public void deleteOrderById(UUID orderId) {
        ArrayList<Order> orders = findAll();
        Optional<Order> orderToDelete = orders.stream()
                .filter(order -> order.getId().equals(orderId))
                .findFirst();

        if (orderToDelete.isPresent()) {
            orders.remove(orderToDelete.get());
            overrideData(orders);
        } else {
            throw new IllegalArgumentException("Order not found");
        }
    }
}
