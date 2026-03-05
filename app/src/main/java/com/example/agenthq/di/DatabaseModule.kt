package com.example.agenthq.di

import android.content.Context
import androidx.room.Room
import com.example.agenthq.data.local.AgentHQDatabase
import com.example.agenthq.data.local.AgentSessionDao
import com.example.agenthq.data.local.CommentDao
import com.example.agenthq.data.local.PullRequestDao
import com.example.agenthq.data.local.RepositoryDao
import com.example.agenthq.data.local.ReviewDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AgentHQDatabase =
        Room.databaseBuilder(
            context,
            AgentHQDatabase::class.java,
            "agent_hq.db"
        )
            // TODO: Replace with addMigrations() before the first production release.
            // fallbackToDestructiveMigration is acceptable while the app has no shipped users,
            // but must be removed once users have production data to preserve.
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun providePullRequestDao(db: AgentHQDatabase): PullRequestDao = db.pullRequestDao()

    @Provides
    fun provideAgentSessionDao(db: AgentHQDatabase): AgentSessionDao = db.agentSessionDao()

    @Provides
    fun provideReviewDao(db: AgentHQDatabase): ReviewDao = db.reviewDao()

    @Provides
    fun provideCommentDao(db: AgentHQDatabase): CommentDao = db.commentDao()

    @Provides
    fun provideRepositoryDao(db: AgentHQDatabase): RepositoryDao = db.repositoryDao()
}
