package com.example.agenthq.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reviews",
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
data class ReviewEntity(
    @PrimaryKey val id: Long,
    val pullRequestId: Long,
    val repoOwner: String,
    val repoName: String,
    val prNumber: Int,
    val authorLogin: String,
    /** One of: APPROVED, CHANGES_REQUESTED, COMMENTED, DISMISSED */
    val state: String,
    val body: String,
    val submittedAt: String
)
