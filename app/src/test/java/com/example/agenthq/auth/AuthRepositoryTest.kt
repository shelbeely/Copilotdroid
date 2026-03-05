package com.example.agenthq.auth

import com.example.agenthq.data.preferences.HostPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRepositoryTest {

    private val tokenStore: TokenStore = mockk(relaxed = true)
    private val hostPreferences: HostPreferences = mockk(relaxed = true) {
        every { githubHost } returns flowOf(HostPreferences.DEFAULT_HOST)
    }
    private val repository = AuthRepository(tokenStore, hostPreferences)

    @Test
    fun `isLoggedIn returns false when token is null`() = runTest {
        every { tokenStore.hasToken() } returns false
        assertFalse(repository.isLoggedIn())
    }

    @Test
    fun `isLoggedIn returns true when token is present`() = runTest {
        every { tokenStore.hasToken() } returns true
        assertTrue(repository.isLoggedIn())
    }

    @Test
    fun `logout clears token`() = runTest {
        repository.logout()
        verify { tokenStore.clearToken() }
    }
}
