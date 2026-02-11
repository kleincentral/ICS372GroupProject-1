package org.example.ui;

import org.example.OrderJsonParser;
import org.example.model.Order;

import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);

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

    // --- Stubs for now (team will connect Orders/JsonParser later) ---
    private void importOrders() {
        System.out.print("Enter JSON file path: ");
        String path = scanner.nextLine().trim();

        OrderJsonParser parser = new OrderJsonParser();
        Order order = parser.parseOrderFromFile(path);

        if (order != null) {
            System.out.println("Successfully imported order!");
            System.out.println(order.toDisplayString());
        } else {
            System.out.println("Failed to import order from: " + path);
        }
    }

    private void listUncompletedOrders() {
        System.out.println("List uncompleted orders (not wired yet)");
    }

    private void displayOrderById() {
        System.out.print("Enter Order ID: ");
        String id = scanner.nextLine().trim();
        System.out.println("Display order " + id + " (not wired yet)");
    }

    private void startOrder() {
        System.out.print("Enter Order ID to start: ");
        String id = scanner.nextLine().trim();
        System.out.println("Start order " + id + " (not wired yet)");
    }

    private void completeOrder() {
        System.out.print("Enter Order ID to complete: ");
        String id = scanner.nextLine().trim();
        System.out.println("Complete order " + id + " (not wired yet)");
    }

    private void exportOrders() {
        System.out.print("Enter output JSON file path: ");
        String path = scanner.nextLine().trim();
        System.out.println("Export requested to: " + path + " (not wired yet)");
    }
}
