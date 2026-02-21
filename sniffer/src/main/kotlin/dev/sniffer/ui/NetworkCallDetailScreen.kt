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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
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

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                Text("←", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
            }
            IconButton(onClick = { CurlSharer.shareCurl(context, call) }) {
                Text("Share", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
            }
        }
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

private fun findMatchIndices(text: String, query: String): List<Int> {
    if (query.isBlank()) return emptyList()
    val lower = text.lowercase()
    val q = query.lowercase()
    val list = mutableListOf<Int>()
    var i = 0
    while (i < lower.length) {
        val idx = lower.indexOf(q, i)
        if (idx < 0) break
        list.add(idx)
        i = idx + 1
    }
    return list
}

@Composable
private fun ResponseTab(call: NetworkCall) {
    val context = LocalContext.current
    val body = call.responseBody ?: "(empty)"
    val beautified = remember(body) { beautifyJson(body) }
    val scrollState = rememberScrollState()
    var searchQuery by remember { mutableStateOf("") }
    var searchVisible by remember { mutableStateOf(true) }
    val matches = remember(beautified, searchQuery) { findMatchIndices(beautified, searchQuery) }
    var currentMatchIndex by remember { mutableStateOf(0) }
    if (currentMatchIndex >= matches.size && matches.isNotEmpty()) currentMatchIndex = matches.size - 1
    if (currentMatchIndex < 0) currentMatchIndex = 0

    val matchStart = if (matches.isNotEmpty() && currentMatchIndex in matches.indices) matches[currentMatchIndex] else -1
    val matchEnd = if (matchStart >= 0 && searchQuery.isNotBlank()) matchStart + searchQuery.length else -1

    LaunchedEffect(currentMatchIndex, matches.size) {
        if (matchStart >= 0 && matches.isNotEmpty()) {
            val ratio = (matchStart.toFloat() / beautified.length.coerceAtLeast(1)).coerceIn(0f, 1f)
            val target = (ratio * scrollState.maxValue).toInt().coerceIn(0, scrollState.maxValue)
            scrollState.animateScrollTo(target)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Fixed top bar: search + arrows + share (always visible)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { searchVisible = !searchVisible; if (!searchVisible) searchQuery = "" },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(Icons.Filled.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
                }
                if (searchVisible) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it; currentMatchIndex = 0 },
                        placeholder = { Text("Search in response...", style = MaterialTheme.typography.bodyMedium) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        ),
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = ""; currentMatchIndex = 0 }) {
                                    Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    )
                    if (matches.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            IconButton(
                                onClick = { currentMatchIndex = (currentMatchIndex - 1 + matches.size) % matches.size },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Previous", tint = MaterialTheme.colorScheme.primary)
                            }
                            Text(
                                "${currentMatchIndex + 1}/${matches.size}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            IconButton(
                                onClick = { currentMatchIndex = (currentMatchIndex + 1) % matches.size },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Next", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
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
        }

        // Scrollable content below
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
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
            Text("Body", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Text(
                text = remember(beautified, matchStart, matchEnd) {
                    jsonToHighlightedAnnotatedStringWithSearch(beautified, matchStart, matchEnd)
                },
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

private val jsonKeyColor = Color(0xFF81D4FA)
private val jsonStringValueColor = Color(0xFFA5D6A7)
private val jsonNumberColor = Color(0xFFFFB74D)
private val jsonBooleanColor = Color(0xFFCE93D8)
private val jsonNullColor = Color(0xFFB0BEC5)
private val jsonPunctuationColor = Color(0xFF90A4AE)

private val jsonSearchHighlightColor = Color(0xFF64B5F6).copy(alpha = 0.35f)

private fun jsonToHighlightedAnnotatedStringWithSearch(input: String, matchStart: Int, matchEnd: Int): AnnotatedString {
    if (input.isBlank() || (!input.trimStart().startsWith("{") && !input.trimStart().startsWith("["))) {
        return AnnotatedString(input)
    }
    return buildAnnotatedString {
        var i = 0
        val len = input.length
        while (i < len) {
            val c = input[i]
            when {
                c == '"' -> {
                    val start = i
                    i++
                    while (i < len) {
                        if (input[i] == '\\') i += 2
                        else if (input[i] == '"') { i++; break }
                        else i++
                    }
                    val token = input.substring(start, i)
                    var j = i
                    while (j < len && input[j].isWhitespace()) j++
                    val isKey = j < len && input[j] == ':'
                    if (isKey) withStyle(SpanStyle(color = jsonKeyColor)) { append(token) }
                    else withStyle(SpanStyle(color = jsonStringValueColor)) { append(token) }
                }
                c.isDigit() || (c == '-' && i + 1 < len && input[i + 1].isDigit()) -> {
                    val start = i
                    if (c == '-') i++
                    while (i < len && (input[i].isDigit() || input[i] == '.' || input[i] == 'e' || input[i] == 'E' || input[i] == '+' || (i > start && input[i] == '-'))) i++
                    withStyle(SpanStyle(color = jsonNumberColor)) { append(input.substring(start, i)) }
                }
                c == 't' && input.startsWith("true", i) -> {
                    withStyle(SpanStyle(color = jsonBooleanColor)) { append("true") }
                    i += 4
                }
                c == 'f' && input.startsWith("false", i) -> {
                    withStyle(SpanStyle(color = jsonBooleanColor)) { append("false") }
                    i += 5
                }
                c == 'n' && input.startsWith("null", i) -> {
                    withStyle(SpanStyle(color = jsonNullColor)) { append("null") }
                    i += 4
                }
                c == '{' || c == '}' || c == '[' || c == ']' || c == ':' || c == ',' -> {
                    withStyle(SpanStyle(color = jsonPunctuationColor)) { append(c) }
                    i++
                }
                else -> {
                    append(c)
                    i++
                }
            }
        }
        if (matchStart >= 0 && matchEnd > matchStart && matchEnd <= length) {
            addStyle(SpanStyle(background = jsonSearchHighlightColor), matchStart, matchEnd)
        }
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
