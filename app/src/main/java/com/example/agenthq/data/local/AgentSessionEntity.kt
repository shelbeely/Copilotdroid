package com.example.agenthq.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "agent_sessions",
    foreignKeys = [
        ForeignKey(
            entity = PullRequestEntity::class,
            parentColumns = ["id"],
            childColumns = ["pullRequestId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("pullRequestId")]
)
data class AgentSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pullRequestId: Long,
    val repoOwner: String,
    val repoName: String,
    val prNumber: Int,
    val agentLogin: String,
    /** One of: active, completed, failed, paused */
    val status: String,
    val inferredAt: Long,
    val steeringCommentCount: Int,
    val lastActivityAt: String
)
