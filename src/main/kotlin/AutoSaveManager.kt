package org.example

import org.example.model.Order
import org.example.persistence.OrderPersistence

class AutoSaveManager {

    private val persistence = OrderPersistence()
    private val filePath = "orders.txt"

    fun autoSave(orders: List<Order>) {
        println("Auto-saving orders...")
        persistence.saveOrders(orders, filePath)
    }

    fun loadOnStartup(): List<Order> {
        println("Loading saved orders...")
        return persistence.loadOrders(filePath)
    }
}