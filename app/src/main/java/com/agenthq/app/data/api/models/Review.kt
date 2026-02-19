package com.agenthq.app.data.api.models

import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("id") val id: Long,
    @SerializedName("user") val user: GitHubUser,
    @SerializedName("body") val body: String?,
    @SerializedName("state") val state: String,
    @SerializedName("submitted_at") val submittedAt: String?
)
