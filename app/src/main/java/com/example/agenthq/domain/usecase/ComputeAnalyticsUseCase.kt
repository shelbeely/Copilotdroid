package com.example.agenthq.domain.usecase

import com.example.agenthq.data.local.AgentSessionDao
import com.example.agenthq.data.local.AgentSessionEntity
import com.example.agenthq.data.local.CommentDao
import com.example.agenthq.data.local.CommentEntity
import com.example.agenthq.data.local.PullRequestDao
import com.example.agenthq.data.local.PullRequestEntity
import com.example.agenthq.domain.model.AnalyticsData
import com.example.agenthq.domain.model.PrSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ComputeAnalyticsUseCase @Inject constructor(
    private val pullRequestDao: PullRequestDao,
    private val agentSessionDao: AgentSessionDao,
    private val commentDao: CommentDao
) {
    fun observe(): Flow<AnalyticsData> = combine(
        pullRequestDao.getAll(),
        agentSessionDao.getAll(),
        commentDao.getAll()
    ) { allPrs, allSessions, allComments ->
        computeAnalytics(allPrs, allSessions, allComments)
    }

    private fun computeAnalytics(
        allPrs: List<PullRequestEntity>,
        allSessions: List<AgentSessionEntity>,
        allComments: List<CommentEntity>
    ): AnalyticsData {
        val agentPrs = allPrs.filter { it.isAgentPr }
        val humanPrs = allPrs.filter { !it.isAgentPr }

        val agentMerged = agentPrs.count { it.mergedAt != null }
        val humanMerged = humanPrs.count { it.mergedAt != null }

        val agentMergeRate = if (agentPrs.isEmpty()) 0f else agentMerged.toFloat() / agentPrs.size
        val humanMergeRate = if (humanPrs.isEmpty()) 0f else humanMerged.toFloat() / humanPrs.size

        val avgAgentCycleHours = averageCycleHours(agentPrs)
        val avgHumanCycleHours = averageCycleHours(humanPrs)

        val agentPrsByStatus = allSessions
            .groupBy { it.status }
            .mapValues { it.value.size }

        val recentAgentPrs = agentPrs.take(5).map { pr ->
            val session = allSessions.firstOrNull { it.pullRequestId == pr.id }
            PrSummary(
                number = pr.number,
                title = pr.title,
                repoFullName = "${pr.repoOwner}/${pr.repoName}",
                status = session?.status ?: pr.state,
                mergedAt = pr.mergedAt
            )
        }

        val totalSteeringComments = allComments.count { it.isSteeringComment }

        val agentSuccessScore = (agentMergeRate * 100f).coerceIn(0f, 100f)

        return AnalyticsData(
            totalAgentPrs = agentPrs.size,
            totalHumanPrs = humanPrs.size,
            agentMergeRate = agentMergeRate,
            humanMergeRate = humanMergeRate,
            avgAgentCycleHours = avgAgentCycleHours,
            avgHumanCycleHours = avgHumanCycleHours,
            agentPrsByStatus = agentPrsByStatus,
            recentAgentPrs = recentAgentPrs,
            totalSteeringComments = totalSteeringComments,
            agentSuccessScore = agentSuccessScore
        )
    }

    private fun averageCycleHours(prs: List<PullRequestEntity>): Float {
        val merged = prs.filter { it.mergedAt != null }
        if (merged.isEmpty()) return 0f
        val totalHours = merged.sumOf { pr ->
            val start = parseIso(pr.createdAt) ?: return@sumOf 0L
            val end = parseIso(pr.mergedAt!!) ?: return@sumOf 0L
            (end - start) / (1000L * 60 * 60)
        }
        return totalHours.toFloat() / merged.size
    }

    private fun parseIso(s: String): Long? = try {
        Instant.parse(s).toEpochMilli()
    } catch (_: Exception) {
        null
    }
}
