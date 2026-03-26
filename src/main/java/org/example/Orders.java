package org.example;

import org.example.model.Item;
import org.example.model.Order;
import org.example.model.OrderStatus;
import org.example.model.OrderType;

import java.util.List;

public class Orders {

    private Order[] orders;
    private int index = 0;

    // Constructor
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

    private Order findByID(String orderID) {
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
        Order order = findByID(orderID);
        if (order == null) {
            return false;
        }

        if (order.getStatus() == OrderStatus.NOT_STARTED ||
                order.getStatus() == OrderStatus.IN_PROGRESS) {
            order.setStatus(OrderStatus.CANCELED);
            return true;
        }

        return false;
    }
}
