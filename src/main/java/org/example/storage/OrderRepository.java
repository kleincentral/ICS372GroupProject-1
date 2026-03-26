package org.example.storage;

import org.example.model.Order;
import org.example.model.OrderStatus;
import org.example.model.OrderType;

import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.File;
import java.util.Scanner;

public class OrderRepository {
    private final List<Order> orders = new ArrayList<>();

    // Add order
    public void addOrder(Order order) {
        if (order != null) {
            orders.add(order);
        }
    }

    // Get all orders
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }

    // Find order by ID
    public Order findById(String id) {
        for (Order order : orders) {
            if (order.getOrderId().equalsIgnoreCase(id)) {
                return order;
            }
        }
        return null;
    }

    // Get uncompleted orders
    public List<Order> getUncompletedOrders() {
        List<Order> result = new ArrayList<>();

        for (Order order : orders) {
            if (order.getStatus() != OrderStatus.COMPLETED
                    && order.getStatus() != OrderStatus.CANCELED) {
                result.add(order);
            }
        }

        return result;
    }

    // Start an order
    public boolean startOrder(String id) {
        Order order = findById(id);

        if (order == null) {
            return false;
        }

        if (order.getStatus() == OrderStatus.COMPLETED) {
            return false;
        }

        if (order.getStatus() == OrderStatus.CANCELED) {
            return false;
        }

        if (order.getStatus() == OrderStatus.IN_PROGRESS) {
            return false;
        }

        order.setStatus(OrderStatus.IN_PROGRESS);
        return true;
    }

    // Complete an order
    public boolean completeOrder(String id) {
        Order order = findById(id);

        if (order == null) {
            return false;
        }

        if (order.getStatus() == OrderStatus.CANCELED) {
            return false;
        }

        if (order.getStatus() == OrderStatus.COMPLETED) {
            return false;
        }

        order.setStatus(OrderStatus.COMPLETED);
        return true;
    }

    // Cancel an order
    public boolean cancelOrder(String id) {
        Order order = findById(id);

        if (order == null) {
            return false;
        }

        if (order.getStatus() == OrderStatus.COMPLETED) {
            return false;
        }

        if (order.getStatus() == OrderStatus.CANCELED) {
            return false;
        }

        order.setStatus(OrderStatus.CANCELED);
        return true;
    }

    // Save orders to JSON file
    public void saveToFile() {
        try {
            FileWriter writer = new FileWriter("orders.json");

            writer.write("[\n");

            for (int i = 0; i < orders.size(); i++) {
                Order o = orders.get(i);

                writer.write("  {\n");
                writer.write("    \"id\": \"" + o.getOrderId() + "\",\n");
                writer.write("    \"status\": \"" + o.getStatus() + "\"\n");
                writer.write("  }");

                if (i < orders.size() - 1) {
                    writer.write(",");
                }

                writer.write("\n");
            }

            writer.write("]");
            writer.close();

            System.out.println("Orders saved.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load orders from JSON file
    public void loadFromFile() {
        try {
            File file = new File("orders.json");

            if (!file.exists()) {
                return;
            }

            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if (line.startsWith("\"id\"")) {
                    String id = line.split(":")[1]
                            .replace("\"", "")
                            .replace(",", "")
                            .trim();

                    String statusLine = scanner.nextLine().trim();
                    String statusStr = statusLine.split(":")[1]
                            .replace("\"", "")
                            .replace(",", "")
                            .trim();

                    OrderStatus status = OrderStatus.valueOf(statusStr);

                    Order order = new Order(
                            id,
                            OrderType.PICKUP,
                            System.currentTimeMillis(),
                            new ArrayList<>()
                    );

                    order.setStatus(status);
                    orders.add(order);
                }
            }

            scanner.close();
            System.out.println("Orders loaded.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}