package com.agenthq.app.data.repository

import com.agenthq.app.data.api.GitHubRestService
import com.agenthq.app.data.api.models.GitHubUser
import com.agenthq.app.data.local.dao.CommentDao
import com.agenthq.app.data.local.dao.PullRequestDao
import com.agenthq.app.data.local.dao.RepositoryDao
import com.agenthq.app.data.local.entities.CachedComment
import com.agenthq.app.data.local.entities.CachedPullRequest
import com.agenthq.app.data.local.entities.CachedRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitHubRepository @Inject constructor(
    private val api: GitHubRestService,
    private val repositoryDao: RepositoryDao,
    private val pullRequestDao: PullRequestDao,
    private val commentDao: CommentDao
) {

    fun getCopilotSessions(): Flow<List<CachedPullRequest>> =
        pullRequestDao.getCopilotSessions()

    fun getRepositories(): Flow<List<CachedRepository>> =
        repositoryDao.getAll()

    suspend fun getPullRequestById(id: Long): CachedPullRequest? =
        pullRequestDao.getById(id)

    fun getComments(pullRequestId: Long): Flow<List<CachedComment>> =
        commentDao.getByPullRequest(pullRequestId)

    suspend fun getAuthenticatedUser(): Result<GitHubUser> = runCatching {
        val response = api.getAuthenticatedUser()
        response.body() ?: throw Exception("Failed to fetch user: ${response.code()}")
    }

    suspend fun syncRepositories() {
        val response = api.listUserRepos(page = 1, perPage = 100)
        val repos = response.body() ?: return
        repositoryDao.insertAll(repos.map { repo ->
            CachedRepository(
                id = repo.id,
                name = repo.name,
                fullName = repo.fullName,
                ownerLogin = repo.owner.login,
                ownerAvatarUrl = repo.owner.avatarUrl ?: "",
                isPrivate = repo.isPrivate,
                description = repo.description,
                htmlUrl = repo.htmlUrl
            )
        })
    }

    suspend fun syncAllPullRequests() {
        val repos = api.listUserRepos(page = 1, perPage = 100).body() ?: return
        for (repo in repos) {
            syncPullRequestsForRepo(repo.owner.login, repo.name, repo.fullName)
        }
    }

    private suspend fun syncPullRequestsForRepo(
        owner: String,
        repo: String,
        fullName: String
    ) {
        val states = listOf("open", "closed")
        for (state in states) {
            val response = api.listPullRequests(owner, repo, state)
            val prs = response.body() ?: continue
            pullRequestDao.insertAll(prs.map { pr ->
                val isCopilot = pr.user.login.contains("[bot]") ||
                    pr.user.login == "copilot" ||
                    pr.labels.any { it.name.contains("copilot", ignoreCase = true) }
                CachedPullRequest(
                    id = pr.id,
                    number = pr.number,
                    repoFullName = fullName,
                    title = pr.title,
                    body = pr.body,
                    state = if (pr.mergedAt != null) "merged" else pr.state,
                    htmlUrl = pr.htmlUrl,
                    authorLogin = pr.user.login,
                    authorAvatarUrl = pr.user.avatarUrl ?: "",
                    createdAt = pr.createdAt,
                    updatedAt = pr.updatedAt,
                    mergedAt = pr.mergedAt,
                    headRef = pr.head.ref,
                    baseRef = pr.base.ref,
                    isDraft = pr.draft,
                    isCopilotSession = isCopilot
                )
            })
        }
    }

    suspend fun syncComments(pullRequestId: Long) {
        val pr = pullRequestDao.getById(pullRequestId) ?: return
        val parts = pr.repoFullName.split("/")
        if (parts.size != 2) return
        val response = api.getIssueComments(parts[0], parts[1], pr.number)
        val comments = response.body() ?: return
        commentDao.deleteByPullRequest(pullRequestId)
        commentDao.insertAll(comments.map { comment ->
            CachedComment(
                id = comment.id,
                pullRequestId = pullRequestId,
                authorLogin = comment.user.login,
                authorAvatarUrl = comment.user.avatarUrl ?: "",
                body = comment.body,
                createdAt = comment.createdAt,
                updatedAt = comment.updatedAt
            )
        })
    }
}
