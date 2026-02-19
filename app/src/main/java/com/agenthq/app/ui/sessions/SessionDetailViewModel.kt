package com.agenthq.app.ui.sessions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agenthq.app.data.local.entities.CachedComment
import com.agenthq.app.data.local.entities.CachedPullRequest
import com.agenthq.app.data.repository.GitHubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SessionDetailUiState {
    data object Loading : SessionDetailUiState()
    data class Success(val pullRequest: CachedPullRequest) : SessionDetailUiState()
    data class Error(val message: String) : SessionDetailUiState()
}

@HiltViewModel
class SessionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: GitHubRepository
) : ViewModel() {

    private val prId: Long = checkNotNull(savedStateHandle["prId"])

    private val _uiState = MutableStateFlow<SessionDetailUiState>(SessionDetailUiState.Loading)
    val uiState: StateFlow<SessionDetailUiState> = _uiState.asStateFlow()

    val comments: StateFlow<List<CachedComment>> = repository.getComments(prId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadDetail()
    }

    private fun loadDetail() {
        viewModelScope.launch {
            try {
                val pr = repository.getPullRequestById(prId)
                if (pr != null) {
                    _uiState.value = SessionDetailUiState.Success(pr)
                } else {
                    _uiState.value = SessionDetailUiState.Error("Session not found")
                }
                repository.syncComments(prId)
            } catch (e: Exception) {
                if (_uiState.value is SessionDetailUiState.Loading) {
                    _uiState.value = SessionDetailUiState.Error(
                        e.message ?: "Failed to load session details"
                    )
                }
            }
        }
    }
}
