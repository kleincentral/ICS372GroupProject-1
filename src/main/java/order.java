import java.time.*;

public class order {
    private String type;
    private LocalDateTime orderTime;
    private String[] items; // placehold for our current items.
    private String completionStatus;
    private int orderID;
    // Parse the JSON input with an order ID (some kind of List based off of the IDS)
    //Order ID
    //order type (direct ship / pickup)
    //order time,
    //requested items,
    //Quantity
    //Completion status (not started, in progress, completed)

    // constructor
    public order(String type, LocalDateTime orderTime, String[] items, int orderID) {
        this.type = type;
        this.orderTime = orderTime;
        this.items = items;
        completionStatus = "not started";
        this.orderID = orderID;
    }

    public int getOrderID() {
        return orderID;
    }

    public String[] getItems() {
        return items;
    }

    public String getCompletionStatus() {
        return completionStatus;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public String getType() {
        return type;
    }

    public void updateCompletionStatus(String completionStatus) {
        this.completionStatus = completionStatus;
    }


}