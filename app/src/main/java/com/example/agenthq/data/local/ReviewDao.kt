package com.example.agenthq.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(review: ReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(reviews: List<ReviewEntity>)

    @Query("SELECT * FROM reviews WHERE repoOwner = :owner AND repoName = :name AND prNumber = :prNumber ORDER BY submittedAt DESC")
    fun getForPr(owner: String, name: String, prNumber: Int): Flow<List<ReviewEntity>>

    @Query("DELETE FROM reviews WHERE repoOwner = :owner AND repoName = :name AND prNumber = :prNumber")
    suspend fun deleteForPr(owner: String, name: String, prNumber: Int)
}
