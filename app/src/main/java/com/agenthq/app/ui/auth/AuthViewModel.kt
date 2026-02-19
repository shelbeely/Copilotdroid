package com.agenthq.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agenthq.app.data.auth.AuthRepository
import com.agenthq.app.data.auth.GitHubAuthService
import com.agenthq.app.data.auth.GitHubOAuthConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    data object Idle : AuthUiState()
    data object Loading : AuthUiState()
    data object Authenticated : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val gitHubAuthService: GitHubAuthService
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(
        if (authRepository.isAuthenticated()) AuthUiState.Authenticated else AuthUiState.Idle
    )
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val isAuthenticated: StateFlow<Boolean> = authRepository.isAuthenticatedFlow

    fun handleOAuthCallback(code: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val response = gitHubAuthService.exchangeCodeForToken(
                    clientId = GitHubOAuthConfig.clientId,
                    clientSecret = GitHubOAuthConfig.clientSecret,
                    code = code,
                    redirectUri = GitHubOAuthConfig.redirectUri
                )
                authRepository.saveToken(response.access_token)
                _uiState.value = AuthUiState.Authenticated
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(
                    e.message ?: "Failed to exchange code for token"
                )
            }
        }
    }

    fun logout() {
        authRepository.clearToken()
        _uiState.value = AuthUiState.Idle
    }

    fun getAuthorizationUrl(): String {
        return GitHubOAuthConfig.buildAuthorizationUrl()
    }
}
