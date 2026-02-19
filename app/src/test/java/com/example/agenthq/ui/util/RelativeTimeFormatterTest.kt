package com.example.agenthq.ui.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class RelativeTimeFormatterTest {

    private fun ago(amount: Long, unit: ChronoUnit): String =
        Instant.now().minus(amount, unit).toString()

    @Test
    fun `just now for less than 1 minute`() {
        val ts = Instant.now().minus(30, ChronoUnit.SECONDS).toString()
        assertEquals("just now", formatRelativeTime(ts))
    }

    @Test
    fun `1 minute ago`() {
        assertEquals("1 minute ago", formatRelativeTime(ago(1, ChronoUnit.MINUTES)))
    }

    @Test
    fun `45 minutes ago`() {
        assertEquals("45 minutes ago", formatRelativeTime(ago(45, ChronoUnit.MINUTES)))
    }

    @Test
    fun `1 hour ago`() {
        assertEquals("1 hour ago", formatRelativeTime(ago(60, ChronoUnit.MINUTES)))
    }

    @Test
    fun `3 hours ago`() {
        assertEquals("3 hours ago", formatRelativeTime(ago(180, ChronoUnit.MINUTES)))
    }

    @Test
    fun `1 day ago`() {
        assertEquals("1 day ago", formatRelativeTime(ago(1, ChronoUnit.DAYS)))
    }

    @Test
    fun `10 days ago`() {
        assertEquals("10 days ago", formatRelativeTime(ago(10, ChronoUnit.DAYS)))
    }

    @Test
    fun `invalid timestamp returns fallback string`() {
        val result = formatRelativeTime("not-a-timestamp")
        assertTrue(result.isNotEmpty())
    }
}
