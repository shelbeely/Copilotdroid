package com.example.agenthq.ui.screens.pullrequest

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.agenthq.data.local.ReviewEntity
import com.example.agenthq.ui.util.formatRelativeTime

@Composable
fun ReviewListItem(review: ReviewEntity) {
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = "https://github.com/${review.authorLogin}.png?size=64",
                        contentDescription = review.authorLogin,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = review.authorLogin,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                ReviewStateBadge(state = review.state)
            }

            Text(
                text = formatRelativeTime(review.submittedAt),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (review.body.isNotBlank()) {
                Text(
                    text = review.body,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = if (expanded) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            }
        }
    }
}

@Composable
private fun ReviewStateBadge(state: String) {
    val (containerColor, contentColor, strikethrough) = when (state.uppercase()) {
        "APPROVED" -> Triple(Color(0xFF1B5E20), Color.White, false)
        "CHANGES_REQUESTED" -> Triple(Color(0xFFB71C1C), Color.White, false)
        "COMMENTED" -> Triple(Color(0xFF616161), Color.White, false)
        "DISMISSED" -> Triple(Color(0xFF424242), Color(0xFFBDBDBD), true)
        else -> Triple(Color(0xFF424242), Color.White, false)
    }
    Surface(shape = MaterialTheme.shapes.extraSmall, color = containerColor) {
        Text(
            text = state.replace("_", " "),
            style = MaterialTheme.typography.labelSmall.copy(
                textDecoration = if (strikethrough) TextDecoration.LineThrough else TextDecoration.None
            ),
            color = contentColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}
