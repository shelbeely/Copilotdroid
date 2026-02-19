package com.agenthq.app.data.api.models

import com.google.gson.annotations.SerializedName

data class Label(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("color") val color: String,
    @SerializedName("description") val description: String?
)
