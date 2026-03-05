package com.example.agenthq.domain.usecase

import androidx.annotation.VisibleForTesting
import com.example.agenthq.data.local.AgentSessionEntity
import com.example.agenthq.data.local.PullRequestEntity
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InferSessionUseCase @Inject constructor() {

    @VisibleForTesting
    fun isAgentPr(pr: PullRequestEntity): Boolean {
        // Rule 1: bot author
        if (pr.authorLogin.contains("[bot]", ignoreCase = true)) return true
        // Rule 2: agent labels (comma-separated)
        val agentLabels = setOf("copilot", "agent", "github-copilot", "ai-generated")
        val prLabels = pr.labels.split(",").map { it.trim().lowercase() }.filter { it.isNotEmpty() }
        if (prLabels.any { it in agentLabels }) return true
        // Rule 3: title prefix
        val titleLower = pr.title.lowercase()
        if (titleLower.startsWith("[agent]") || titleLower.startsWith("[copilot]")) return true
        // Rule 4: branch name
        if (pr.headRef.startsWith("copilot/") || pr.headRef.startsWith("agent/")) return true
        return false
    }

    @VisibleForTesting
    fun inferStatus(pr: PullRequestEntity): String {
        val updatedAt = runCatching { Instant.parse(pr.updatedAt) }.getOrNull()
        val isRecent = updatedAt?.let {
            ChronoUnit.HOURS.between(it, Instant.now()) < 24
        } ?: false

        return when {
            pr.mergedAt != null -> "completed"
            pr.state.equals("closed", ignoreCase = true) -> "failed"
            pr.state.equals("open", ignoreCase = true) && isRecent -> "active"
            pr.state.equals("open", ignoreCase = true) && !isRecent -> "paused"
            else -> "unknown"
        }
    }

    /**
     * Builds an [AgentSessionEntity] for the given PR, or null if the PR is not an agent PR.
     * Pass an existing session to preserve its [AgentSessionEntity.id] and
     * [AgentSessionEntity.steeringCommentCount].
     */
    fun buildSession(
        pr: PullRequestEntity,
        existing: AgentSessionEntity? = null
    ): AgentSessionEntity? {
        if (!isAgentPr(pr)) return null
        return AgentSessionEntity(
            id = existing?.id ?: pr.id,
            pullRequestId = pr.id,
            repoOwner = pr.repoOwner,
            repoName = pr.repoName,
            prNumber = pr.number,
            agentLogin = pr.authorLogin,
            status = inferStatus(pr),
            inferredAt = System.currentTimeMillis(),
            steeringCommentCount = existing?.steeringCommentCount ?: 0,
            lastActivityAt = pr.updatedAt
        )
    }
}
