package com.agenthq.app.di

import android.content.Context
import androidx.room.Room
import com.agenthq.app.data.local.AgentHQDatabase
import com.agenthq.app.data.local.dao.CommentDao
import com.agenthq.app.data.local.dao.PullRequestDao
import com.agenthq.app.data.local.dao.RepositoryDao
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
    fun provideDatabase(@ApplicationContext context: Context): AgentHQDatabase {
        return Room.databaseBuilder(
            context,
            AgentHQDatabase::class.java,
            "agenthq_database"
        ).build()
    }

    @Provides
    fun provideRepositoryDao(database: AgentHQDatabase): RepositoryDao {
        return database.repositoryDao()
    }

    @Provides
    fun providePullRequestDao(database: AgentHQDatabase): PullRequestDao {
        return database.pullRequestDao()
    }

    @Provides
    fun provideCommentDao(database: AgentHQDatabase): CommentDao {
        return database.commentDao()
    }
}
