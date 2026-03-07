package com.example.agenthq.domain.usecase

import com.example.agenthq.auth.TokenStore
import com.example.agenthq.data.local.RepositoryDao
import com.example.agenthq.data.local.RepositoryEntity
import com.example.agenthq.data.remote.rest.GitHubApiService
import com.example.agenthq.util.Logger
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fetches the authenticated user's repositories from GitHub and saves them to the local database.
 * All fetched repos become available for PR syncing via [SyncSessionsUseCase].
 */
@Singleton
class SyncRepositoriesUseCase @Inject constructor(
    private val api: GitHubApiService,
    private val repositoryDao: RepositoryDao,
    private val tokenStore: TokenStore
) {
    suspend operator fun invoke(): Result<Int> = runCatching {
        val token = tokenStore.getToken() ?: return@runCatching 0
        var page = 1
        var totalSaved = 0
        while (true) {
            val dtos = api.getUserRepositories(
                token = "Bearer $token",
                perPage = 100,
                page = page
            )
            if (dtos.isEmpty()) break
            val entities = dtos.map { dto ->
                RepositoryEntity(
                    id = dto.id,
                    owner = dto.owner.login,
                    name = dto.name,
                    fullName = dto.fullName,
                    isPrivate = dto.private,
                    description = dto.description ?: "",
                    defaultBranch = dto.defaultBranch,
                    lastSyncedAt = System.currentTimeMillis()
                )
            }
            repositoryDao.upsertAll(entities)
            totalSaved += entities.size
            if (dtos.size < 100) break
            page++
        }
        totalSaved
    }.onFailure { e ->
        Logger.e("Failed to sync repositories", e)
    }
}
