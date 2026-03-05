package com.example.agenthq.ui.screens.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenthq.data.local.AgentSessionDao
import com.example.agenthq.data.local.AgentSessionEntity
import com.example.agenthq.data.local.PullRequestDao
import com.example.agenthq.data.local.PullRequestEntity
import com.example.agenthq.data.local.ReviewEntity
import com.example.agenthq.data.local.ReviewDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SessionDetailUiState(
    val session: AgentSessionEntity? = null,
    val pullRequest: PullRequestEntity? = null,
    val reviews: List<ReviewEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class SessionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val agentSessionDao: AgentSessionDao,
    private val pullRequestDao: PullRequestDao,
    private val reviewDao: ReviewDao
) : ViewModel() {

    private val sessionId: Long = checkNotNull(savedStateHandle["sessionId"])

    private val _uiState = MutableStateFlow(SessionDetailUiState())
    val uiState: StateFlow<SessionDetailUiState> = _uiState

    init {
        loadSession()
    }

    private fun loadSession() {
        viewModelScope.launch {
            val session = agentSessionDao.getById(sessionId)
            if (session == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Session not found"
                )
                return@launch
            }
            _uiState.value = _uiState.value.copy(session = session)
            val pr = pullRequestDao.getByNumber(session.repoOwner, session.repoName, session.prNumber)
            _uiState.value = _uiState.value.copy(pullRequest = pr, isLoading = false)
            observeReviews(session.repoOwner, session.repoName, session.prNumber)
        }
    }

    private fun observeReviews(owner: String, repo: String, prNumber: Int) {
        viewModelScope.launch {
            reviewDao.getForPr(owner, repo, prNumber)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
                .collect { reviews ->
                    _uiState.value = _uiState.value.copy(reviews = reviews)
                }
        }
    }
}
