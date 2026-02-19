package com.agenthq.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.agenthq.app.data.local.entities.CachedComment
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {

    @Upsert
    suspend fun insertAll(comments: List<CachedComment>)

    @Query("SELECT * FROM comments WHERE pullRequestId = :pullRequestId ORDER BY createdAt ASC")
    fun getByPullRequest(pullRequestId: Long): Flow<List<CachedComment>>

    @Query("DELETE FROM comments WHERE pullRequestId = :pullRequestId")
    suspend fun deleteByPullRequest(pullRequestId: Long)
}
