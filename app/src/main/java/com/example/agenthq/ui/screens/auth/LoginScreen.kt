package com.example.agenthq.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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

                Spacer(Modifier.height(16.dp))
                PatLoginSection(onSubmit = { token -> viewModel.loginWithPat(token) })
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

/** Collapsible section that lets a developer sign in with a Personal Access Token. */
@Composable
private fun PatLoginSection(onSubmit: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var pat by remember { mutableStateOf("") }
    var patVisible by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    TextButton(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(if (expanded) "Cancel" else "Use Personal Access Token")
    }

    if (expanded) {
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = pat,
            onValueChange = { pat = it },
            label = { Text("Personal Access Token") },
            placeholder = { Text("ghp_…") },
            singleLine = true,
            visualTransformation = if (patVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { patVisible = !patVisible }) {
                    Icon(
                        imageVector = if (patVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (patVisible) "Hide token" else "Show token"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Required scopes: repo (read/write PRs & comments) · read:user (profile)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        TextButton(
            onClick = {
                uriHandler.openUri(
                    "https://github.com/settings/tokens/new" +
                        "?scopes=repo,read:user&description=Agent+HQ"
                )
            },
            contentPadding = PaddingValues(vertical = 0.dp)
        ) {
            Text(
                text = "Generate token (scopes pre-selected)",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = { onSubmit(pat) },
            enabled = pat.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign in with PAT")
        }
    }
}

