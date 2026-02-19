package com.example.agenthq.data.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.exception.ApolloException
import com.example.agenthq.data.local.PullRequestDao
import com.example.agenthq.data.local.PullRequestEntity
import com.example.agenthq.data.remote.rest.CreateCommentRequest
import com.example.agenthq.data.remote.rest.CreateReviewRequest
import com.example.agenthq.data.remote.rest.GitHubApiService
import com.example.agenthq.data.remote.rest.IssueCommentDto
import com.example.agenthq.data.remote.rest.ReviewCommentDto
import com.example.agenthq.data.remote.rest.ReviewDto
import com.example.agenthq.domain.model.PullRequest
import com.example.agenthq.domain.model.PullRequestState
import com.example.agenthq.graphql.GetPullRequestsQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PullRequestRepository @Inject constructor(
    private val api: GitHubApiService,
    private val apolloClient: ApolloClient,
    private val dao: PullRequestDao
) {
    fun observePullRequests(owner: String, repo: String): Flow<List<PullRequest>> =
        dao.getAllForRepo(owner, repo).map { entities ->
            entities.map { it.toDomain() }
        }

    /** Refresh PRs via GraphQL (preferred) with REST fallback. */
    suspend fun refreshPullRequests(token: String, owner: String, repo: String) {
        val graphqlSuccess = tryRefreshViaGraphQL(owner, repo)
        if (!graphqlSuccess) {
            refreshPullRequestsViaRest(token, owner, repo)
        }
    }

    private suspend fun tryRefreshViaGraphQL(owner: String, repo: String): Boolean {
        return try {
            val response = apolloClient.query(
                GetPullRequestsQuery(
                    owner = owner,
                    repo = repo,
                    first = 50,
                    after = Optional.absent()
                )
            ).execute()

            val nodes = response.data?.repository?.pullRequests?.nodes
                ?: return false

            val entities = nodes.filterNotNull().map { node ->
                val isAgentPr = node.labels?.nodes
                    ?.filterNotNull()
                    ?.any { it.name.contains("copilot", ignoreCase = true) } == true
                PullRequestEntity(
                    id = node.number.toLong(),
                    number = node.number,
                    title = node.title,
                    body = "",
                    state = node.state.rawValue,
                    repoOwner = owner,
                    repoName = repo,
                    htmlUrl = "",
                    headRef = node.headRefName,
                    baseRef = node.baseRefName,
                    authorLogin = node.author?.login ?: "",
                    authorAvatarUrl = node.author?.avatarUrl?.toString() ?: "",
                    createdAt = node.createdAt.toString(),
                    updatedAt = node.updatedAt.toString(),
                    isDraft = false,
                    mergedAt = null,
                    labels = "[]",
                    isAgentPr = isAgentPr,
                    lastSyncedAt = System.currentTimeMillis()
                )
            }
            dao.upsertAll(entities)
            true
        } catch (e: ApolloException) {
            false
        }
    }

    private suspend fun refreshPullRequestsViaRest(token: String, owner: String, repo: String) {
        val dtos = api.getPullRequests(
            token = "Bearer $token",
            owner = owner,
            repo = repo,
            state = "all"
        )
        val entities = dtos.map { dto ->
            val isAgentPr = dto.labels.any { it.name.contains("copilot", ignoreCase = true) }
            PullRequestEntity(
                id = dto.id,
                number = dto.number,
                title = dto.title,
                body = dto.body ?: "",
                state = dto.state.uppercase(),
                repoOwner = owner,
                repoName = repo,
                htmlUrl = "",
                headRef = dto.head.ref,
                baseRef = dto.base.ref,
                authorLogin = dto.user.login,
                authorAvatarUrl = dto.user.avatarUrl ?: "",
                createdAt = dto.createdAt,
                updatedAt = dto.updatedAt,
                isDraft = dto.draft,
                mergedAt = null,
                labels = "[]",
                isAgentPr = isAgentPr,
                lastSyncedAt = System.currentTimeMillis()
            )
        }
        dao.upsertAll(entities)
    }

    suspend fun getReviews(token: String, owner: String, repo: String, pullNumber: Int): Result<List<ReviewDto>> =
        runCatching {
            api.getPullRequestReviews("Bearer $token", owner, repo, pullNumber)
        }

    suspend fun getReviewComments(token: String, owner: String, repo: String, pullNumber: Int): Result<List<ReviewCommentDto>> =
        runCatching {
            api.getPullRequestComments("Bearer $token", owner, repo, pullNumber)
        }

    suspend fun createReview(
        token: String,
        owner: String,
        repo: String,
        pullNumber: Int,
        review: CreateReviewRequest
    ): Result<ReviewDto> =
        runCatching {
            api.createReview("Bearer $token", owner, repo, pullNumber, review)
        }

    suspend fun createSteeringComment(
        token: String,
        owner: String,
        repo: String,
        issueNumber: Int,
        body: String
    ): Result<IssueCommentDto> =
        runCatching {
            api.createIssueComment("Bearer $token", owner, repo, issueNumber, CreateCommentRequest(body))
        }

    private fun PullRequestEntity.toDomain() = PullRequest(
        id = id,
        number = number,
        title = title,
        body = body.ifEmpty { null },
        state = when (state.uppercase()) {
            "OPEN" -> PullRequestState.OPEN
            "CLOSED" -> PullRequestState.CLOSED
            "MERGED" -> PullRequestState.MERGED
            else -> PullRequestState.OPEN
        },
        owner = repoOwner,
        repo = repoName,
        headBranch = headRef,
        baseBranch = baseRef,
        authorLogin = authorLogin,
        authorAvatarUrl = authorAvatarUrl.ifEmpty { null },
        createdAt = createdAt,
        updatedAt = updatedAt,
        isDraft = isDraft,
        hasAgentAssigned = isAgentPr
    )
}
