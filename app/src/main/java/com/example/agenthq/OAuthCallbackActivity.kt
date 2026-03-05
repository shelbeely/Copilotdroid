package com.example.agenthq

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Handles the OAuth redirect URI (agenthq://oauth) after GitHub authorization.
 * Passes the authorization code to the main app flow via broadcast.
 */
@AndroidEntryPoint
class OAuthCallbackActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleOAuthCallback(intent)
        finish()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleOAuthCallback(intent)
        finish()
    }

    private fun handleOAuthCallback(intent: Intent) {
        val uri: Uri? = intent.data
        if (uri != null && uri.scheme == "agenthq" && uri.host == "oauth") {
            val code = uri.getQueryParameter("code")
            val state = uri.getQueryParameter("state")
            if (code != null) {
                val broadcastIntent = Intent(ACTION_OAUTH_CALLBACK).apply {
                    putExtra(EXTRA_CODE, code)
                    putExtra(EXTRA_STATE, state)
                    setPackage(packageName)
                }
                sendBroadcast(broadcastIntent)
            }
        }
    }

    companion object {
        const val ACTION_OAUTH_CALLBACK = "com.example.agenthq.OAUTH_CALLBACK"
        const val EXTRA_CODE = "code"
        const val EXTRA_STATE = "state"
    }
}
