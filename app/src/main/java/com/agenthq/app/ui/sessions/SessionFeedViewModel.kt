package com.agenthq.app.ui.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agenthq.app.data.local.entities.CachedPullRequest
import com.agenthq.app.data.repository.GitHubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SessionFeedUiState {
    data object Loading : SessionFeedUiState()
    data class Success(val sessions: List<CachedPullRequest>) : SessionFeedUiState()
    data object Empty : SessionFeedUiState()
    data class Error(val message: String) : SessionFeedUiState()
}

@HiltViewModel
class SessionFeedViewModel @Inject constructor(
    private val repository: GitHubRepository
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val uiState: StateFlow<SessionFeedUiState> = repository.getCopilotSessions()
        .map { sessions ->
            if (sessions.isEmpty()) SessionFeedUiState.Empty
            else SessionFeedUiState.Success(sessions)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SessionFeedUiState.Loading)

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                repository.syncRepositories()
                repository.syncAllPullRequests()
            } catch (e: Exception) {
                // Cache still provides data via Flow; swallow network errors
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
