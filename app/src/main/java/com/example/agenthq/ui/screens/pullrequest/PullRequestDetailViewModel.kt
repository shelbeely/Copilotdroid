package com.example.agenthq.ui.screens.pullrequest

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenthq.auth.TokenStore
import com.example.agenthq.data.local.PullRequestDao
import com.example.agenthq.data.local.PullRequestEntity
import com.example.agenthq.data.local.ReviewDao
import com.example.agenthq.data.local.ReviewEntity
import com.example.agenthq.data.remote.rest.CreateReviewRequest
import com.example.agenthq.data.repository.PullRequestRepository
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
    val reviewSubmitted: Boolean = false,
    /** Non-null while a snackbar message should be shown; null after consumed. */
    val snackbarMessage: String? = null
)

@HiltViewModel
class PullRequestDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val pullRequestDao: PullRequestDao,
    private val reviewDao: ReviewDao,
    private val pullRequestRepository: PullRequestRepository,
    private val tokenStore: TokenStore
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

    fun submitReview(event: String, body: String) {
        val token = tokenStore.getToken() ?: run {
            _uiState.value = _uiState.value.copy(snackbarMessage = "Not authenticated")
            return
        }
        _uiState.value = _uiState.value.copy(isSubmittingReview = true)
        viewModelScope.launch {
            val result = pullRequestRepository.createReview(
                token = token,
                owner = owner,
                repo = repo,
                pullNumber = prNumber,
                review = CreateReviewRequest(body = body, event = event)
            )
            _uiState.value = _uiState.value.copy(
                isSubmittingReview = false,
                reviewSubmitted = result.isSuccess,
                snackbarMessage = if (result.isSuccess) "Review submitted" else "Failed: ${result.exceptionOrNull()?.message}"
            )
        }
    }

    fun snackbarMessageShown() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }
}
