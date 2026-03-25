package org.example.storage;

import org.example.model.Order;

import java.util.ArrayList;
import java.util.List;

// I need to store all orders in a list so they can be managed in memory
// I need to make sure null orders are not added to prevent errors
// I need to allow adding new orders into the repository
// I need to return a copy of all orders to avoid modifying the original list directly
// I need to search for an order using its ID
// I need to compare order IDs ignoring case for better user input flexibility
// I need to return null if no matching order is found
// I need to filter and return only uncompleted orders
// I need to exclude completed and cancelled orders from the uncompleted list
// I need to keep the repository simple and focused only on storing and retrieving orders

public class OrderRepository {
    private final List<Order> orders = new ArrayList<>();

    public void addOrder(Order order) {
        if (order != null) {
            orders.add(order);
        }
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }

    public Order findById(String id) {
        for (Order order : orders) {
            if (order.getOrderId().equalsIgnoreCase(id)) {
                return order;
            }
        }
        return null;
    }

    public List<Order> getUncompletedOrders() {
        List<Order> result = new ArrayList<>();
        for (Order order : orders) {
            if (order.getStatus() != org.example.model.OrderStatus.COMPLETED
                    && order.getStatus() != org.example.model.OrderStatus.CANCELLED) {
                result.add(order);
            }
        }
        return result;
    }
}