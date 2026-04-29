package org.example;

import org.example.model.Order;
import org.example.model.OrderStatus;
import org.example.model.OrderType;
import org.example.storage.OrderRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class OrderRepositoryTest {

    @Test
    public void testAddAndFindOrder() {
        OrderRepository repo = new OrderRepository();

        Order order = new Order("123", OrderType.SHIP, System.currentTimeMillis(), new ArrayList<>());
        repo.addOrder(order);

        Order found = repo.findById("123");

        assertNotNull(found);
        assertEquals("123", found.getOrderId());
    }

    @Test
    public void testFindOrderIgnoreCase() {
        OrderRepository repo = new OrderRepository();

        Order order = new Order("ABC", OrderType.SHIP, System.currentTimeMillis(), new ArrayList<>());
        repo.addOrder(order);

        Order found = repo.findById("abc");

        assertNotNull(found);
    }

    @Test
    public void testUncompletedOrders() {
        OrderRepository repo = new OrderRepository();

        Order o1 = new Order("1", OrderType.SHIP, System.currentTimeMillis(), new ArrayList<>());
        Order o2 = new Order("2", OrderType.SHIP, System.currentTimeMillis(), new ArrayList<>());

        o2.setStatus(OrderStatus.COMPLETED);

        repo.addOrder(o1);
        repo.addOrder(o2);

        assertEquals(1, repo.getUncompletedOrders().size());
    }

    @Test
    public void testAddNullOrder() {
        OrderRepository repo = new OrderRepository();

        repo.addOrder(null);

        assertEquals(0, repo.getAllOrders().size());
    }


    @Test
    public void testSaveAndLoad() {
        OrderRepository repo = new OrderRepository();

        Order order = new Order("999", OrderType.SHIP, System.currentTimeMillis(), new ArrayList<>());
        repo.addOrder(order);

        // Save to file
        repo.saveToFile();

        // Load into a new repository
        OrderRepository newRepo = new OrderRepository();
        newRepo.loadFromFile();

        Order loaded = newRepo.findById("999");

        assertNotNull(loaded);
    }
}