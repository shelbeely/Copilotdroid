package com.example.agenthq.data.repository

import com.example.agenthq.data.local.PullRequestDao
import com.example.agenthq.data.local.PullRequestEntity
import com.example.agenthq.data.remote.rest.GitHubApiService
import com.example.agenthq.domain.model.PullRequest
import com.example.agenthq.domain.model.PullRequestState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PullRequestRepository @Inject constructor(
    private val api: GitHubApiService,
    private val dao: PullRequestDao
) {
    fun observePullRequests(owner: String, repo: String): Flow<List<PullRequest>> =
        dao.observePullRequests(owner, repo).map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun refreshPullRequests(token: String, owner: String, repo: String) {
        val dtos = api.getPullRequests(
            token = "Bearer $token",
            owner = owner,
            repo = repo
        )
        val entities = dtos.map { dto ->
            PullRequestEntity(
                id = dto.id,
                number = dto.number,
                title = dto.title,
                body = dto.body,
                state = dto.state.uppercase(),
                owner = owner,
                repo = repo,
                headBranch = dto.head.ref,
                baseBranch = dto.base.ref,
                authorLogin = dto.user.login,
                authorAvatarUrl = dto.user.avatarUrl,
                createdAt = dto.createdAt,
                updatedAt = dto.updatedAt,
                isDraft = dto.draft,
                hasAgentAssigned = dto.labels.any { it.name.contains("copilot", ignoreCase = true) }
            )
        }
        dao.upsertAll(entities)
    }

    private fun PullRequestEntity.toDomain() = PullRequest(
        id = id,
        number = number,
        title = title,
        body = body,
        state = when (state) {
            "OPEN" -> PullRequestState.OPEN
            "CLOSED" -> PullRequestState.CLOSED
            "MERGED" -> PullRequestState.MERGED
            else -> PullRequestState.OPEN
        },
        owner = owner,
        repo = repo,
        headBranch = headBranch,
        baseBranch = baseBranch,
        authorLogin = authorLogin,
        authorAvatarUrl = authorAvatarUrl,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isDraft = isDraft,
        hasAgentAssigned = hasAgentAssigned
    )
}
