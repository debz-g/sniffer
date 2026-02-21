package dev.sniffer.data.model

import dev.sniffer.data.entity.NetworkCallEntity

/**
 * UI-friendly model for a captured network call.
 */
data class NetworkCall(
    val id: Long,
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
) {
    companion object {
        fun fromEntity(entity: NetworkCallEntity) = NetworkCall(
            id = entity.id,
            requestUrl = entity.requestUrl,
            requestMethod = entity.requestMethod,
            requestHeaders = entity.requestHeaders,
            requestBody = entity.requestBody,
            responseCode = entity.responseCode,
            responseMessage = entity.responseMessage,
            responseHeaders = entity.responseHeaders,
            responseBody = entity.responseBody,
            timestamp = entity.timestamp,
            durationMs = entity.durationMs,
            wasMocked = entity.wasMocked
        )
    }
}
