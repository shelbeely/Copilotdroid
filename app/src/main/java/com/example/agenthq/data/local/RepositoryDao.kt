package com.example.agenthq.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RepositoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(repo: RepositoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(repos: List<RepositoryEntity>)

    @Query("SELECT * FROM repositories ORDER BY fullName ASC")
    fun getAll(): Flow<List<RepositoryEntity>>

    @Query("SELECT * FROM repositories WHERE fullName = :fullName LIMIT 1")
    suspend fun getByFullName(fullName: String): RepositoryEntity?

    @Query("DELETE FROM repositories")
    suspend fun deleteAll()
}
