package org.example;

import org.example.model.Item;
import org.example.model.Order;
import org.example.model.OrderType;
import org.example.model.OrderStatus;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads JSON order files and converts them into Order objects.
 */
public class OrderJsonParser {

    // Counter for generating unique order IDs
    private static int nextId = 1000;

    // Reads a JSON file and returns an Order object
    public Order parseOrderFromFile(String filePath) {
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader(filePath));
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject orderJson = (JSONObject) jsonObject.get("order");

            if (orderJson == null) {
                System.err.println("Error: No 'order' field found in JSON");
                return null;
            }

            String typeString = (String) orderJson.get("type");
            Long orderDateLong = (Long) orderJson.get("order_date");

            if (typeString == null || orderDateLong == null) {
                System.err.println("Error: Missing required fields");
                return null;
            }

            // Convert type string to OrderType enum
            OrderType orderType = convertToOrderType(typeString);
            if (orderType == null) {
                System.err.println("Error: Unknown order type: " + typeString);
                return null;
            }

            JSONArray itemsArray = (JSONArray) orderJson.get("items");
            if (itemsArray == null || itemsArray.isEmpty()) {
                System.err.println("Error: No items found in order");
                return null;
            }

            // Parse items and generate order ID
            List<Item> items = parseItems(itemsArray);
            String orderId = "ORDER-" + nextId++;

            return new Order(orderId, orderType, orderDateLong, items);

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return null;
        } catch (ParseException e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return null;
        }
    }

    // Creates a list of Item objects from the JSON items array
    private List<Item> parseItems(JSONArray itemsArray) {
        List<Item> items = new ArrayList<>();

        for (Object itemObj : itemsArray) {
            JSONObject itemJson = (JSONObject) itemObj;

            String name = (String) itemJson.get("name");
            Long quantityLong = (Long) itemJson.get("quantity");
            Number priceNumber = (Number) itemJson.get("price");

            if (name == null || quantityLong == null || priceNumber == null) {
                System.err.println("Warning: Skipping item with missing fields");
                continue;
            }

            items.add(new Item(name, quantityLong.intValue(), priceNumber.doubleValue()));
        }

        return items;
    }

    // Converts type string ("ship"/"pickup") to OrderType enum
    private OrderType convertToOrderType(String typeString) {
        return switch (typeString.toLowerCase()) {
            case "ship" -> OrderType.SHIP;
            case "pickup" -> OrderType.PICKUP;
            default -> null;
        };
    }
}