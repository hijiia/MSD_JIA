package com.example.maracas.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaracaScreen(
    shakes: List<MaracaVM.UiShake>,
    onDeleteOlderThan: (Long) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var selectedThreshold by remember { mutableStateOf(3_600_000L) } // default 1 hour

    val thresholds = listOf(
        "1 min" to 60_000L,
        "10 min" to 600_000L,
        "1 hour" to 3_600_000L,
        "1 day" to 86_400_000L,
        "7 days" to 7 * 86_400_000L
    )

    Scaffold(topBar = { TopAppBar(title = { Text("Maracas") }) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Shake the phone to record a shake. Cooldown prevents duplicates.")

            // Visualization of shakes timeline
            ShakeTimeline(
                shakes = shakes,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )

            // Delete controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = menuExpanded,
                    onExpandedChange = { menuExpanded = !menuExpanded }
                ) {
                    OutlinedTextField(
                        value = thresholds.firstOrNull { it.second == selectedThreshold }?.first
                            ?: "Select threshold",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Delete older than") },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        thresholds.forEach { (label, ms) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    selectedThreshold = ms
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                }

                Button(onClick = { onDeleteOlderThan(selectedThreshold) }) {
                    Text("Delete")
                }
            }

            Divider()
            Text("Recent shakes", style = MaterialTheme.typography.titleMedium)

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(shakes, key = { it.id }) { s ->
                    ListItem(
                        headlineContent = {
                            Text("Intensity: ${"%.2f".format(s.intensityG)} g")
                        },
                        supportingContent = { Text(s.timeText) }
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
fun ShakeTimeline(shakes: List<MaracaVM.UiShake>, modifier: Modifier = Modifier) {
    if (shakes.isEmpty()) {
        Box(modifier, contentAlignment = Alignment.Center) {
            Text("No shakes yet — try shaking!")
        }
        return
    }

    val minTime = shakes.minOf { it.timestamp }
    val maxTime = shakes.maxOf { it.timestamp }
    val timeSpan = (maxTime - minTime).coerceAtLeast(1L)
    val maxG = shakes.maxOf { it.intensityG }.coerceAtLeast(2f)

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val baselineY = h - 6f

        // Draw horizontal axis
        drawLine(Color.Gray, Offset(0f, baselineY), Offset(w, baselineY), strokeWidth = 4f)

        // Draw a vertical bar for each shake
        shakes.forEach { s ->
            val x = ((s.timestamp - minTime).toFloat() / timeSpan) * w
            val barHeight = (s.intensityG / maxG) * (h - 20f)
            drawLine(
                color = Color(0xFF3F51B5),
                start = Offset(x, baselineY),
                end = Offset(x, baselineY - barHeight),
                strokeWidth = 8f
            )
        }
    }
}