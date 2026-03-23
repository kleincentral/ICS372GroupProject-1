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
    private String source;

    public Order(String orderId, OrderType type, long orderTimeMillis, List<Item> items) {
        this(orderId, type, orderTimeMillis, items, "internal");
    }

    public Order(String orderId, OrderType type, long orderTimeMillis, List<Item> items, String source) {
        this.orderId = orderId;
        this.type = type;
        this.orderTimeMillis = orderTimeMillis;
        this.items = (items == null) ? new ArrayList<>() : new ArrayList<>(items);
        this.status = OrderStatus.NOT_STARTED;
        this.source = source;
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

    public String getSource() {
        return source;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void cancelOrder() {
        if (status == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Completed orders cannot be cancelled.");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public boolean isCompleted() {
        return status == OrderStatus.COMPLETED;
    }

    public boolean isCancelled() {
        return status == OrderStatus.CANCELLED;
    }

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
        sb.append("Source: ").append(source).append("\n");
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