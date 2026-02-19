package com.agenthq.app.data.api.models

import com.google.gson.annotations.SerializedName

data class GitHubUser(
    @SerializedName("login") val login: String,
    @SerializedName("id") val id: Long,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?
)
