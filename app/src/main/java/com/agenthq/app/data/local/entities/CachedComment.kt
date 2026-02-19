package com.agenthq.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CachedComment(
    @PrimaryKey val id: Long,
    val pullRequestId: Long,
    val authorLogin: String,
    val authorAvatarUrl: String,
    val body: String,
    val createdAt: String,
    val updatedAt: String
)
