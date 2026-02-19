package com.example.agenthq.domain.usecase

import com.example.agenthq.auth.TokenStore
import com.example.agenthq.data.local.RepositoryDao
import com.example.agenthq.data.repository.PullRequestRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fetches PRs for all watched repos, runs session inference, and persists the results.
 */
@Singleton
class SyncSessionsUseCase @Inject constructor(
    private val pullRequestRepository: PullRequestRepository,
    private val repositoryDao: RepositoryDao,
    private val tokenStore: TokenStore
) {
    suspend operator fun invoke() {
        val token = tokenStore.getToken() ?: return
        val repos = repositoryDao.getAll().first()
        for (repo in repos) {
            runCatching {
                pullRequestRepository.syncRepo(token, repo.owner, repo.name)
            }
        }
    }
}
