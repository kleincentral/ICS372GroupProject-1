package org.example.ui;

import org.example.model.Order;
import org.example.model.OrderStatus;
import org.example.persistence.OrderPersistence;
import org.example.storage.OrderRepository;
//testing
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);
    private final OrderRepository repository = new OrderRepository();
    private final OrderPersistence persistence = new OrderPersistence();

    public void run() {
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
                case "7" -> exportOrders();
                case "0" -> running = false;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }

        persistence.saveOrders(repository.getAllOrders(), "orders.txt");
        System.out.println("Thank you.");
    }

    private void printMenu() {
        System.out.println("\n----- Order Tracking System -----");
        System.out.println("1) Import orders from JSON file");
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
        Order testOrder = new Order(
                "TEST-" + System.currentTimeMillis(),
                org.example.model.OrderType.SHIP,
                System.currentTimeMillis(),
                java.util.List.of(
                        new org.example.model.Item("Test Item", 2, 10.0)
                )
        );

        repository.addOrder(testOrder);

        System.out.println("Order added (temporary test mode).");
        System.out.println(testOrder.toDisplayString());
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

        if (order.getStatus() == OrderStatus.CANCELLED) {
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

        if (order.getStatus() == OrderStatus.CANCELLED) {
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

        order.setStatus(OrderStatus.CANCELLED);
        System.out.println("Order cancelled.");
    }

    private void exportOrders() {
        System.out.print("Enter output JSON file path: ");
        String path = scanner.nextLine().trim();
        System.out.println("Export requested to: " + path + " (not wired yet)");
    }
}