package com.example.agenthq.data.remote.rest

import com.google.gson.annotations.SerializedName

data class GitHubUserDto(
    val id: Long,
    val login: String,
    val name: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    val email: String?
)

data class GitHubRepositoryDto(
    val id: Long,
    val name: String,
    @SerializedName("full_name") val fullName: String,
    val description: String?,
    val private: Boolean,
    val owner: GitHubOwnerDto,
    @SerializedName("default_branch") val defaultBranch: String
)

data class GitHubOwnerDto(
    val id: Long,
    val login: String,
    @SerializedName("avatar_url") val avatarUrl: String?
)

data class GitHubPullRequestDto(
    val id: Long,
    val number: Int,
    val title: String,
    val body: String?,
    val state: String,
    val draft: Boolean,
    val user: GitHubOwnerDto,
    val head: GitHubRefDto,
    val base: GitHubRefDto,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("merged_at") val mergedAt: String?,
    val labels: List<GitHubLabelDto>
)

data class GitHubRefDto(
    val ref: String,
    val repo: GitHubRepositoryDto?
)

data class GitHubLabelDto(
    val id: Long,
    val name: String,
    val color: String,
    val description: String?
)
