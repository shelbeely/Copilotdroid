package com.example.agenthq.domain.model

data class AnalyticsData(
    val totalAgentPrs: Int,
    val totalHumanPrs: Int,
    val agentMergeRate: Float,
    val humanMergeRate: Float,
    val avgAgentCycleHours: Float,
    val avgHumanCycleHours: Float,
    val agentPrsByStatus: Map<String, Int>,
    val recentAgentPrs: List<PrSummary>,
    val totalSteeringComments: Int,
    val agentSuccessScore: Float
)

data class PrSummary(
    val number: Int,
    val title: String,
    val repoFullName: String,
    val status: String,
    val mergedAt: String?
)
