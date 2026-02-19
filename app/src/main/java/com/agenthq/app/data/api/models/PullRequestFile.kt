package com.agenthq.app.data.api.models

import com.google.gson.annotations.SerializedName

data class PullRequestFile(
    @SerializedName("filename") val filename: String,
    @SerializedName("status") val status: String,
    @SerializedName("additions") val additions: Int,
    @SerializedName("deletions") val deletions: Int,
    @SerializedName("patch") val patch: String?
)
