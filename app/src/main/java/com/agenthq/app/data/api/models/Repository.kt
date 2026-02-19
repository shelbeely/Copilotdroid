package com.agenthq.app.data.api.models

import com.google.gson.annotations.SerializedName

data class Repository(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("owner") val owner: GitHubUser,
    @SerializedName("private") val isPrivate: Boolean,
    @SerializedName("description") val description: String?,
    @SerializedName("html_url") val htmlUrl: String
)
