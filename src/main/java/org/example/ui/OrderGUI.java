package org.example.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.OrderJsonParser;
import org.example.XMLInput;
import org.example.model.Order;
import org.example.persistence.OrderPersistence;
import org.example.storage.OrderRepository;

import java.io.File;
import java.util.List;

public class OrderGUI extends Application {

    private final OrderRepository repository = new OrderRepository();
    private final OrderPersistence persistence = new OrderPersistence();

    private final ListView<String> orderListView = new ListView<>();
    private final TextArea detailsArea = new TextArea();

    @Override
    public void start(Stage stage) {
        loadOrders();

        Label titleLabel = new Label("Warehouse Order Tracking System");

        Button importButton = new Button("Import");
        Button refreshButton = new Button("Refresh");
        Button startButton = new Button("Start Order");
        Button completeButton = new Button("Complete Order");
        Button cancelButton = new Button("Cancel Order");
        Button saveButton = new Button("Save");

        detailsArea.setEditable(false);
        detailsArea.setWrapText(true);

        refreshOrderList();

        orderListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            showSelectedOrderDetails();
        });

        importButton.setOnAction(e -> importOrder(stage));
        refreshButton.setOnAction(e -> refreshOrderList());

        startButton.setOnAction(e -> {
            Order selected = getSelectedOrder();
            if (selected == null) {
                showAlert("Please select an order.");
                return;
            }

            boolean success = repository.startOrder(selected.getOrderId());
            if (!success) {
                showAlert("Order could not be started.");
            }
            refreshOrderList();
            showSelectedOrderDetails();
        });

        completeButton.setOnAction(e -> {
            Order selected = getSelectedOrder();
            if (selected == null) {
                showAlert("Please select an order.");
                return;
            }

            boolean success = repository.completeOrder(selected.getOrderId());
            if (!success) {
                showAlert("Order could not be completed.");
            }
            refreshOrderList();
            showSelectedOrderDetails();
        });

        cancelButton.setOnAction(e -> {
            Order selected = getSelectedOrder();
            if (selected == null) {
                showAlert("Please select an order.");
                return;
            }

            boolean success = repository.cancelOrder(selected.getOrderId());
            if (!success) {
                showAlert("Order could not be cancelled.");
            }
            refreshOrderList();
            showSelectedOrderDetails();
        });

        saveButton.setOnAction(e -> {
            repository.saveToFile();
            persistence.saveOrders(repository.getAllOrders(), "orders.txt");
            showAlert("Orders saved.");
        });

        VBox leftPane = new VBox(10, new Label("Orders"), orderListView);
        leftPane.setPadding(new Insets(10));
        leftPane.setPrefWidth(250);

        VBox rightPane = new VBox(10, new Label("Order Details"), detailsArea);
        rightPane.setPadding(new Insets(10));
        VBox.setVgrow(detailsArea, Priority.ALWAYS);

        HBox buttonBar = new HBox(10, importButton, refreshButton, startButton, completeButton, cancelButton, saveButton);
        buttonBar.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(titleLabel);
        BorderPane.setMargin(titleLabel, new Insets(10));
        root.setLeft(leftPane);
        root.setCenter(rightPane);
        root.setBottom(buttonBar);

        Scene scene = new Scene(root, 900, 500);
        stage.setTitle("Order Tracking GUI");
        stage.setScene(scene);
        stage.show();
    }

    private void loadOrders() {
        repository.loadFromFile();

        List<Order> loadedOrders = persistence.loadOrders("orders.txt");
        for (Order order : loadedOrders) {
            if (repository.findById(order.getOrderId()) == null) {
                repository.addOrder(order);
            }
        }
    }

    private void refreshOrderList() {
        orderListView.getItems().clear();

        for (Order order : repository.getAllOrders()) {
            orderListView.getItems().add(
                    order.getOrderId() + " | " + order.getType() + " | " + order.getStatus()
            );
        }
    }

    private Order getSelectedOrder() {
        String selectedLine = orderListView.getSelectionModel().getSelectedItem();
        if (selectedLine == null || selectedLine.isBlank()) {
            return null;
        }

        String id = selectedLine.split("\\|")[0].trim();
        return repository.findById(id);
    }

    private void showSelectedOrderDetails() {
        Order order = getSelectedOrder();
        if (order == null) {
            detailsArea.clear();
            return;
        }

        detailsArea.setText(order.toDisplayString());
    }

    private void importOrder(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose JSON or XML Order File");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Order Files", "*.json", "*.xml")
        );

        File file = chooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }

        Order order = null;
        String fileName = file.getName().toLowerCase();

        if (fileName.endsWith(".json")) {
            OrderJsonParser parser = new OrderJsonParser();
            order = parser.parseOrderFromFile(file.getAbsolutePath());
        } else if (fileName.endsWith(".xml")) {
            XMLInput parser = new XMLInput();
            order = parser.parseOrderFromFile(file.getAbsolutePath());
        }

        if (order != null) {
            repository.addOrder(order);
            refreshOrderList();
            showAlert("Order imported successfully.");
        } else {
            showAlert("Failed to import order.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void stop() {
        repository.saveToFile();
        persistence.saveOrders(repository.getAllOrders(), "orders.txt");
    }
}