package com.example.agenthq.ui.screens.pullrequest

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun CiStatusChip(ciStatus: String?) {
    val (label, icon, containerColor) = when (ciStatus?.uppercase()) {
        "SUCCESS" -> Triple(
            "CI Passed",
            Icons.Filled.Check,
            Color(0xFF1B5E20)
        )
        "FAILURE", "ERROR" -> Triple(
            "CI Failed",
            Icons.Filled.Close,
            Color(0xFFB71C1C)
        )
        "PENDING" -> Triple(
            "CI Pending",
            null,
            Color(0xFFE65100)
        )
        else -> Triple(
            "CI Unknown",
            Icons.AutoMirrored.Filled.HelpOutline,
            Color(0xFF424242)
        )
    }

    AssistChip(
        onClick = {},
        label = { Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White) },
        leadingIcon = if (icon != null) {
            {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White
                )
            }
        } else null,
        colors = AssistChipDefaults.assistChipColors(containerColor = containerColor)
    )
}
