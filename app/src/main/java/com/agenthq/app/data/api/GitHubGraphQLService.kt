package com.agenthq.app.data.api

import com.agenthq.app.data.api.models.GraphQLRequest
import com.agenthq.app.data.api.models.GraphQLResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GitHubGraphQLService {

    @POST("graphql")
    suspend fun execute(
        @Body request: GraphQLRequest
    ): Response<GraphQLResponse>
}
