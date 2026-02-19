package com.example.agenthq.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PullRequestDao {

    @Query("SELECT * FROM pull_requests WHERE owner = :owner AND repo = :repo ORDER BY updatedAt DESC")
    fun observePullRequests(owner: String, repo: String): Flow<List<PullRequestEntity>>

    @Query("SELECT * FROM pull_requests WHERE id = :id")
    suspend fun getPullRequestById(id: Long): PullRequestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(pullRequests: List<PullRequestEntity>)

    @Query("DELETE FROM pull_requests WHERE owner = :owner AND repo = :repo")
    suspend fun deleteByRepo(owner: String, repo: String)
}
