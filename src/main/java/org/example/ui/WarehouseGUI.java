package org.example.ui;

import org.example.DirectoryImporter;
import org.example.OrderParser;
import org.example.ParserFactory;
import org.example.model.*;
import org.example.persistence.OrderPersistence;
import org.example.storage.OrderRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Enhanced GUI with modern colors and styling
 */
public class WarehouseGUI extends JFrame {

    // Modern color scheme
    private static final Color HEADER_COLOR = new Color(63, 81, 181);       // Blue
    private static final Color BUTTON_BLUE = new Color(33, 150, 243);       // Light Blue
    private static final Color BUTTON_GREEN = new Color(76, 175, 80);       // Green
    private static final Color BUTTON_RED = new Color(244, 67, 54);         // Red
    private static final Color BUTTON_ORANGE = new Color(255, 152, 0);      // Orange
    private static final Color BACKGROUND = new Color(245, 245, 245);       // Light Gray

    private final OrderRepository repository = new OrderRepository();
    private final OrderPersistence persistence = new OrderPersistence();
    private final DirectoryImporter directoryImporter;

    private final JTextArea displayArea = new JTextArea();
    private final JTextField orderIdField = new JTextField(15);

    public WarehouseGUI() {
        directoryImporter = new DirectoryImporter(repository, persistence);

        setTitle("Warehouse Order System");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND);

        // HEADER with blue background
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("📦 Warehouse Order System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        JLabel idLabel = new JLabel("Order ID:");
        idLabel.setForeground(Color.WHITE);
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        orderIdField.setPreferredSize(new Dimension(150, 30));
        orderIdField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchPanel.add(idLabel);
        searchPanel.add(orderIdField);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        // MENU BAR
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(HEADER_COLOR);
        JMenu menu = new JMenu("☰ Menu");
        menu.setForeground(Color.WHITE);
        menu.setFont(new Font("Segoe UI", Font.BOLD, 14));

        String[] items = {"Import", "Import Directory", "View Orders", "Search",
                "Start", "Complete", "Cancel", "Save"};
        for (String item : items) {
            JMenuItem menuItem = new JMenuItem(item);
            menuItem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            switch (item) {
                case "Import" -> menuItem.addActionListener(e -> importOrder());
                case "Import Directory" -> menuItem.addActionListener(e -> importDirectory());
                case "View Orders" -> menuItem.addActionListener(e -> viewOrders());
                case "Search" -> menuItem.addActionListener(e -> searchOrder());
                case "Start" -> menuItem.addActionListener(e -> startOrder());
                case "Complete" -> menuItem.addActionListener(e -> completeOrder());
                case "Cancel" -> menuItem.addActionListener(e -> cancelOrder());
                case "Save" -> menuItem.addActionListener(e -> save());
            }
            menu.add(menuItem);
        }
        menuBar.add(menu);
        setJMenuBar(menuBar);

        // DISPLAY AREA
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(BACKGROUND);
        centerPanel.setBorder(new EmptyBorder(15, 15, 10, 15));

        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        displayArea.setBackground(Color.WHITE);
        displayArea.setBorder(new EmptyBorder(15, 15, 15, 15));
        displayArea.setLineWrap(true);

        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        centerPanel.add(scrollPane);

        // BUTTONS with colors!
        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        buttonPanel.setBackground(BACKGROUND);
        buttonPanel.setBorder(new EmptyBorder(10, 15, 15, 15));

        JButton importBtn = createButton("📥 Import", BUTTON_BLUE);
        JButton viewBtn = createButton("👁 View", BUTTON_BLUE);
        JButton searchBtn = createButton("🔍 Search", BUTTON_BLUE);
        JButton startBtn = createButton("▶ Start", BUTTON_GREEN);
        JButton completeBtn = createButton("✓ Complete", BUTTON_GREEN);
        JButton cancelBtn = createButton("✗ Cancel", BUTTON_RED);
        JButton saveBtn = createButton("💾 Save", BUTTON_ORANGE);
        JButton importDirBtn = createButton("📁 Import Directory", BUTTON_BLUE);

        importBtn.addActionListener(e -> importOrder());
        viewBtn.addActionListener(e -> viewOrders());
        searchBtn.addActionListener(e -> searchOrder());
        startBtn.addActionListener(e -> startOrder());
        completeBtn.addActionListener(e -> completeOrder());
        cancelBtn.addActionListener(e -> cancelOrder());
        saveBtn.addActionListener(e -> save());
        importDirBtn.addActionListener(e -> importDirectory());

        buttonPanel.add(importBtn);
        buttonPanel.add(viewBtn);
        buttonPanel.add(searchBtn);
        buttonPanel.add(startBtn);
        buttonPanel.add(completeBtn);
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        buttonPanel.add(importDirBtn);

        // STATUS BAR
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(230, 230, 230));
        statusBar.setBorder(new EmptyBorder(5, 15, 5, 15));
        JLabel statusLabel = new JLabel("Ready  |  v3.0 Kotlin Edition");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(100, 100, 100));
        statusBar.add(statusLabel);

        // Assemble everything
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel container = new JPanel(new BorderLayout());
        container.add(mainPanel, BorderLayout.CENTER);
        container.add(statusBar, BorderLayout.SOUTH);

        add(container);

        loadOrders();
        viewOrders();
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(140, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            Color original = bgColor;
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(original);
            }
        });

        return btn;
    }

    // ========== ORDER OPERATIONS ==========

    private void loadOrders() {
        List<Order> list = persistence.loadOrders("orders.txt");
        for (Order order : list) {
            repository.addOrder(order);
        }
    }

    private void save() {
        persistence.saveOrders(repository.getAllOrders(), "orders.txt");
        JOptionPane.showMessageDialog(this, "✓ Saved successfully!");
    }

    private void importOrder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Order Files (*.json, *.xml)", "json", "xml"));

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            OrderParser parser = ParserFactory.getParserForFile(file);

            if (parser != null) {
                Order order = parser.parseOrderFromFile(file.getAbsolutePath());
                if (order != null) {
                    repository.addOrder(order);
                    save();
                    displayArea.setText("✓ Imported:\n\n" + formatOrder(order));
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to import", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void importDirectory() {
        JFileChooser dirChooser = new JFileChooser();
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (dirChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            DirectoryImporter.ImportResult result =
                    directoryImporter.importFromDirectory(dirChooser.getSelectedFile());

            JOptionPane.showMessageDialog(this, result.getSummaryMessage());
            if (result.getSuccessCount() > 0) {
                viewOrders();
            }
        }
    }

    private void viewOrders() {
        List<Order> list = repository.getUncompletedOrders();

        if (list.isEmpty()) {
            displayArea.setText("\n\n    No uncompleted orders found.\n\n    Try importing some orders to get started!");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append("              UNCOMPLETED ORDERS\n");
        sb.append("═══════════════════════════════════════════════════\n\n");

        for (Order order : list) {
            sb.append(formatOrderSummary(order)).append("\n\n");
        }

        displayArea.setText(sb.toString());
    }

    private void searchOrder() {
        String id = orderIdField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter an Order ID");
            return;
        }

        Order order = repository.findById(id);
        if (order == null) {
            displayArea.setText("\n\n    ✗ Order not found: " + id);
        } else {
            displayArea.setText("✓ Found:\n\n" + formatOrder(order));
        }
    }

    private Order getOrder() {
        String id = orderIdField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter an Order ID");
            return null;
        }
        Order order = repository.findById(id);
        if (order == null) {
            JOptionPane.showMessageDialog(this, "Order not found");
        }
        return order;
    }

    private void startOrder() {
        Order order = getOrder();
        if (order == null) return;

        if (order.getStatus() == OrderStatus.COMPLETED ||
                order.getStatus() == OrderStatus.CANCELED) {
            JOptionPane.showMessageDialog(this, "Cannot start this order");
            return;
        }

        order.setStatus(OrderStatus.IN_PROGRESS);
        save();
        displayArea.setText("✓ Started:\n\n" + formatOrder(order));
    }

    private void completeOrder() {
        Order order = getOrder();
        if (order == null) return;

        if (order.getStatus() == OrderStatus.CANCELED) {
            JOptionPane.showMessageDialog(this, "Cannot complete canceled order");
            return;
        }

        order.setStatus(OrderStatus.COMPLETED);
        save();
        displayArea.setText("✓ Completed:\n\n" + formatOrder(order));
    }

    private void cancelOrder() {
        Order order = getOrder();
        if (order == null) return;

        if (order.getStatus() == OrderStatus.COMPLETED) {
            JOptionPane.showMessageDialog(this, "Cannot cancel completed order");
            return;
        }

        String reason = JOptionPane.showInputDialog(this, "Cancellation reason:");
        if (reason != null && !reason.trim().isEmpty()) {
            order.cancelOrder(reason);
            save();
            displayArea.setText("✓ Canceled:\n\n" + formatOrder(order));
        }
    }

    private String formatOrder(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("Order ID:   ").append(order.getOrderId()).append("\n");
        sb.append("Type:       *** ").append(order.getType()).append(" ***\n");
        sb.append("Status:     ").append(order.getStatus()).append("\n");
        sb.append("Source:     ").append(order.getSource()).append("\n\n");
        sb.append("Items:\n");
        sb.append("───────────────────────────────────────\n");

        double total = 0;
        for (Item item : order.getItems()) {
            double itemTotal = item.getQuantity() * item.getPrice();
            total += itemTotal;
            sb.append(String.format("  • %-25s $%.2f\n",
                    item.getName() + " (x" + item.getQuantity() + ")", itemTotal));
        }
        sb.append("───────────────────────────────────────\n");
        sb.append(String.format("Total: $%.2f", total));

        return sb.toString();
    }

    private String formatOrderSummary(Order order) {
        double total = order.getItems().stream()
                .mapToDouble(i -> i.getQuantity() * i.getPrice()).sum();
        return String.format("  [%s] %s | %s | $%.2f | %d items",
                order.getOrderId(), order.getType(), order.getStatus(),
                total, order.getItems().size());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WarehouseGUI gui = new WarehouseGUI();
            gui.setVisible(true);
        });
    }
}