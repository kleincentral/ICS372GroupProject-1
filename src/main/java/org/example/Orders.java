package org.example;

import org.example.model.Item;
import org.example.model.Order;
import org.example.model.OrderStatus;
import org.example.model.OrderType;

import java.util.ArrayList;
import java.util.List;

public class Orders {

    private Order[] orders;
    private int index = 0;

    public Orders() {
        this.orders = new Order[1000];
    }

    public void addOrder(OrderType type, long time, List<Item> items, String orderID) {
        orders[index] = new Order(orderID, type, time, items);
        index++;
    }

    public Order[] getOrders() {
        return orders;
    }

    public Order findByID(String orderID) {
        for (int i = 0; i < index; i++) {
            if (orders[i] != null && orders[i].getOrderId().equals(orderID)) {
                return orders[i];
            }
        }
        return null;
    }

    public OrderStatus getCompletionStatus(String orderID) {
        Order order = findByID(orderID);

        if (order == null) {
            return null;
        }

        return order.getStatus();
    }

    public boolean startOrder(String orderID) {
        Order order = findByID(orderID);

        if (order == null) {
            return false;
        }

        if (order.getStatus() == OrderStatus.NOT_STARTED) {
            order.setStatus(OrderStatus.IN_PROGRESS);
            return true;
        }

        return false;
    }

    public boolean completeOrder(String orderID) {
        Order order = findByID(orderID);

        if (order == null) {
            return false;
        }

        if (order.getStatus() == OrderStatus.IN_PROGRESS) {
            order.setStatus(OrderStatus.COMPLETED);
            return true;
        }

        return false;
    }

    public boolean cancelOrder(String orderID) {
        return cancelOrder(orderID, "Warehouse cannot fulfill order");
    }

    public boolean cancelOrder(String orderID, String reason) {
        Order order = findByID(orderID);

        if (order == null) {
            return false;
        }

        if (order.getStatus() == OrderStatus.NOT_STARTED ||
                order.getStatus() == OrderStatus.IN_PROGRESS) {
            order.cancelOrder(reason);
            return true;
        }

        return false;
    }

    public List<Order> filterByStatus(OrderStatus status) {
        List<Order> matchingOrders = new ArrayList<>();

        for (int i = 0; i < index; i++) {
            if (orders[i] != null && orders[i].getStatus() == status) {
                matchingOrders.add(orders[i]);
            }
        }

        return matchingOrders;
    }

    public List<Order> filterByType(OrderType type) {
        List<Order> matchingOrders = new ArrayList<>();

        for (int i = 0; i < index; i++) {
            if (orders[i] != null && orders[i].getType() == type) {
                matchingOrders.add(orders[i]);
            }
        }

        return matchingOrders;
    }

    public List<Order> getActiveOrders() {
        List<Order> activeOrders = new ArrayList<>();

        for (int i = 0; i < index; i++) {
            if (orders[i] != null &&
                    orders[i].getStatus() != OrderStatus.COMPLETED &&
                    orders[i].getStatus() != OrderStatus.CANCELED) {
                activeOrders.add(orders[i]);
            }
        }

        return activeOrders;
    }
}