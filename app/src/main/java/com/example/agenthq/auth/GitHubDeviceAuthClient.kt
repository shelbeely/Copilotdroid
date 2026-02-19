package com.example.agenthq.auth

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface GitHubDeviceAuthService {
    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("login/device/code")
    suspend fun requestDeviceCode(
        @Field("client_id") clientId: String,
        @Field("scope") scope: String = "repo read:user"
    ): DeviceCodeResponse

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("login/oauth/access_token")
    suspend fun pollAccessToken(
        @Field("client_id") clientId: String,
        @Field("device_code") deviceCode: String,
        @Field("grant_type") grantType: String = "urn:ietf:params:oauth:grant-type:device_code"
    ): AccessTokenResponse
}

data class DeviceCodeResponse(
    val device_code: String = "",
    val user_code: String = "",
    val verification_uri: String = "",
    val expires_in: Int = 900,
    val interval: Int = 5
)

data class AccessTokenResponse(
    val access_token: String? = null,
    val error: String? = null,
    val error_description: String? = null
)
