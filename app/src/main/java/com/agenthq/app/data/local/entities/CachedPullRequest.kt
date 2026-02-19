package com.agenthq.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pull_requests")
data class CachedPullRequest(
    @PrimaryKey val id: Long,
    val number: Int,
    val repoFullName: String,
    val title: String,
    val body: String?,
    val state: String,
    val htmlUrl: String,
    val authorLogin: String,
    val authorAvatarUrl: String,
    val createdAt: String,
    val updatedAt: String,
    val mergedAt: String?,
    val headRef: String,
    val baseRef: String,
    val isDraft: Boolean,
    val isCopilotSession: Boolean = false,
    val lastSyncedAt: Long = System.currentTimeMillis()
)
