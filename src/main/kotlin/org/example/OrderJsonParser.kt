package org.example

import org.example.model.Item
import org.example.model.Order
import org.example.model.OrderType
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.File
import java.io.FileReader

/**
 * Reads JSON order files and converts them into Order objects.
 */
class OrderJsonParser {

    companion object {
        // Counter for generating unique order IDs
        private var nextId = 1000
    }

    /**
     * Reads a JSON file and returns an Order object
     * @param filePath path to the JSON file
     * @return Order object or null if parsing fails
     */
    fun parseOrderFromFile(filePath: String): Order? {
        val parser = JSONParser()

        return try {
            val obj = parser.parse(FileReader(filePath))
            val jsonObject = obj as JSONObject
            val orderJson = jsonObject["order"] as? JSONObject

            if (orderJson == null) {
                System.err.println("Error: No 'order' field found in JSON")
                return null
            }

            val typeString = orderJson["type"] as? String
            val orderDateLong = orderJson["order_date"] as? Long

            if (typeString == null || orderDateLong == null) {
                System.err.println("Error: Missing required fields")
                return null
            }

            // Convert type string to OrderType enum
            val orderType = convertToOrderType(typeString)
            if (orderType == null) {
                System.err.println("Error: Unknown order type: $typeString")
                return null
            }

            val itemsArray = orderJson["items"] as? JSONArray
            if (itemsArray == null || itemsArray.isEmpty()) {
                System.err.println("Error: No items found in order")
                return null
            }

            // Parse items and generate order ID
            val items = parseItems(itemsArray)
            val orderId = "ORDER-${nextId++}"

            // Extract filename from path for source tracking
            val sourceFile = File(filePath).name

            // Create Order with source tracking
            Order(orderId, orderType, orderDateLong, items, sourceFile, "unknown")

        } catch (e: java.io.IOException) {
            System.err.println("Error reading file: ${e.message}")
            null
        } catch (e: org.json.simple.parser.ParseException) {
            System.err.println("Error parsing JSON: ${e.message}")
            null
        } catch (e: Exception) {
            System.err.println("Unexpected error: ${e.message}")
            null
        }
    }

    /**
     * Creates a list of Item objects from the JSON items array
     * @param itemsArray JSONArray containing item data
     * @return List of Item objects
     */
    private fun parseItems(itemsArray: JSONArray): List<Item> {
        val items = mutableListOf<Item>()

        for (itemObj in itemsArray) {
            val itemJson = itemObj as JSONObject

            val name = itemJson["name"] as? String
            val quantityLong = itemJson["quantity"] as? Long
            val priceNumber = itemJson["price"] as? Number

            if (name == null || quantityLong == null || priceNumber == null) {
                System.err.println("Warning: Skipping item with missing fields")
                continue
            }

            items.add(Item(name, quantityLong.toInt(), priceNumber.toDouble()))
        }

        return items
    }

    /**
     * Converts type string to OrderType enum
     * @param typeString Type string from JSON ("ship"/"pickup"/"delivery")
     * @return OrderType enum or null if unknown
     */
    private fun convertToOrderType(typeString: String): OrderType? {
        return when (typeString.lowercase()) {
            "ship" -> OrderType.SHIP
            "pickup" -> OrderType.PICKUP
            "delivery" -> OrderType.DIRECT_DELIVERY
            else -> null
        }
    }
}