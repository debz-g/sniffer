package dev.sniffer.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persisted representation of a custom log entry from [Sniffer.log].
 */
@Entity(tableName = "logs")
data class LogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val message: String,
    val tag: String?,
    val timestamp: Long,
    val level: String
)
