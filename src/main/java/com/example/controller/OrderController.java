package com.example.controller;

import com.example.model.Order;
import com.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Endpoint to add a new order
    @PostMapping("/add")
    public String addOrder(@RequestBody Order order) {
        orderService.addOrder(order);
        return "Order added successfully!";
    }

    // Endpoint to get all orders
    @GetMapping("/all")
    public ArrayList<Order> getOrders() {
        return orderService.getOrders();
    }

    // Endpoint to get a specific order by ID
    @GetMapping("/{orderId}")
    public Order getOrderById(@PathVariable UUID orderId) {
        return orderService.getOrderById(orderId);
    }

    // Endpoint to delete a specific order by ID
    @DeleteMapping("/{orderId}")
    public String deleteOrderById(@PathVariable UUID orderId) {
        orderService.deleteOrderById(orderId);
        return "Order deleted successfully!";
    }
}
