package org.example

import org.example.model.Item
import org.example.model.Order
import org.example.model.OrderType
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Reads XML order files and converts them into Order objects.
 * Handles wallyworld.com XML format with error tolerance for buggy data.
 */
class XMLInput : OrderParser {

    /**
     * Parse a single order from an XML file
     */
    override fun parseOrderFromFile(filePath: String): Order? {
        return try {
            // Set up XML parser
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()

            // Parse the XML file
            val doc = builder.parse(File(filePath))
            doc.documentElement.normalize()

            // Get all Order elements
            val orderNodes = doc.getElementsByTagName("Order")

            if (orderNodes.length == 0) {
                System.err.println("Error: No Order elements found in XML")
                return null
            }

            // Parse the first order
            val orderElement = orderNodes.item(0) as Element

            // Extract order ID from attribute
            val orderId = orderElement.getAttribute("id")
            if (orderId.isNullOrBlank()) {
                System.err.println("Error: Order missing id attribute")
                return null
            }

            // Extract order type
            val orderTypeString = getElementText(orderElement, "OrderType")
            if (orderTypeString.isNullOrBlank()) {
                System.err.println("Error: Order missing OrderType")
                return null
            }

            val orderType = convertToOrderType(orderTypeString)
            if (orderType == null) {
                System.err.println("Error: Unknown order type: $orderTypeString")
                return null
            }

            // Use current timestamp as order date
            val orderDate = System.currentTimeMillis()

            // Parse items
            val itemNodes = orderElement.getElementsByTagName("Item")
            val items = parseItems(itemNodes, orderId)

            if (items.isEmpty()) {
                System.err.println("Error: Order $orderId has no valid items")
                return null
            }

            // Extract filename from path for source tracking
            val sourceFile = File(filePath).name

            // Create and return Order with source tracking
            Order(orderId, orderType, orderDate, items, sourceFile, "unknown")

        } catch (e: Exception) {
            System.err.println("Error parsing XML file: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    /**
     * Parse all items from the XML NodeList
     */
    private fun parseItems(itemNodes: org.w3c.dom.NodeList, orderId: String): List<Item> {
        val items = mutableListOf<Item>()

        for (i in 0 until itemNodes.length) {
            val node = itemNodes.item(i)

            if (node.nodeType == Node.ELEMENT_NODE) {
                val itemElement = node as Element

                // Extract item type from attribute
                val itemType = itemElement.getAttribute("type")
                if (itemType.isNullOrBlank()) {
                    System.err.println("Warning: Skipped item in order $orderId - missing type attribute")
                    continue
                }

                // Extract price
                val priceString = getElementText(itemElement, "Price")
                if (priceString.isNullOrBlank()) {
                    System.err.println("Warning: Skipped item '$itemType' in order $orderId - missing price")
                    continue
                }

                // Extract quantity
                val quantityString = getElementText(itemElement, "Quantity")
                if (quantityString.isNullOrBlank()) {
                    System.err.println("Warning: Skipped item '$itemType' in order $orderId - missing quantity")
                    continue
                }

                // Parse price and quantity with validation
                try {
                    val price = priceString.trim().toDouble()
                    val quantity = quantityString.trim().toInt()

                    // Validate values are positive
                    if (price <= 0 || quantity <= 0) {
                        System.err.println("Warning: Skipped item '$itemType' in order $orderId - invalid price or quantity values")
                        continue
                    }

                    // Create Item object
                    items.add(Item(itemType, quantity, price))

                } catch (e: NumberFormatException) {
                    System.err.println("Warning: Skipped item '$itemType' in order $orderId - invalid number format: ${e.message}")
                }
            }
        }

        return items
    }

    /**
     * Get text content from an element by tag name
     */
    private fun getElementText(parent: Element, tagName: String): String? {
        val nodeList = parent.getElementsByTagName(tagName)
        return if (nodeList.length > 0) {
            nodeList.item(0).textContent
        } else {
            null
        }
    }

    /**
     * Convert type string to OrderType enum
     */
    private fun convertToOrderType(typeString: String): OrderType? {
        return when (typeString.lowercase()) {
            "delivery" -> OrderType.DIRECT_DELIVERY
            "ship" -> OrderType.SHIP
            "pickup" -> OrderType.PICKUP
            else -> null
        }
    }

    /**
     * Parse multiple orders from an XML file
     */
    fun parseMultipleOrdersFromFile(filePath: String): List<Order> {
        val orders = mutableListOf<Order>()

        try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val doc = builder.parse(File(filePath))
            doc.documentElement.normalize()

            val orderNodes = doc.getElementsByTagName("Order")
            val sourceFile = File(filePath).name

            for (i in 0 until orderNodes.length) {
                val orderElement = orderNodes.item(i) as Element

                // Extract order ID
                val orderId = orderElement.getAttribute("id")
                if (orderId.isNullOrBlank()) {
                    System.err.println("Warning: Skipped order - missing id attribute")
                    continue
                }

                // Extract order type
                val orderTypeString = getElementText(orderElement, "OrderType")
                if (orderTypeString.isNullOrBlank()) {
                    System.err.println("Warning: Skipped order $orderId - missing OrderType")
                    continue
                }

                val orderType = convertToOrderType(orderTypeString)
                if (orderType == null) {
                    System.err.println("Warning: Skipped order $orderId - unknown order type: $orderTypeString")
                    continue
                }

                // Parse items
                val itemNodes = orderElement.getElementsByTagName("Item")
                val items = parseItems(itemNodes, orderId)

                if (items.isEmpty()) {
                    System.err.println("Warning: Skipped order $orderId - no valid items")
                    continue
                }

                // Create Order with source tracking
                val orderDate = System.currentTimeMillis()
                orders.add(Order(orderId, orderType, orderDate, items, sourceFile, "unknown"))
            }

            println("Successfully imported ${orders.size} orders from $sourceFile")

        } catch (e: Exception) {
            System.err.println("Error parsing XML file: ${e.message}")
        }

        return orders
    }
}