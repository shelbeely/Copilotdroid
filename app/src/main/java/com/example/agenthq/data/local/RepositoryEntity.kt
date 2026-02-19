package com.example.agenthq.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repositories")
data class RepositoryEntity(
    @PrimaryKey val id: Long,
    val owner: String,
    val name: String,
    val fullName: String,
    val isPrivate: Boolean,
    val description: String,
    val defaultBranch: String,
    val lastSyncedAt: Long
)
