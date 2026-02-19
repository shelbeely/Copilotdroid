package com.agenthq.app.data.auth

import android.content.SharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class AuthRepositoryTest {

    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var repo: AuthRepository

    @Before
    fun setUp() {
        prefs = mock(SharedPreferences::class.java)
        editor = mock(SharedPreferences.Editor::class.java)
        `when`(prefs.edit()).thenReturn(editor)
        `when`(editor.putString(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString())).thenReturn(editor)
        `when`(editor.remove(org.mockito.ArgumentMatchers.anyString())).thenReturn(editor)

        // Initial state: no token stored
        `when`(prefs.getString("github_access_token", null)).thenReturn(null)
        repo = AuthRepository(prefs)
    }

    @Test
    fun `isAuthenticated returns false when no token stored`() {
        assertFalse(repo.isAuthenticated())
    }

    @Test
    fun `saveToken persists token and updates auth state`() {
        repo.saveToken("ghp_test123")

        verify(editor).putString("github_access_token", "ghp_test123")
        verify(editor).apply()
        assertTrue(repo.isAuthenticatedFlow.value)
    }

    @Test
    fun `getToken returns stored token`() {
        `when`(prefs.getString("github_access_token", null)).thenReturn("ghp_abc")
        assertEquals("ghp_abc", repo.getToken())
    }

    @Test
    fun `getToken returns null when nothing stored`() {
        assertNull(repo.getToken())
    }

    @Test
    fun `clearToken removes token and updates auth state`() {
        // Start authenticated
        `when`(prefs.getString("github_access_token", null)).thenReturn("ghp_token")
        repo = AuthRepository(prefs)
        assertTrue(repo.isAuthenticatedFlow.value)

        // Clear
        repo.clearToken()
        verify(editor).remove("github_access_token")
        verify(editor).apply()
        assertFalse(repo.isAuthenticatedFlow.value)
    }

    @Test
    fun `isAuthenticated returns true when token exists`() {
        `when`(prefs.getString("github_access_token", null)).thenReturn("ghp_token")
        assertTrue(repo.isAuthenticated())
    }

    @Test
    fun `isAuthenticatedFlow initially false when no token`() {
        assertFalse(repo.isAuthenticatedFlow.value)
    }
}
