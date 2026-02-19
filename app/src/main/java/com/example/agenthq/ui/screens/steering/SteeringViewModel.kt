package com.example.agenthq.ui.screens.steering

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenthq.auth.TokenStore
import com.example.agenthq.data.local.CommentDao
import com.example.agenthq.data.local.CommentEntity
import com.example.agenthq.data.repository.PullRequestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SteeringUiState(
    val commentText: String = "",
    val isSending: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val quickTemplates: List<String> = SteeringViewModel.DEFAULT_TEMPLATES
)

@HiltViewModel
class SteeringViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val pullRequestRepository: PullRequestRepository,
    private val commentDao: CommentDao,
    private val tokenStore: TokenStore
) : ViewModel() {

    val owner: String = savedStateHandle["owner"] ?: ""
    val repo: String = savedStateHandle["repo"] ?: ""
    val prNumber: Int = savedStateHandle.get<String>("number")?.toIntOrNull() ?: 0

    val steeringHistory: StateFlow<List<CommentEntity>> =
        commentDao.getSteeringComments(owner, repo, prNumber)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _uiState = MutableStateFlow(SteeringUiState())
    val uiState: StateFlow<SteeringUiState> = _uiState

    fun onCommentTextChanged(text: String) {
        _uiState.value = _uiState.value.copy(commentText = text)
    }

    fun sendSteeringComment() {
        val body = _uiState.value.commentText.trim()
        if (body.isEmpty()) return
        val token = tokenStore.getToken() ?: run {
            _uiState.value = _uiState.value.copy(error = "Not authenticated")
            return
        }
        _uiState.value = _uiState.value.copy(isSending = true, error = null)
        viewModelScope.launch {
            val result = pullRequestRepository.createSteeringComment(token, owner, repo, prNumber, body)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isSending = false,
                        commentText = "",
                        successMessage = "Instruction sent!"
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isSending = false,
                        error = e.message ?: "Failed to send comment"
                    )
                }
            )
        }
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun dismissSuccess() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    companion object {
        const val MAX_COMMENT_LENGTH = 2000
        val DEFAULT_TEMPLATES = listOf(
            "Please add unit tests for this change.",
            "Fix the failing CI checks.",
            "Add documentation/comments to this code.",
            "Please reduce the scope of changes in this PR.",
            "Address the review comments before continuing."
        )
    }
}
