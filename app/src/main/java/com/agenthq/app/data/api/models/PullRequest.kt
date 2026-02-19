package com.agenthq.app.data.api.models

import com.google.gson.annotations.SerializedName

data class PullRequest(
    @SerializedName("id") val id: Long,
    @SerializedName("number") val number: Int,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String?,
    @SerializedName("state") val state: String,
    @SerializedName("html_url") val htmlUrl: String,
    @SerializedName("user") val user: GitHubUser,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("merged_at") val mergedAt: String?,
    @SerializedName("head") val head: BranchRef,
    @SerializedName("base") val base: BranchRef,
    @SerializedName("draft") val draft: Boolean,
    @SerializedName("labels") val labels: List<Label>
)

data class BranchRef(
    @SerializedName("label") val label: String,
    @SerializedName("ref") val ref: String,
    @SerializedName("sha") val sha: String
)
