package org.example.ui;

import org.example.OrderJsonParser;
import org.example.model.*;
import org.example.persistence.OrderPersistence;
import org.example.storage.OrderRepository;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class WarehouseGUI extends JFrame {

    private final OrderRepository repository = new OrderRepository();
    private final OrderPersistence persistence = new OrderPersistence();

    private final JTextArea displayArea = new JTextArea();
    private final JTextField orderIdField = new JTextField(15);

    public WarehouseGUI() {
        setTitle("Warehouse Order System");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("☰ Menu");

        JMenuItem importItem = new JMenuItem("Import");
        JMenuItem viewItem = new JMenuItem("View Orders");
        JMenuItem searchItem = new JMenuItem("Search");
        JMenuItem startItem = new JMenuItem("Start");
        JMenuItem completeItem = new JMenuItem("Complete");
        JMenuItem cancelItem = new JMenuItem("Cancel");
        JMenuItem saveItem = new JMenuItem("Save");

        menu.add(importItem);
        menu.add(viewItem);
        menu.add(searchItem);
        menu.add(startItem);
        menu.add(completeItem);
        menu.add(cancelItem);
        menu.add(saveItem);

        menuBar.add(menu);
        setJMenuBar(menuBar);

        loadOrders();

        displayArea.setEditable(false);

        JPanel top = new JPanel();
        top.add(new JLabel("Order ID:"));
        top.add(orderIdField);

        JButton importBtn = new JButton("Import");
        JButton viewBtn = new JButton("View");
        JButton searchBtn = new JButton("Search");
        JButton startBtn = new JButton("Start");
        JButton completeBtn = new JButton("Complete");
        JButton cancelBtn = new JButton("Cancel");
        JButton saveBtn = new JButton("Save");

        JPanel buttons = new JPanel(new GridLayout(2, 4));
        buttons.add(importBtn);
        buttons.add(viewBtn);
        buttons.add(searchBtn);
        buttons.add(startBtn);
        buttons.add(completeBtn);
        buttons.add(cancelBtn);
        buttons.add(saveBtn);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        importBtn.addActionListener(e -> importOrder());
        viewBtn.addActionListener(e -> viewOrders());
        searchBtn.addActionListener(e -> searchOrder());
        startBtn.addActionListener(e -> startOrder());
        completeBtn.addActionListener(e -> completeOrder());
        cancelBtn.addActionListener(e -> cancelOrder());
        saveBtn.addActionListener(e -> save());

        importItem.addActionListener(e -> importOrder());
        viewItem.addActionListener(e -> viewOrders());
        searchItem.addActionListener(e -> searchOrder());
        startItem.addActionListener(e -> startOrder());
        completeItem.addActionListener(e -> completeOrder());
        cancelItem.addActionListener(e -> cancelOrder());
        saveItem.addActionListener(e -> save());

        viewOrders();
    }

    private void loadOrders() {
        List<Order> list = persistence.loadOrders("orders.txt");
        for (Order order : list) {
            repository.addOrder(order);
        }
    }

    private void save() {
        persistence.saveOrders(repository.getAllOrders(), "orders.txt");
        JOptionPane.showMessageDialog(this, "Saved!");
    }

    private void importOrder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Order Files (*.json, *.xml)", "json", "xml"));

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            Order order = null;

            if (file.getName().endsWith(".xml")) {
                org.example.XMLInput xmlParser = new org.example.XMLInput();
                order = xmlParser.parseOrderFromFile(file.getAbsolutePath());
            } else if (file.getName().endsWith(".json")) {
                OrderJsonParser jsonParser = new OrderJsonParser();
                order = jsonParser.parseOrderFromFile(file.getAbsolutePath());
            } else {
                JOptionPane.showMessageDialog(this,
                        "Unsupported file format. Please use .json or .xml files.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (order != null) {
                repository.addOrder(order);
                save();
                displayArea.setText("✓ Imported:\n\n" + order);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to import order. Check file format.",
                        "Import Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void viewOrders() {
        List<Order> list = repository.getUncompletedOrders();

        if (list.isEmpty()) {
            displayArea.setText("No orders.");
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (Order order : list) {
            sb.append(order).append("\n");
        }

        displayArea.setText(sb.toString());
    }

    private void searchOrder() {
        String id = orderIdField.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter an Order ID.");
            return;
        }

        Order order = repository.findById(id);

        if (order == null) {
            displayArea.setText("Order not found.");
        } else {
            displayArea.setText(order.toString());
        }
    }

    private Order getOrder() {
        String id = orderIdField.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter an Order ID.");
            return null;
        }

        Order order = repository.findById(id);

        if (order == null) {
            JOptionPane.showMessageDialog(this, "Order not found.");
            return null;
        }

        return order;
    }

    private void startOrder() {
        Order order = getOrder();

        if (order == null) {
            return;
        }

        if (order.getStatus() == OrderStatus.COMPLETED) {
            JOptionPane.showMessageDialog(this, "Completed orders cannot be started.");
            return;
        }

        if (order.getStatus() == OrderStatus.CANCELED) {
            JOptionPane.showMessageDialog(this, "Canceled orders cannot be started.");
            return;
        }

        order.setStatus(OrderStatus.IN_PROGRESS);
        save();

        displayArea.setText("Started:\n\n" + order);
    }

    private void completeOrder() {
        Order order = getOrder();

        if (order == null) {
            return;
        }

        if (order.getStatus() == OrderStatus.CANCELED) {
            JOptionPane.showMessageDialog(this, "Canceled orders cannot be completed.");
            return;
        }

        order.setStatus(OrderStatus.COMPLETED);
        save();

        displayArea.setText("Completed:\n\n" + order);
    }

    private void cancelOrder() {
        Order order = getOrder();

        if (order == null) {
            return;
        }

        if (order.getStatus() == OrderStatus.COMPLETED) {
            JOptionPane.showMessageDialog(this, "Completed orders cannot be canceled.");
            return;
        }

        order.setStatus(OrderStatus.CANCELED);
        save();

        displayArea.setText("Canceled:\n\n" + order);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WarehouseGUI().setVisible(true));
    }
}