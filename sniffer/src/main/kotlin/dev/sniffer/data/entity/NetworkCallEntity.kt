package dev.sniffer.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persisted representation of an HTTP request/response captured by [SnifferInterceptor].
 */
@Entity(tableName = "network_calls")
data class NetworkCallEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val requestUrl: String,
    val requestMethod: String,
    val requestHeaders: String,
    val requestBody: String?,
    val responseCode: Int,
    val responseMessage: String,
    val responseHeaders: String,
    val responseBody: String?,
    val timestamp: Long,
    val durationMs: Long,
    val wasMocked: Boolean
)
