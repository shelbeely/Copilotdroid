package com.agenthq.app

import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun `app package is correct`() {
        assertEquals("com.agenthq.app", AgentHQApplication::class.java.`package`?.name)
    }
}
