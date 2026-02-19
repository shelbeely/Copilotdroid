package com.example.agenthq.auth

import com.example.agenthq.data.preferences.HostPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthState {
    object Idle : AuthState()
    data class WaitingForUser(val userCode: String, val verificationUri: String) : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

@Singleton
class AuthRepository @Inject constructor(
    private val tokenStore: TokenStore,
    private val hostPreferences: HostPreferences
) {
    private val clientId: String
        get() = BuildConfigHelper.GITHUB_CLIENT_ID

    private fun buildDeviceAuthService(oauthBase: String): GitHubDeviceAuthService =
        Retrofit.Builder()
            .baseUrl("$oauthBase/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubDeviceAuthService::class.java)

    fun startDeviceFlow(): Flow<AuthState> = flow {
        emit(AuthState.Idle)
        try {
            val host = hostPreferences.githubHost.first()
            val oauthBase = HostPreferences.oauthBaseFor(host)
            val deviceAuthService = buildDeviceAuthService(oauthBase)
            val deviceCodeResp = deviceAuthService.requestDeviceCode(clientId)
            emit(AuthState.WaitingForUser(deviceCodeResp.user_code, deviceCodeResp.verification_uri))
            val interval = (deviceCodeResp.interval * 1000L).coerceAtLeast(5000L)
            val expiresAt = System.currentTimeMillis() + deviceCodeResp.expires_in * 1000L
            while (System.currentTimeMillis() < expiresAt) {
                delay(interval)
                val tokenResp = deviceAuthService.pollAccessToken(clientId, deviceCodeResp.device_code)
                when {
                    tokenResp.access_token != null -> {
                        tokenStore.saveToken(tokenResp.access_token)
                        emit(AuthState.Success)
                        return@flow
                    }
                    tokenResp.error == "slow_down" -> delay(interval)
                    tokenResp.error == "access_denied" -> {
                        emit(AuthState.Error("Access denied by user"))
                        return@flow
                    }
                    tokenResp.error == "expired_token" -> {
                        emit(AuthState.Error("Login timed out"))
                        return@flow
                    }
                }
            }
            emit(AuthState.Error("Login timed out"))
        } catch (e: Exception) {
            emit(AuthState.Error(e.message ?: "Unknown error"))
        }
    }

    fun isLoggedIn(): Boolean = tokenStore.hasToken()
    fun logout() = tokenStore.clearToken()
}
