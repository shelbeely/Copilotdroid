package com.agenthq.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agenthq.app.data.auth.AuthRepository
import com.agenthq.app.data.repository.GitHubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SettingsUiState {
    data object Loading : SettingsUiState()
    data class Loaded(
        val login: String,
        val displayName: String?,
        val avatarUrl: String?
    ) : SettingsUiState()
    data class Error(val message: String) : SettingsUiState()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val gitHubRepository: GitHubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            gitHubRepository.getAuthenticatedUser()
                .onSuccess { user ->
                    _uiState.value = SettingsUiState.Loaded(
                        login = user.login,
                        displayName = user.name,
                        avatarUrl = user.avatarUrl
                    )
                }
                .onFailure { e ->
                    _uiState.value = SettingsUiState.Error(
                        e.message ?: "Failed to load user"
                    )
                }
        }
    }

    fun logout() {
        authRepository.clearToken()
    }
}
