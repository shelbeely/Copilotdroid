package com.example.agenthq.auth

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRepositoryTest {

    private val tokenStore: TokenStore = mockk(relaxed = true)
    private val repository = AuthRepository(tokenStore)

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
