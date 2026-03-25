package org.example;

import org.example.model.Item;
import org.example.model.Order;
import org.example.model.OrderType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads XML order files and converts them into Order objects.
 * Handles wallyworld.com XML format with error tolerance for buggy data.
 */
public class XMLInput {

    // Parse a single order from an XML file
    public Order parseOrderFromFile(String filePath) {
        try {
            // Set up XML parser
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Parse the XML file
            Document doc = builder.parse(new File(filePath));
            doc.getDocumentElement().normalize();

            // Get all Order elements
            NodeList orderNodes = doc.getElementsByTagName("Order");

            if (orderNodes.getLength() == 0) {
                System.err.println("Error: No Order elements found in XML");
                return null;
            }

            // Parse the first order (can be extended to handle multiple orders)
            Element orderElement = (Element) orderNodes.item(0);

            // Extract order ID from attribute
            String orderId = orderElement.getAttribute("id");
            if (orderId == null || orderId.isBlank()) {
                System.err.println("Error: Order missing id attribute");
                return null;
            }

            // Extract order type
            String orderTypeString = getElementText(orderElement, "OrderType");
            if (orderTypeString == null || orderTypeString.isBlank()) {
                System.err.println("Error: Order missing OrderType");
                return null;
            }

            OrderType orderType = convertToOrderType(orderTypeString);
            if (orderType == null) {
                System.err.println("Error: Unknown order type: " + orderTypeString);
                return null;
            }

            // Use current timestamp as order date (XML doesn't specify date)
            long orderDate = System.currentTimeMillis();

            // Parse items
            NodeList itemNodes = orderElement.getElementsByTagName("Item");
            List<Item> items = parseItems(itemNodes, orderId);

            if (items.isEmpty()) {
                System.err.println("Error: Order " + orderId + " has no valid items");
                return null;
            }

            // Extract filename from path for source tracking
            String sourceFile = new File(filePath).getName();

            // Create and return Order with source tracking
            return new Order(orderId, orderType, orderDate, items, sourceFile, "unknown");

        } catch (ParserConfigurationException e) {
            System.err.println("Error: XML parser configuration failed: " + e.getMessage());
            return null;
        } catch (SAXException e) {
            System.err.println("Error: Invalid XML format: " + e.getMessage());
            return null;
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Parse a single order from an XML file

    private List<Item> parseItems(NodeList itemNodes, String orderId) {
        List<Item> items = new ArrayList<>();

        for (int i = 0; i < itemNodes.getLength(); i++) {
            Node node = itemNodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element itemElement = (Element) node;

                // Extract item type from attribute
                String itemType = itemElement.getAttribute("type");
                if (itemType == null || itemType.isBlank()) {
                    System.err.println("Warning: Skipped item in order " + orderId + " - missing type attribute");
                    continue;
                }

                // Extract price
                String priceString = getElementText(itemElement, "Price");
                if (priceString == null || priceString.isBlank()) {
                    System.err.println("Warning: Skipped item '" + itemType + "' in order " + orderId + " - missing price");
                    continue;
                }

                // Extract quantity
                String quantityString = getElementText(itemElement, "Quantity");
                if (quantityString == null || quantityString.isBlank()) {
                    System.err.println("Warning: Skipped item '" + itemType + "' in order " + orderId + " - missing quantity");
                    continue;
                }

                // Parse price and quantity with validation
                try {
                    double price = Double.parseDouble(priceString.trim());
                    int quantity = Integer.parseInt(quantityString.trim());

                    // Validate values are positive
                    if (price <= 0 || quantity <= 0) {
                        System.err.println("Warning: Skipped item '" + itemType + "' in order " + orderId + " - invalid price or quantity values");
                        continue;
                    }

                    // Create Item object
                    items.add(new Item(itemType, quantity, price));

                } catch (NumberFormatException e) {
                    System.err.println("Warning: Skipped item '" + itemType + "' in order " + orderId + " - invalid number format: " + e.getMessage());
                }
            }
        }

        return items;
    }

    // Get text content from an element by tag name

    private String getElementText(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent();
        }
        return null;
    }

    // Convert string to OrderType enum

    private OrderType convertToOrderType(String typeString) {
        return switch (typeString.toLowerCase()) {
            case "delivery" -> OrderType.DIRECT_DELIVERY;
            case "ship" -> OrderType.SHIP;
            case "pickup" -> OrderType.PICKUP;
            default -> null;
        };
    }

    // Parse multiple orders from one XML file

    public List<Order> parseMultipleOrdersFromFile(String filePath) {
        List<Order> orders = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(filePath));
            doc.getDocumentElement().normalize();

            NodeList orderNodes = doc.getElementsByTagName("Order");
            String sourceFile = new File(filePath).getName();

            for (int i = 0; i < orderNodes.getLength(); i++) {
                Element orderElement = (Element) orderNodes.item(i);

                // Extract order ID
                String orderId = orderElement.getAttribute("id");
                if (orderId == null || orderId.isBlank()) {
                    System.err.println("Warning: Skipped order - missing id attribute");
                    continue;
                }

                // Extract order type
                String orderTypeString = getElementText(orderElement, "OrderType");
                if (orderTypeString == null || orderTypeString.isBlank()) {
                    System.err.println("Warning: Skipped order " + orderId + " - missing OrderType");
                    continue;
                }

                OrderType orderType = convertToOrderType(orderTypeString);
                if (orderType == null) {
                    System.err.println("Warning: Skipped order " + orderId + " - unknown order type: " + orderTypeString);
                    continue;
                }

                // Parse items
                NodeList itemNodes = orderElement.getElementsByTagName("Item");
                List<Item> items = parseItems(itemNodes, orderId);

                if (items.isEmpty()) {
                    System.err.println("Warning: Skipped order " + orderId + " - no valid items");
                    continue;
                }

                // Create Order with source tracking
                long orderDate = System.currentTimeMillis();
                orders.add(new Order(orderId, orderType, orderDate, items, sourceFile, "unknown"));
            }

            System.out.println("Successfully imported " + orders.size() + " orders from " + sourceFile);

        } catch (Exception e) {
            System.err.println("Error parsing XML file: " + e.getMessage());
        }

        return orders;
    }
}