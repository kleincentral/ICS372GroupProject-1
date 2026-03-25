package org.example.model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class Order{
    private final String orderId;
    private final OrderType type;
    private final long orderTimeMillis;
    private final List<Item> items;
    private OrderStatus status;
    private String source;
    private String warehouse;
    private String cancelReason;
    public Order(String orderId, OrderType type, long orderTimeMillis, List<Item> items){
        this(orderId,type,orderTimeMillis,items,"unknown","unknown");
    }
    public Order(String orderId , OrderType type , long orderTimeMillis ,
                 List<Item> items , String source , String warehouse){
        this.orderId = orderId;
        this.type = type;
        this.orderTimeMillis = orderTimeMillis;
        if(items == null){
            this.items = new ArrayList<>();
        } else{
            this.items = new ArrayList<>(items);
        }
        this.status = OrderStatus.NOT_STARTED;
        this.source = (source == null || source.isBlank()) ? "unknown" : source;
        this.warehouse = (warehouse == null || warehouse.isBlank()) ? "unknown" : warehouse;
        this.cancelReason = "";
    }

    public String getOrderId(){
        return orderId;
    }

    public OrderType getType(){
        return type;
    }

    public long getOrderTimeMillis(){
        return orderTimeMillis;
    }

    public List<Item> getItems(){
        return Collections.unmodifiableList(items);
    }

    public OrderStatus getStatus(){
        return status;
    }

    public void setStatus(OrderStatus status){
        this.status = status;
    }


    public String getSource(){
        return source;
    }

    public void setSource(String source){
        if(source != null && !source.isBlank()){
            this.source = source;
        }
    }
    public String getWarehouse( ){
        return warehouse;
    }
    public void setWarehouse(String warehouse){
        if(warehouse != null && !warehouse.isBlank()){
            this.warehouse = warehouse;
        }
    }
    public String getCancelReason(){
        return cancelReason;
    }
    public void setCancelReason(String cancelReason){
        this.cancelReason = (cancelReason == null) ? "" : cancelReason;
    }
    public boolean isCanceled(){
        return status == OrderStatus.CANCELED;
    }
    public void cancelOrder(String reason){
        this.status = OrderStatus.CANCELED;
        this.cancelReason = (reason == null) ? "" : reason;
    }
    public double getTotalPrice(){
        double total = 0.0;
        for(Item item : items){
            total += item.getQuantity() * item.getPrice();
        }
        return total;
    }
    public String toDisplayString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Order ID: ").append(orderId).append("\n");
        sb.append("Type: ").append(type).append("\n");
        sb.append("Order Date: ").append(orderTimeMillis).append("\n");
        sb.append("Status: ").append(status).append("\n");
        sb.append("Source: ").append(source).append("\n");
        sb.append("Warehouse: ").append(warehouse).append("\n");
        if(status == OrderStatus.CANCELED && !cancelReason.isBlank()){
            sb.append("Cancel Reason: ").append(cancelReason).append("\n");
        }
        sb.append("Items:\n");
        for(Item item : items){
            sb.append("  - ")
              .append(item.getName())
              .append(" | qty=").append(item.getQuantity())
              .append(" | price=").append(item.getPrice())
              .append("\n");
        }
        sb.append("Total: $")
          .append(String.format("%.2f", getTotalPrice()))
          .append("\n");
        return sb.toString();
    }
    @Override
    public String toString(){
        return toDisplayString();
    }
}
