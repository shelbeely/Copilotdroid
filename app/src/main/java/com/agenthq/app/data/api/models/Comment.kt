package com.agenthq.app.data.api.models

import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("id") val id: Long,
    @SerializedName("user") val user: GitHubUser,
    @SerializedName("body") val body: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)
