package com.agenthq.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.agenthq.app.data.local.dao.CommentDao
import com.agenthq.app.data.local.dao.PullRequestDao
import com.agenthq.app.data.local.dao.RepositoryDao
import com.agenthq.app.data.local.entities.CachedComment
import com.agenthq.app.data.local.entities.CachedPullRequest
import com.agenthq.app.data.local.entities.CachedRepository

@Database(
    entities = [
        CachedRepository::class,
        CachedPullRequest::class,
        CachedComment::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AgentHQDatabase : RoomDatabase() {
    abstract fun repositoryDao(): RepositoryDao
    abstract fun pullRequestDao(): PullRequestDao
    abstract fun commentDao(): CommentDao
}
