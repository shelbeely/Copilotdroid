package com.example.agenthq.auth

import com.example.agenthq.BuildConfig

object BuildConfigHelper {
    // Sentinel value used as the default when GITHUB_CLIENT_ID is not set at build time.
    const val UNCONFIGURED_CLIENT_ID = "NOT_CONFIGURED"

    // GitHub OAuth App Client ID – injected at build time via the GITHUB_CLIENT_ID
    // environment variable.  Register your OAuth App at:
    // https://github.com/settings/developers
    val GITHUB_CLIENT_ID: String = BuildConfig.GITHUB_CLIENT_ID
}
