package com.example.agenthq.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pull_requests")
data class PullRequestEntity(
    @PrimaryKey val id: Long,
    val number: Int,
    val title: String,
    val body: String?,
    val state: String,
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
