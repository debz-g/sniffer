package dev.sniffer.ui

import android.content.Context
import android.content.Intent
import dev.sniffer.data.model.NetworkCall

/**
 * Builds a cURL command string from a [NetworkCall] and shares it via system share sheet.
 */
object CurlSharer {

    fun buildCurl(call: NetworkCall): String {
        val sb = StringBuilder()
        sb.append("curl -X ").append(call.requestMethod).append(" ")
        sb.append("'").append(escapeSingleQuotes(call.requestUrl)).append("'")
        parseHeaders(call.requestHeaders).forEach { (name, value) ->
            if (name.equals("Content-Length", ignoreCase = true)) return@forEach
            sb.append(" \\\n  -H '").append(escapeSingleQuotes("$name: $value")).append("'")
        }
        if (!call.requestBody.isNullOrBlank()) {
            sb.append(" \\\n  --data-raw '").append(escapeSingleQuotes(call.requestBody)).append("'")
        }
        return sb.toString()
    }

    private fun escapeSingleQuotes(s: String): String = s.replace("'", "'\"'\"'")

    private fun parseHeaders(headersString: String): List<Pair<String, String>> {
        if (headersString.isBlank()) return emptyList()
        return headersString.lines()
            .mapNotNull { line ->
                val colon = line.indexOf(':')
                if (colon > 0) line.substring(0, colon).trim() to line.substring(colon + 1).trim()
                else null
            }
    }

    fun shareCurl(context: Context, call: NetworkCall) {
        val curl = buildCurl(call)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, curl)
            putExtra(Intent.EXTRA_TITLE, "cURL: ${call.requestMethod} ${call.requestUrl}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, "Share cURL")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
