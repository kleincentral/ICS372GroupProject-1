package org.example.model;

public class Item {
    private final String name;
    private final int quantity;
    private final double price;

    public Item(String name, int quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }


    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double lineTotal() {
        return quantity * price;
    }

    @Override
    public String toString() {
        return name + " x" + quantity + " @ $" + price;
    }
}
