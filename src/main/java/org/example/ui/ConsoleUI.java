package org.example.ui;

import org.example.OrderJsonParser;
import org.example.XMLInput;
import org.example.model.Order;
import org.example.model.OrderStatus;
import org.example.persistence.OrderPersistence;
import org.example.storage.OrderRepository;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);
    private final OrderRepository repository = new OrderRepository();
    private final OrderPersistence persistence = new OrderPersistence();

    public void run() {

        // Load from JSON (your feature)
        repository.loadFromFile();

        // Existing team load
        List<Order> loadedOrders = persistence.loadOrders("orders.txt");
        for (Order order : loadedOrders) {
            repository.addOrder(order);
        }

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
                case "6" -> cancelOrder();
                case "7" -> exportOrders(); // ✅ FIXED
                case "0" -> running = false;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }

        // Save to JSON (your feature)
        repository.saveToFile();

        // Existing team save
        persistence.saveOrders(repository.getAllOrders(), "orders.txt");

        System.out.println("Thank you.");
    }

    private void printMenu() {
        System.out.println("\n----- Order Tracking System -----");
        System.out.println("1) Import orders from JSON or XML file");
        System.out.println("2) List uncompleted orders");
        System.out.println("3) Display order by ID");
        System.out.println("4) Start order");
        System.out.println("5) Complete order");
        System.out.println("6) Cancel order");
        System.out.println("7) Export all orders to JSON");
        System.out.println("0) Exit");
        System.out.print("Choose: ");
    }

    private void importOrders() {
        System.out.print("Enter file path: ");
        String filePath = scanner.nextLine().trim();

        Order order = null;

        if (filePath.toLowerCase().endsWith(".json")) {
            OrderJsonParser jsonParser = new OrderJsonParser();
            order = jsonParser.parseOrderFromFile(filePath);
        } else if (filePath.toLowerCase().endsWith(".xml")) {
            XMLInput xmlParser = new XMLInput();
            order = xmlParser.parseOrderFromFile(filePath);
        } else {
            System.out.println("Error: Unsupported file format. Please use .json or .xml files.");
            return;
        }

        if (order != null) {
            repository.addOrder(order);
            System.out.println("✓ Order imported successfully!");
            System.out.println(order.toDisplayString());
        } else {
            System.out.println("✗ Failed to import order. Check file format and try again.");
        }
    }

    private void listUncompletedOrders() {
        List<Order> orders = repository.getUncompletedOrders();

        if (orders.isEmpty()) {
            System.out.println("No uncompleted orders.");
            return;
        }

        for (Order order : orders) {
            System.out.println(order.toDisplayString());
        }
    }

    private void displayOrderById() {
        System.out.print("Enter Order ID: ");
        String id = scanner.nextLine().trim();

        Order order = repository.findById(id);

        if (order == null) {
            System.out.println("Order not found.");
        } else {
            System.out.println(order.toDisplayString());
        }
    }

    private void startOrder() {
        System.out.print("Enter Order ID to start: ");
        String id = scanner.nextLine().trim();

        Order order = repository.findById(id);

        if (order == null) {
            System.out.println("Order not found.");
            return;
        }

        if (order.getStatus() == OrderStatus.COMPLETED) {
            System.out.println("Order is already completed.");
            return;
        }

        if (order.getStatus() == OrderStatus.CANCELED) {
            System.out.println("Cancelled orders cannot be started.");
            return;
        }

        order.setStatus(OrderStatus.IN_PROGRESS);
        System.out.println("Order started.");
    }

    private void completeOrder() {
        System.out.print("Enter Order ID to complete: ");
        String id = scanner.nextLine().trim();

        Order order = repository.findById(id);

        if (order == null) {
            System.out.println("Order not found.");
            return;
        }

        if (order.getStatus() == OrderStatus.CANCELED) {
            System.out.println("Cancelled orders cannot be completed.");
            return;
        }

        order.setStatus(OrderStatus.COMPLETED);
        System.out.println("Order completed.");
    }

    private void cancelOrder() {
        System.out.print("Enter Order ID to cancel: ");
        String id = scanner.nextLine().trim();

        Order order = repository.findById(id);

        if (order == null) {
            System.out.println("Order not found.");
            return;
        }

        if (order.getStatus() == OrderStatus.COMPLETED) {
            System.out.println("Completed orders cannot be cancelled.");
            return;
        }

        order.setStatus(OrderStatus.CANCELED);
        System.out.println("Order cancelled.");
    }


    private void exportOrders() {
        repository.saveToFile();
        System.out.println("Orders exported to JSON file successfully.");
    }
}