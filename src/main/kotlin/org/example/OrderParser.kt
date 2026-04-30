package org.example

import org.example.model.Order

/**
 * Interface for order file parsers.
 * Defines a common contract for parsing orders from different file formats.
 */
interface OrderParser {
    /**
     * Parse a single order from a file
     */
    fun parseOrderFromFile(filePath: String): Order?
}