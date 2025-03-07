package com.example.repository;

import com.example.model.Order;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class OrderRepository extends MainRepository<Order> {

    public OrderRepository() {
        ensureDataFolderExists(); // Ensure the 'data' folder exists
    }

    @Override
    protected String getDataPath() {
        return "src/main/java/com/example/data/orders.json"; // Store the file inside the 'data' folder
    }

    @Override
    protected Class<Order[]> getArrayType() {
        return Order[].class;
    }

    // Ensure the 'data' folder exists before writing the JSON file
    private void ensureDataFolderExists() {
        File folder = new File("data");
        if (!folder.exists()) {
            folder.mkdirs(); // Create the folder if it doesn't exist
        }
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
