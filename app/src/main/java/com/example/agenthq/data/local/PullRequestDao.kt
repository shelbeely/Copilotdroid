package com.example.agenthq.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PullRequestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(pr: PullRequestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(prs: List<PullRequestEntity>)

    @Query("SELECT * FROM pull_requests ORDER BY updatedAt DESC")
    fun getAll(): Flow<List<PullRequestEntity>>

    @Query("SELECT * FROM pull_requests WHERE repoOwner = :owner AND repoName = :name ORDER BY updatedAt DESC")
    fun getAllForRepo(owner: String, name: String): Flow<List<PullRequestEntity>>

    @Query("SELECT * FROM pull_requests WHERE id = :id")
    suspend fun getById(id: Long): PullRequestEntity?

    @Query("SELECT * FROM pull_requests WHERE repoOwner = :owner AND repoName = :name AND number = :number LIMIT 1")
    suspend fun getByNumber(owner: String, name: String, number: Int): PullRequestEntity?

    @Query("SELECT * FROM pull_requests WHERE isAgentPr = 1 ORDER BY updatedAt DESC")
    fun getAgentPrs(): Flow<List<PullRequestEntity>>

    @Query("DELETE FROM pull_requests WHERE lastSyncedAt < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)

    @Query("SELECT COUNT(*) FROM pull_requests")
    suspend fun count(): Int
}
