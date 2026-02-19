package com.agenthq.app.ui.review

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AdditionBackground = Color(0x332EA043)
private val DeletionBackground = Color(0x33F85149)
private val HunkHeaderBackground = Color(0x331F6FEB)

data class DiffLine(
    val text: String,
    val type: LineType
)

enum class LineType { ADDITION, DELETION, CONTEXT, HUNK_HEADER }

@Composable
fun DiffView(
    patch: String,
    modifier: Modifier = Modifier
) {
    val lines = remember(patch) { parsePatch(patch) }
    val horizontalScrollState = rememberScrollState()

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(horizontalScrollState)
    ) {
        itemsIndexed(lines) { index, line ->
            val bgColor = when (line.type) {
                LineType.ADDITION -> AdditionBackground
                LineType.DELETION -> DeletionBackground
                LineType.HUNK_HEADER -> HunkHeaderBackground
                LineType.CONTEXT -> Color.Transparent
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgColor)
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "${index + 1}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.widthIn(min = 36.dp)
                )
                Text(
                    text = line.text,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun parsePatch(patch: String): List<DiffLine> {
    return patch.lines().map { line ->
        when {
            line.startsWith("@@") -> DiffLine(line, LineType.HUNK_HEADER)
            line.startsWith("+") -> DiffLine(line, LineType.ADDITION)
            line.startsWith("-") -> DiffLine(line, LineType.DELETION)
            else -> DiffLine(line, LineType.CONTEXT)
        }
    }
}
