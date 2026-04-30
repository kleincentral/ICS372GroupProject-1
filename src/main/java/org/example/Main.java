package org.example;

import org.example.model.Order;
import org.example.ui.WarehouseGUI;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        AutoSaveManager autoSaveManager = new AutoSaveManager();

        // Load saved orders at startup
        List<Order> orders = autoSaveManager.loadOnStartup();

        WarehouseGUI.main(args);

        // Auto-save orders when the program reaches the end
        autoSaveManager.autoSave(orders);
    }
}