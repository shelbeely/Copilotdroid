package com.example.agenthq.data.remote.rest

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/** GitHub REST API v3 endpoints used by Agent HQ. */
interface GitHubApiService {

    @GET("user")
    suspend fun getAuthenticatedUser(
        @Header("Authorization") token: String
    ): UserDto

    @GET("user/repos")
    suspend fun getUserRepositories(
        @Header("Authorization") token: String,
        @Query("sort") sort: String = "updated",
        @Query("per_page") perPage: Int = 50,
        @Query("page") page: Int = 1
    ): List<RepoDto>

    @GET("repos/{owner}/{repo}/pulls")
    suspend fun getPullRequests(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("state") state: String = "all",
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): List<PullRequestDto>

    @GET("repos/{owner}/{repo}/pulls/{pull_number}")
    suspend fun getPullRequest(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("pull_number") pullNumber: Int
    ): PullRequestDto

    @GET("repos/{owner}/{repo}/pulls/{pull_number}/reviews")
    suspend fun getPullRequestReviews(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("pull_number") pullNumber: Int
    ): List<ReviewDto>

    @GET("repos/{owner}/{repo}/pulls/{pull_number}/comments")
    suspend fun getPullRequestComments(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("pull_number") pullNumber: Int
    ): List<ReviewCommentDto>

    @GET("repos/{owner}/{repo}/pulls/{pull_number}/commits")
    suspend fun getPullRequestCommits(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("pull_number") pullNumber: Int
    ): List<CommitDto>

    @POST("repos/{owner}/{repo}/pulls/{pull_number}/reviews")
    suspend fun createReview(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("pull_number") pullNumber: Int,
        @Body review: CreateReviewRequest
    ): ReviewDto

    @POST("repos/{owner}/{repo}/issues/{issue_number}/comments")
    suspend fun createIssueComment(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("issue_number") issueNumber: Int,
        @Body comment: CreateCommentRequest
    ): IssueCommentDto
}
