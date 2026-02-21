package dev.sniffer.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * User-defined mock: when a request matches [urlPattern], return [responseBody] with [statusCode]
 * instead of executing the real network call.
 */
@Entity(
    tableName = "mocks",
    indices = [Index(value = ["urlPattern"], unique = true)]
)
data class MockEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val urlPattern: String,
    val responseBody: String,
    val statusCode: Int,
    val enabled: Boolean,
    val createdAt: Long
)
