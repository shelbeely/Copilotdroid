package com.example.agenthq.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenthq.data.repository.AgentSessionRepository
import com.example.agenthq.domain.model.AgentSessionUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val sessions: List<AgentSessionUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val agentSessionRepository: AgentSessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState

    init {
        observeSessions()
    }

    private fun observeSessions() {
        viewModelScope.launch {
            agentSessionRepository.getAgentSessions()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
                .collect { sessions ->
                    _uiState.value = _uiState.value.copy(
                        sessions = sessions,
                        isLoading = false,
                        error = null
                    )
                }
        }
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)
        viewModelScope.launch {
            // Re-collect sessions; a full network sync would be triggered here.
            _uiState.value = _uiState.value.copy(isRefreshing = false)
        }
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
