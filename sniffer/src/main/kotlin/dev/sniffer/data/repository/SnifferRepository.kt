package dev.sniffer.data.repository

import dev.sniffer.data.dao.LogDao
import dev.sniffer.data.dao.MockDao
import dev.sniffer.data.dao.NetworkCallDao
import dev.sniffer.data.entity.LogEntity
import dev.sniffer.data.entity.MockEntity
import dev.sniffer.data.entity.NetworkCallEntity
import dev.sniffer.data.model.LogEntry
import dev.sniffer.data.model.NetworkCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.CoroutineScope

/**
 * Singleton repository: single source of truth for network calls, logs, and mocks.
 * UI observes StateFlows from this repository.
 */
class SnifferRepository(
    private val networkCallDao: NetworkCallDao,
    private val logDao: LogDao,
    private val mockDao: MockDao,
    private val scope: CoroutineScope
) {
    private val _recentNetworkCallsLimit = 500
    private val _recentLogsLimit = 500

    val networkCalls: StateFlow<List<NetworkCall>> = networkCallDao
        .observeRecent(_recentNetworkCallsLimit)
        .map { list -> list.map(NetworkCall::fromEntity) }
        .stateIn(scope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())

    val logs: StateFlow<List<LogEntry>> = logDao
        .observeRecent(_recentLogsLimit)
        .map { list -> list.map(LogEntry::fromEntity) }
        .stateIn(scope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())

    val enabledMocks: Flow<List<MockEntity>> = mockDao.observeEnabledMocks()

    private val _newNetworkCall = MutableSharedFlow<NetworkCall>(extraBufferCapacity = 1)
    val newNetworkCall: SharedFlow<NetworkCall> = _newNetworkCall

    private val _newLog = MutableSharedFlow<LogEntry>(extraBufferCapacity = 1)
    val newLog: SharedFlow<LogEntry> = _newLog

    suspend fun insertNetworkCall(
        requestUrl: String,
        requestMethod: String,
        requestHeaders: String,
        requestBody: String?,
        responseCode: Int,
        responseMessage: String,
        responseHeaders: String,
        responseBody: String?,
        timestamp: Long,
        durationMs: Long,
        wasMocked: Boolean
    ): Long {
        val entity = NetworkCallEntity(
            requestUrl = requestUrl,
            requestMethod = requestMethod,
            requestHeaders = requestHeaders,
            requestBody = requestBody,
            responseCode = responseCode,
            responseMessage = responseMessage,
            responseHeaders = responseHeaders,
            responseBody = responseBody,
            timestamp = timestamp,
            durationMs = durationMs,
            wasMocked = wasMocked
        )
        val id = networkCallDao.insert(entity)
        _newNetworkCall.tryEmit(NetworkCall.fromEntity(entity.copy(id = id)))
        return id
    }

    suspend fun insertLog(message: String, tag: String?, level: String) {
        val entity = LogEntity(
            message = message,
            tag = tag,
            timestamp = System.currentTimeMillis(),
            level = level
        )
        val id = logDao.insert(entity)
        _newLog.tryEmit(LogEntry.fromEntity(entity.copy(id = id)))
    }

    suspend fun getEnabledMocks(): List<MockEntity> = mockDao.getEnabledMocks()

    suspend fun addMock(urlPattern: String, responseBody: String, statusCode: Int) {
        mockDao.insert(
            MockEntity(
                urlPattern = urlPattern,
                responseBody = responseBody,
                statusCode = statusCode,
                enabled = true,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun setMockEnabled(id: Long, enabled: Boolean) {
        mockDao.setEnabled(id, enabled)
    }

    suspend fun clearNetworkCalls() = networkCallDao.clearAll()
    suspend fun clearLogs() = logDao.clearAll()
}
