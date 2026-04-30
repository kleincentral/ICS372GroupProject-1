package org.example

import org.example.model.Order
import org.example.storage.OrderRepository
import org.example.persistence.OrderPersistence
import java.io.File

/**
 * Handles batch importing of orders from a directory.
 * Automatically detects and imports all .xml and .json files.
 */
class DirectoryImporter(
    private val repository: OrderRepository,
    private val persistence: OrderPersistence
) {

    /**
     * Import all order files from a directory
     * @param directory Directory containing order files
     * @return Number of orders successfully imported
     */
    fun importFromDirectory(directory: File): ImportResult {
        if (!directory.isDirectory) {
            return ImportResult(0, 0, "Not a valid directory")
        }

        val files = directory.listFiles { file ->
            file.name.endsWith(".xml", ignoreCase = true) ||
                    file.name.endsWith(".json", ignoreCase = true)
        } ?: return ImportResult(0, 0, "Could not read directory")

        var successCount = 0
        var failCount = 0
        val errors = mutableListOf<String>()

        for (file in files) {
            try {
                val parser = ParserFactory.getParserForFile(file)

                if (parser != null) {
                    val order = parser.parseOrderFromFile(file.absolutePath)

                    if (order != null) {
                        repository.addOrder(order)
                        successCount++
                        println("✓ Imported: ${order.orderId} from ${file.name}")
                    } else {
                        failCount++
                        errors.add("Failed to parse: ${file.name}")
                    }
                } else {
                    failCount++
                    errors.add("Unsupported file type: ${file.name}")
                }
            } catch (e: Exception) {
                failCount++
                errors.add("Error importing ${file.name}: ${e.message}")
            }
        }

        // Save all imported orders
        if (successCount > 0) {
            persistence.saveOrders(repository.allOrders, "orders.txt")
        }

        return ImportResult(successCount, failCount, errors.joinToString("\n"))
    }

    /**
     * Result of a directory import operation
     */
    data class ImportResult(
        val successCount: Int,
        val failCount: Int,
        val errorMessages: String
    ) {
        val totalFiles: Int get() = successCount + failCount

        fun getSummaryMessage(): String {
            return buildString {
                append("Import Complete!\n\n")
                append("Successfully imported: $successCount orders\n")
                if (failCount > 0) {
                    append("Failed: $failCount files\n\n")
                    if (errorMessages.isNotEmpty()) {
                        append("Errors:\n$errorMessages")
                    }
                }
            }
        }
    }
}