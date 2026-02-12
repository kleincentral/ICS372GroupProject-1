package org.example.ui;

import org.example.OrderJsonParser;
import org.example.Orders;
import org.example.model.Item;
import org.example.model.Order;
import org.example.model.OrderStatus;

import java.io.FileWriter;
import java.util.Scanner;

public class ConsoleUI {

    private final Scanner scanner = new Scanner(System.in);
    private final Orders ordersManager = new Orders();
    private final OrderJsonParser parser = new OrderJsonParser();

    public void run() {
        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> importOrders();
                case "2" -> listUncompletedOrders();
                case "3" -> displayOrderById();
                case "4" -> startOrder();
                case "5" -> completeOrder();
                case "6" -> exportOrders();
                case "0" -> running = false;
                default -> System.out.println("Invalid choice. Please Try again.");
            }
        }

        System.out.println("Thank you.");
    }

    private void printMenu() {
        System.out.println("\n----- Order Tracking System -----");
        System.out.println("1) Import orders from JSON file");
        System.out.println("2) List uncompleted orders");
        System.out.println("3) Display order by ID");
        System.out.println("4) Start order");
        System.out.println("5) Complete order");
        System.out.println("6) Export all orders to JSON");
        System.out.println("0) Exit");
        System.out.print("Choose: ");
    }

    private void importOrders() {
        System.out.print("Enter JSON file path: ");
        String path = scanner.nextLine().trim();

        Order order = parser.parseOrderFromFile(path);

        if (order != null) {
            ordersManager.addOrder(
                    order.getType(),
                    order.getOrderTimeMillis(),
                    order.getItems(),
                    order.getOrderId()
            );
            System.out.println("Order imported successfully!");
            System.out.println(order.toDisplayString());
        } else {
            System.out.println("Failed to import order from: " + path);
        }
    }

    private void listUncompletedOrders() {
        Order[] orders = ordersManager.getOrders();
        double totalPrice = 0.0;
        boolean found = false;

        for (Order order : orders) {
            if (order == null) break;

            if (order.getStatus() != OrderStatus.COMPLETED) {
                System.out.println(order.toDisplayString());
                System.out.println("-----------------------------------");
                totalPrice += order.getTotalPrice();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No uncompleted orders found.");
        } else {
            System.out.println("Total price of uncompleted orders: $" +
                    String.format("%.2f", totalPrice));
        }
    }

    private void displayOrderById() {
        System.out.print("Enter Order ID: ");
        String id = scanner.nextLine().trim();

        for (Order order : ordersManager.getOrders()) {
            if (order == null) break;

            if (order.getOrderId().equals(id)) {
                System.out.println(order.toDisplayString());
                return;
            }
        }

        System.out.println("Order not found: " + id);
    }

    private void startOrder() {
        System.out.print("Enter Order ID to start: ");
        String id = scanner.nextLine().trim();

        OrderStatus status = ordersManager.getCompletionStatus(id);

        if (status == null) {
            System.out.println("Order not found.");
            return;
        }

        if (status == OrderStatus.NOT_STARTED) {
            if (ordersManager.updateCompletion(OrderStatus.IN_PROGRESS, id)) {
                System.out.println("Order started successfully.");
            }
        } else {
            System.out.println("Cannot start order. Current status: " + status);
        }
    }

    private void completeOrder() {
        System.out.print("Enter Order ID to complete: ");
        String id = scanner.nextLine().trim();

        OrderStatus status = ordersManager.getCompletionStatus(id);

        if (status == null) {
            System.out.println("Order not found.");
            return;
        }

        if (status == OrderStatus.IN_PROGRESS) {
            if (ordersManager.updateCompletion(OrderStatus.COMPLETED, id)) {
                System.out.println("Order completed successfully.");
            }
        } else {
            System.out.println("Cannot complete order. Current status: " + status);
        }
    }

    private void exportOrders() {
        System.out.print("Enter output JSON file path: ");
        String path = scanner.nextLine().trim();

        try {
            org.json.simple.JSONArray ordersArray = new org.json.simple.JSONArray();

            for (Order order : ordersManager.getOrders()) {
                if (order == null) break;

                org.json.simple.JSONObject orderJson = new org.json.simple.JSONObject();
                orderJson.put("order_id", order.getOrderId());
                orderJson.put("type", order.getType().toString());
                orderJson.put("order_date", order.getOrderTimeMillis());
                orderJson.put("status", order.getStatus().toString());

                org.json.simple.JSONArray itemsArray = new org.json.simple.JSONArray();

                for (Item item : order.getItems()) {
                    org.json.simple.JSONObject itemJson = new org.json.simple.JSONObject();
                    itemJson.put("name", item.getName());
                    itemJson.put("quantity", item.getQuantity());
                    itemJson.put("price", item.getPrice());
                    itemsArray.add(itemJson);
                }

                orderJson.put("items", itemsArray);
                ordersArray.add(orderJson);
            }

            FileWriter file = new FileWriter(path);
            file.write(ordersArray.toJSONString());
            file.flush();
            file.close();

            System.out.println("Orders exported successfully!");

        } catch (Exception e) {
            System.out.println("Error exporting orders: " + e.getMessage());
        }
    }
}
