package com.example.agenthq.domain.model

data class AgentSessionUiModel(
    val id: Long,
    val prNumber: Int,
    val prTitle: String,
    val repoFullName: String,
    val status: AgentSessionStatus,
    val lastActivityAt: String,
    val authorLogin: String,
    val ciStatus: CiStatus = CiStatus.UNKNOWN
)

enum class AgentSessionStatus {
    ACTIVE, COMPLETED, FAILED, PAUSED
}

enum class CiStatus {
    PASSING, FAILING, PENDING, UNKNOWN
}
