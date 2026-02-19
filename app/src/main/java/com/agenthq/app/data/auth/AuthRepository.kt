package com.agenthq.app.data.auth

import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val encryptedPrefs: SharedPreferences
) {
    private val _isAuthenticated = MutableStateFlow(getToken() != null)
    val isAuthenticatedFlow: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    fun saveToken(token: String) {
        encryptedPrefs.edit().putString(KEY_ACCESS_TOKEN, token).apply()
        _isAuthenticated.value = true
    }

    fun getToken(): String? {
        return encryptedPrefs.getString(KEY_ACCESS_TOKEN, null)
    }

    fun clearToken() {
        encryptedPrefs.edit().remove(KEY_ACCESS_TOKEN).apply()
        _isAuthenticated.value = false
    }

    fun isAuthenticated(): Boolean {
        return getToken() != null
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "github_access_token"
    }
}
