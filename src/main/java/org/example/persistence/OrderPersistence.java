package org.example.persistence;

import org.example.model.Item;
import org.example.model.Order;
import org.example.model.OrderStatus;
import org.example.model.OrderType;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OrderPersistence {

// I need to save all orders to a file so data is not lost when the program closes
// I need to loop through each order and write its basic information to the file
// I need to also save all items inside each order
// I need to separate each order using an "END" marker so it can be read correctly later
// I need to handle file writing errors using try-catch to prevent crashes
// I need to load orders from a file when the program starts
// I need to read each line and reconstruct the order details (ID, type, status, time)
// I need to convert strings back into enums and numbers when loading data
// I need to read items until reaching the "END" marker for each order
// I need to handle missing files or errors gracefully if no previous data exists


    // Save all orders to a file
    public void saveOrders(List<Order> orders, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {

            for (Order order : orders) {
                // Save order info
                writer.println(order.getOrderId() + "," +
                        order.getType() + "," +
                        order.getStatus() + "," +
                        order.getOrderTimeMillis());

                // Save items
                for (Item item : order.getItems()) {
                    writer.println(item.getName() + "," +
                            item.getQuantity() + "," +
                            item.getPrice());
                }

                // Mark end of this order
                writer.println("END");
            }

        } catch (IOException e) {
            System.out.println("Error saving orders: " + e.getMessage());
        }
    }

    // Load orders from a file
    public List<Order> loadOrders(String filePath) {
        List<Order> orders = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {

                String[] orderParts = line.split(",");

                String id = orderParts[0];
                OrderType type = OrderType.valueOf(orderParts[1]);
                OrderStatus status = OrderStatus.valueOf(orderParts[2]);
                long time = Long.parseLong(orderParts[3]);

                List<Item> items = new ArrayList<>();

                // Read items until END
                while (!(line = reader.readLine()).equals("END")) {
                    String[] itemParts = line.split(",");

                    items.add(new Item(
                            itemParts[0],
                            Integer.parseInt(itemParts[1]),
                            Double.parseDouble(itemParts[2])
                    ));
                }

                Order order = new Order(id, type, time, items);
                order.setStatus(status);

                orders.add(order);
            }

        } catch (IOException e) {
            System.out.println("No previous data found.");
        }

        return orders;
    }
}