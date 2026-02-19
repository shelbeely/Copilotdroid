package com.agenthq.app.ui.steering

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agenthq.app.data.api.GitHubRestService
import com.agenthq.app.data.api.models.Comment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SteeringUiState {
    data object Idle : SteeringUiState()
    data object Posting : SteeringUiState()
    data class Success(val message: String) : SteeringUiState()
    data class Error(val message: String) : SteeringUiState()
}

@HiltViewModel
class SteeringViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val gitHubRestService: GitHubRestService
) : ViewModel() {

    val owner: String = checkNotNull(savedStateHandle["owner"])
    val repo: String = checkNotNull(savedStateHandle["repo"])
    val issueNumber: Int = checkNotNull(savedStateHandle["issueNumber"])

    private val _uiState = MutableStateFlow<SteeringUiState>(SteeringUiState.Idle)
    val uiState: StateFlow<SteeringUiState> = _uiState.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val _commentsLoading = MutableStateFlow(true)
    val commentsLoading: StateFlow<Boolean> = _commentsLoading.asStateFlow()

    init {
        loadComments()
    }

    private fun loadComments() {
        viewModelScope.launch {
            _commentsLoading.value = true
            try {
                val response = gitHubRestService.getIssueComments(owner, repo, issueNumber)
                _comments.value = response.body() ?: emptyList()
            } catch (_: Exception) {
                // Comments are contextual; failure is non-critical
            } finally {
                _commentsLoading.value = false
            }
        }
    }

    fun postComment(body: String) {
        if (body.isBlank()) return
        viewModelScope.launch {
            _uiState.value = SteeringUiState.Posting
            try {
                val response = gitHubRestService.createComment(
                    owner, repo, issueNumber, mapOf("body" to body)
                )
                if (response.isSuccessful) {
                    _uiState.value = SteeringUiState.Success("Comment posted")
                    loadComments()
                } else {
                    _uiState.value = SteeringUiState.Error(
                        "Failed to post comment (${response.code()})"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = SteeringUiState.Error(
                    e.message ?: "Failed to post comment"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = SteeringUiState.Idle
    }
}
