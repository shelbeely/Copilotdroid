package com.example.agenthq.data.remote.rest

import com.google.gson.annotations.SerializedName

data class UserDto(
    val id: Long,
    val login: String,
    val name: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    val email: String?
)

data class RepoDto(
    val id: Long,
    val name: String,
    @SerializedName("full_name") val fullName: String,
    val description: String?,
    val private: Boolean,
    val owner: OwnerDto,
    @SerializedName("default_branch") val defaultBranch: String
)

data class OwnerDto(
    val id: Long,
    val login: String,
    @SerializedName("avatar_url") val avatarUrl: String?
)

data class PullRequestDto(
    val id: Long,
    val number: Int,
    val title: String,
    val body: String?,
    val state: String,
    val draft: Boolean,
    val user: OwnerDto,
    val head: RefDto,
    val base: RefDto,
    @SerializedName("html_url") val htmlUrl: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("merged_at") val mergedAt: String?,
    val labels: List<LabelDto>,
    @SerializedName("requested_reviewers") val requestedReviewers: List<OwnerDto> = emptyList()
)

data class RefDto(
    val ref: String,
    val repo: RepoDto?
)

data class LabelDto(
    val id: Long,
    val name: String,
    val color: String,
    val description: String?
)

data class ReviewDto(
    val id: Long,
    val user: OwnerDto,
    val state: String,
    val body: String?,
    @SerializedName("submitted_at") val submittedAt: String?
)

data class ReviewCommentDto(
    val id: Long,
    val user: OwnerDto,
    val body: String,
    val path: String,
    val position: Int?,
    @SerializedName("diff_hunk") val diffHunk: String,
    @SerializedName("created_at") val createdAt: String
)

data class CommitDto(
    val sha: String,
    val commit: CommitDetailDto
)

data class CommitDetailDto(
    val message: String,
    val author: GitAuthorDto?
)

data class GitAuthorDto(
    val name: String?,
    val email: String?,
    val date: String?
)

data class IssueCommentDto(
    val id: Long,
    val user: OwnerDto,
    val body: String,
    @SerializedName("created_at") val createdAt: String
)

data class CreateReviewRequest(
    val body: String,
    val event: String,
    val comments: List<ReviewCommentRequest> = emptyList()
)

data class ReviewCommentRequest(
    val path: String,
    val position: Int,
    val body: String
)

data class CreateCommentRequest(
    val body: String
)

// Keep old type aliases for backward compatibility
typealias GitHubUserDto = UserDto
typealias GitHubRepositoryDto = RepoDto
typealias GitHubOwnerDto = OwnerDto
typealias GitHubPullRequestDto = PullRequestDto
typealias GitHubRefDto = RefDto
typealias GitHubLabelDto = LabelDto
