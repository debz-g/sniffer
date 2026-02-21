package dev.sniffer.network

import kotlin.text.RegexOption
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.io.IOException

/**
 * OkHttp Interceptor that:
 * 1. Checks for an enabled Mock matching the request URL; if found, short-circuits and returns
 *    a fake Response without calling chain.proceed().
 * 2. Otherwise proceeds with the real request and records request/response (headers, body, status, time)
 *    into the repository and emits to Flow for UI.
 */
class SnifferInterceptor(
    private val repositoryProvider: () -> dev.sniffer.data.repository.SnifferRepository?
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val repository = repositoryProvider() ?: return chain.proceed(request)

        val urlString = request.url.toString()
        val path = request.url.encodedPath

        val mock = runBlocking {
            repository.getEnabledMocks().firstOrNull { mock ->
                urlMatches(urlString, path, mock.urlPattern)
            }
        }

        if (mock != null) {
            val mockResponse = buildMockResponse(request, mock.statusCode, mock.responseBody)
            recordMockedCall(repository, request, mock.statusCode, mock.responseBody)
            return mockResponse
        }

        val startNs = System.nanoTime()
        var response: Response
        try {
            response = chain.proceed(request)
        } catch (e: IOException) {
            recordFailure(repository, request, startNs, e)
            throw e
        }

        val durationMs = (System.nanoTime() - startNs) / 1_000_000
        return recordSuccessAndRebuildResponse(repository, request, response, durationMs)
    }

    /** Build header string by iterating so all headers (e.g. Authorization) are captured and not redacted. */
    private fun headersToString(headers: okhttp3.Headers): String {
        return (0 until headers.size).joinToString("\n") { i ->
            "${headers.name(i)}: ${headers.value(i)}"
        }
    }

    private fun urlMatches(url: String, path: String, pattern: String): Boolean {
        if (pattern.isBlank()) return false
        return url.contains(pattern, ignoreCase = true) ||
            path.contains(pattern, ignoreCase = true) ||
            pattern.toRegex(RegexOption.IGNORE_CASE).containsMatchIn(url)
    }

    private fun buildMockResponse(request: Request, statusCode: Int, bodyJson: String): Response {
        val body = bodyJson.toResponseBody("application/json; charset=utf-8".toMediaType())
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(statusCode)
            .message(statusMessage(statusCode))
            .body(body)
            .build()
    }

    private fun statusMessage(code: Int): String = when (code) {
        200 -> "OK"
        201 -> "Created"
        204 -> "No Content"
        400 -> "Bad Request"
        401 -> "Unauthorized"
        403 -> "Forbidden"
        404 -> "Not Found"
        500 -> "Internal Server Error"
        else -> "Code $code"
    }

    private fun recordSuccessAndRebuildResponse(
        repository: dev.sniffer.data.repository.SnifferRepository,
        request: Request,
        response: Response,
        durationMs: Long
    ): Response {
        val requestBody = request.body
        val reqBodyString = requestBody?.let { body ->
            Buffer().apply { body.writeTo(this) }.readUtf8()
        }
        val responseBody = response.body
        val contentType = responseBody?.contentType()?.toString() ?: "application/json; charset=utf-8"
        val resBodyString = responseBody?.string() ?: ""
        val responseHeadersString = headersToString(response.headers)

        runBlocking {
            repository.insertNetworkCall(
                requestUrl = request.url.toString(),
                requestMethod = request.method,
                requestHeaders = headersToString(request.headers),
                requestBody = reqBodyString?.take(MAX_BODY_LOG),
                responseCode = response.code,
                responseMessage = response.message,
                responseHeaders = responseHeadersString,
                responseBody = resBodyString.take(MAX_BODY_LOG),
                timestamp = System.currentTimeMillis(),
                durationMs = durationMs,
                wasMocked = false
            )
        }

        return response.newBuilder()
            .body(resBodyString.toResponseBody(contentType.toMediaType()))
            .build()
    }

    private fun recordMockedCall(
        repository: dev.sniffer.data.repository.SnifferRepository,
        request: Request,
        statusCode: Int,
        responseBody: String
    ) {
        runBlocking {
            repository.insertNetworkCall(
                requestUrl = request.url.toString(),
                requestMethod = request.method,
                requestHeaders = headersToString(request.headers),
                requestBody = null,
                responseCode = statusCode,
                responseMessage = statusMessage(statusCode),
                responseHeaders = "",
                responseBody = responseBody.take(MAX_BODY_LOG),
                timestamp = System.currentTimeMillis(),
                durationMs = 0L,
                wasMocked = true
            )
        }
    }

    private fun recordFailure(
        repository: dev.sniffer.data.repository.SnifferRepository,
        request: Request,
        startNs: Long,
        e: IOException
    ) {
        val durationMs = (System.nanoTime() - startNs) / 1_000_000
        runBlocking {
            repository.insertNetworkCall(
                requestUrl = request.url.toString(),
                requestMethod = request.method,
                requestHeaders = headersToString(request.headers),
                requestBody = null,
                responseCode = 0,
                responseMessage = "Error: ${e.message}",
                responseHeaders = "",
                responseBody = null,
                timestamp = System.currentTimeMillis(),
                durationMs = durationMs,
                wasMocked = false
            )
        }
    }

    companion object {
        private const val MAX_BODY_LOG = 64 * 1024
    }
}
