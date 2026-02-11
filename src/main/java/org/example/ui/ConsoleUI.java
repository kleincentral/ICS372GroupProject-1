package org.example.ui;

import org.example.OrderJsonParser;
import org.example.model.Order;
import org.example.Orders;
import org.example.model.OrderStatus;
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

    // --- Stubs for now (your team will connect Orders/JsonParser later) ---
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
            System.out.println("✓ Order imported successfully!");
            System.out.println(order.toDisplayString());
        } else {
            System.out.println("✗ Failed to import order from: " + path);
        }
    }

    private void listUncompletedOrders() {
        Order[] orders = ordersManager.getOrders();
        System.out.println("\n--- Uncompleted Orders ---");

        boolean foundAny = false;
        double totalPrice = 0.0;

        for (Order order : orders) {
            if (order == null) break;

            if (order.getStatus() != OrderStatus.COMPLETED) {
                System.out.println(order.toDisplayString());
                System.out.println("-".repeat(50));
                totalPrice += order.getTotalPrice();
                foundAny = true;
            }
        }

        if (!foundAny) {
            System.out.println("No uncompleted orders found.");
        } else {
            System.out.println("Total price of all uncompleted orders: $" + String.format("%.2f", totalPrice));
        }
    }

    private void displayOrderById() {
        System.out.print("Enter Order ID: ");
        String id = scanner.nextLine().trim();

        Order[] orders = ordersManager.getOrders();
        Order found = null;

        for (Order order : orders) {
            if (order == null) break;
            if (order.getOrderId().equals(id)) {
                found = order;
                break;
            }
        }

        if (found != null) {
            System.out.println(found.toDisplayString());
        } else {
            System.out.println("Order not found: " + id);
        }
    }

    private void startOrder() {
        System.out.print("Enter Order ID to start: ");
        String id = scanner.nextLine().trim();

        OrderStatus currentStatus = ordersManager.getCompletionStatus(id);

        if (currentStatus == null) {
            System.out.println("Order not found: " + id);
            return;
        }

        if (currentStatus == OrderStatus.NOT_STARTED) {
            boolean success = ordersManager.updateCompletion(OrderStatus.IN_PROGRESS, id);
            if (success) {
                System.out.println("✓ Order " + id + " started successfully!");
            } else {
                System.out.println("✗ Failed to start order.");
            }
        } else {
            System.out.println("Cannot start order. Current status: " + currentStatus);
        }
    }

    private void completeOrder() {
        System.out.print("Enter Order ID to complete: ");
        String id = scanner.nextLine().trim();

        OrderStatus currentStatus = ordersManager.getCompletionStatus(id);

        if (currentStatus == null) {
            System.out.println("Order not found: " + id);
            return;
        }

        if (currentStatus == OrderStatus.IN_PROGRESS) {
            boolean success = ordersManager.updateCompletion(OrderStatus.COMPLETED, id);
            if (success) {
                System.out.println("✓ Order " + id + " completed successfully!");
            } else {
                System.out.println("✗ Failed to complete order.");
            }
        } else {
            System.out.println("Cannot complete order. Current status: " + currentStatus);
            if (currentStatus == OrderStatus.NOT_STARTED) {
                System.out.println("Hint: Start the order first before completing it.");
            }
        }
    }

    private void exportOrders() {
        System.out.print("Enter output JSON file path: ");
        String path = scanner.nextLine().trim();
        System.out.println("Export requested to: " + path + " (not wired yet)");
    }
}
