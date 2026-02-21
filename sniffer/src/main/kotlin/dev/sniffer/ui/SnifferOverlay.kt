package dev.sniffer.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.sniffer.data.model.LogEntry
import dev.sniffer.data.model.NetworkCall
import dev.sniffer.data.repository.SnifferRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val BUBBLE_SIZE_DP = 56.dp
private val BUBBLE_SIZE_COLLAPSED_DP = 40.dp
private const val INSPECTOR_HEIGHT_FRACTION = 0.85f

@Composable
fun SnifferOverlay(
    repository: SnifferRepository,
    onDismiss: () -> Unit
) {
    MaterialTheme(colorScheme = darkColorScheme()) {
        SnifferOverlayContent(repository = repository, onDismiss = onDismiss)
    }
}

@Composable
private fun SnifferOverlayContent(
    repository: SnifferRepository,
    onDismiss: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val maxWidthPx = with(density) { maxWidth.toPx() }
        val maxHeightPx = with(density) { maxHeight.toPx() }
        val bubbleSizePx = with(density) { BUBBLE_SIZE_DP.toPx() }
        val collapsedSizePx = with(density) { BUBBLE_SIZE_COLLAPSED_DP.toPx() }

        var bubblePosXPx by remember { mutableFloatStateOf((maxWidthPx - bubbleSizePx).coerceAtLeast(0f)) }
        var bubblePosYPx by remember { mutableFloatStateOf(0f) }

        val maxPosX = (maxWidthPx - bubbleSizePx).coerceAtLeast(0f)
        val maxPosY = (maxHeightPx - bubbleSizePx).coerceAtLeast(0f)

        val animatedBubbleSizePx by animateFloatAsState(
            targetValue = if (expanded) collapsedSizePx else bubbleSizePx,
            animationSpec = tween(300), label = "bubbleSize"
        )
        val expandedTargetX = (maxWidthPx - collapsedSizePx).coerceAtLeast(0f)
        val expandedTargetY = with(density) { 8.dp.toPx() }
        val animatedPosX by animateFloatAsState(
            targetValue = if (expanded) expandedTargetX else bubblePosXPx,
            animationSpec = tween(300), label = "posX"
        )
        val animatedPosY by animateFloatAsState(
            targetValue = if (expanded) expandedTargetY else bubblePosYPx,
            animationSpec = tween(300), label = "posY"
        )

        AnimatedVisibility(
            visible = expanded,
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(300)),
            exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(300)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(INSPECTOR_HEIGHT_FRACTION)
                .shadow(12.dp, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
        ) {
            SnifferInspector(
                repository = repository,
                onClose = { expanded = false }
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset { IntOffset(animatedPosX.roundToInt(), animatedPosY.roundToInt()) }
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { expanded = !expanded })
                }
                .pointerInput(expanded, maxPosX, maxPosY, bubbleSizePx) {
                    if (!expanded) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                bubblePosXPx = (bubblePosXPx + dragAmount.x).coerceIn(0f, maxPosX)
                                bubblePosYPx = (bubblePosYPx + dragAmount.y).coerceIn(0f, maxPosY)
                            }
                        )
                    }
                }
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .size(with(density) { androidx.compose.ui.unit.Dp(animatedBubbleSizePx / density.density) }),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "S",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnifferInspector(
    repository: SnifferRepository,
    onClose: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var selectedCall by remember { mutableStateOf<NetworkCall?>(null) }
    val networkCalls by repository.networkCalls.collectAsState()
    val logs by repository.logs.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        if (selectedCall != null) {
            NetworkCallDetailScreen(
                call = selectedCall!!,
                onBack = { selectedCall = null }
            )
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Text("×", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Network") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Logs") }
                    )
                }
                when (selectedTab) {
                    0 -> NetworkTab(
                        calls = networkCalls,
                        repository = repository,
                        onCallClick = { selectedCall = it }
                    )
                    1 -> LogsTab(entries = logs, repository = repository)
                }
            }
        }
    }
}

@Composable
fun NetworkTab(
    calls: List<NetworkCall>,
    repository: SnifferRepository,
    onCallClick: (NetworkCall) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        item {
            TextButton(onClick = { scope.launch(Dispatchers.IO) { repository.clearNetworkCalls() } }) {
                Text("Clear")
            }
        }
        items(calls) { call ->
            NetworkCallItem(call = call, onClick = { onCallClick(call) })
        }
    }
}

private fun statusColor(code: Int): Color = when {
    code in 200..299 -> Color(0xFF81C784)
    code in 400..499 -> Color(0xFFFFB74D)
    code in 500..599 -> Color(0xFFE57373)
    else -> Color(0xFFB0BEC5)
}

@Composable
fun NetworkCallItem(call: NetworkCall, onClick: () -> Unit = {}) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "${call.requestMethod} ${call.requestUrl}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${call.responseCode} • ${call.durationMs}ms${if (call.wasMocked) " (mocked)" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = statusColor(call.responseCode)
            )
        }
    }
}

@Composable
fun LogsTab(
    entries: List<LogEntry>,
    repository: SnifferRepository
) {
    val scope = rememberCoroutineScope()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        item {
            TextButton(onClick = { scope.launch(Dispatchers.IO) { repository.clearLogs() } }) {
                Text("Clear")
            }
        }
        items(entries) { entry ->
            LogEntryItem(entry = entry)
        }
    }
}

@Composable
fun LogEntryItem(entry: LogEntry) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "[${entry.level}]",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            if (entry.tag != null) {
                Text(
                    text = " ${entry.tag}: ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = entry.message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
