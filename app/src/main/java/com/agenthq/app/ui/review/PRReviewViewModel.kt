package com.agenthq.app.ui.review

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agenthq.app.data.api.GitHubRestService
import com.agenthq.app.data.api.models.PullRequestFile
import com.agenthq.app.data.api.models.Review
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PRReviewUiState {
    data object Loading : PRReviewUiState()
    data class Success(
        val files: List<PullRequestFile>,
        val reviews: List<Review>
    ) : PRReviewUiState()
    data class Error(val message: String) : PRReviewUiState()
}

@HiltViewModel
class PRReviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val gitHubRestService: GitHubRestService
) : ViewModel() {

    private val owner: String = checkNotNull(savedStateHandle["owner"])
    private val repo: String = checkNotNull(savedStateHandle["repo"])
    private val pullNumber: Int = checkNotNull(savedStateHandle["pullNumber"])

    private val _uiState = MutableStateFlow<PRReviewUiState>(PRReviewUiState.Loading)
    val uiState: StateFlow<PRReviewUiState> = _uiState.asStateFlow()

    init {
        loadReviewData()
    }

    private fun loadReviewData() {
        viewModelScope.launch {
            try {
                val filesResponse = gitHubRestService.getPullRequestFiles(owner, repo, pullNumber)
                val reviewsResponse = gitHubRestService.getPullRequestReviews(owner, repo, pullNumber)

                val files = filesResponse.body() ?: emptyList()
                val reviews = reviewsResponse.body() ?: emptyList()

                _uiState.value = PRReviewUiState.Success(files = files, reviews = reviews)
            } catch (e: Exception) {
                _uiState.value = PRReviewUiState.Error(
                    e.message ?: "Failed to load review data"
                )
            }
        }
    }
}
