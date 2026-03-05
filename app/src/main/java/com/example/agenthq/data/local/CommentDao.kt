package com.example.agenthq.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(comments: List<CommentEntity>)

    @Query("SELECT * FROM comments WHERE repoOwner = :owner AND repoName = :name AND prNumber = :prNumber ORDER BY createdAt ASC")
    fun getForPr(owner: String, name: String, prNumber: Int): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments WHERE repoOwner = :owner AND repoName = :name AND prNumber = :prNumber AND isSteeringComment = 1 ORDER BY createdAt ASC")
    fun getSteeringComments(owner: String, name: String, prNumber: Int): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments ORDER BY createdAt ASC")
    fun getAll(): Flow<List<CommentEntity>>

    @Query("DELETE FROM comments WHERE repoOwner = :owner AND repoName = :name AND prNumber = :prNumber")
    suspend fun deleteForPr(owner: String, name: String, prNumber: Int)
}
