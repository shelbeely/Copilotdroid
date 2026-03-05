package com.example.agenthq.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.agenthq.auth.AuthState

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.authState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(state) {
        if (state is AuthState.Success) onLoginSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Agent HQ", style = MaterialTheme.typography.displaySmall)
        Spacer(Modifier.height(8.dp))
        Text(
            "Manage GitHub Copilot Agent sessions",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(48.dp))

        when (val s = state) {
            is AuthState.Idle -> {
                Button(
                    onClick = { viewModel.startLogin() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign in with GitHub")
                }
            }
            is AuthState.WaitingForUser -> {
                Text(
                    "Open this URL on your device or browser:",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { uriHandler.openUri(s.verificationUri) }) {
                    Text(s.verificationUri)
                }
                Spacer(Modifier.height(16.dp))
                Text("Enter code:", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                Card {
                    Text(
                        text = s.userCode,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                        style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.Monospace)
                    )
                }
                Spacer(Modifier.height(24.dp))
                CircularProgressIndicator()
                Spacer(Modifier.height(8.dp))
                Text("Waiting for authorization…", style = MaterialTheme.typography.bodySmall)
            }
            is AuthState.Error -> {
                Text(
                    "Error: ${s.message}",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.startLogin() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Try Again")
                }
            }
            is AuthState.Success -> {
                CircularProgressIndicator()
            }
        }
    }
}
