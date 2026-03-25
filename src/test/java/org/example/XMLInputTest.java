package org.example;

import org.example.model.Item;
import org.example.model.Order;
import org.example.model.OrderType;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class XMLInputTest {

    @Test
    public void testParseValidXML() {
        XMLInput xmlParser = new XMLInput();
        File xmlFile = new File("test_order.xml");

        Order order = xmlParser.parseOrderFromFile(xmlFile.getAbsolutePath());

        assertNotNull(order, "Order should not be null");
        assertEquals("485", order.getOrderId());
        assertEquals(OrderType.DIRECT_DELIVERY, order.getType());

        List<Item> items = order.getItems();
        assertEquals(3, items.size(), "Should have 3 items");

        Item item1 = items.get(0);
        assertEquals("Rubber duck", item1.getName());
        assertEquals(2, item1.getQuantity());
        assertEquals(13.45, item1.getPrice(), 0.001);

        Item item2 = items.get(1);
        assertEquals("Soap", item2.getName());
        assertEquals(2, item2.getQuantity());
        assertEquals(5.25, item2.getPrice(), 0.001);

        Item item3 = items.get(2);
        assertEquals("Paper Towel Roll", item3.getName());
        assertEquals(1, item3.getQuantity());
        assertEquals(8.99, item3.getPrice(), 0.001);
    }

    @Test
    public void testParseBuggyXML() {
        XMLInput xmlParser = new XMLInput();
        File xmlFile = new File("test_order_buggy.xml");

        Order order = xmlParser.parseOrderFromFile(xmlFile.getAbsolutePath());

        assertNotNull(order, "Buggy XML should still return an order");
        assertEquals("486", order.getOrderId());

        List<Item> items = order.getItems();
        assertEquals(2, items.size(), "Only valid items should be kept");

        Item item1 = items.get(0);
        assertEquals("Widget", item1.getName());
        assertEquals(3, item1.getQuantity());
        assertEquals(15.99, item1.getPrice(), 0.001);
    }
}