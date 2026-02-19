package com.agenthq.app.data.api.models

data class GraphQLRequest(
    val query: String,
    val variables: Map<String, Any>? = null
)
