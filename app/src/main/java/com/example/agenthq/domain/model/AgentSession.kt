package com.example.agenthq.domain.model

data class AgentSession(
    val id: Long,
    val pullRequestId: Long,
    val pullRequestNumber: Int,
    val owner: String,
    val repo: String,
    val status: AgentStatus,
    val startedAt: String,
    val lastActivityAt: String?,
    val completedAt: String?
)

enum class AgentStatus {
    QUEUED, IN_PROGRESS, COMPLETED, FAILED, CANCELLED
}
