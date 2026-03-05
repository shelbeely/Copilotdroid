package com.example.agenthq.domain.model

data class PullRequest(
    val id: Long,
    val number: Int,
    val title: String,
    val body: String?,
    val state: PullRequestState,
    val owner: String,
    val repo: String,
    val headBranch: String,
    val baseBranch: String,
    val authorLogin: String,
    val authorAvatarUrl: String?,
    val createdAt: String,
    val updatedAt: String,
    val isDraft: Boolean,
    val hasAgentAssigned: Boolean
)

enum class PullRequestState {
    OPEN, CLOSED, MERGED
}
