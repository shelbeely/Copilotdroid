package com.example.agenthq.ui.screens.pullrequest

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenthq.data.local.PullRequestDao
import com.example.agenthq.data.local.PullRequestEntity
import com.example.agenthq.data.local.ReviewDao
import com.example.agenthq.data.local.ReviewEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PullRequestDetailUiState(
    val pullRequest: PullRequestEntity? = null,
    val reviews: List<ReviewEntity> = emptyList(),
    val isLoading: Boolean = true,
    val isSubmittingReview: Boolean = false,
    val error: String? = null,
    val reviewSubmitted: Boolean = false
)

@HiltViewModel
class PullRequestDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val pullRequestDao: PullRequestDao,
    private val reviewDao: ReviewDao
) : ViewModel() {

    private val owner: String = checkNotNull(savedStateHandle["owner"])
    private val repo: String = checkNotNull(savedStateHandle["repo"])
    private val prNumber: Int = checkNotNull(savedStateHandle.get<String>("number")?.toIntOrNull())

    private val _uiState = MutableStateFlow(PullRequestDetailUiState())
    val uiState: StateFlow<PullRequestDetailUiState> = _uiState

    init {
        loadPullRequest()
        observeReviews()
    }

    private fun loadPullRequest() {
        viewModelScope.launch {
            val pr = pullRequestDao.getByNumber(owner, repo, prNumber)
            _uiState.value = _uiState.value.copy(
                pullRequest = pr,
                isLoading = false,
                error = if (pr == null) "Pull request not found" else null
            )
        }
    }

    private fun observeReviews() {
        viewModelScope.launch {
            reviewDao.getForPr(owner, repo, prNumber)
                .catch { e -> _uiState.value = _uiState.value.copy(error = e.message) }
                .collect { reviews ->
                    _uiState.value = _uiState.value.copy(reviews = reviews)
                }
        }
    }

    fun submitReview(body: String, event: String = "COMMENT") {
        // Placeholder: a real implementation would call PullRequestRepository.createReview
        _uiState.value = _uiState.value.copy(isSubmittingReview = true)
        viewModelScope.launch {
            // Network call would go here; for now immediately mark as submitted
            _uiState.value = _uiState.value.copy(
                isSubmittingReview = false,
                reviewSubmitted = true
            )
        }
    }
}
