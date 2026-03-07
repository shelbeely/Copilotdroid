package com.example.agenthq.ui.screens.pullrequest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenthq.data.local.PullRequestDao
import com.example.agenthq.data.local.PullRequestEntity
import com.example.agenthq.domain.usecase.SyncSessionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PullRequestListUiState(
    val pullRequests: List<PullRequestEntity> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PullRequestListViewModel @Inject constructor(
    private val pullRequestDao: PullRequestDao,
    private val syncSessionsUseCase: SyncSessionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PullRequestListUiState())
    val uiState: StateFlow<PullRequestListUiState> = _uiState

    init {
        observePullRequests()
    }

    private fun observePullRequests() {
        viewModelScope.launch {
            pullRequestDao.getAll()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
                .collect { prs ->
                    _uiState.value = _uiState.value.copy(
                        pullRequests = prs,
                        isLoading = false,
                        error = null
                    )
                }
        }
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)
        viewModelScope.launch {
            try {
                syncSessionsUseCase()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Sync failed")
            } finally {
                _uiState.value = _uiState.value.copy(isRefreshing = false)
            }
        }
    }
}
