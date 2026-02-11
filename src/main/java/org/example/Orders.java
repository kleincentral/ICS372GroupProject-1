package org.example;

import org.example.model.Order;
import org.example.model.OrderStatus;
import org.example.model.OrderType;
import java.util.List;

import java.time.LocalDateTime;
import java.util.List;

public class Orders {
    private Order[] Orders;
    private int index = 0;

    // constructor
    public Orders() {
        this.Orders = new Order[1000];
    }

    public void addOrder(OrderType type, long time, List<Item> items, String orderID) {
        Orders[index] = new Order(orderID, type, time, items);
        index++;
    }

    public Order[] getOrders() {
        return Orders;
    }

    private Order findByID(String orderID) {
        try {
            for (int i = 0; i < Orders.length; i++) {
                if (Orders[i] == null) {
                    return null;
                }
                else if (Orders[i].getOrderId().equals(orderID)) {
                    return Orders[i];
                }
            }
        } catch (Exception E) {
            throw new RuntimeException("An error occurred: " + E);
        }
        return null;
    }

    public OrderStatus getCompletionStatus(String orderID) {
        return findByID(orderID).getStatus();
    }

    // Returns true once the completion status has been updated.
    // You must pass an INT for the orderID, however if you want to just use
    // the last order you looked up in completion status you can enter -1
    public boolean updateCompletion(OrderStatus completionStatus, String orderID) {
        try {
            if (findByID(orderID) != null){
                findByID(orderID).setStatus(completionStatus);
                return true;
            } else return false;
        } catch (Exception E) {
            return false;
        }
    }





}  //item price

