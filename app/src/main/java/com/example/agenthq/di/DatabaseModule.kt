package com.example.agenthq.di

import android.content.Context
import androidx.room.Room
import com.example.agenthq.data.local.AgentHQDatabase
import com.example.agenthq.data.local.PullRequestDao
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
        ).build()

    @Provides
    fun providePullRequestDao(db: AgentHQDatabase): PullRequestDao = db.pullRequestDao()
}
