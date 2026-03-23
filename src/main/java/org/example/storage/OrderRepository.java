package org.example.storage;

import org.example.model.Order;

import java.util.ArrayList;
import java.util.List;

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