package com.example.agenthq.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AgentSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(session: AgentSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(sessions: List<AgentSessionEntity>)

    @Query("SELECT * FROM agent_sessions ORDER BY lastActivityAt DESC")
    fun getAll(): Flow<List<AgentSessionEntity>>

    @Query("SELECT * FROM agent_sessions WHERE id = :id")
    suspend fun getById(id: Long): AgentSessionEntity?

    @Query("SELECT * FROM agent_sessions WHERE pullRequestId = :prId LIMIT 1")
    suspend fun getByPrId(prId: Long): AgentSessionEntity?

    @Query("SELECT * FROM agent_sessions WHERE status IN ('active', 'paused') ORDER BY lastActivityAt DESC")
    fun getActiveSessions(): Flow<List<AgentSessionEntity>>

    @Query("DELETE FROM agent_sessions")
    suspend fun deleteAll()

    @Query("DELETE FROM agent_sessions WHERE pullRequestId = :prId")
    suspend fun deleteByPrId(prId: Long)
}
