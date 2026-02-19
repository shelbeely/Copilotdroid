package com.agenthq.app.di

import com.agenthq.app.data.session.SessionInferenceEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SessionModule {

    @Provides
    @Singleton
    fun provideSessionInferenceEngine(): SessionInferenceEngine {
        return SessionInferenceEngine()
    }
}
