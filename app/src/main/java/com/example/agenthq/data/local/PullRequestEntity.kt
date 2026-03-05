package com.example.agenthq.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pull_requests")
data class PullRequestEntity(
    @PrimaryKey val id: Long,
    val number: Int,
    val repoOwner: String,
    val repoName: String,
    val title: String,
    val state: String,
    val htmlUrl: String,
    val authorLogin: String,
    val authorAvatarUrl: String,
    val headRef: String,
    val baseRef: String,
    val body: String,
    val isDraft: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val mergedAt: String?,
    /** Comma-separated label names, e.g. "copilot,bug,enhancement" */
    val labels: String,
    val isAgentPr: Boolean,
    val lastSyncedAt: Long
)
