package com.agenthq.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repositories")
data class CachedRepository(
    @PrimaryKey val id: Long,
    val name: String,
    val fullName: String,
    val ownerLogin: String,
    val ownerAvatarUrl: String,
    val isPrivate: Boolean,
    val description: String?,
    val htmlUrl: String,
    val lastSyncedAt: Long = System.currentTimeMillis()
)
