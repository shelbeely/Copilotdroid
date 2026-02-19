package com.example.agenthq.data.repository

import com.example.agenthq.data.local.AgentSessionDao
import com.example.agenthq.data.local.PullRequestDao
import com.example.agenthq.domain.model.AgentSessionStatus
import com.example.agenthq.domain.model.AgentSessionUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgentSessionRepository @Inject constructor(
    private val agentSessionDao: AgentSessionDao,
    private val pullRequestDao: PullRequestDao
) {
    fun getAgentSessions(): Flow<List<AgentSessionUiModel>> =
        combine(
            agentSessionDao.getAll(),
            pullRequestDao.getAll()
        ) { sessions, prs ->
            val prByOwnerRepoNumber = prs.associateBy {
                Triple(it.repoOwner, it.repoName, it.number)
            }
            sessions.mapNotNull { session ->
                val pr = prByOwnerRepoNumber[
                    Triple(session.repoOwner, session.repoName, session.prNumber)
                ] ?: return@mapNotNull null
                AgentSessionUiModel(
                    id = session.id,
                    prNumber = session.prNumber,
                    prTitle = pr.title,
                    repoFullName = "${session.repoOwner}/${session.repoName}",
                    status = when (session.status.lowercase()) {
                        "active" -> AgentSessionStatus.ACTIVE
                        "completed" -> AgentSessionStatus.COMPLETED
                        "failed" -> AgentSessionStatus.FAILED
                        else -> AgentSessionStatus.PAUSED
                    },
                    lastActivityAt = session.lastActivityAt,
                    authorLogin = pr.authorLogin
                )
            }
        }
}
