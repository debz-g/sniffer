package dev.sniffer.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import dev.sniffer.data.model.NetworkCall
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkCallDetailScreen(
    call: NetworkCall,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request details", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", style = MaterialTheme.typography.titleLarge)
                    }
                },
                actions = {
                    IconButton(onClick = { CurlSharer.shareCurl(context, call) }) {
                        Text("Share", style = MaterialTheme.typography.labelLarge)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Overview") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Request") })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Response") })
            }
            when (selectedTab) {
                0 -> OverviewTab(call = call)
                1 -> RequestTab(call = call)
                2 -> ResponseTab(call = call)
            }
        }
    }
}

@Composable
private fun OverviewTab(call: NetworkCall) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DetailRow("Method", call.requestMethod)
        UrlRowWithCopy(url = call.requestUrl, context = context)
        DetailRow("Status", "${call.responseCode} ${call.responseMessage}")
        DetailRow("Duration", "${call.durationMs} ms")
        if (call.wasMocked) {
            DetailRow("Mocked", "Yes")
        }
        DetailRow("Timestamp", SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date(call.timestamp)))
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun UrlRowWithCopy(url: String, context: Context) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "URL",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = url,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                    clipboard?.setPrimaryClip(ClipData.newPlainText("url", url))
                },
                modifier = Modifier.size(40.dp)
            ) {
                Text("⎘", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun RequestTab(call: NetworkCall) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Headers", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(
            text = call.requestHeaders.ifBlank { "(none)" },
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text("Body", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(
            text = call.requestBody?.ifBlank { "(empty)" } ?: "(none)",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ResponseTab(call: NetworkCall) {
    val context = LocalContext.current
    val body = call.responseBody ?: "(empty)"
    val beautified = remember(body) { beautifyJson(body) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Headers", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(
            text = call.responseHeaders.ifBlank { "(none)" },
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Body", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            IconButton(
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, beautified)
                        putExtra(Intent.EXTRA_TITLE, "Response body")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    val chooser = Intent.createChooser(shareIntent, "Share response")
                    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(chooser)
                },
                modifier = Modifier.size(40.dp)
            ) {
                Text("Share", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            }
        }
        Text(
            text = beautified,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

private fun beautifyJson(input: String): String {
    if (input.isBlank()) return input
    val trimmed = input.trim()
    if (!trimmed.startsWith("{") && !trimmed.startsWith("[")) return input
    return try {
        val sb = StringBuilder()
        var indent = 0
        var i = 0
        val len = trimmed.length
        var inString = false
        var escape = false
        val quoteChar = '"'
        while (i < len) {
            val c = trimmed[i]
            when {
                escape -> { sb.append(c); escape = false }
                inString -> {
                    sb.append(c)
                    if (c == quoteChar) inString = false
                    else if (c == '\\') escape = true
                }
                c == quoteChar -> { sb.append(c); inString = true }
                c == '{' || c == '[' -> {
                    sb.append(c)
                    indent++
                    if (i + 1 < len && trimmed[i + 1] != '}' && trimmed[i + 1] != ']') sb.append('\n').append("  ".repeat(indent))
                }
                c == '}' || c == ']' -> {
                    indent--
                    sb.append('\n').append("  ".repeat(indent)).append(c)
                }
                c == ',' -> {
                    sb.append(c)
                    if (i + 1 < len) sb.append('\n').append("  ".repeat(indent))
                }
                c == ':' -> sb.append(": ")
                !c.isWhitespace() -> sb.append(c)
            }
            i++
        }
        sb.toString()
    } catch (_: Exception) {
        input
    }
}
