package com.agenthq.app.data.api.models

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class GraphQLResponse(
    @SerializedName("data") val data: JsonObject?,
    @SerializedName("errors") val errors: List<GraphQLError>?
)

data class GraphQLError(
    @SerializedName("message") val message: String,
    @SerializedName("type") val type: String?,
    @SerializedName("path") val path: List<String>?
)
