package com.example.agenthq.ui.util

import java.time.Instant
import java.time.temporal.ChronoUnit

fun formatRelativeTime(isoTimestamp: String): String {
    return try {
        val then = Instant.parse(isoTimestamp)
        val now = Instant.now()
        val minutes = ChronoUnit.MINUTES.between(then, now)
        when {
            minutes < 1 -> "just now"
            minutes < 60 -> if (minutes == 1L) "1 minute ago" else "$minutes minutes ago"
            minutes < 1440 -> (minutes / 60).let { h -> if (h == 1L) "1 hour ago" else "$h hours ago" }
            minutes < 43200 -> (minutes / 1440).let { d -> if (d == 1L) "1 day ago" else "$d days ago" }
            else -> (minutes / 43200).let { m -> if (m == 1L) "1 month ago" else "$m months ago" }
        }
    } catch (_: Exception) {
        isoTimestamp
    }
}
