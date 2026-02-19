package com.agenthq.app.data.auth

import com.agenthq.app.BuildConfig

object GitHubOAuthConfig {
    val clientId: String = BuildConfig.GITHUB_CLIENT_ID
    val clientSecret: String = BuildConfig.GITHUB_CLIENT_SECRET
    val redirectUri: String = BuildConfig.GITHUB_REDIRECT_URI

    const val AUTHORIZE_URL = "https://github.com/login/oauth/authorize"
    const val TOKEN_URL = "https://github.com/login/oauth/access_token"
    const val SCOPES = "repo read:user read:org"

    fun buildAuthorizationUrl(): String {
        return "$AUTHORIZE_URL?client_id=$clientId&redirect_uri=$redirectUri&scope=$SCOPES"
    }
}
