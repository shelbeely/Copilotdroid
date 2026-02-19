package com.agenthq.app.data.session

enum class SessionType {
    COPILOT_AGENT,
    COPILOT_WORKSPACE,
    UNKNOWN
}

data class SessionMetadata(
    val sessionType: SessionType,
    val isAutomated: Boolean,
    val issueReference: String? = null,
    val agentModel: String? = null
)
