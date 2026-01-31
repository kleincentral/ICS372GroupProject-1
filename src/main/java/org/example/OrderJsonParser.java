package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON Parser for Order Files
 * This class reads JSON files that contain order information and converts them
 * into Order objects that our program can use.
 * What it does:
 * 1. Opens and reads a JSON file
 * 2. Figures out if it's a ship order or pickup order
 * 3. Gets the date the order was placed
 * 4. Creates Item objects for each item in the order
 * 5. Creates the right type of Order object (DirectShipOrder or PickupOrder)
 */
public class OrderJsonParser {

    /**
     * This is the main method that reads a JSON file and creates an Order from it.
     * Give it a file path, and it gives you back an Order object (or null if something went wrong).
     */
    public Order parseOrderFromFile(String filePath) {
        // Create a JSON parser - this is the tool that reads JSON files
        JSONParser parser = new JSONParser();

        try {
            // Step 1: Open and read the JSON file
            Object obj = parser.parse(new FileReader(filePath));
            JSONObject jsonObject = (JSONObject) obj;

            // Step 2: Get the "order" section from the JSON
            JSONObject orderJson = (JSONObject) jsonObject.get("order");

            // Make sure we actually found an "order" section
            if (orderJson == null) {
                System.err.println("Error: No 'order' field found in JSON");
                return null;
            }

            // Step 3: Extract the order details we need

            // Get the type (either "ship" or "pickup")
            String type = (String) orderJson.get("type");

            // Get the order date
            Long orderDateLong = (Long) orderJson.get("order_date");

            // Check that we got both type and date
            if (type == null || orderDateLong == null) {
                System.err.println("Error: Missing required fields (type or order_date)");
                return null;
            }

            // Convert the Long to a regular long (just a Java thing)
            long orderDate = orderDateLong;

            // Step 4: Get the items array from the JSON
            JSONArray itemsArray = (JSONArray) orderJson.get("items");

            // Make sure we have at least one item
            if (itemsArray == null || itemsArray.isEmpty()) {
                System.err.println("Error: No items found in order");
                return null;
            }

            // Step 5: Convert the JSON items into actual Item objects
            List<Item> items = parseItems(itemsArray);

            // Step 6: Create the right type of order (ship or pickup) with all the info
            return createOrder(type, orderDate, items);

        } catch (IOException e) {
            // This happens if the file doesn't exist or can't be opened
            System.err.println("Error reading file: " + e.getMessage());
            return null;

        } catch (ParseException e) {
            // This happens if the JSON is formatted wrong
            System.err.println("Error parsing JSON: " + e.getMessage());
            return null;

        } catch (Exception e) {
            // This catches any other unexpected errors
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method takes the items array from the JSON and creates Item objects.
     * It goes through each item in the array and makes an Item object for it.
     * Returns a list (array) of all the Item objects.
     */
    private List<Item> parseItems(JSONArray itemsArray) {
        // Create an empty list to store our Item objects
        List<Item> items = new ArrayList<>();

        // Loop through each item in the JSON array
        for (Object itemObj : itemsArray) {
            // Convert this item to a JSONObject so we can read its fields
            JSONObject itemJson = (JSONObject) itemObj;

            // Get the item's name (like "Chair" or "Lamp")
            String name = (String) itemJson.get("name");

            // Get the quantity (how many of this item - comes as a Long from JSON)
            Long quantityLong = (Long) itemJson.get("quantity");

            // Get the price (could be a whole number or decimal in the JSON)
            Number priceNumber = (Number) itemJson.get("price");
            double price = priceNumber.doubleValue();

            // Make sure this item has all the required info
            if (name == null || quantityLong == null || priceNumber == null) {
                System.err.println("Warning: Skipping item with missing fields");
                continue; // Skip this item and move to the next one
            }

            // Convert quantity from Long to int (just a Java type thing)
            int quantity = quantityLong.intValue();

            // Create a new Item object and add it to our list
            items.add(new Item(name, quantity, price));
        }

        // Return the complete list of Item objects
        return items;
    }

    /**
     * This method creates the right type of Order based on the type string.
     * If type is "ship", it creates a DirectShipOrder.
     * If type is "pickup", it creates a PickupOrder.
     */
    private Order createOrder(String type, long orderDate, List<Item> items) {
        // Make the type lowercase so "Ship" and "ship" both work
        type = type.toLowerCase();

        // Check what type of order this is and create the right one
        if (type.equals("ship")) {
            // It's a shipping order - create a DirectShipOrder
            return new DirectShipOrder(orderDate, items);

        } else if (type.equals("pickup")) {
            // It's a pickup order - create a PickupOrder
            return new PickupOrder(orderDate, items);

        } else {
            // We got a type we don't recognize - throw an error
            throw new IllegalArgumentException("Unknown order type: " + type +
                    ". Expected 'ship' or 'pickup'");
        }
    }
}
