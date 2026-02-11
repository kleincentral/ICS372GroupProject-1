package org.example.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class Order {

    private final String orderId;
    private final OrderType type;
    private final long orderTimeMillis;
    private final List<Item> items;
    private OrderStatus status;

    public Order(String orderId , OrderType type, long orderTimeMillis, List<Item> items) {
        this.orderId = orderId;
        this.type = type;
        this.orderTimeMillis = orderTimeMillis;
        this.items = (items == null) ? new ArrayList<>() : new ArrayList<>(items);
        this.status = OrderStatus.NOT_STARTED;
    }

    public String getOrderId() {
        return orderId;
    }
    public OrderType getType() {
        return type;
    }

    public long getOrderTimeMillis() {
        return orderTimeMillis;
    }
    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
// Calculates the total cost of all items in a order
    public double getTotalPrice() {
        double total = 0.0;
        for (Item item : items) {
            total += item.getQuantity() * item.getPrice();
        }
        return total;
    }
    public String toDisplayString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order ID: ").append(orderId).append("\n");
        sb.append("Type: ").append(type).append("\n");
        sb.append("Order Date: ").append(orderTimeMillis).append("\n");
        sb.append("Status: ").append(status).append("\n");
        sb.append("Items:\n");
        for (Item item : items) {
            sb.append("  - ")
              .append(item.getName())
              .append(" | qty=").append(item.getQuantity())
              .append(" | price=").append(item.getPrice())
              .append("\n");
        }
        sb.append("Total: $").append(String.format("%.2f", getTotalPrice())).append("\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return toDisplayString();
    }
}
