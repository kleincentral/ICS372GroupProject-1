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
 * Unit tests for OrderJsonParser class
 * Tests valid JSON parsing, error handling for invalid JSON, and edge cases
 */
public class OrderJsonParserTest {

    private OrderJsonParser jsonParser;

    @BeforeEach
    public void setUp() {
        jsonParser = new OrderJsonParser();
    }

    @Test
    public void testParseValidJSON(@TempDir Path tempDir) throws IOException {
        File jsonFile = tempDir.resolve("valid_order.json").toFile();
        String validJSON = """
                {
                  "order": {
                    "type": "ship",
                    "order_date": 1774329686030,
                    "items": [
                      {
                        "name": "Laptop",
                        "quantity": 1,
                        "price": 999.99
                      },
                      {
                        "name": "Mouse",
                        "quantity": 2,
                        "price": 25.50
                      }
                    ]
                  }
                }
                """;

        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(validJSON);
        }

        Order order = jsonParser.parseOrderFromFile(jsonFile.getAbsolutePath());

        assertNotNull(order, "Order should not be null");
        assertTrue(order.getOrderId().startsWith("ORDER-"), "Order ID should start with ORDER-");
        assertEquals(OrderType.SHIP, order.getType());
        assertEquals(1774329686030L, order.getOrderTimeMillis());
        assertEquals("valid_order.json", order.getSource());

        List<Item> items = order.getItems();
        assertEquals(2, items.size(), "Should have 2 items");

        Item item1 = items.get(0);
        assertEquals("Laptop", item1.getName());
        assertEquals(1, item1.getQuantity());
        assertEquals(999.99, item1.getPrice(), 0.01);

        Item item2 = items.get(1);
        assertEquals("Mouse", item2.getName());
        assertEquals(2, item2.getQuantity());
        assertEquals(25.50, item2.getPrice(), 0.01);
    }

    @Test
    public void testParsePickupOrderType(@TempDir Path tempDir) throws IOException {
        File jsonFile = tempDir.resolve("pickup_order.json").toFile();
        String json = """
                {
                  "order": {
                    "type": "pickup",
                    "order_date": 1774329686030,
                    "items": [
                      {
                        "name": "Book",
                        "quantity": 3,
                        "price": 12.99
                      }
                    ]
                  }
                }
                """;

        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(json);
        }

        Order order = jsonParser.parseOrderFromFile(jsonFile.getAbsolutePath());

        assertNotNull(order);
        assertEquals(OrderType.PICKUP, order.getType());
    }

    @Test
    public void testParseDeliveryOrderType(@TempDir Path tempDir) throws IOException {
        File jsonFile = tempDir.resolve("delivery_order.json").toFile();
        String json = """
                {
                  "order": {
                    "type": "delivery",
                    "order_date": 1774329686030,
                    "items": [
                      {
                        "name": "Pizza",
                        "quantity": 2,
                        "price": 15.99
                      }
                    ]
                  }
                }
                """;

        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(json);
        }

        Order order = jsonParser.parseOrderFromFile(jsonFile.getAbsolutePath());

        assertNotNull(order);
        assertEquals(OrderType.DIRECT_DELIVERY, order.getType());
    }

    @Test
    public void testMissingOrderField(@TempDir Path tempDir) throws IOException {
        File jsonFile = tempDir.resolve("no_order.json").toFile();
        String json = """
                {
                  "data": {
                    "type": "ship",
                    "items": []
                  }
                }
                """;

        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(json);
        }

        Order order = jsonParser.parseOrderFromFile(jsonFile.getAbsolutePath());

        assertNull(order, "Order should be null when 'order' field is missing");
    }

    @Test
    public void testMissingType(@TempDir Path tempDir) throws IOException {
        File jsonFile = tempDir.resolve("no_type.json").toFile();
        String json = """
                {
                  "order": {
                    "order_date": 1774329686030,
                    "items": [
                      {
                        "name": "Test",
                        "quantity": 1,
                        "price": 10.0
                      }
                    ]
                  }
                }
                """;

        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(json);
        }

        Order order = jsonParser.parseOrderFromFile(jsonFile.getAbsolutePath());

        assertNull(order, "Order should be null when type is missing");
    }

    @Test
    public void testMissingOrderDate(@TempDir Path tempDir) throws IOException {
        File jsonFile = tempDir.resolve("no_date.json").toFile();
        String json = """
                {
                  "order": {
                    "type": "ship",
                    "items": [
                      {
                        "name": "Test",
                        "quantity": 1,
                        "price": 10.0
                      }
                    ]
                  }
                }
                """;

        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(json);
        }

        Order order = jsonParser.parseOrderFromFile(jsonFile.getAbsolutePath());

        assertNull(order, "Order should be null when order_date is missing");
    }

    @Test
    public void testInvalidOrderType(@TempDir Path tempDir) throws IOException {
        File jsonFile = tempDir.resolve("invalid_type.json").toFile();
        String json = """
                {
                  "order": {
                    "type": "invalid_type",
                    "order_date": 1774329686030,
                    "items": [
                      {
                        "name": "Test",
                        "quantity": 1,
                        "price": 10.0
                      }
                    ]
                  }
                }
                """;

        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(json);
        }

        Order order = jsonParser.parseOrderFromFile(jsonFile.getAbsolutePath());

        assertNull(order, "Order should be null when order type is invalid");
    }

    @Test
    public void testEmptyItemsArray(@TempDir Path tempDir) throws IOException {
        File jsonFile = tempDir.resolve("empty_items.json").toFile();
        String json = """
                {
                  "order": {
                    "type": "ship",
                    "order_date": 1774329686030,
                    "items": []
                  }
                }
                """;

        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(json);
        }

        Order order = jsonParser.parseOrderFromFile(jsonFile.getAbsolutePath());

        assertNull(order, "Order should be null when items array is empty");
    }

    @Test
    public void testMissingItemsArray(@TempDir Path tempDir) throws IOException {
        File jsonFile = tempDir.resolve("no_items.json").toFile();
        String json = """
                {
                  "order": {
                    "type": "ship",
                    "order_date": 1774329686030
                  }
                }
                """;

        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(json);
        }

        Order order = jsonParser.parseOrderFromFile(jsonFile.getAbsolutePath());

        assertNull(order, "Order should be null when items array is missing");
    }

    @Test
    public void testSkipItemWithMissingName(@TempDir Path tempDir) throws IOException {
        File jsonFile = tempDir.resolve("missing_name.json").toFile();
        String json = """
                {
                  "order": {
                    "type": "ship",
                    "order_date": 1774329686030,
                    "items": [
                      {
                        "quantity": 1,
                        "price": 10.0
                      },
                      {
                        "name": "ValidItem",
                        "quantity": 2,
                        "price": 5.0
                      }
                    ]
                  }
                }
                """;

        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(json);
        }

        Order order = jsonParser.parseOrderFromFile(jsonFile.getAbsolutePath());

        assertNotNull(order);
        assertEquals(1, order.getItems().size(), "Should skip item with missing name");
        assertEquals("ValidItem", order.getItems().get(0).getName());
    }

    @Test
    public void testSkipItemWithMissingQuantity(@TempDir Path tempDir) throws IOException {
        File jsonFile = tempDir.resolve("missing_quantity.json").toFile();
        String json = """
                {
                  "order": {
                    "type": "ship",
                    "order_date": 1774329686030,
                    "items": [
                      {
                        "name": "NoQuantity",
                        "price": 10.0
                      },
                      {
                        "name": "WithQuantity",
                        "quantity": 2,
                        "price": 5.0
                      }
                    ]
                  }
                }
                """;

        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(json);
        }

        Order order = jsonParser.parseOrderFromFile(jsonFile.getAbsolutePath());

        assertNotNull(order);
        assertEquals(1, order.getItems().size(), "Should skip item with missing quantity");
        assertEquals("WithQuantity", order.getItems().get(0).getName());
    }

    @Test
    public void testSkipItemWithMissingPrice(@TempDir Path tempDir) throws IOException {
        File jsonFile = tempDir.resolve("missing_price.json").toFile();
        String json = """
                {
                  "order": {
                    "type": "ship",
                    "order_date": 1774329686030,
                    "items": [
                      {
                        "name": "NoPrice",
                        "quantity": 1
                      },
                      {
                        "name": "WithPrice",
                        "quantity": 2,
                        "price": 5.0
                      }
                    ]
                  }
                }
                """;

        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(json);
        }

        Order order = jsonParser.parseOrderFromFile(jsonFile.getAbsolutePath());

        assertNotNull(order);
        assertEquals(1, order.getItems().size(), "Should skip item with missing price");
        assertEquals("WithPrice", order.getItems().get(0).getName());
    }

    @Test
    public void testMalformedJSON(@TempDir Path tempDir) throws IOException {
        File jsonFile = tempDir.resolve("malformed.json").toFile();
        String json = """
                {
                  "order": {
                    "type": "ship"
                    "order_date": 1774329686030,
                    "items": []
                }
                """;

        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(json);
        }

        Order order = jsonParser.parseOrderFromFile(jsonFile.getAbsolutePath());

        assertNull(order, "Order should be null for malformed JSON");
    }

    @Test
    public void testFileNotFound() {
        Order order = jsonParser.parseOrderFromFile("nonexistent_file.json");

        assertNull(order, "Order should be null when file doesn't exist");
    }

    @Test
    public void testSourceFileTracking(@TempDir Path tempDir) throws IOException {
        File jsonFile = tempDir.resolve("source_test.json").toFile();
        String json = """
                {
                  "order": {
                    "type": "ship",
                    "order_date": 1774329686030,
                    "items": [
                      {
                        "name": "Test",
                        "quantity": 1,
                        "price": 1.0
                      }
                    ]
                  }
                }
                """;

        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(json);
        }

        Order order = jsonParser.parseOrderFromFile(jsonFile.getAbsolutePath());

        assertNotNull(order);
        assertEquals("source_test.json", order.getSource(), "Source filename should be recorded");
    }

    @Test
    public void testMultipleItems(@TempDir Path tempDir) throws IOException {
        File jsonFile = tempDir.resolve("multiple_items.json").toFile();
        String json = """
                {
                  "order": {
                    "type": "pickup",
                    "order_date": 1774329686030,
                    "items": [
                      {
                        "name": "Item1",
                        "quantity": 1,
                        "price": 10.0
                      },
                      {
                        "name": "Item2",
                        "quantity": 2,
                        "price": 20.0
                      },
                      {
                        "name": "Item3",
                        "quantity": 3,
                        "price": 30.0
                      }
                    ]
                  }
                }
                """;

        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(json);
        }

        Order order = jsonParser.parseOrderFromFile(jsonFile.getAbsolutePath());

        assertNotNull(order);
        assertEquals(3, order.getItems().size(), "Should parse all 3 items");
        assertEquals("Item1", order.getItems().get(0).getName());
        assertEquals("Item2", order.getItems().get(1).getName());
        assertEquals("Item3", order.getItems().get(2).getName());
    }
}
