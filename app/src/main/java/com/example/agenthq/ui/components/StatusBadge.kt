package com.example.agenthq.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Pill-shaped status badge.
 * [status]: "active" → green, "paused" → amber, "completed" → blue, "failed" → red.
 */
@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val statusLower = status.lowercase()
    val (containerColor, contentColor) = when (statusLower) {
        "active"    -> Color(0xFF1B5E20) to Color.White
        "paused"    -> Color(0xFFB45309) to Color.White
        "completed" -> Color(0xFF0550AE) to Color.White
        "failed"    -> Color(0xFFB71C1C) to Color.White
        else        -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        shape = CircleShape,
        color = containerColor,
        modifier = modifier
    ) {
        Text(
            text = statusLower.replaceFirstChar { it.uppercaseChar() },
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
        )
    }
}
