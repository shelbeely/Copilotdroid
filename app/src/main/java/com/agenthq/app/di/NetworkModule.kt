package com.agenthq.app.di

import com.agenthq.app.data.api.AuthInterceptor
import com.agenthq.app.data.api.GitHubGraphQLService
import com.agenthq.app.data.api.GitHubRestService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("github_api")
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGitHubRestService(
        @Named("github_api") retrofit: Retrofit
    ): GitHubRestService {
        return retrofit.create(GitHubRestService::class.java)
    }

    @Provides
    @Singleton
    fun provideGitHubGraphQLService(
        @Named("github_api") retrofit: Retrofit
    ): GitHubGraphQLService {
        return retrofit.create(GitHubGraphQLService::class.java)
    }
}
