package org.example

import java.io.File

/**
 * Factory class for creating appropriate parsers based on file type.
 * Removes parsing logic from UI and other classes.
 */
object ParserFactory {

    /**
     * Get the appropriate parser for a file based on its extension
     */
    @JvmStatic
    fun getParserForFile(file: File): OrderParser? {
        return when (file.extension.lowercase()) {
            "xml" -> XMLInput()
            "json" -> OrderJsonParser()
            else -> null
        }
    }

    /**
     * Get the appropriate parser for a file path
     */
    fun getParserForFile(filePath: String): OrderParser? {
        return getParserForFile(File(filePath))
    }
}