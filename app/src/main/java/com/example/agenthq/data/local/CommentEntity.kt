package com.example.agenthq.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "comments",
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
data class CommentEntity(
    @PrimaryKey val id: Long,
    val pullRequestId: Long,
    val prNumber: Int,
    val repoOwner: String,
    val repoName: String,
    /** One of: REVIEW_COMMENT, ISSUE_COMMENT */
    val commentType: String,
    val authorLogin: String,
    val body: String,
    val path: String?,
    val position: Int?,
    val createdAt: String,
    val isSteeringComment: Boolean
)
