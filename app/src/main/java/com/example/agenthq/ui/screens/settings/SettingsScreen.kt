package com.example.agenthq.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.agenthq.auth.AuthRepository
import com.example.agenthq.data.preferences.HostPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val hostPreferences: HostPreferences,
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentHost: StateFlow<String> = hostPreferences.githubHost
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HostPreferences.DEFAULT_HOST)

    fun setHost(url: String) {
        viewModelScope.launch {
            hostPreferences.setHost(url)
        }
    }

    fun useGithubCom() {
        viewModelScope.launch {
            hostPreferences.resetToDefault()
        }
    }

    fun signOut() {
        authRepository.logout()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    appVersion: String,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val currentHost by viewModel.currentHost.collectAsStateWithLifecycle()
    val isEnterprise = currentHost != HostPreferences.DEFAULT_HOST

    var useEnterprise by rememberSaveable(isEnterprise) { mutableStateOf(isEnterprise) }
    var enterpriseUrl by rememberSaveable(currentHost) {
        mutableStateOf(if (isEnterprise) currentHost else "")
    }
    var urlError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("GitHub Host", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioButton(
                    selected = !useEnterprise,
                    onClick = {
                        useEnterprise = false
                        urlError = null
                    }
                )
                Text(
                    text = "GitHub.com",
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioButton(
                    selected = useEnterprise,
                    onClick = { useEnterprise = true }
                )
                Text(
                    text = "GitHub Enterprise Server",
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            if (useEnterprise) {
                OutlinedTextField(
                    value = enterpriseUrl,
                    onValueChange = {
                        enterpriseUrl = it
                        urlError = null
                    },
                    label = { Text("Enterprise URL") },
                    placeholder = { Text("https://github.mycompany.com") },
                    isError = urlError != null,
                    supportingText = urlError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    if (!useEnterprise) {
                        viewModel.useGithubCom()
                        scope.launch { snackbarHostState.showSnackbar("Saved: GitHub.com") }
                    } else {
                        val url = enterpriseUrl.trim()
                        if (!url.startsWith("https://")) {
                            urlError = "URL must start with https://"
                        } else {
                            viewModel.setHost(url)
                            scope.launch { snackbarHostState.showSnackbar("Saved: $url") }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            OutlinedButton(
                onClick = {
                    viewModel.signOut()
                    onSignOut()
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Out")
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Version $appVersion",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
