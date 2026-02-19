package com.agenthq.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.agenthq.app.data.local.entities.CachedRepository
import kotlinx.coroutines.flow.Flow

@Dao
interface RepositoryDao {

    @Upsert
    suspend fun insertAll(repositories: List<CachedRepository>)

    @Query("SELECT * FROM repositories ORDER BY fullName ASC")
    fun getAll(): Flow<List<CachedRepository>>

    @Query("SELECT * FROM repositories WHERE fullName = :fullName LIMIT 1")
    suspend fun getByFullName(fullName: String): CachedRepository?

    @Query("DELETE FROM repositories")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM repositories")
    suspend fun getCount(): Int
}
