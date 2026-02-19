package com.agenthq.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.agenthq.app.data.local.entities.CachedPullRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface PullRequestDao {

    @Upsert
    suspend fun insertAll(pullRequests: List<CachedPullRequest>)

    @Query("SELECT * FROM pull_requests WHERE repoFullName = :repoFullName ORDER BY updatedAt DESC")
    fun getByRepo(repoFullName: String): Flow<List<CachedPullRequest>>

    @Query("SELECT * FROM pull_requests WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): CachedPullRequest?

    @Query("SELECT * FROM pull_requests WHERE isCopilotSession = 1 ORDER BY updatedAt DESC")
    fun getCopilotSessions(): Flow<List<CachedPullRequest>>

    @Query("DELETE FROM pull_requests WHERE repoFullName = :repoFullName")
    suspend fun deleteByRepo(repoFullName: String)

    @Query("SELECT COUNT(*) FROM pull_requests")
    suspend fun getCount(): Int
}
