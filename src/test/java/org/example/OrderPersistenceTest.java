package org.example;

import org.example.model.Item;
import org.example.model.Order;
import org.example.model.OrderStatus;
import org.example.model.OrderType;
import org.example.persistence.OrderPersistence;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderPersistenceTest {

    @Test
    public void testSaveAndLoadOrders() {

        OrderPersistence persistence = new OrderPersistence();
        String filePath = "test_orders.txt";

        // create test order
        List<Item> items = new ArrayList<>();
        items.add(new Item("TestItem", 2, 10.0));

        Order order = new Order("123", OrderType.SHIP, System.currentTimeMillis(), items);
        order.setStatus(OrderStatus.COMPLETED);
        order.setSource("testSource");
        order.setWarehouse("testWarehouse");
        order.setCancelReason("none");

        List<Order> orders = new ArrayList<>();
        orders.add(order);

        // save orders
        persistence.saveOrders(orders, filePath);

        // load orders
        List<Order> loaded = persistence.loadOrders(filePath);

        // checks
        assertEquals(1, loaded.size());
        assertEquals("123", loaded.get(0).getOrderId());
        assertEquals(OrderStatus.COMPLETED, loaded.get(0).getStatus());
        assertEquals("testSource", loaded.get(0).getSource());
        assertEquals("testWarehouse", loaded.get(0).getWarehouse());
    }
}