import java.time.LocalDateTime;

public class orders{
    private order[] orders;
    private int index = 0;
    private int memory = 1;

    // constructor
    public orders() {
        this.orders = new order[1000];
    }

    public void addOrder(String type, LocalDateTime time, String[] items, int orderID) {
        orders[index] = new order(type, time, items, orderID);
        index++;
    }

    public order[] getOrders() {
        return orders;
    }

    private order findByID(int orderID) {
        try {
            for (int i = 0; i < orders.length; i++) {
                if (orders[i] == null) {
                    return null;
                }
                else if (orders[i].getOrderID() == orderID) {
                    memory = i;
                    return orders[i];
                }
            }
        } catch (Exception E) {
            throw new RuntimeException("An error occurred: " + E);
        }
        return null;
    }

    public String getCompletionStatus(int orderID) {
        return findByID(orderID).getCompletionStatus();
    }

    // Returns true once the completion status has been updated.
    // You must pass an INT for the orderID, however if you want to just use
    // the last order you looked up in completion status you can enter -1
    public boolean updateCompletion(String completionStatus, int orderID) {
        try {
            if (orderID >= 0) {
                findByID(orderID).updateCompletionStatus(completionStatus);
                return true;
            } else {
                orders[memory].updateCompletionStatus(completionStatus);
                return true;
            }
        } catch (Exception E) {
            return false;
        }
    }


}  //item price

