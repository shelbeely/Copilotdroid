package com.example.agenthq.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        PullRequestEntity::class,
        AgentSessionEntity::class,
        ReviewEntity::class,
        CommentEntity::class,
        RepositoryEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AgentHQDatabase : RoomDatabase() {
    abstract fun pullRequestDao(): PullRequestDao
    abstract fun agentSessionDao(): AgentSessionDao
    abstract fun reviewDao(): ReviewDao
    abstract fun commentDao(): CommentDao
    abstract fun repositoryDao(): RepositoryDao
}
