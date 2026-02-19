package com.agenthq.app.data.api

import com.agenthq.app.data.api.models.Comment
import com.agenthq.app.data.api.models.GitHubUser
import com.agenthq.app.data.api.models.PullRequest
import com.agenthq.app.data.api.models.PullRequestFile
import com.agenthq.app.data.api.models.Repository
import com.agenthq.app.data.api.models.Review
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET

import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubRestService {

    @GET("user")
    suspend fun getAuthenticatedUser(): Response<GitHubUser>

    @GET("user/repos")
    suspend fun listUserRepos(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): Response<List<Repository>>

    @GET("repos/{owner}/{repo}/pulls")
    suspend fun listPullRequests(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("state") state: String = "open"
    ): Response<List<PullRequest>>

    @GET("repos/{owner}/{repo}/pulls/{pull_number}")
    suspend fun getPullRequest(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("pull_number") pullNumber: Int
    ): Response<PullRequest>

    @GET("repos/{owner}/{repo}/pulls/{pull_number}/files")
    suspend fun getPullRequestFiles(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("pull_number") pullNumber: Int
    ): Response<List<PullRequestFile>>

    @GET("repos/{owner}/{repo}/pulls/{pull_number}/reviews")
    suspend fun getPullRequestReviews(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("pull_number") pullNumber: Int
    ): Response<List<Review>>

    @GET("repos/{owner}/{repo}/issues/{issue_number}/comments")
    suspend fun getIssueComments(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("issue_number") issueNumber: Int
    ): Response<List<Comment>>

    @POST("repos/{owner}/{repo}/issues/{issue_number}/comments")
    suspend fun createComment(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("issue_number") issueNumber: Int,
        @Body body: Map<String, String>
    ): Response<Comment>
}
