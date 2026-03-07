package com.example.agenthq.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.example.agenthq.auth.AuthRepository
import com.example.agenthq.auth.AuthState
import com.example.agenthq.domain.usecase.SyncRepositoriesUseCase
import com.example.agenthq.domain.usecase.SyncSessionsUseCase
import com.example.agenthq.work.SyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val syncRepositoriesUseCase: SyncRepositoriesUseCase,
    private val syncSessionsUseCase: SyncSessionsUseCase,
    private val workManager: WorkManager
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun startLogin() {
        viewModelScope.launch {
            authRepository.startDeviceFlow().collect { state ->
                _authState.value = state
                if (state is AuthState.Success) {
                    onLoginSuccess()
                }
            }
        }
    }

    fun loginWithPat(token: String) {
        viewModelScope.launch {
            authRepository.loginWithPat(token).collect { state ->
                _authState.value = state
                if (state is AuthState.Success) {
                    onLoginSuccess()
                }
            }
        }
    }

    private fun onLoginSuccess() {
        viewModelScope.launch {
            SyncWorker.schedulePeriodic(workManager)
            syncRepositoriesUseCase()
            syncSessionsUseCase()
        }
    }
}
