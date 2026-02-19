package com.agenthq.app.data.auth

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

data class AccessTokenResponse(
    val access_token: String,
    val token_type: String,
    val scope: String
)

interface GitHubAuthService {
    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("login/oauth/access_token")
    suspend fun exchangeCodeForToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String
    ): AccessTokenResponse
}
