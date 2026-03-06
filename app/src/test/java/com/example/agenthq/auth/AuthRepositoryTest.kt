package com.example.agenthq.auth

import com.example.agenthq.data.preferences.HostPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
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

    @Test
    fun `startDeviceFlow emits error when client ID is blank`() = runTest {
        val repositoryWithBlankId = AuthRepository(tokenStore, hostPreferences, "")
        val states = repositoryWithBlankId.startDeviceFlow().toList()
        val lastState = states.last()
        assertTrue(lastState is AuthState.Error)
        assertTrue((lastState as AuthState.Error).message.contains("GITHUB_CLIENT_ID"))
    }

    @Test
    fun `startDeviceFlow emits error when client ID is NOT_CONFIGURED sentinel`() = runTest {
        val repositoryWithSentinelId = AuthRepository(tokenStore, hostPreferences, BuildConfigHelper.UNCONFIGURED_CLIENT_ID)
        val states = repositoryWithSentinelId.startDeviceFlow().toList()
        val lastState = states.last()
        assertTrue(lastState is AuthState.Error)
        assertTrue((lastState as AuthState.Error).message.contains("GITHUB_CLIENT_ID"))
    }

    @Test
    fun `loginWithPat saves token and emits Success`() = runTest {
        val states = repository.loginWithPat("ghp_testtoken123").toList()
        verify { tokenStore.saveToken("ghp_testtoken123") }
        assertTrue(states.last() is AuthState.Success)
    }

    @Test
    fun `loginWithPat emits error when token is blank`() = runTest {
        val states = repository.loginWithPat("  ").toList()
        val last = states.last()
        assertTrue(last is AuthState.Error)
        assertTrue((last as AuthState.Error).message.contains("empty"))
    }
}
