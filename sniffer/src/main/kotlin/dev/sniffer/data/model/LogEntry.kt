package dev.sniffer.data.model

import dev.sniffer.data.entity.LogEntity

/**
 * UI-friendly model for a custom log entry.
 */
data class LogEntry(
    val id: Long,
    val message: String,
    val tag: String?,
    val timestamp: Long,
    val level: String
) {
    companion object {
        fun fromEntity(entity: LogEntity) = LogEntry(
            id = entity.id,
            message = entity.message,
            tag = entity.tag,
            timestamp = entity.timestamp,
            level = entity.level
        )
    }
}
