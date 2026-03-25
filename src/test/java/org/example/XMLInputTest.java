package org.example;

import org.example.model.Order;
import org.example.model.OrderType;
import org.example.model.Item;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for XMLInput class
 * Tests valid XML parsing, error handling for buggy XML, and edge cases
 */
public class XMLInputTest {

    private XMLInput xmlParser;

    @BeforeEach
    public void setUp() {
        xmlParser = new XMLInput();
    }

    @Test
    public void testParseValidXML(@TempDir Path tempDir) throws IOException {
        // Create valid XML file
        File xmlFile = tempDir.resolve("valid_order.xml").toFile();
        String validXML = """
                <Orders>
                    <Order id="123">
                        <OrderType>Delivery</OrderType>
                        <Item type="Widget">
                            <Price>15.99</Price>
                            <Quantity>2</Quantity>
                        </Item>
                        <Item type="Gadget">
                            <Price>9.50</Price>
                            <Quantity>1</Quantity>
                        </Item>
                    </Order>
                </Orders>
                """;

        try (FileWriter writer = new FileWriter(xmlFile)) {
            writer.write(validXML);
        }

        // Parse the file
        Order order = xmlParser.parseOrderFromFile(xmlFile.getAbsolutePath());

        // Verify order was created
        assertNotNull(order, "Order should not be null");
        assertEquals("123", order.getOrderId());
        assertEquals(OrderType.DIRECT_DELIVERY, order.getType());
        assertEquals("valid_order.xml", order.getSource());

        // Verify items
        List<Item> items = order.getItems();
        assertEquals(2, items.size(), "Should have 2 items");

        Item item1 = items.get(0);
        assertEquals("Widget", item1.getName());
        assertEquals(2, item1.getQuantity());
        assertEquals(15.99, item1.getPrice(), 0.01);

        Item item2 = items.get(1);
        assertEquals("Gadget", item2.getName());
        assertEquals(1, item2.getQuantity());
        assertEquals(9.50, item2.getPrice(), 0.01);
    }

    @Test
    public void testParseShipOrderType(@TempDir Path tempDir) throws IOException {
        File xmlFile = tempDir.resolve("ship_order.xml").toFile();
        String xml = """
                <Orders>
                    <Order id="456">
                        <OrderType>Ship</OrderType>
                        <Item type="Book">
                            <Price>12.99</Price>
                            <Quantity>1</Quantity>
                        </Item>
                    </Order>
                </Orders>
                """;

        try (FileWriter writer = new FileWriter(xmlFile)) {
            writer.write(xml);
        }

        Order order = xmlParser.parseOrderFromFile(xmlFile.getAbsolutePath());

        assertNotNull(order);
        assertEquals(OrderType.SHIP, order.getType());
    }

    @Test
    public void testParsePickupOrderType(@TempDir Path tempDir) throws IOException {
        File xmlFile = tempDir.resolve("pickup_order.xml").toFile();
        String xml = """
                <Orders>
                    <Order id="789">
                        <OrderType>Pickup</OrderType>
                        <Item type="Pen">
                            <Price>1.50</Price>
                            <Quantity>5</Quantity>
                        </Item>
                    </Order>
                </Orders>
                """;

        try (FileWriter writer = new FileWriter(xmlFile)) {
            writer.write(xml);
        }

        Order order = xmlParser.parseOrderFromFile(xmlFile.getAbsolutePath());

        assertNotNull(order);
        assertEquals(OrderType.PICKUP, order.getType());
    }

    @Test
    public void testMissingOrderId(@TempDir Path tempDir) throws IOException {
        File xmlFile = tempDir.resolve("no_id.xml").toFile();
        String xml = """
                <Orders>
                    <Order>
                        <OrderType>Delivery</OrderType>
                        <Item type="Test">
                            <Price>10.00</Price>
                            <Quantity>1</Quantity>
                        </Item>
                    </Order>
                </Orders>
                """;

        try (FileWriter writer = new FileWriter(xmlFile)) {
            writer.write(xml);
        }

        Order order = xmlParser.parseOrderFromFile(xmlFile.getAbsolutePath());

        assertNull(order, "Order should be null when ID is missing");
    }

    @Test
    public void testMissingOrderType(@TempDir Path tempDir) throws IOException {
        File xmlFile = tempDir.resolve("no_type.xml").toFile();
        String xml = """
                <Orders>
                    <Order id="999">
                        <Item type="Test">
                            <Price>10.00</Price>
                            <Quantity>1</Quantity>
                        </Item>
                    </Order>
                </Orders>
                """;

        try (FileWriter writer = new FileWriter(xmlFile)) {
            writer.write(xml);
        }

        Order order = xmlParser.parseOrderFromFile(xmlFile.getAbsolutePath());

        assertNull(order, "Order should be null when OrderType is missing");
    }

    @Test
    public void testInvalidOrderType(@TempDir Path tempDir) throws IOException {
        File xmlFile = tempDir.resolve("invalid_type.xml").toFile();
        String xml = """
                <Orders>
                    <Order id="888">
                        <OrderType>InvalidType</OrderType>
                        <Item type="Test">
                            <Price>10.00</Price>
                            <Quantity>1</Quantity>
                        </Item>
                    </Order>
                </Orders>
                """;

        try (FileWriter writer = new FileWriter(xmlFile)) {
            writer.write(xml);
        }

        Order order = xmlParser.parseOrderFromFile(xmlFile.getAbsolutePath());

        assertNull(order, "Order should be null when OrderType is invalid");
    }

    @Test
    public void testSkipItemWithMissingType(@TempDir Path tempDir) throws IOException {
        File xmlFile = tempDir.resolve("missing_item_type.xml").toFile();
        String xml = """
                <Orders>
                    <Order id="111">
                        <OrderType>Delivery</OrderType>
                        <Item>
                            <Price>10.00</Price>
                            <Quantity>1</Quantity>
                        </Item>
                        <Item type="ValidItem">
                            <Price>5.00</Price>
                            <Quantity>2</Quantity>
                        </Item>
                    </Order>
                </Orders>
                """;

        try (FileWriter writer = new FileWriter(xmlFile)) {
            writer.write(xml);
        }

        Order order = xmlParser.parseOrderFromFile(xmlFile.getAbsolutePath());

        assertNotNull(order);
        assertEquals(1, order.getItems().size(), "Should skip item with missing type");
        assertEquals("ValidItem", order.getItems().get(0).getName());
    }

    @Test
    public void testSkipItemWithMissingPrice(@TempDir Path tempDir) throws IOException {
        File xmlFile = tempDir.resolve("missing_price.xml").toFile();
        String xml = """
                <Orders>
                    <Order id="222">
                        <OrderType>Ship</OrderType>
                        <Item type="NoPrice">
                            <Quantity>3</Quantity>
                        </Item>
                        <Item type="WithPrice">
                            <Price>7.99</Price>
                            <Quantity>1</Quantity>
                        </Item>
                    </Order>
                </Orders>
                """;

        try (FileWriter writer = new FileWriter(xmlFile)) {
            writer.write(xml);
        }

        Order order = xmlParser.parseOrderFromFile(xmlFile.getAbsolutePath());

        assertNotNull(order);
        assertEquals(1, order.getItems().size(), "Should skip item with missing price");
        assertEquals("WithPrice", order.getItems().get(0).getName());
    }

    @Test
    public void testSkipItemWithMissingQuantity(@TempDir Path tempDir) throws IOException {
        File xmlFile = tempDir.resolve("missing_quantity.xml").toFile();
        String xml = """
                <Orders>
                    <Order id="333">
                        <OrderType>Pickup</OrderType>
                        <Item type="NoQuantity">
                            <Price>8.50</Price>
                        </Item>
                        <Item type="WithQuantity">
                            <Price>4.25</Price>
                            <Quantity>2</Quantity>
                        </Item>
                    </Order>
                </Orders>
                """;

        try (FileWriter writer = new FileWriter(xmlFile)) {
            writer.write(xml);
        }

        Order order = xmlParser.parseOrderFromFile(xmlFile.getAbsolutePath());

        assertNotNull(order);
        assertEquals(1, order.getItems().size(), "Should skip item with missing quantity");
        assertEquals("WithQuantity", order.getItems().get(0).getName());
    }

    @Test
    public void testSkipItemWithInvalidPrice(@TempDir Path tempDir) throws IOException {
        File xmlFile = tempDir.resolve("invalid_price.xml").toFile();
        String xml = """
                <Orders>
                    <Order id="444">
                        <OrderType>Delivery</OrderType>
                        <Item type="BadPrice">
                            <Price>NOT_A_NUMBER</Price>
                            <Quantity>1</Quantity>
                        </Item>
                        <Item type="GoodPrice">
                            <Price>12.50</Price>
                            <Quantity>1</Quantity>
                        </Item>
                    </Order>
                </Orders>
                """;

        try (FileWriter writer = new FileWriter(xmlFile)) {
            writer.write(xml);
        }

        Order order = xmlParser.parseOrderFromFile(xmlFile.getAbsolutePath());

        assertNotNull(order);
        assertEquals(1, order.getItems().size(), "Should skip item with invalid price");
        assertEquals("GoodPrice", order.getItems().get(0).getName());
    }

    @Test
    public void testSkipItemWithInvalidQuantity(@TempDir Path tempDir) throws IOException {
        File xmlFile = tempDir.resolve("invalid_quantity.xml").toFile();
        String xml = """
                <Orders>
                    <Order id="555">
                        <OrderType>Ship</OrderType>
                        <Item type="BadQuantity">
                            <Price>10.00</Price>
                            <Quantity>ABC</Quantity>
                        </Item>
                        <Item type="GoodQuantity">
                            <Price>15.00</Price>
                            <Quantity>3</Quantity>
                        </Item>
                    </Order>
                </Orders>
                """;

        try (FileWriter writer = new FileWriter(xmlFile)) {
            writer.write(xml);
        }

        Order order = xmlParser.parseOrderFromFile(xmlFile.getAbsolutePath());

        assertNotNull(order);
        assertEquals(1, order.getItems().size(), "Should skip item with invalid quantity");
        assertEquals("GoodQuantity", order.getItems().get(0).getName());
    }

    @Test
    public void testOrderWithNoValidItems(@TempDir Path tempDir) throws IOException {
        File xmlFile = tempDir.resolve("no_valid_items.xml").toFile();
        String xml = """
                <Orders>
                    <Order id="666">
                        <OrderType>Delivery</OrderType>
                        <Item type="Bad1">
                            <Price>INVALID</Price>
                            <Quantity>1</Quantity>
                        </Item>
                        <Item type="Bad2">
                            <Price>10.00</Price>
                            <Quantity>INVALID</Quantity>
                        </Item>
                    </Order>
                </Orders>
                """;

        try (FileWriter writer = new FileWriter(xmlFile)) {
            writer.write(xml);
        }

        Order order = xmlParser.parseOrderFromFile(xmlFile.getAbsolutePath());

        assertNull(order, "Order should be null when all items are invalid");
    }

    @Test
    public void testMalformedXML(@TempDir Path tempDir) throws IOException {
        File xmlFile = tempDir.resolve("malformed.xml").toFile();
        String xml = """
                <Orders>
                    <Order id="777">
                        <OrderType>Delivery
                        <Item type="Test">
                            <Price>10.00</Price>
                        </Item>
                    </Order>
                """;

        try (FileWriter writer = new FileWriter(xmlFile)) {
            writer.write(xml);
        }

        Order order = xmlParser.parseOrderFromFile(xmlFile.getAbsolutePath());

        assertNull(order, "Order should be null for malformed XML");
    }

    @Test
    public void testFileNotFound() {
        Order order = xmlParser.parseOrderFromFile("nonexistent_file.xml");

        assertNull(order, "Order should be null when file doesn't exist");
    }

    @Test
    public void testParseMultipleOrders(@TempDir Path tempDir) throws IOException {
        File xmlFile = tempDir.resolve("multiple_orders.xml").toFile();
        String xml = """
                <Orders>
                    <Order id="100">
                        <OrderType>Ship</OrderType>
                        <Item type="Item1">
                            <Price>5.00</Price>
                            <Quantity>1</Quantity>
                        </Item>
                    </Order>
                    <Order id="200">
                        <OrderType>Pickup</OrderType>
                        <Item type="Item2">
                            <Price>10.00</Price>
                            <Quantity>2</Quantity>
                        </Item>
                    </Order>
                    <Order id="300">
                        <OrderType>Delivery</OrderType>
                        <Item type="Item3">
                            <Price>15.00</Price>
                            <Quantity>3</Quantity>
                        </Item>
                    </Order>
                </Orders>
                """;

        try (FileWriter writer = new FileWriter(xmlFile)) {
            writer.write(xml);
        }

        List<Order> orders = xmlParser.parseMultipleOrdersFromFile(xmlFile.getAbsolutePath());

        assertEquals(3, orders.size(), "Should parse all 3 orders");
        assertEquals("100", orders.get(0).getOrderId());
        assertEquals("200", orders.get(1).getOrderId());
        assertEquals("300", orders.get(2).getOrderId());
    }

    @Test
    public void testSourceFileTracking(@TempDir Path tempDir) throws IOException {
        File xmlFile = tempDir.resolve("source_test.xml").toFile();
        String xml = """
                <Orders>
                    <Order id="999">
                        <OrderType>Ship</OrderType>
                        <Item type="Test">
                            <Price>1.00</Price>
                            <Quantity>1</Quantity>
                        </Item>
                    </Order>
                </Orders>
                """;

        try (FileWriter writer = new FileWriter(xmlFile)) {
            writer.write(xml);
        }

        Order order = xmlParser.parseOrderFromFile(xmlFile.getAbsolutePath());

        assertNotNull(order);
        assertEquals("source_test.xml", order.getSource(), "Source filename should be recorded");
    }
}
